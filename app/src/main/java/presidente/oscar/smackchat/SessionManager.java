package presidente.oscar.smackchat;

import android.content.Context;

/**
 * Created by oscarr on 8/17/16.
 */
public class SessionManager {

    private static SessionManager sInstance;
    private Context mContext;
    private CharSequence mXmppUsername;
    private String mXmppPassword;
    private String mXmppJid;

    public static SessionManager getInstance(Context context) {
        if (sInstance == null)
            sInstance = new SessionManager(context);

        return sInstance;
    }

    private SessionManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public CharSequence getXmppUsername() {
        return mXmppUsername;
    }

    public String getXmppPassword() {
        return mXmppPassword;
    }

    public void setXmppUsername(CharSequence xmppUsername) {
        mXmppUsername = xmppUsername;
    }

    public void setXmppPassword(String xmppPassword) {
        mXmppPassword = xmppPassword;
    }

    public String getXmppJid() {
        return mXmppJid;
    }

    public void setXmppJid(String xmppJid) {
        mXmppJid = xmppJid;
    }
}
