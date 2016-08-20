package presidente.oscar.smackchat.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by oscarr on 8/18/16.
 */
public class SmackChatContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "presidente.oscar.smackchat";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.chekku.chekku/customer/ is a valid path for
    // looking at customer data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MESSAGE = "message";

    public static class MessageContract implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGE;

        public static final String COLUMN_OTHERUSER = "otherUser";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ISMINE = "isMine";
        public static final String COLUMN_SENT = "sent";

        public static final String TABLE_NAME = "Message";

        public static Uri buildMessageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMessageUriForUser(String user) {
            return Uri.withAppendedPath(CONTENT_URI, user);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }

        public static String getUserFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }

    }

}
