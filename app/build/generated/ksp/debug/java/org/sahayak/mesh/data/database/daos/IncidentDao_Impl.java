package org.sahayak.mesh.data.database.daos;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import org.sahayak.mesh.core.model.AlertLevel;
import org.sahayak.mesh.core.model.IncidentCategory;
import org.sahayak.mesh.core.model.IncidentStatus;
import org.sahayak.mesh.data.database.Converters;
import org.sahayak.mesh.data.database.entities.IncidentEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IncidentDao_Impl implements IncidentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IncidentEntity> __insertionAdapterOfIncidentEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<IncidentEntity> __updateAdapterOfIncidentEntity;

  public IncidentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIncidentEntity = new EntityInsertionAdapter<IncidentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `incidents` (`incidentId`,`originDeviceId`,`category`,`severity`,`description`,`latitude`,`longitude`,`locationDescription`,`status`,`assignedResponderId`,`createdAtMs`,`updatedAtMs`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncidentEntity entity) {
        statement.bindString(1, entity.getIncidentId());
        statement.bindString(2, entity.getOriginDeviceId());
        final String _tmp = __converters.fromIncidentCategory(entity.getCategory());
        statement.bindString(3, _tmp);
        final String _tmp_1 = __converters.fromAlertLevel(entity.getSeverity());
        statement.bindString(4, _tmp_1);
        statement.bindString(5, entity.getDescription());
        if (entity.getLatitude() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getLongitude());
        }
        statement.bindString(8, entity.getLocationDescription());
        final String _tmp_2 = __converters.fromIncidentStatus(entity.getStatus());
        statement.bindString(9, _tmp_2);
        if (entity.getAssignedResponderId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAssignedResponderId());
        }
        statement.bindLong(11, entity.getCreatedAtMs());
        statement.bindLong(12, entity.getUpdatedAtMs());
      }
    };
    this.__updateAdapterOfIncidentEntity = new EntityDeletionOrUpdateAdapter<IncidentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `incidents` SET `incidentId` = ?,`originDeviceId` = ?,`category` = ?,`severity` = ?,`description` = ?,`latitude` = ?,`longitude` = ?,`locationDescription` = ?,`status` = ?,`assignedResponderId` = ?,`createdAtMs` = ?,`updatedAtMs` = ? WHERE `incidentId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IncidentEntity entity) {
        statement.bindString(1, entity.getIncidentId());
        statement.bindString(2, entity.getOriginDeviceId());
        final String _tmp = __converters.fromIncidentCategory(entity.getCategory());
        statement.bindString(3, _tmp);
        final String _tmp_1 = __converters.fromAlertLevel(entity.getSeverity());
        statement.bindString(4, _tmp_1);
        statement.bindString(5, entity.getDescription());
        if (entity.getLatitude() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getLongitude());
        }
        statement.bindString(8, entity.getLocationDescription());
        final String _tmp_2 = __converters.fromIncidentStatus(entity.getStatus());
        statement.bindString(9, _tmp_2);
        if (entity.getAssignedResponderId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getAssignedResponderId());
        }
        statement.bindLong(11, entity.getCreatedAtMs());
        statement.bindLong(12, entity.getUpdatedAtMs());
        statement.bindString(13, entity.getIncidentId());
      }
    };
  }

  @Override
  public Object insertIncident(final IncidentEntity incident,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIncidentEntity.insert(incident);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateIncident(final IncidentEntity incident,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIncidentEntity.handle(incident);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<IncidentEntity>> getAllIncidents() {
    final String _sql = "SELECT * FROM incidents ORDER BY createdAtMs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"incidents"}, new Callable<List<IncidentEntity>>() {
      @Override
      @NonNull
      public List<IncidentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfOriginDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "originDeviceId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "locationDescription");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
          final List<IncidentEntity> _result = new ArrayList<IncidentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IncidentEntity _item;
            final String _tmpIncidentId;
            _tmpIncidentId = _cursor.getString(_cursorIndexOfIncidentId);
            final String _tmpOriginDeviceId;
            _tmpOriginDeviceId = _cursor.getString(_cursorIndexOfOriginDeviceId);
            final IncidentCategory _tmpCategory;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCategory);
            _tmpCategory = __converters.toIncidentCategory(_tmp);
            final AlertLevel _tmpSeverity;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toAlertLevel(_tmp_1);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationDescription;
            _tmpLocationDescription = _cursor.getString(_cursorIndexOfLocationDescription);
            final IncidentStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toIncidentStatus(_tmp_2);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final long _tmpUpdatedAtMs;
            _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
            _item = new IncidentEntity(_tmpIncidentId,_tmpOriginDeviceId,_tmpCategory,_tmpSeverity,_tmpDescription,_tmpLatitude,_tmpLongitude,_tmpLocationDescription,_tmpStatus,_tmpAssignedResponderId,_tmpCreatedAtMs,_tmpUpdatedAtMs);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getIncident(final String id,
      final Continuation<? super IncidentEntity> $completion) {
    final String _sql = "SELECT * FROM incidents WHERE incidentId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IncidentEntity>() {
      @Override
      @Nullable
      public IncidentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIncidentId = CursorUtil.getColumnIndexOrThrow(_cursor, "incidentId");
          final int _cursorIndexOfOriginDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "originDeviceId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfLocationDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "locationDescription");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAssignedResponderId = CursorUtil.getColumnIndexOrThrow(_cursor, "assignedResponderId");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
          final IncidentEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpIncidentId;
            _tmpIncidentId = _cursor.getString(_cursorIndexOfIncidentId);
            final String _tmpOriginDeviceId;
            _tmpOriginDeviceId = _cursor.getString(_cursorIndexOfOriginDeviceId);
            final IncidentCategory _tmpCategory;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfCategory);
            _tmpCategory = __converters.toIncidentCategory(_tmp);
            final AlertLevel _tmpSeverity;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toAlertLevel(_tmp_1);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final String _tmpLocationDescription;
            _tmpLocationDescription = _cursor.getString(_cursorIndexOfLocationDescription);
            final IncidentStatus _tmpStatus;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toIncidentStatus(_tmp_2);
            final String _tmpAssignedResponderId;
            if (_cursor.isNull(_cursorIndexOfAssignedResponderId)) {
              _tmpAssignedResponderId = null;
            } else {
              _tmpAssignedResponderId = _cursor.getString(_cursorIndexOfAssignedResponderId);
            }
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final long _tmpUpdatedAtMs;
            _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
            _result = new IncidentEntity(_tmpIncidentId,_tmpOriginDeviceId,_tmpCategory,_tmpSeverity,_tmpDescription,_tmpLatitude,_tmpLongitude,_tmpLocationDescription,_tmpStatus,_tmpAssignedResponderId,_tmpCreatedAtMs,_tmpUpdatedAtMs);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
