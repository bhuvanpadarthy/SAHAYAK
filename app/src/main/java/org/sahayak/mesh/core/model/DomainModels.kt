package org.sahayak.mesh.core.model

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val state: MessageState = MessageState.LOCAL_QUEUED,
    val priority: Priority = Priority.NORMAL,
    val timestampMs: Long = System.currentTimeMillis(),
    val expiresAtMs: Long = timestampMs + 24 * 3600 * 1000L,
    val replyToId: String? = null,
    val isBroadcast: Boolean = false,
    val missionId: String? = null
)

data class Contact(
    val deviceId: String,
    val displayName: String,
    val publicKeyHex: String,
    val trustLevel: TrustLevel = TrustLevel.UNKNOWN,
    val role: Role = Role.VISITOR,
    val lastSeenMs: Long = 0L,
    val isBlocked: Boolean = false
)

data class Mission(
    val missionId: String,
    val title: String,
    val description: String,
    val coordinatorId: String,
    val coordinatorPublicKeyHex: String,
    val createdAtMs: Long,
    val expiresAtMs: Long,
    val isActive: Boolean = true
)

data class Incident(
    val incidentId: String,
    val originDeviceId: String,
    val category: IncidentCategory,
    val severity: AlertLevel,
    val description: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationDescription: String = "",
    val status: IncidentStatus = IncidentStatus.OPEN,
    val assignedResponderId: String? = null,
    val createdAtMs: Long = System.currentTimeMillis(),
    val updatedAtMs: Long = createdAtMs
)

data class RecipientDeliveryStatus(
    val messageId: String,
    val recipientId: String,
    val state: MessageState,
    val updatedAtMs: Long = System.currentTimeMillis(),
    val notes: String? = null
)
