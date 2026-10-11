package org.sahayak.mesh

import android.app.Application
import org.sahayak.mesh.core.security.CryptoEngine
import org.sahayak.mesh.core.security.KeyPairBytes
import org.sahayak.mesh.data.database.SahayakDatabase
import java.io.File

class SahayakApp : Application() {

    lateinit var database: SahayakDatabase
        private set

    lateinit var identitySigningKeyPair: KeyPairBytes
        private set

    lateinit var identityEncryptionKeyPair: KeyPairBytes
        private set

    lateinit var deviceId: String
        private set

    override fun onCreate() {
        super.onCreate()
        database = SahayakDatabase.getInstance(this)
        initIdentityKeys()
    }

    private fun initIdentityKeys() {
        val signingKeyFile = File(filesDir, "identity_signing.key")
        val encKeyFile = File(filesDir, "identity_enc.key")

        if (signingKeyFile.exists() && encKeyFile.exists()) {
            val signBytes = signingKeyFile.readBytes()
            val encBytes = encKeyFile.readBytes()
            identitySigningKeyPair = KeyPairBytes(
                publicKey = signBytes.copyOfRange(0, 32),
                privateKey = signBytes.copyOfRange(32, 64)
            )
            identityEncryptionKeyPair = KeyPairBytes(
                publicKey = encBytes.copyOfRange(0, 32),
                privateKey = encBytes.copyOfRange(32, 64)
            )
        } else {
            identitySigningKeyPair = CryptoEngine.generateSigningKeyPair()
            identityEncryptionKeyPair = CryptoEngine.generateEncryptionKeyPair()

            signingKeyFile.writeBytes(identitySigningKeyPair.publicKey + identitySigningKeyPair.privateKey)
            encKeyFile.writeBytes(identityEncryptionKeyPair.publicKey + identityEncryptionKeyPair.privateKey)
        }

        deviceId = CryptoEngine.toHex(identitySigningKeyPair.publicKey)
    }
}
