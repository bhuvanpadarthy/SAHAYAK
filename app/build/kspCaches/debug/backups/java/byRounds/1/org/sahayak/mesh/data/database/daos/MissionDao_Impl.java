package org.sahayak.mesh.data.database.daos;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
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
import org.sahayak.mesh.data.database.entities.MissionEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MissionDao_Impl implements MissionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MissionEntity> __insertionAdapterOfMissionEntity;

  public MissionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMissionEntity = new EntityInsertionAdapter<MissionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `missions` (`missionId`,`title`,`description`,`coordinatorId`,`coordinatorPublicKeyHex`,`createdAtMs`,`expiresAtMs`,`isActive`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MissionEntity entity) {
        statement.bindString(1, entity.getMissionId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getCoordinatorId());
        statement.bindString(5, entity.getCoordinatorPublicKeyHex());
        statement.bindLong(6, entity.getCreatedAtMs());
        statement.bindLong(7, entity.getExpiresAtMs());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
  }

  @Override
  public Object insertMission(final MissionEntity mission,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMissionEntity.insert(mission);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MissionEntity>> getActiveMissions() {
    final String _sql = "SELECT * FROM missions WHERE isActive = 1 ORDER BY createdAtMs DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"missions"}, new Callable<List<MissionEntity>>() {
      @Override
      @NonNull
      public List<MissionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoordinatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "coordinatorId");
          final int _cursorIndexOfCoordinatorPublicKeyHex = CursorUtil.getColumnIndexOrThrow(_cursor, "coordinatorPublicKeyHex");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfExpiresAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAtMs");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<MissionEntity> _result = new ArrayList<MissionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MissionEntity _item;
            final String _tmpMissionId;
            _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCoordinatorId;
            _tmpCoordinatorId = _cursor.getString(_cursorIndexOfCoordinatorId);
            final String _tmpCoordinatorPublicKeyHex;
            _tmpCoordinatorPublicKeyHex = _cursor.getString(_cursorIndexOfCoordinatorPublicKeyHex);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final long _tmpExpiresAtMs;
            _tmpExpiresAtMs = _cursor.getLong(_cursorIndexOfExpiresAtMs);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _item = new MissionEntity(_tmpMissionId,_tmpTitle,_tmpDescription,_tmpCoordinatorId,_tmpCoordinatorPublicKeyHex,_tmpCreatedAtMs,_tmpExpiresAtMs,_tmpIsActive);
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
  public Object getMission(final String missionId,
      final Continuation<? super MissionEntity> $completion) {
    final String _sql = "SELECT * FROM missions WHERE missionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, missionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MissionEntity>() {
      @Override
      @Nullable
      public MissionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoordinatorId = CursorUtil.getColumnIndexOrThrow(_cursor, "coordinatorId");
          final int _cursorIndexOfCoordinatorPublicKeyHex = CursorUtil.getColumnIndexOrThrow(_cursor, "coordinatorPublicKeyHex");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfExpiresAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAtMs");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final MissionEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpMissionId;
            _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpCoordinatorId;
            _tmpCoordinatorId = _cursor.getString(_cursorIndexOfCoordinatorId);
            final String _tmpCoordinatorPublicKeyHex;
            _tmpCoordinatorPublicKeyHex = _cursor.getString(_cursorIndexOfCoordinatorPublicKeyHex);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final long _tmpExpiresAtMs;
            _tmpExpiresAtMs = _cursor.getLong(_cursorIndexOfExpiresAtMs);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            _result = new MissionEntity(_tmpMissionId,_tmpTitle,_tmpDescription,_tmpCoordinatorId,_tmpCoordinatorPublicKeyHex,_tmpCreatedAtMs,_tmpExpiresAtMs,_tmpIsActive);
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
