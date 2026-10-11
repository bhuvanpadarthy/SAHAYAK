package org.sahayak.mesh.core.security

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.util.Base64

class CryptoEngineTest {

    @Test
    fun testEd25519SignatureSigningAndVerification() {
        val keyPair = CryptoEngine.generateSigningKeyPair()
        val message = "Evacuate North Gate Immediately".toByteArray(StandardCharsets.UTF_8)

        val signature = CryptoEngine.sign(keyPair.privateKey, message)
        assertEquals(64, signature.size) // Ed25519 signatures are 64 bytes

        val isValid = CryptoEngine.verify(keyPair.publicKey, message, signature)
        assertTrue(isValid)

        // Tampered message should fail
        val tamperedMessage = "Evacuate South Gate Immediately".toByteArray(StandardCharsets.UTF_8)
        val isTamperedValid = CryptoEngine.verify(keyPair.publicKey, tamperedMessage, signature)
        assertFalse(isTamperedValid)
    }

    @Test
    fun testX25519EncryptionAndDecryptionRoundtrip() {
        val recipientKeyPair = CryptoEngine.generateEncryptionKeyPair()
        val originalText = "Field Responder Alpha: Patient stabilized, requesting stretcher."
        val originalBytes = originalText.toByteArray(StandardCharsets.UTF_8)

        val encryptedBundleBase64 = CryptoEngine.encrypt(recipientKeyPair.publicKey, originalBytes)
        val decryptedBytes = CryptoEngine.decrypt(recipientKeyPair.privateKey, encryptedBundleBase64)

        assertArrayEquals(originalBytes, decryptedBytes)
        assertEquals(originalText, String(decryptedBytes, StandardCharsets.UTF_8))
    }

    @Test(expected = Exception::class)
    fun testTamperedCiphertextFailsAuthentication() {
        val recipientKeyPair = CryptoEngine.generateEncryptionKeyPair()
        val originalBytes = "Secret coordinates".toByteArray(StandardCharsets.UTF_8)

        val encryptedBundleBase64 = CryptoEngine.encrypt(recipientKeyPair.publicKey, originalBytes)
        val bundle = Base64.getDecoder().decode(encryptedBundleBase64)

        // Tamper with the last byte of the ciphertext / auth tag
        bundle[bundle.size - 1] = (bundle[bundle.size - 1].toInt() xor 0xFF).toByte()
        val tamperedBase64 = Base64.getEncoder().encodeToString(bundle)

        // Decryption must throw due to GCM authentication tag mismatch
        CryptoEngine.decrypt(recipientKeyPair.privateKey, tamperedBase64)
    }

    @Test(expected = Exception::class)
    fun testWrongPrivateKeyCannotDecrypt() {
        val recipientA = CryptoEngine.generateEncryptionKeyPair()
        val eavesdropperB = CryptoEngine.generateEncryptionKeyPair()
        val originalBytes = "Private Citizen Message".toByteArray(StandardCharsets.UTF_8)

        val encryptedBundleBase64 = CryptoEngine.encrypt(recipientA.publicKey, originalBytes)

        // Eavesdropper B attempts to decrypt with their own key -> throws
        CryptoEngine.decrypt(eavesdropperB.privateKey, encryptedBundleBase64)
    }
}
