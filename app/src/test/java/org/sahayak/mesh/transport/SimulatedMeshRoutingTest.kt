package org.sahayak.mesh.transport

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sahayak.mesh.core.model.PayloadType
import org.sahayak.mesh.core.protocol.PacketEnvelope
import org.sahayak.mesh.core.protocol.ProtocolValidator
import org.sahayak.mesh.core.security.CryptoEngine
import org.sahayak.mesh.transport.api.PacketListener
import org.sahayak.mesh.transport.api.SimulatedMeshTransport
import java.nio.charset.StandardCharsets

class SimulatedMeshRoutingTest {

    @Test
    fun testStoreAndForwardE2EERoutingThroughUntrustedRelay() = runBlocking {
        // Setup 3 nodes: Node A, Relay B, Node C
        val transportA = SimulatedMeshTransport(nodeId = "node-A")
        val transportB = SimulatedMeshTransport(nodeId = "relay-B")
        val transportC = SimulatedMeshTransport(nodeId = "node-C")

        // Topology: A <--> B <--> C (A and C cannot reach each other directly)
        transportA.linkTo(transportB)
        transportB.linkTo(transportC)

        // Cryptographic keys for A and C
        val keyPairA = CryptoEngine.generateEncryptionKeyPair()
        val keyPairB = CryptoEngine.generateEncryptionKeyPair() // Relay's key
        val keyPairC = CryptoEngine.generateEncryptionKeyPair()

        val recipientCPubHex = CryptoEngine.toHex(keyPairC.publicKey)

        val plaintextMessage = "Urgent: Supplies located in Warehouse 4"
        val encryptedBundleBase64 = CryptoEngine.encrypt(
            recipientPublicKeyBytes = keyPairC.publicKey,
            plaintext = plaintextMessage.toByteArray(StandardCharsets.UTF_8)
        )

        val validator = ProtocolValidator()
        var relayBReceivedPacket: PacketEnvelope? = null
        var relayBAttemptedDecryptionSuccess = false

        var nodeCReceivedPacket: PacketEnvelope? = null
        var nodeCDecryptedPlaintext: String? = null

        // Relay B Packet Listener: store-and-forward behavior
        transportB.registerPacketListener(object : PacketListener {
            override fun onPacketReceived(envelope: PacketEnvelope, fromPeerId: String) {
                relayBReceivedPacket = envelope

                // Relay B tries to eavesdrop and decrypt:
                try {
                    CryptoEngine.decrypt(keyPairB.privateKey, envelope.encryptedPayload)
                    relayBAttemptedDecryptionSuccess = true
                } catch (e: Exception) {
                    relayBAttemptedDecryptionSuccess = false // Expected! Relay cannot read E2EE payload
                }

                // If eligible for relay, forward to neighbors except sender
                if (validator.isEligibleForRelay(envelope)) {
                    val forwarded = envelope.copy(hopCount = envelope.hopCount + 1)
                    runBlocking {
                        transportB.sendPacket("node-C", forwarded)
                    }
                }
            }
        })

        // Node C Packet Listener: final recipient behavior
        transportC.registerPacketListener(object : PacketListener {
            override fun onPacketReceived(envelope: PacketEnvelope, fromPeerId: String) {
                nodeCReceivedPacket = envelope
                if (envelope.recipientId == recipientCPubHex) {
                    val decryptedBytes = CryptoEngine.decrypt(keyPairC.privateKey, envelope.encryptedPayload)
                    nodeCDecryptedPlaintext = String(decryptedBytes, StandardCharsets.UTF_8)
                }
            }
        })

        // Node A sends packet to direct neighbor Relay B
        val envelopeFromA = PacketEnvelope(
            packetId = "msg-e2ee-101",
            senderId = CryptoEngine.toHex(keyPairA.publicKey),
            recipientId = recipientCPubHex,
            payloadType = PayloadType.CHAT_DIRECT,
            hopLimit = 5,
            hopCount = 0,
            encryptedPayload = encryptedBundleBase64
        )

        val sentToB = transportA.sendPacket("relay-B", envelopeFromA)
        assertTrue(sentToB)

        // Assertions:
        // 1. Relay B received packet
        assertTrue(relayBReceivedPacket != null)
        assertEquals("msg-e2ee-101", relayBReceivedPacket?.packetId)

        // 2. Relay B failed to decrypt (E2EE integrity maintained)
        assertFalse(relayBAttemptedDecryptionSuccess)

        // 3. Node C received forwarded packet
        assertTrue(nodeCReceivedPacket != null)
        assertEquals(1, nodeCReceivedPacket?.hopCount) // Hop incremented by Relay B

        // 4. Node C decrypted original plaintext correctly
        assertEquals(plaintextMessage, nodeCDecryptedPlaintext)
    }
}
