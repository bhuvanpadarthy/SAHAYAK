package org.sahayak.mesh.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.sahayak.mesh.data.database.daos.ContactDao;
import org.sahayak.mesh.data.database.daos.ContactDao_Impl;
import org.sahayak.mesh.data.database.daos.IncidentDao;
import org.sahayak.mesh.data.database.daos.IncidentDao_Impl;
import org.sahayak.mesh.data.database.daos.MessageDao;
import org.sahayak.mesh.data.database.daos.MessageDao_Impl;
import org.sahayak.mesh.data.database.daos.MissionDao;
import org.sahayak.mesh.data.database.daos.MissionDao_Impl;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SahayakDatabase_Impl extends SahayakDatabase {
  private volatile MessageDao _messageDao;

  private volatile ContactDao _contactDao;

  private volatile MissionDao _missionDao;

  private volatile IncidentDao _incidentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `conversationId` TEXT NOT NULL, `senderId` TEXT NOT NULL, `recipientId` TEXT NOT NULL, `content` TEXT NOT NULL, `state` TEXT NOT NULL, `priority` TEXT NOT NULL, `timestampMs` INTEGER NOT NULL, `expiresAtMs` INTEGER NOT NULL, `replyToId` TEXT, `isBroadcast` INTEGER NOT NULL, `missionId` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_conversationId` ON `messages` (`conversationId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_state` ON `messages` (`state`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_timestampMs` ON `messages` (`timestampMs`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_expiresAtMs` ON `messages` (`expiresAtMs`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recipient_statuses` (`messageId` TEXT NOT NULL, `recipientId` TEXT NOT NULL, `state` TEXT NOT NULL, `updatedAtMs` INTEGER NOT NULL, `notes` TEXT, PRIMARY KEY(`messageId`, `recipientId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipient_statuses_messageId` ON `recipient_statuses` (`messageId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipient_statuses_recipientId` ON `recipient_statuses` (`recipientId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `contacts` (`deviceId` TEXT NOT NULL, `displayName` TEXT NOT NULL, `publicKeyHex` TEXT NOT NULL, `trustLevel` TEXT NOT NULL, `role` TEXT NOT NULL, `lastSeenMs` INTEGER NOT NULL, `isBlocked` INTEGER NOT NULL, PRIMARY KEY(`deviceId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `missions` (`missionId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `coordinatorId` TEXT NOT NULL, `coordinatorPublicKeyHex` TEXT NOT NULL, `createdAtMs` INTEGER NOT NULL, `expiresAtMs` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`missionId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `incidents` (`incidentId` TEXT NOT NULL, `originDeviceId` TEXT NOT NULL, `category` TEXT NOT NULL, `severity` TEXT NOT NULL, `description` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `locationDescription` TEXT NOT NULL, `status` TEXT NOT NULL, `assignedResponderId` TEXT, `createdAtMs` INTEGER NOT NULL, `updatedAtMs` INTEGER NOT NULL, PRIMARY KEY(`incidentId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incidents_status` ON `incidents` (`status`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_incidents_severity` ON `incidents` (`severity`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '301ab6608aa9f1b3765b94b21ef1bda1')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `messages`");
        db.execSQL("DROP TABLE IF EXISTS `recipient_statuses`");
        db.execSQL("DROP TABLE IF EXISTS `contacts`");
        db.execSQL("DROP TABLE IF EXISTS `missions`");
        db.execSQL("DROP TABLE IF EXISTS `incidents`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMessages = new HashMap<String, TableInfo.Column>(12);
        _columnsMessages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("conversationId", new TableInfo.Column("conversationId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("senderId", new TableInfo.Column("senderId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("recipientId", new TableInfo.Column("recipientId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("priority", new TableInfo.Column("priority", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("timestampMs", new TableInfo.Column("timestampMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("expiresAtMs", new TableInfo.Column("expiresAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("replyToId", new TableInfo.Column("replyToId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("isBroadcast", new TableInfo.Column("isBroadcast", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("missionId", new TableInfo.Column("missionId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessages = new HashSet<TableInfo.Index>(4);
        _indicesMessages.add(new TableInfo.Index("index_messages_conversationId", false, Arrays.asList("conversationId"), Arrays.asList("ASC")));
        _indicesMessages.add(new TableInfo.Index("index_messages_state", false, Arrays.asList("state"), Arrays.asList("ASC")));
        _indicesMessages.add(new TableInfo.Index("index_messages_timestampMs", false, Arrays.asList("timestampMs"), Arrays.asList("ASC")));
        _indicesMessages.add(new TableInfo.Index("index_messages_expiresAtMs", false, Arrays.asList("expiresAtMs"), Arrays.asList("ASC")));
        final TableInfo _infoMessages = new TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages);
        final TableInfo _existingMessages = TableInfo.read(db, "messages");
        if (!_infoMessages.equals(_existingMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "messages(org.sahayak.mesh.data.database.entities.MessageEntity).\n"
                  + " Expected:\n" + _infoMessages + "\n"
                  + " Found:\n" + _existingMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsRecipientStatuses = new HashMap<String, TableInfo.Column>(5);
        _columnsRecipientStatuses.put("messageId", new TableInfo.Column("messageId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipientStatuses.put("recipientId", new TableInfo.Column("recipientId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipientStatuses.put("state", new TableInfo.Column("state", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipientStatuses.put("updatedAtMs", new TableInfo.Column("updatedAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipientStatuses.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecipientStatuses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecipientStatuses = new HashSet<TableInfo.Index>(2);
        _indicesRecipientStatuses.add(new TableInfo.Index("index_recipient_statuses_messageId", false, Arrays.asList("messageId"), Arrays.asList("ASC")));
        _indicesRecipientStatuses.add(new TableInfo.Index("index_recipient_statuses_recipientId", false, Arrays.asList("recipientId"), Arrays.asList("ASC")));
        final TableInfo _infoRecipientStatuses = new TableInfo("recipient_statuses", _columnsRecipientStatuses, _foreignKeysRecipientStatuses, _indicesRecipientStatuses);
        final TableInfo _existingRecipientStatuses = TableInfo.read(db, "recipient_statuses");
        if (!_infoRecipientStatuses.equals(_existingRecipientStatuses)) {
          return new RoomOpenHelper.ValidationResult(false, "recipient_statuses(org.sahayak.mesh.data.database.entities.RecipientStatusEntity).\n"
                  + " Expected:\n" + _infoRecipientStatuses + "\n"
                  + " Found:\n" + _existingRecipientStatuses);
        }
        final HashMap<String, TableInfo.Column> _columnsContacts = new HashMap<String, TableInfo.Column>(7);
        _columnsContacts.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("publicKeyHex", new TableInfo.Column("publicKeyHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("trustLevel", new TableInfo.Column("trustLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("lastSeenMs", new TableInfo.Column("lastSeenMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("isBlocked", new TableInfo.Column("isBlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContacts = new TableInfo("contacts", _columnsContacts, _foreignKeysContacts, _indicesContacts);
        final TableInfo _existingContacts = TableInfo.read(db, "contacts");
        if (!_infoContacts.equals(_existingContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "contacts(org.sahayak.mesh.data.database.entities.ContactEntity).\n"
                  + " Expected:\n" + _infoContacts + "\n"
                  + " Found:\n" + _existingContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsMissions = new HashMap<String, TableInfo.Column>(8);
        _columnsMissions.put("missionId", new TableInfo.Column("missionId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("coordinatorId", new TableInfo.Column("coordinatorId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("coordinatorPublicKeyHex", new TableInfo.Column("coordinatorPublicKeyHex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("expiresAtMs", new TableInfo.Column("expiresAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMissions.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMissions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMissions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMissions = new TableInfo("missions", _columnsMissions, _foreignKeysMissions, _indicesMissions);
        final TableInfo _existingMissions = TableInfo.read(db, "missions");
        if (!_infoMissions.equals(_existingMissions)) {
          return new RoomOpenHelper.ValidationResult(false, "missions(org.sahayak.mesh.data.database.entities.MissionEntity).\n"
                  + " Expected:\n" + _infoMissions + "\n"
                  + " Found:\n" + _existingMissions);
        }
        final HashMap<String, TableInfo.Column> _columnsIncidents = new HashMap<String, TableInfo.Column>(12);
        _columnsIncidents.put("incidentId", new TableInfo.Column("incidentId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("originDeviceId", new TableInfo.Column("originDeviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("locationDescription", new TableInfo.Column("locationDescription", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("assignedResponderId", new TableInfo.Column("assignedResponderId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncidents.put("updatedAtMs", new TableInfo.Column("updatedAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncidents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncidents = new HashSet<TableInfo.Index>(2);
        _indicesIncidents.add(new TableInfo.Index("index_incidents_status", false, Arrays.asList("status"), Arrays.asList("ASC")));
        _indicesIncidents.add(new TableInfo.Index("index_incidents_severity", false, Arrays.asList("severity"), Arrays.asList("ASC")));
        final TableInfo _infoIncidents = new TableInfo("incidents", _columnsIncidents, _foreignKeysIncidents, _indicesIncidents);
        final TableInfo _existingIncidents = TableInfo.read(db, "incidents");
        if (!_infoIncidents.equals(_existingIncidents)) {
          return new RoomOpenHelper.ValidationResult(false, "incidents(org.sahayak.mesh.data.database.entities.IncidentEntity).\n"
                  + " Expected:\n" + _infoIncidents + "\n"
                  + " Found:\n" + _existingIncidents);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "301ab6608aa9f1b3765b94b21ef1bda1", "bf6db683ac8ff7ff09259c6ab2c46608");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "messages","recipient_statuses","contacts","missions","incidents");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `messages`");
      _db.execSQL("DELETE FROM `recipient_statuses`");
      _db.execSQL("DELETE FROM `contacts`");
      _db.execSQL("DELETE FROM `missions`");
      _db.execSQL("DELETE FROM `incidents`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MessageDao.class, MessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ContactDao.class, ContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MissionDao.class, MissionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncidentDao.class, IncidentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MessageDao messageDao() {
    if (_messageDao != null) {
      return _messageDao;
    } else {
      synchronized(this) {
        if(_messageDao == null) {
          _messageDao = new MessageDao_Impl(this);
        }
        return _messageDao;
      }
    }
  }

  @Override
  public ContactDao contactDao() {
    if (_contactDao != null) {
      return _contactDao;
    } else {
      synchronized(this) {
        if(_contactDao == null) {
          _contactDao = new ContactDao_Impl(this);
        }
        return _contactDao;
      }
    }
  }

  @Override
  public MissionDao missionDao() {
    if (_missionDao != null) {
      return _missionDao;
    } else {
      synchronized(this) {
        if(_missionDao == null) {
          _missionDao = new MissionDao_Impl(this);
        }
        return _missionDao;
      }
    }
  }

  @Override
  public IncidentDao incidentDao() {
    if (_incidentDao != null) {
      return _incidentDao;
    } else {
      synchronized(this) {
        if(_incidentDao == null) {
          _incidentDao = new IncidentDao_Impl(this);
        }
        return _incidentDao;
      }
    }
  }
}
