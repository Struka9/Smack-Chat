package presidente.oscar.smackchat.smack;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

import presidente.oscar.smackchat.SessionManager;
import presidente.oscar.smackchat.Util;
import presidente.oscar.smackchat.events.LoginAttempt;

/**
 * Created by oscarr on 7/19/16.
 */
public class SmackService extends Service {
    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;

    private SmackConnection mConnection;

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        start();
        return Service.START_STICKY;
    }

    public SmackConnection getConnection() {
        return mConnection;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stop();
    }

    public void start() {
        if (!mActive) {
            mActive = true;

            // Create ConnectionThread Loop
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        Looper.loop();
                    }

                });
                mThread.start();
            }

        }
    }

    public void stop() {
        mActive = false;
        mTHandler.post(new Runnable() {

            @Override
            public void run() {
                if(mConnection != null){
                    mConnection.disconnect();
                }
            }
        });

    }

    private void initConnection() {
        if(mConnection == null){
            mConnection = SmackConnection.getInstance(this);
        }

        try {
            mConnection.connect();
        } catch (IOException | SmackException | XMPPException e) {
            Util.LogStackTrace(e);
        }
    }

    @Subscribe
    public void onAttemptLogin(LoginAttempt loginAttempt) {
        SessionManager sessionManager = SessionManager.getInstance(this);
        sessionManager.setXmppUsername(loginAttempt.username);
        sessionManager.setXmppPassword(loginAttempt.password);
        start();
    }
}
