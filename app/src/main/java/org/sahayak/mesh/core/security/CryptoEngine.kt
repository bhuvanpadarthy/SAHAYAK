package org.sahayak.mesh.core.security

import org.bouncycastle.crypto.agreement.X25519Agreement
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.params.HKDFParameters
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters
import org.bouncycastle.crypto.params.X25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

data class KeyPairBytes(
    val publicKey: ByteArray,
    val privateKey: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KeyPairBytes) return false
        return publicKey.contentEquals(other.publicKey) && privateKey.contentEquals(other.privateKey)
    }

    override fun hashCode(): Int = 31 * publicKey.contentHashCode() + privateKey.contentHashCode()
}

/**
 * Core cryptographic engine for Sahayak-Mesh X.
 *
 * NOTE ON CRYPTOGRAPHIC LIMITATIONS & THREAT MODEL:
 * - Digital signatures use Ed25519 (RFC 8032) for coordinator authentication and identity verification.
 * - One-to-one encryption currently uses Ephemeral-Static X25519 + HKDF-SHA256 + AES-256-GCM.
 * - This provides sender-ephemeral forward secrecy, but NOT bidirectional double-ratcheting or
 *   post-compromise break-in recovery. If the recipient's static private key is ever compromised,
 *   captured historical messages to that recipient can be decrypted.
 * - Production hardening roadmap: Migrate to a formally verified protocol (Noise Protocol Noise_IK/Noise_XX
 *   or Google Tink HPKE RFC 9180) to eliminate ad-hoc hybrid protocol assembly.
 */
object CryptoEngine {
    private val secureRandom = SecureRandom()
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128 // bits
    private const val X25519_KEY_LENGTH = 32
    private const val AES_KEY_LENGTH = 32 // 256 bits

    private val HKDF_INFO = "Sahayak-Mesh-X-v1-E2EE".toByteArray(StandardCharsets.UTF_8)

    // ==========================================
    // Ed25519 Digital Signatures
    // ==========================================

    fun generateSigningKeyPair(): KeyPairBytes {
        val generator = Ed25519KeyPairGenerator()
        generator.init(Ed25519KeyGenerationParameters(secureRandom))
        val pair = generator.generateKeyPair()
        val priv = (pair.private as Ed25519PrivateKeyParameters).encoded
        val pub = (pair.public as Ed25519PublicKeyParameters).encoded
        return KeyPairBytes(pub, priv)
    }

    fun sign(privateKeyBytes: ByteArray, data: ByteArray): ByteArray {
        val privateKey = Ed25519PrivateKeyParameters(privateKeyBytes, 0)
        val signer = Ed25519Signer()
        signer.init(true, privateKey)
        signer.update(data, 0, data.size)
        return signer.generateSignature()
    }

    fun verify(publicKeyBytes: ByteArray, data: ByteArray, signature: ByteArray): Boolean {
        return try {
            val publicKey = Ed25519PublicKeyParameters(publicKeyBytes, 0)
            val signer = Ed25519Signer()
            signer.init(false, publicKey)
            signer.update(data, 0, data.size)
            signer.verifySignature(signature)
        } catch (e: Exception) {
            false
        }
    }

    // ==========================================
    // X25519 + HKDF-SHA256 + AES-256-GCM E2EE
    // ==========================================

    fun generateEncryptionKeyPair(): KeyPairBytes {
        val generator = X25519KeyPairGenerator()
        generator.init(X25519KeyGenerationParameters(secureRandom))
        val pair = generator.generateKeyPair()
        val priv = (pair.private as X25519PrivateKeyParameters).encoded
        val pub = (pair.public as X25519PublicKeyParameters).encoded
        return KeyPairBytes(pub, priv)
    }

    /**
     * Encrypts plaintext bytes for recipient's X25519 public key.
     * Uses Ephemeral-Static X25519, HKDF-SHA256 key derivation, and AES-256-GCM AEAD.
     * Optionally binds [associatedData] (e.g. sender + recipient identities).
     */
    fun encrypt(
        recipientPublicKeyBytes: ByteArray,
        plaintext: ByteArray,
        associatedData: ByteArray? = null
    ): String {
        val ephemeralKeyPair = generateEncryptionKeyPair()
        val sharedSecret = calculateSharedSecret(ephemeralKeyPair.privateKey, recipientPublicKeyBytes)
        val aesKeyBytes = deriveKeyHkdf(sharedSecret, salt = ephemeralKeyPair.publicKey, info = HKDF_INFO)

        val iv = ByteArray(GCM_IV_LENGTH)
        secureRandom.nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(aesKeyBytes, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

        associatedData?.let { cipher.updateAAD(it) }

        val ciphertext = cipher.doFinal(plaintext)

        val bundle = ByteBuffer.allocate(X25519_KEY_LENGTH + GCM_IV_LENGTH + ciphertext.size)
            .put(ephemeralKeyPair.publicKey)
            .put(iv)
            .put(ciphertext)
            .array()

        return Base64.getEncoder().encodeToString(bundle)
    }

    /**
     * Decrypts ciphertext bundle using recipient's private X25519 key.
     * Verifies GCM auth tag and any bound [associatedData].
     */
    fun decrypt(
        recipientPrivateKeyBytes: ByteArray,
        base64Bundle: String,
        associatedData: ByteArray? = null
    ): ByteArray {
        val bundle = Base64.getDecoder().decode(base64Bundle)
        require(bundle.size >= X25519_KEY_LENGTH + GCM_IV_LENGTH + 16) { "Malformed encrypted bundle: too short" }

        val buffer = ByteBuffer.wrap(bundle)
        val ephemeralPublicKey = ByteArray(X25519_KEY_LENGTH)
        buffer.get(ephemeralPublicKey)

        val iv = ByteArray(GCM_IV_LENGTH)
        buffer.get(iv)

        val ciphertext = ByteArray(bundle.size - X25519_KEY_LENGTH - GCM_IV_LENGTH)
        buffer.get(ciphertext)

        val sharedSecret = calculateSharedSecret(recipientPrivateKeyBytes, ephemeralPublicKey)
        val aesKeyBytes = deriveKeyHkdf(sharedSecret, salt = ephemeralPublicKey, info = HKDF_INFO)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val keySpec = SecretKeySpec(aesKeyBytes, "AES")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

        associatedData?.let { cipher.updateAAD(it) }

        return cipher.doFinal(ciphertext)
    }

    private fun calculateSharedSecret(privateKeyBytes: ByteArray, peerPublicKeyBytes: ByteArray): ByteArray {
        val priv = X25519PrivateKeyParameters(privateKeyBytes, 0)
        val pub = X25519PublicKeyParameters(peerPublicKeyBytes, 0)
        val agreement = X25519Agreement()
        agreement.init(priv)
        val secret = ByteArray(agreement.agreementSize)
        agreement.calculateAgreement(pub, secret, 0)

        // Verify shared secret is not all zeros (RFC 7748 check)
        require(!secret.all { it == 0.toByte() }) { "Invalid X25519 key agreement: shared secret is all zeros" }

        return secret
    }

    /**
     * RFC 5869 HKDF-SHA256 Key Derivation Function.
     */
    private fun deriveKeyHkdf(sharedSecret: ByteArray, salt: ByteArray, info: ByteArray): ByteArray {
        val hkdf = HKDFBytesGenerator(SHA256Digest())
        hkdf.init(HKDFParameters(sharedSecret, salt, info))
        val output = ByteArray(AES_KEY_LENGTH)
        hkdf.generateBytes(output, 0, AES_KEY_LENGTH)
        return output
    }

    fun toHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    fun fromHex(hex: String): ByteArray {
        check(hex.length % 2 == 0) { "Hex string must have an even length" }
        return ByteArray(hex.length / 2) { i ->
            hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }
}
