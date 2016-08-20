package presidente.oscar.smackchat;

import android.content.ContentValues;
import android.util.Log;

import org.jivesoftware.smack.packet.Message;
import org.jxmpp.util.XmppStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import presidente.oscar.smackchat.data.SmackChatContract;
import presidente.oscar.smackchat.models.MessageModel;

/**
 * Created by oscarr on 8/17/16.
 */
public class Util {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-yyyy-MMMM @hh:mm a");

    private static final boolean DEBUG = true;
    private static final String TAG = Util.class.getSimpleName();

    public static void Log(String tag, String message) {
        if (DEBUG)
            Log.d(tag, message);
    }

    public static void LogError(String tag, String e) {
        if (DEBUG)
            Log.e(tag, e);
    }

    public static void LogStackTrace(Exception e) {
        if (DEBUG)
            e.printStackTrace();
    }

    public static ContentValues messageToContentValues(Message message, boolean isMine) {
        ContentValues values = new ContentValues();

        String body = message.getBody();
        if (body == null) return null;

        String otherUserBareJid = XmppStringUtils.parseBareJid(isMine ? message.getTo() : message.getFrom());
        values.put(SmackChatContract.MessageContract.COLUMN_BODY, body);
        values.put(SmackChatContract.MessageContract.COLUMN_ISMINE, isMine);
        values.put(SmackChatContract.MessageContract.COLUMN_OTHERUSER, otherUserBareJid);
        values.put(SmackChatContract.MessageContract.COLUMN_DATE, new Date().getTime());
        values.put(SmackChatContract.MessageContract.COLUMN_SENT, false);

        return values;
    }

    public static ContentValues messageModelToContentValues(MessageModel messageModel) {
        ContentValues values = new ContentValues();

        values.put(SmackChatContract.MessageContract.COLUMN_BODY, messageModel.getBody());
        values.put(SmackChatContract.MessageContract.COLUMN_DATE, messageModel.getDate().getTime());
        values.put(SmackChatContract.MessageContract.COLUMN_ISMINE, messageModel.isMine());
        values.put(SmackChatContract.MessageContract.COLUMN_SENT, messageModel.isSent());
        values.put(SmackChatContract.MessageContract.COLUMN_OTHERUSER, messageModel.getOtherUser());

        return values;
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
}
