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
import org.sahayak.mesh.core.model.Role;
import org.sahayak.mesh.core.model.TrustLevel;
import org.sahayak.mesh.data.database.Converters;
import org.sahayak.mesh.data.database.entities.ContactEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ContactDao_Impl implements ContactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ContactEntity> __insertionAdapterOfContactEntity;

  private final Converters __converters = new Converters();

  public ContactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfContactEntity = new EntityInsertionAdapter<ContactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `contacts` (`deviceId`,`displayName`,`publicKeyHex`,`trustLevel`,`role`,`lastSeenMs`,`isBlocked`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ContactEntity entity) {
        statement.bindString(1, entity.getDeviceId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getPublicKeyHex());
        final String _tmp = __converters.fromTrustLevel(entity.getTrustLevel());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromRole(entity.getRole());
        statement.bindString(5, _tmp_1);
        statement.bindLong(6, entity.getLastSeenMs());
        final int _tmp_2 = entity.isBlocked() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
      }
    };
  }

  @Override
  public Object insertContact(final ContactEntity contact,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfContactEntity.insert(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ContactEntity>> getAllContacts() {
    final String _sql = "SELECT * FROM contacts ORDER BY displayName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"contacts"}, new Callable<List<ContactEntity>>() {
      @Override
      @NonNull
      public List<ContactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfPublicKeyHex = CursorUtil.getColumnIndexOrThrow(_cursor, "publicKeyHex");
          final int _cursorIndexOfTrustLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "trustLevel");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfLastSeenMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenMs");
          final int _cursorIndexOfIsBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlocked");
          final List<ContactEntity> _result = new ArrayList<ContactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ContactEntity _item;
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPublicKeyHex;
            _tmpPublicKeyHex = _cursor.getString(_cursorIndexOfPublicKeyHex);
            final TrustLevel _tmpTrustLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfTrustLevel);
            _tmpTrustLevel = __converters.toTrustLevel(_tmp);
            final Role _tmpRole;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfRole);
            _tmpRole = __converters.toRole(_tmp_1);
            final long _tmpLastSeenMs;
            _tmpLastSeenMs = _cursor.getLong(_cursorIndexOfLastSeenMs);
            final boolean _tmpIsBlocked;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBlocked);
            _tmpIsBlocked = _tmp_2 != 0;
            _item = new ContactEntity(_tmpDeviceId,_tmpDisplayName,_tmpPublicKeyHex,_tmpTrustLevel,_tmpRole,_tmpLastSeenMs,_tmpIsBlocked);
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
  public Object getContact(final String deviceId,
      final Continuation<? super ContactEntity> $completion) {
    final String _sql = "SELECT * FROM contacts WHERE deviceId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, deviceId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ContactEntity>() {
      @Override
      @Nullable
      public ContactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfPublicKeyHex = CursorUtil.getColumnIndexOrThrow(_cursor, "publicKeyHex");
          final int _cursorIndexOfTrustLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "trustLevel");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfLastSeenMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeenMs");
          final int _cursorIndexOfIsBlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBlocked");
          final ContactEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpPublicKeyHex;
            _tmpPublicKeyHex = _cursor.getString(_cursorIndexOfPublicKeyHex);
            final TrustLevel _tmpTrustLevel;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfTrustLevel);
            _tmpTrustLevel = __converters.toTrustLevel(_tmp);
            final Role _tmpRole;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfRole);
            _tmpRole = __converters.toRole(_tmp_1);
            final long _tmpLastSeenMs;
            _tmpLastSeenMs = _cursor.getLong(_cursorIndexOfLastSeenMs);
            final boolean _tmpIsBlocked;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBlocked);
            _tmpIsBlocked = _tmp_2 != 0;
            _result = new ContactEntity(_tmpDeviceId,_tmpDisplayName,_tmpPublicKeyHex,_tmpTrustLevel,_tmpRole,_tmpLastSeenMs,_tmpIsBlocked);
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
