package org.sahayak.mesh.core.protocol

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sahayak.mesh.core.model.PayloadType
import org.sahayak.mesh.core.model.Priority

class ProtocolValidatorTest {

    private val validator = ProtocolValidator()

    @Test
    fun testValidEnvelopePasses() {
        val now = System.currentTimeMillis()
        val envelope = PacketEnvelope(
            packetId = "test-pkt-1",
            senderId = "sender-key-hex",
            recipientId = "recip-key-hex",
            payloadType = PayloadType.CHAT_DIRECT,
            priority = Priority.NORMAL,
            createdAtMs = now,
            expiresAtMs = now + 3600_000L,
            encryptedPayload = "dGVzdCBlbmNyeXB0ZWQgcGF5bG9hZA=="
        )

        val result = validator.validate(envelope, now)
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun testOversizedPayloadIsRejected() {
        val now = System.currentTimeMillis()
        val hugePayload = "A".repeat(65 * 1024) // 65 KB exceeds 64 KB limit
        val envelope = PacketEnvelope(
            packetId = "test-pkt-2",
            senderId = "sender-key-hex",
            recipientId = "recip-key-hex",
            payloadType = PayloadType.CHAT_DIRECT,
            createdAtMs = now,
            expiresAtMs = now + 3600_000L,
            encryptedPayload = hugePayload
        )

        val result = validator.validate(envelope, now)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).reason.contains("exceeds maximum allowed bound"))
    }

    @Test
    fun testExpiredPacketIsRejected() {
        val now = System.currentTimeMillis()
        val envelope = PacketEnvelope(
            packetId = "test-pkt-3",
            senderId = "sender-key-hex",
            recipientId = "recip-key-hex",
            payloadType = PayloadType.CHAT_DIRECT,
            createdAtMs = now - 7200_000L,
            expiresAtMs = now - 3600_000L, // Expired 1 hour ago
            encryptedPayload = "valid-payload"
        )

        val result = validator.validate(envelope, now)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).reason.contains("expired"))
    }

    @Test
    fun testFutureClockSkewBeyondToleranceIsRejected() {
        val now = System.currentTimeMillis()
        val envelope = PacketEnvelope(
            packetId = "test-pkt-4",
            senderId = "sender-key-hex",
            recipientId = "recip-key-hex",
            payloadType = PayloadType.CHAT_DIRECT,
            createdAtMs = now + 60 * 60 * 1000L, // 60 minutes in future (tolerance is 15m)
            expiresAtMs = now + 24 * 3600 * 1000L,
            encryptedPayload = "valid-payload"
        )

        val result = validator.validate(envelope, now)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).reason.contains("clock skew"))
    }

    @Test
    fun testRelayEligibilityRespectsHopLimit() {
        val now = System.currentTimeMillis()
        val eligibleEnvelope = PacketEnvelope(
            packetId = "test-pkt-5",
            senderId = "sender-key-hex",
            recipientId = "recip-key-hex",
            payloadType = PayloadType.CHAT_DIRECT,
            hopLimit = 5,
            hopCount = 3,
            createdAtMs = now,
            expiresAtMs = now + 3600_000L,
            encryptedPayload = "payload"
        )
        assertTrue(validator.isEligibleForRelay(eligibleEnvelope, now))

        val exhaustedEnvelope = eligibleEnvelope.copy(hopCount = 5)
        assertFalse(validator.isEligibleForRelay(exhaustedEnvelope, now))
    }
}
