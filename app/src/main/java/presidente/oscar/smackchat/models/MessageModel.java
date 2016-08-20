package presidente.oscar.smackchat.models;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import presidente.oscar.smackchat.data.SmackChatContract;

/**
 * Created by oscarr on 8/18/16.
 */
public class MessageModel {
    private long mId;
    private String mBody;
    private boolean mIsMine;
    private boolean mSent;
    private String mOtherUser;
    private Date mDate;


    public MessageModel(String body, String otherUser, boolean isMine, Date date) {
        this(body, otherUser, isMine, date, false);
    }

    public MessageModel(String body, String otherUser, boolean isMine, Date date, boolean sent) {
        this(-1, body, otherUser, isMine, date, sent);
    }

    public MessageModel(long localId, String body, String otherUser, boolean isMine, Date date, boolean sent) {
        setId(localId);
        setBody(body);
        setMine(isMine);
        setOtherUser(otherUser);
        setDate(date);
        setSent(sent);
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public boolean isMine() {
        return mIsMine;
    }

    public void setMine(boolean mine) {
        mIsMine = mine;
    }

    public String getOtherUser() {
        return mOtherUser;
    }

    public void setOtherUser(String otherUser) {
        mOtherUser = otherUser;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSent() {
        return mSent;
    }

    public void setSent(boolean sent) {
        mSent = sent;
    }

    public static List<MessageModel> listFromCursor(Cursor cursor) {
        List<MessageModel> messageModels = new ArrayList<>();

        if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        int idIndex = cursor.getColumnIndex(SmackChatContract.MessageContract._ID);
                        int bodyIndex = cursor.getColumnIndex(SmackChatContract.MessageContract.COLUMN_BODY);
                        int dateIndex = cursor.getColumnIndex(SmackChatContract.MessageContract.COLUMN_DATE);
                        int isMineIndex = cursor.getColumnIndex(SmackChatContract.MessageContract.COLUMN_ISMINE);
                        int otherUserIndex = cursor.getColumnIndex(SmackChatContract.MessageContract.COLUMN_OTHERUSER);
                        int sentIndex = cursor.getColumnIndex(SmackChatContract.MessageContract.COLUMN_SENT);

                        String body = cursor.getString(bodyIndex);
                        Date date = new Date(cursor.getLong(dateIndex));
                        boolean isMine = cursor.getInt(isMineIndex) != 0;
                        String otherUser = cursor.getString(otherUserIndex);
                        boolean sent = cursor.getInt(sentIndex) != 0;
                        long id = cursor.getLong(idIndex);

                        messageModels.add(new MessageModel(id, body, otherUser, isMine, date, sent));
                    } while (cursor.moveToNext());
                }
        }

        return messageModels;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
