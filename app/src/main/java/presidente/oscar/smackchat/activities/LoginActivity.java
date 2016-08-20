package presidente.oscar.smackchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import presidente.oscar.smackchat.R;
import presidente.oscar.smackchat.SessionManager;
import presidente.oscar.smackchat.smack.SmackConnection;
import presidente.oscar.smackchat.smack.SmackService;
import presidente.oscar.smackchat.events.LoginAttempt;
import presidente.oscar.smackchat.events.ConnectionStateChanged;

/**
 * Created by oscarr on 8/17/16.
 */
public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private Button mLoginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startService(new Intent(this, SmackService.class));

        mUsernameEt = (EditText)findViewById(R.id.et_username);
        mPasswordEt = (EditText)findViewById(R.id.et_password);

        mLoginButton = (Button)findViewById(R.id.bt_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = mUsernameEt.getText().toString();
                String password = mPasswordEt.getText().toString();

                if (username.length() == 0 || password.length() == 0) {
                    Toast.makeText(LoginActivity.this, R.string.empty_credentials_error, Toast.LENGTH_LONG).show();
                    return;
                }

                SessionManager sessionManager = SessionManager.getInstance(LoginActivity.this);
                sessionManager.setXmppUsername(username);
                sessionManager.setXmppPassword(password);

                LoginAttempt newLoginAttempt = new LoginAttempt();
                newLoginAttempt.host = "blah.im";
                newLoginAttempt.username = username;
                newLoginAttempt.password = password;

                EventBus.getDefault().post(newLoginAttempt);
            }
        });
    }

    public void setUiEnabled(boolean enabled) {
        if (enabled) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onConnectionStateChange(ConnectionStateChanged connectionStateChanged) {
        if (connectionStateChanged.connectionState == SmackConnection.ConnectionState.Connected) {
            //It was a successful connection, move to the next activity
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }
}
