package presidente.oscar.smackchat.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import presidente.oscar.smackchat.R;
import presidente.oscar.smackchat.Util;
import presidente.oscar.smackchat.adapters.MessagesCursorAdapter;
import presidente.oscar.smackchat.data.SmackChatContract;
import presidente.oscar.smackchat.events.MessageReceivedEvent;
import presidente.oscar.smackchat.events.SendMessageEvent;
import presidente.oscar.smackchat.events.SendMessageResultEvent;
import presidente.oscar.smackchat.models.MessageModel;

/**
 * Created by oscarr on 8/17/16.
 */
public class ConversationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_TO = "extra-to";
    private static final String TAG = ConversationActivity.class.getSimpleName();

    private static final int LOADER_URL = 0;

    private EventBus mEventBus;

    private EditText mTextToSend;
    private String mTo;

    private RecyclerView mMessagesRv;
    private LinearLayoutManager mLinearLayoutManager;
    private MessagesCursorAdapter mMessagesCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mMessagesRv = (RecyclerView)findViewById(R.id.rv_conversation);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMessagesCursorAdapter = new MessagesCursorAdapter(this, null);

        mMessagesRv.setAdapter(mMessagesCursorAdapter);
        mMessagesRv.setLayoutManager(mLinearLayoutManager);

        mTo = getIntent().getStringExtra(EXTRA_TO);

        if (mTo == null || mTo.length() == 0) {
            Util.LogError(TAG,
                    ConversationActivity.class.getSimpleName() + " needs another participant");
            finish();
            return;
        }

        mTextToSend = (EditText)findViewById(R.id.et_message_to_send);
        mEventBus = EventBus.getDefault();

        getSupportLoaderManager().initLoader(LOADER_URL, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_send_message:

                String textToSend = mTextToSend.getText().toString().trim();

                if (textToSend.length() == 0)return;

                mTextToSend.setText("");

                SendMessageEvent event = new SendMessageEvent();
                MessageModel message = new MessageModel(textToSend, mTo, true, new Date());
                event.message = message;

                Uri uri = getContentResolver().insert(SmackChatContract.MessageContract.CONTENT_URI, Util.messageModelToContentValues(message));
                long id = SmackChatContract.MessageContract.getIdFromUri(uri);
                message.setId(id);

                mEventBus.post(event);


                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportLoaderManager().restartLoader(LOADER_URL, null, ConversationActivity.this);
            }
        });
    }

    @Subscribe
    public void onSendMessageResultEvent(final SendMessageResultEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event.success)
                    getSupportLoaderManager().restartLoader(LOADER_URL, null, ConversationActivity.this);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                SmackChatContract.MessageContract.buildMessageUriForUser(mTo),
                null, null, null, SmackChatContract.MessageContract.COLUMN_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMessagesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMessagesCursorAdapter.swapCursor(null);
    }
}
