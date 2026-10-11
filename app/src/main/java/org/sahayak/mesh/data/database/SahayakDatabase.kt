package org.sahayak.mesh.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.sahayak.mesh.data.database.daos.ContactDao
import org.sahayak.mesh.data.database.daos.IncidentDao
import org.sahayak.mesh.data.database.daos.MessageDao
import org.sahayak.mesh.data.database.daos.MissionDao
import org.sahayak.mesh.data.database.entities.ContactEntity
import org.sahayak.mesh.data.database.entities.IncidentEntity
import org.sahayak.mesh.data.database.entities.MessageEntity
import org.sahayak.mesh.data.database.entities.MissionEntity
import org.sahayak.mesh.data.database.entities.RecipientStatusEntity

@Database(
    entities = [
        MessageEntity::class,
        RecipientStatusEntity::class,
        ContactEntity::class,
        MissionEntity::class,
        IncidentEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class SahayakDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
    abstract fun missionDao(): MissionDao
    abstract fun incidentDao(): IncidentDao

    companion object {
        const val DATABASE_NAME = "sahayak_mesh.db"

        @Volatile
        private var INSTANCE: SahayakDatabase? = null

        fun getInstance(context: Context): SahayakDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SahayakDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigrationOnDowngrade()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
