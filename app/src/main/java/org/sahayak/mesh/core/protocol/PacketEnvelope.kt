package org.sahayak.mesh.core.protocol

import org.sahayak.mesh.core.model.PayloadType
import org.sahayak.mesh.core.model.Priority
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Versioned wire envelope transported across the mesh.
 */
data class PacketEnvelope(
    val protocolVersion: Int = CURRENT_VERSION,
    val packetId: String,
    val senderId: String,
    val recipientId: String, // Device public key hex or "*" for broadcast
    val missionId: String? = null,
    val payloadType: PayloadType,
    val priority: Priority = Priority.NORMAL,
    val hopLimit: Int = DEFAULT_HOP_LIMIT,
    val hopCount: Int = 0,
    val createdAtMs: Long = System.currentTimeMillis(),
    val expiresAtMs: Long = createdAtMs + 24 * 3600 * 1000L,
    val signature: String? = null, // Base64 signature of canonical header + payload
    val encryptedPayload: String // Base64 ciphertext or canonical JSON string
) {
    companion object {
        const val CURRENT_VERSION = 1
        const val DEFAULT_HOP_LIMIT = 5
        const val MAX_PACKET_BYTES = 64 * 1024 // 64 KB limit
        const val BROADCAST_RECIPIENT = "*"
    }

    /**
     * Produces deterministic canonical bytes for signature generation and verification.
     */
    fun canonicalHeaderBytes(): ByteArray {
        val payloadHash = hashSha256(encryptedPayload.toByteArray(StandardCharsets.UTF_8))
        val canonicalString = buildString {
            append(protocolVersion).append(":")
            append(packetId).append(":")
            append(senderId).append(":")
            append(recipientId).append(":")
            append(missionId ?: "").append(":")
            append(payloadType.name).append(":")
            append(priority.name).append(":")
            append(createdAtMs).append(":")
            append(expiresAtMs).append(":")
            append(payloadHash)
        }
        return canonicalString.toByteArray(StandardCharsets.UTF_8)
    }

    private fun hashSha256(data: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data)
        return hash.joinToString("") { "%02x".format(it) }
    }
}
