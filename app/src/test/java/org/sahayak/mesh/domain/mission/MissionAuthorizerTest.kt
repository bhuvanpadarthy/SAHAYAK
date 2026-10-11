package org.sahayak.mesh.domain.mission

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sahayak.mesh.core.model.Mission
import org.sahayak.mesh.core.model.PayloadType
import org.sahayak.mesh.core.model.Role
import org.sahayak.mesh.core.protocol.PacketEnvelope
import org.sahayak.mesh.core.security.CryptoEngine
import java.util.Base64

class MissionAuthorizerTest {

    private val authorizer = MissionAuthorizer()

    @Test
    fun testValidSignedCoordinatorBroadcastIsAuthorized() {
        val coordinatorKeyPair = CryptoEngine.generateSigningKeyPair()
        val coordinatorPubHex = CryptoEngine.toHex(coordinatorKeyPair.publicKey)
        val coordinatorId = "coord-01"

        val mission = Mission(
            missionId = "mission-olympus",
            title = "Campus Flood Response",
            description = "Coordinate building evacuations",
            coordinatorId = coordinatorId,
            coordinatorPublicKeyHex = coordinatorPubHex,
            createdAtMs = System.currentTimeMillis(),
            expiresAtMs = System.currentTimeMillis() + 86400_000L,
            isActive = true
        )

        val envelopeUnsigned = PacketEnvelope(
            packetId = "bcast-01",
            senderId = coordinatorId,
            recipientId = PacketEnvelope.BROADCAST_RECIPIENT,
            missionId = "mission-olympus",
            payloadType = PayloadType.BROADCAST_ALERT,
            encryptedPayload = "{\"alert\":\"Move to higher ground\"}"
        )

        // Sign the canonical header
        val canonicalBytes = envelopeUnsigned.canonicalHeaderBytes()
        val signature = CryptoEngine.sign(coordinatorKeyPair.privateKey, canonicalBytes)
        val envelopeSigned = envelopeUnsigned.copy(
            signature = Base64.getEncoder().encodeToString(signature)
        )

        assertTrue(authorizer.authorizeBroadcast(envelopeSigned, mission))
    }

    @Test
    fun testUnsignedOrForgedBroadcastIsRejected() {
        val coordinatorKeyPair = CryptoEngine.generateSigningKeyPair()
        val rogueKeyPair = CryptoEngine.generateSigningKeyPair()

        val mission = Mission(
            missionId = "mission-olympus",
            title = "Campus Flood Response",
            description = "Coordinate building evacuations",
            coordinatorId = "coord-01",
            coordinatorPublicKeyHex = CryptoEngine.toHex(coordinatorKeyPair.publicKey),
            createdAtMs = System.currentTimeMillis(),
            expiresAtMs = System.currentTimeMillis() + 86400_000L,
            isActive = true
        )

        val envelope = PacketEnvelope(
            packetId = "bcast-forged",
            senderId = "coord-01",
            recipientId = PacketEnvelope.BROADCAST_RECIPIENT,
            missionId = "mission-olympus",
            payloadType = PayloadType.BROADCAST_ALERT,
            encryptedPayload = "{\"alert\":\"Fake notice\"}"
        )

        // Sign with rogue key instead of coordinator key
        val rogueSignature = CryptoEngine.sign(rogueKeyPair.privateKey, envelope.canonicalHeaderBytes())
        val envelopeForged = envelope.copy(
            signature = Base64.getEncoder().encodeToString(rogueSignature)
        )

        assertFalse(authorizer.authorizeBroadcast(envelopeForged, mission))
    }

    @Test
    fun testInactiveMissionRejectsBroadcast() {
        val coordinatorKeyPair = CryptoEngine.generateSigningKeyPair()
        val mission = Mission(
            missionId = "mission-closed",
            title = "Closed Mission",
            description = "Old drill",
            coordinatorId = "coord-01",
            coordinatorPublicKeyHex = CryptoEngine.toHex(coordinatorKeyPair.publicKey),
            createdAtMs = System.currentTimeMillis() - 100_000L,
            expiresAtMs = System.currentTimeMillis() - 50_000L,
            isActive = false // Closed!
        )

        val envelope = PacketEnvelope(
            packetId = "bcast-closed",
            senderId = "coord-01",
            recipientId = PacketEnvelope.BROADCAST_RECIPIENT,
            missionId = "mission-closed",
            payloadType = PayloadType.BROADCAST_ALERT,
            encryptedPayload = "{\"alert\":\"Late drill\"}"
        )
        val signature = CryptoEngine.sign(coordinatorKeyPair.privateKey, envelope.canonicalHeaderBytes())
        val envelopeSigned = envelope.copy(signature = Base64.getEncoder().encodeToString(signature))

        assertFalse(authorizer.authorizeBroadcast(envelopeSigned, mission))
    }

    @Test
    fun testRolePermissions() {
        assertTrue(authorizer.canBroadcast(Role.COORDINATOR))
        assertFalse(authorizer.canBroadcast(Role.RESPONDER))
        assertFalse(authorizer.canBroadcast(Role.VOLUNTEER))
        assertFalse(authorizer.canBroadcast(Role.VISITOR))

        assertTrue(authorizer.canAssignResponders(Role.COORDINATOR))
        assertTrue(authorizer.canAssignResponders(Role.STAFF))
        assertFalse(authorizer.canAssignResponders(Role.VISITOR))
    }
}
