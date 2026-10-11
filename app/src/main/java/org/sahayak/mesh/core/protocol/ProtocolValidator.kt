package org.sahayak.mesh.core.protocol

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

class ProtocolValidator(
    private val clockSkewToleranceMs: Long = 15 * 60 * 1000L // 15 minutes
) {

    fun validate(envelope: PacketEnvelope, currentTimeMs: Long = System.currentTimeMillis()): ValidationResult {
        if (envelope.protocolVersion <= 0 || envelope.protocolVersion > PacketEnvelope.CURRENT_VERSION) {
            return ValidationResult.Invalid("Unsupported protocol version: ${envelope.protocolVersion}")
        }

        if (envelope.packetId.isBlank()) {
            return ValidationResult.Invalid("packetId must not be blank")
        }

        if (envelope.senderId.isBlank()) {
            return ValidationResult.Invalid("senderId must not be blank")
        }

        if (envelope.recipientId.isBlank()) {
            return ValidationResult.Invalid("recipientId must not be blank")
        }

        if (envelope.encryptedPayload.isBlank()) {
            return ValidationResult.Invalid("encryptedPayload must not be blank")
        }

        if (envelope.encryptedPayload.length > PacketEnvelope.MAX_PACKET_BYTES) {
            return ValidationResult.Invalid("Payload size exceeds maximum allowed bound of ${PacketEnvelope.MAX_PACKET_BYTES} bytes")
        }

        // Expiry check
        if (envelope.expiresAtMs < currentTimeMs) {
            return ValidationResult.Invalid("Packet has expired: expiresAtMs=${envelope.expiresAtMs}, now=$currentTimeMs")
        }

        // Future clock skew check (tolerance window)
        if (envelope.createdAtMs > currentTimeMs + clockSkewToleranceMs) {
            return ValidationResult.Invalid("Packet createdAtMs is too far in the future (exceeds clock skew tolerance)")
        }

        // Hop limit check
        if (envelope.hopCount < 0) {
            return ValidationResult.Invalid("hopCount cannot be negative")
        }

        if (envelope.hopLimit <= 0) {
            return ValidationResult.Invalid("hopLimit must be strictly positive")
        }

        return ValidationResult.Valid
    }

    fun isEligibleForRelay(envelope: PacketEnvelope, currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        if (validate(envelope, currentTimeMs) !is ValidationResult.Valid) {
            return false
        }
        return envelope.hopCount < envelope.hopLimit
    }
}
