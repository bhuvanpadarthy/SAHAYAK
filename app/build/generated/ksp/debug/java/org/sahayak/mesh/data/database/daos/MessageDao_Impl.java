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
import androidx.room.SharedSQLiteStatement;
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
import org.sahayak.mesh.core.model.MessageState;
import org.sahayak.mesh.core.model.Priority;
import org.sahayak.mesh.data.database.Converters;
import org.sahayak.mesh.data.database.entities.MessageEntity;
import org.sahayak.mesh.data.database.entities.RecipientStatusEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageEntity> __insertionAdapterOfMessageEntity;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter<RecipientStatusEntity> __insertionAdapterOfRecipientStatusEntity;

  private final EntityDeletionOrUpdateAdapter<MessageEntity> __updateAdapterOfMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateState;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageEntity = new EntityInsertionAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `messages` (`id`,`conversationId`,`senderId`,`recipientId`,`content`,`state`,`priority`,`timestampMs`,`expiresAtMs`,`replyToId`,`isBroadcast`,`missionId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getConversationId());
        statement.bindString(3, entity.getSenderId());
        statement.bindString(4, entity.getRecipientId());
        statement.bindString(5, entity.getContent());
        final String _tmp = __converters.fromMessageState(entity.getState());
        statement.bindString(6, _tmp);
        final String _tmp_1 = __converters.fromPriority(entity.getPriority());
        statement.bindString(7, _tmp_1);
        statement.bindLong(8, entity.getTimestampMs());
        statement.bindLong(9, entity.getExpiresAtMs());
        if (entity.getReplyToId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getReplyToId());
        }
        final int _tmp_2 = entity.isBroadcast() ? 1 : 0;
        statement.bindLong(11, _tmp_2);
        if (entity.getMissionId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getMissionId());
        }
      }
    };
    this.__insertionAdapterOfRecipientStatusEntity = new EntityInsertionAdapter<RecipientStatusEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `recipient_statuses` (`messageId`,`recipientId`,`state`,`updatedAtMs`,`notes`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecipientStatusEntity entity) {
        statement.bindString(1, entity.getMessageId());
        statement.bindString(2, entity.getRecipientId());
        final String _tmp = __converters.fromMessageState(entity.getState());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getUpdatedAtMs());
        if (entity.getNotes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNotes());
        }
      }
    };
    this.__updateAdapterOfMessageEntity = new EntityDeletionOrUpdateAdapter<MessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `messages` SET `id` = ?,`conversationId` = ?,`senderId` = ?,`recipientId` = ?,`content` = ?,`state` = ?,`priority` = ?,`timestampMs` = ?,`expiresAtMs` = ?,`replyToId` = ?,`isBroadcast` = ?,`missionId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MessageEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getConversationId());
        statement.bindString(3, entity.getSenderId());
        statement.bindString(4, entity.getRecipientId());
        statement.bindString(5, entity.getContent());
        final String _tmp = __converters.fromMessageState(entity.getState());
        statement.bindString(6, _tmp);
        final String _tmp_1 = __converters.fromPriority(entity.getPriority());
        statement.bindString(7, _tmp_1);
        statement.bindLong(8, entity.getTimestampMs());
        statement.bindLong(9, entity.getExpiresAtMs());
        if (entity.getReplyToId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getReplyToId());
        }
        final int _tmp_2 = entity.isBroadcast() ? 1 : 0;
        statement.bindLong(11, _tmp_2);
        if (entity.getMissionId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getMissionId());
        }
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateState = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE messages SET state = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMessage(final MessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMessageEntity.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertRecipientStatus(final RecipientStatusEntity status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecipientStatusEntity.insert(status);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMessage(final MessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMessageEntity.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateState(final String id, final MessageState newState,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateState.acquire();
        int _argIndex = 1;
        final String _tmp = __converters.fromMessageState(newState);
        _stmt.bindString(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateState.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getMessageById(final String id,
      final Continuation<? super MessageEntity> $completion) {
    final String _sql = "SELECT * FROM messages WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MessageEntity>() {
      @Override
      @Nullable
      public MessageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfExpiresAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAtMs");
          final int _cursorIndexOfReplyToId = CursorUtil.getColumnIndexOrThrow(_cursor, "replyToId");
          final int _cursorIndexOfIsBroadcast = CursorUtil.getColumnIndexOrThrow(_cursor, "isBroadcast");
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final MessageEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpSenderId;
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final MessageState _tmpState;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfState);
            _tmpState = __converters.toMessageState(_tmp);
            final Priority _tmpPriority;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toPriority(_tmp_1);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final long _tmpExpiresAtMs;
            _tmpExpiresAtMs = _cursor.getLong(_cursorIndexOfExpiresAtMs);
            final String _tmpReplyToId;
            if (_cursor.isNull(_cursorIndexOfReplyToId)) {
              _tmpReplyToId = null;
            } else {
              _tmpReplyToId = _cursor.getString(_cursorIndexOfReplyToId);
            }
            final boolean _tmpIsBroadcast;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBroadcast);
            _tmpIsBroadcast = _tmp_2 != 0;
            final String _tmpMissionId;
            if (_cursor.isNull(_cursorIndexOfMissionId)) {
              _tmpMissionId = null;
            } else {
              _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            }
            _result = new MessageEntity(_tmpId,_tmpConversationId,_tmpSenderId,_tmpRecipientId,_tmpContent,_tmpState,_tmpPriority,_tmpTimestampMs,_tmpExpiresAtMs,_tmpReplyToId,_tmpIsBroadcast,_tmpMissionId);
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

  @Override
  public Flow<List<MessageEntity>> getMessagesForConversation(final String conversationId) {
    final String _sql = "SELECT * FROM messages WHERE conversationId = ? ORDER BY timestampMs ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, conversationId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"messages"}, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfExpiresAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAtMs");
          final int _cursorIndexOfReplyToId = CursorUtil.getColumnIndexOrThrow(_cursor, "replyToId");
          final int _cursorIndexOfIsBroadcast = CursorUtil.getColumnIndexOrThrow(_cursor, "isBroadcast");
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpSenderId;
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final MessageState _tmpState;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfState);
            _tmpState = __converters.toMessageState(_tmp);
            final Priority _tmpPriority;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toPriority(_tmp_1);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final long _tmpExpiresAtMs;
            _tmpExpiresAtMs = _cursor.getLong(_cursorIndexOfExpiresAtMs);
            final String _tmpReplyToId;
            if (_cursor.isNull(_cursorIndexOfReplyToId)) {
              _tmpReplyToId = null;
            } else {
              _tmpReplyToId = _cursor.getString(_cursorIndexOfReplyToId);
            }
            final boolean _tmpIsBroadcast;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsBroadcast);
            _tmpIsBroadcast = _tmp_2 != 0;
            final String _tmpMissionId;
            if (_cursor.isNull(_cursorIndexOfMissionId)) {
              _tmpMissionId = null;
            } else {
              _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationId,_tmpSenderId,_tmpRecipientId,_tmpContent,_tmpState,_tmpPriority,_tmpTimestampMs,_tmpExpiresAtMs,_tmpReplyToId,_tmpIsBroadcast,_tmpMissionId);
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
  public Object getMessagesByState(final MessageState state,
      final Continuation<? super List<MessageEntity>> $completion) {
    final String _sql = "SELECT * FROM messages WHERE state = ? ORDER BY timestampMs ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromMessageState(state);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MessageEntity>>() {
      @Override
      @NonNull
      public List<MessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfConversationId = CursorUtil.getColumnIndexOrThrow(_cursor, "conversationId");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfTimestampMs = CursorUtil.getColumnIndexOrThrow(_cursor, "timestampMs");
          final int _cursorIndexOfExpiresAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "expiresAtMs");
          final int _cursorIndexOfReplyToId = CursorUtil.getColumnIndexOrThrow(_cursor, "replyToId");
          final int _cursorIndexOfIsBroadcast = CursorUtil.getColumnIndexOrThrow(_cursor, "isBroadcast");
          final int _cursorIndexOfMissionId = CursorUtil.getColumnIndexOrThrow(_cursor, "missionId");
          final List<MessageEntity> _result = new ArrayList<MessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MessageEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpConversationId;
            _tmpConversationId = _cursor.getString(_cursorIndexOfConversationId);
            final String _tmpSenderId;
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final MessageState _tmpState;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfState);
            _tmpState = __converters.toMessageState(_tmp_1);
            final Priority _tmpPriority;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toPriority(_tmp_2);
            final long _tmpTimestampMs;
            _tmpTimestampMs = _cursor.getLong(_cursorIndexOfTimestampMs);
            final long _tmpExpiresAtMs;
            _tmpExpiresAtMs = _cursor.getLong(_cursorIndexOfExpiresAtMs);
            final String _tmpReplyToId;
            if (_cursor.isNull(_cursorIndexOfReplyToId)) {
              _tmpReplyToId = null;
            } else {
              _tmpReplyToId = _cursor.getString(_cursorIndexOfReplyToId);
            }
            final boolean _tmpIsBroadcast;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsBroadcast);
            _tmpIsBroadcast = _tmp_3 != 0;
            final String _tmpMissionId;
            if (_cursor.isNull(_cursorIndexOfMissionId)) {
              _tmpMissionId = null;
            } else {
              _tmpMissionId = _cursor.getString(_cursorIndexOfMissionId);
            }
            _item = new MessageEntity(_tmpId,_tmpConversationId,_tmpSenderId,_tmpRecipientId,_tmpContent,_tmpState,_tmpPriority,_tmpTimestampMs,_tmpExpiresAtMs,_tmpReplyToId,_tmpIsBroadcast,_tmpMissionId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRecipientStatuses(final String messageId,
      final Continuation<? super List<RecipientStatusEntity>> $completion) {
    final String _sql = "SELECT * FROM recipient_statuses WHERE messageId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, messageId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<RecipientStatusEntity>>() {
      @Override
      @NonNull
      public List<RecipientStatusEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMessageId = CursorUtil.getColumnIndexOrThrow(_cursor, "messageId");
          final int _cursorIndexOfRecipientId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipientId");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<RecipientStatusEntity> _result = new ArrayList<RecipientStatusEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecipientStatusEntity _item;
            final String _tmpMessageId;
            _tmpMessageId = _cursor.getString(_cursorIndexOfMessageId);
            final String _tmpRecipientId;
            _tmpRecipientId = _cursor.getString(_cursorIndexOfRecipientId);
            final MessageState _tmpState;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfState);
            _tmpState = __converters.toMessageState(_tmp);
            final long _tmpUpdatedAtMs;
            _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new RecipientStatusEntity(_tmpMessageId,_tmpRecipientId,_tmpState,_tmpUpdatedAtMs,_tmpNotes);
            _result.add(_item);
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
