package org.sahayak.mesh.data.database

import androidx.room.TypeConverter
import org.sahayak.mesh.core.model.AlertLevel
import org.sahayak.mesh.core.model.IncidentCategory
import org.sahayak.mesh.core.model.IncidentStatus
import org.sahayak.mesh.core.model.MessageState
import org.sahayak.mesh.core.model.Priority
import org.sahayak.mesh.core.model.Role
import org.sahayak.mesh.core.model.TrustLevel

class Converters {
    @TypeConverter
    fun fromMessageState(value: MessageState): String = value.name

    @TypeConverter
    fun toMessageState(value: String): MessageState = MessageState.valueOf(value)

    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromRole(value: Role): String = value.name

    @TypeConverter
    fun toRole(value: String): Role = Role.valueOf(value)

    @TypeConverter
    fun fromTrustLevel(value: TrustLevel): String = value.name

    @TypeConverter
    fun toTrustLevel(value: String): TrustLevel = TrustLevel.valueOf(value)

    @TypeConverter
    fun fromIncidentCategory(value: IncidentCategory): String = value.name

    @TypeConverter
    fun toIncidentCategory(value: String): IncidentCategory = IncidentCategory.valueOf(value)

    @TypeConverter
    fun fromIncidentStatus(value: IncidentStatus): String = value.name

    @TypeConverter
    fun toIncidentStatus(value: String): IncidentStatus = IncidentStatus.valueOf(value)

    @TypeConverter
    fun fromAlertLevel(value: AlertLevel): String = value.name

    @TypeConverter
    fun toAlertLevel(value: String): AlertLevel = AlertLevel.valueOf(value)
}
