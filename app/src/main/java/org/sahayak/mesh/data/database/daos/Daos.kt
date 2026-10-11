package org.sahayak.mesh.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.sahayak.mesh.core.model.MessageState
import org.sahayak.mesh.data.database.entities.ContactEntity
import org.sahayak.mesh.data.database.entities.IncidentEntity
import org.sahayak.mesh.data.database.entities.MessageEntity
import org.sahayak.mesh.data.database.entities.MissionEntity
import org.sahayak.mesh.data.database.entities.RecipientStatusEntity

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestampMs ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE state = :state ORDER BY timestampMs ASC")
    suspend fun getMessagesByState(state: MessageState): List<MessageEntity>

    @Query("UPDATE messages SET state = :newState WHERE id = :id")
    suspend fun updateState(id: String, newState: MessageState)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecipientStatus(status: RecipientStatusEntity)

    @Query("SELECT * FROM recipient_statuses WHERE messageId = :messageId")
    suspend fun getRecipientStatuses(messageId: String): List<RecipientStatusEntity>
}

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Query("SELECT * FROM contacts ORDER BY displayName ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE deviceId = :deviceId")
    suspend fun getContact(deviceId: String): ContactEntity?
}

@Dao
interface MissionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity)

    @Query("SELECT * FROM missions WHERE isActive = 1 ORDER BY createdAtMs DESC")
    fun getActiveMissions(): Flow<List<MissionEntity>>

    @Query("SELECT * FROM missions WHERE missionId = :missionId")
    suspend fun getMission(missionId: String): MissionEntity?
}

@Dao
interface IncidentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: IncidentEntity)

    @Update
    suspend fun updateIncident(incident: IncidentEntity)

    @Query("SELECT * FROM incidents ORDER BY createdAtMs DESC")
    fun getAllIncidents(): Flow<List<IncidentEntity>>

    @Query("SELECT * FROM incidents WHERE incidentId = :id")
    suspend fun getIncident(id: String): IncidentEntity?
}
