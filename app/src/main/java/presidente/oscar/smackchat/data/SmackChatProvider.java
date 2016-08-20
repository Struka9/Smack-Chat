package presidente.oscar.smackchat.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by oscarr on 8/18/16.
 */
public class SmackChatProvider extends ContentProvider {

    private static final String TAG = SmackChatProvider.class.getSimpleName();

    private static final int MESSAGE = 200;
    private static final int MESSAGE_WITH_USER = 201;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(SmackChatContract.CONTENT_AUTHORITY, SmackChatContract.PATH_MESSAGE, MESSAGE);
        matcher.addURI(SmackChatContract.CONTENT_AUTHORITY, SmackChatContract.PATH_MESSAGE + "/*", MESSAGE_WITH_USER);

        return matcher;
    }

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor retCursor = null;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (match) {
            case MESSAGE:
            {
                retCursor = db.query(SmackChatContract.MessageContract.TABLE_NAME,
                        projection, null, null, null, null, sortOrder);
                break;
            }

            case MESSAGE_WITH_USER:
            {
                String user = SmackChatContract.MessageContract.getUserFromUri(uri);
                retCursor = db.query(SmackChatContract.MessageContract.TABLE_NAME,
                        projection, SmackChatContract.MessageContract.COLUMN_OTHERUSER + " = ?  ",
                        new String[]{user}, null, null, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }

//        db.close();
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MESSAGE:
            case MESSAGE_WITH_USER:
                return SmackChatContract.MessageContract.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long id;
        Uri returnUri = null;

        switch (match) {
            case MESSAGE:
                id = db.insert(SmackChatContract.MessageContract.TABLE_NAME, null, values);
                if (id < 0) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                } else {
                    returnUri = SmackChatContract.MessageContract.buildMessageUri(id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }

        db.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int affectedRows = 0;

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match) {
            case MESSAGE:
                affectedRows = db.delete(SmackChatContract.MessageContract.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        int affectedRows;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (match) {
            case MESSAGE:
                affectedRows = db.update(SmackChatContract.MessageContract.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri.toString());
        }
        db.close();
        getContext().getContentResolver().notifyChange(uri, null);

        return affectedRows;
    }
}
