package presidente.oscar.smackchat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by oscarr on 8/18/16.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smackchat.db";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_MESSAGE_TABLE = "CREATE TABLE " + SmackChatContract.MessageContract.TABLE_NAME + " ( " +
                SmackChatContract.MessageContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SmackChatContract.MessageContract.COLUMN_OTHERUSER + " TEXT NOT NULL, " +
                SmackChatContract.MessageContract.COLUMN_BODY + " TEXT NOT NULL, " +
                SmackChatContract.MessageContract.COLUMN_DATE + " NUMBER NOT NULL, " +
                SmackChatContract.MessageContract.COLUMN_SENT + " NUMBER NOT NULL, " +
                SmackChatContract.MessageContract.COLUMN_ISMINE + " NUMBER NOT NULL);";

        db.execSQL(CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SmackChatContract.MessageContract.TABLE_NAME);
    }
}
