package org.sahayak.mesh.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.sahayak.mesh.core.model.MessageState
import org.sahayak.mesh.core.model.Priority
import org.sahayak.mesh.data.database.entities.MessageEntity
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RoomPersistenceTest {

    @Test
    fun testQueuedMessagesSurviveDatabaseCloseAndReopen() {
        runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = File(context.filesDir, "test_persistence.db")
        dbFile.delete()

        // 1. First DB instance (Process 1)
        var db = Room.databaseBuilder(context, SahayakDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()

        val queuedMessage = MessageEntity(
            id = "msg-persist-101",
            conversationId = "conv-field-team",
            senderId = "sender-alpha",
            recipientId = "recip-bravo",
            content = "Encrypted mission update payload",
            state = MessageState.LOCAL_QUEUED,
            priority = Priority.HIGH,
            timestampMs = 1791720000000L,
            expiresAtMs = 1791723600000L,
            replyToId = null,
            isBroadcast = false,
            missionId = "mission-1"
        )

        db.messageDao().insertMessage(queuedMessage)

        // Verify it exists in DB 1
        val retrieved1 = db.messageDao().getMessageById("msg-persist-101")
        assertNotNull(retrieved1)
        assertEquals(MessageState.LOCAL_QUEUED, retrieved1?.state)

        // 2. Simulate Process Termination: close database instance completely
        db.close()

        // 3. Second DB instance (Process 2 / App Restart)
        val dbAfterRestart = Room.databaseBuilder(context, SahayakDatabase::class.java, dbFile.absolutePath)
            .allowMainThreadQueries()
            .build()

        // Query queued messages awaiting transmission
        val pendingList = dbAfterRestart.messageDao().getMessagesByState(MessageState.LOCAL_QUEUED)
        assertEquals(1, pendingList.size)

        val retrievedAfterRestart = pendingList.first()
        assertEquals("msg-persist-101", retrievedAfterRestart.id)
        assertEquals(MessageState.LOCAL_QUEUED, retrievedAfterRestart.state)
        assertEquals("Encrypted mission update payload", retrievedAfterRestart.content)
        assertEquals(Priority.HIGH, retrievedAfterRestart.priority)

        dbAfterRestart.close()
        dbFile.delete()
        }
    }
}
