package org.sahayak.mesh.data.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.sahayak.mesh.core.model.AlertLevel
import org.sahayak.mesh.core.model.IncidentCategory
import org.sahayak.mesh.core.model.IncidentStatus
import org.sahayak.mesh.core.model.MessageState
import org.sahayak.mesh.core.model.Priority
import org.sahayak.mesh.core.model.Role
import org.sahayak.mesh.core.model.TrustLevel

@Entity(
    tableName = "messages",
    indices = [
        Index("conversationId"),
        Index("state"),
        Index("timestampMs"),
        Index("expiresAtMs")
    ]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val state: MessageState,
    val priority: Priority,
    val timestampMs: Long,
    val expiresAtMs: Long,
    val replyToId: String? = null,
    val isBroadcast: Boolean = false,
    val missionId: String? = null
)

@Entity(
    tableName = "recipient_statuses",
    primaryKeys = ["messageId", "recipientId"],
    indices = [Index("messageId"), Index("recipientId")]
)
data class RecipientStatusEntity(
    val messageId: String,
    val recipientId: String,
    val state: MessageState,
    val updatedAtMs: Long,
    val notes: String? = null
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val deviceId: String,
    val displayName: String,
    val publicKeyHex: String,
    val trustLevel: TrustLevel,
    val role: Role,
    val lastSeenMs: Long,
    val isBlocked: Boolean = false
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val missionId: String,
    val title: String,
    val description: String,
    val coordinatorId: String,
    val coordinatorPublicKeyHex: String,
    val createdAtMs: Long,
    val expiresAtMs: Long,
    val isActive: Boolean = true
)

@Entity(
    tableName = "incidents",
    indices = [Index("status"), Index("severity")]
)
data class IncidentEntity(
    @PrimaryKey val incidentId: String,
    val originDeviceId: String,
    val category: IncidentCategory,
    val severity: AlertLevel,
    val description: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationDescription: String = "",
    val status: IncidentStatus,
    val assignedResponderId: String? = null,
    val createdAtMs: Long,
    val updatedAtMs: Long
)
