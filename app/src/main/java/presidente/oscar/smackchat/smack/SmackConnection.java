package presidente.oscar.smackchat.smack;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import presidente.oscar.smackchat.SessionManager;
import presidente.oscar.smackchat.Util;
import presidente.oscar.smackchat.data.SmackChatContract;
import presidente.oscar.smackchat.events.ChatCreatedEvent;
import presidente.oscar.smackchat.events.ConnectionStateChanged;
import presidente.oscar.smackchat.events.CreateChatEvent;
import presidente.oscar.smackchat.events.MessageReceivedEvent;
import presidente.oscar.smackchat.events.SendMessageEvent;
import presidente.oscar.smackchat.events.RosterEntriesUpdatedEvent;
import presidente.oscar.smackchat.events.SendMessageResultEvent;
import presidente.oscar.smackchat.models.MessageModel;

/**
 * Created by oscarr on 7/20/16.
 */
public class SmackConnection implements ConnectionListener, ChatManagerListener, RosterListener, ChatMessageListener, PingFailedListener,
        ChatStateListener, InvitationListener, InvitationRejectionListener, MessageListener {

    public static final String TAG = SmackConnection.class.getSimpleName();

    public static final String ACTION_JOIN_CONFERENCE = "com.presidente.oscar.ACTION_JOIN_CONFERENCE";
    public static final String ACTION_CREATE_CONFERENCE = "com.presidente.oscar.ACTION_CREATE_CONFERENCE";
    public static final String ACTION_CREATE_CHAT = "com.presidente.oscar.ACTION_CREATE_CHAT";


    private ConnectionState mConnectionState;

    private static SmackConnection sInstance;

    public enum ConnectionState {
        Connected, Connecting, Disconnected, Reconnecting;

    }
    public static SmackConnection getInstance(Context context) {
        if (sInstance == null) {
            try {
                sInstance = new SmackConnection(context);
            } catch (IOException | XMPPException | SmackException e) {
                Util.LogStackTrace(e);
                return null;
            }
        }

        return sInstance;
    }

    public static final String HOST = "blah.im";

    public static final int SERVER_PORT = 5222;
    private BroadcastReceiver mSendMessageReceiver;

    private Set<RosterEntry> mContacts;
    private Map<String, Chat> mUserChatMap = new HashMap<>(); //Maps a user to a given chat

    private BroadcastReceiver mChatEventReceiver;
    private Context mApplicationContext;
    private XMPPTCPConnectionConfiguration mConnectionConfiguration;

    private SessionManager mSessionManager;
    private XMPPTCPConnection mXMPPConnection;

    private ChatManager mChatManager;
    private MultiUserChatManager mMultiUserChatManager;

    private EventBus mEventBus;

    private SmackConnection(Context context) throws IOException, XMPPException, SmackException {
        mApplicationContext = context.getApplicationContext();
        mSessionManager = SessionManager.getInstance(mApplicationContext);

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);
    }

    public void connect() throws IOException, XMPPException, SmackException {

        mConnectionConfiguration = XMPPTCPConnectionConfiguration
                .builder()
                .setResource("Android")
                .setPort(SERVER_PORT)
                .setServiceName(HOST)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .setUsernameAndPassword(mSessionManager.getXmppUsername(), mSessionManager.getXmppPassword())
                .setKeystoreType("BKS")
                .build();

        mXMPPConnection = new XMPPTCPConnection(mConnectionConfiguration);

        Roster.getInstanceFor(mXMPPConnection).setRosterLoadedAtLogin(true);

        mXMPPConnection.addConnectionListener(this);


        mXMPPConnection.connect().login();

        PingManager.setDefaultPingInterval(600);
        PingManager pingManager = PingManager.getInstanceFor(mXMPPConnection);
        pingManager.registerPingFailedListener(this);

        setupChatEventReceiver();

        mMultiUserChatManager = MultiUserChatManager.getInstanceFor(mXMPPConnection);
        mChatManager = ChatManager.getInstanceFor(mXMPPConnection);
        mChatManager.setMatchMode(ChatManager.MatchMode.BARE_JID);

        mChatManager.addChatListener(this);

        Roster.getInstanceFor(mXMPPConnection).addRosterListener(this);
        Roster.getInstanceFor(mXMPPConnection).setSubscriptionMode(Roster.SubscriptionMode.accept_all);
    }

    public void disconnect() {
        if (mXMPPConnection != null)
            mXMPPConnection.disconnect();
        mXMPPConnection = null;

        if (mChatEventReceiver != null)
            mApplicationContext.unregisterReceiver(mChatEventReceiver);
        mChatEventReceiver = null;

        if (mSendMessageReceiver != null)
            mApplicationContext.unregisterReceiver(mSendMessageReceiver);
        mSendMessageReceiver = null;
    }

    private void setupChatEventReceiver() {
        mChatEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_CREATE_CHAT:
                        break;

                    case ACTION_CREATE_CONFERENCE:
                        break;

                    case ACTION_JOIN_CONFERENCE:
                        break;

                    default:
                        Util.LogError(TAG, "Unsupported action for the ChatEventReceiver");
                        break;
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CREATE_CHAT);
        intentFilter.addAction(ACTION_JOIN_CONFERENCE);
        intentFilter.addAction(ACTION_CREATE_CONFERENCE);

        mApplicationContext.registerReceiver(mChatEventReceiver, intentFilter);
    }

    private void sendMessage(MessageModel message, boolean isGroup) {
        if (!isGroup) {
            SendMessageResultEvent event = new SendMessageResultEvent();
            try {
                Chat chat = mUserChatMap.get(XmppStringUtils.parseBareJid(message.getOtherUser()));

                if (chat == null) {
                    chat = mChatManager.createChat(XmppStringUtils.parseBareJid(message.getOtherUser()));
                }

                chat.sendMessage(new Message(message.getOtherUser(), message.getBody()));

                //TODO: We would need to abstract this kind of logic outside this class
                message.setSent(true);
                mApplicationContext.getContentResolver().update(
                        SmackChatContract.MessageContract.CONTENT_URI,
                        Util.messageModelToContentValues(message),
                        SmackChatContract.MessageContract._ID + " = ?",
                        new String[]{String.valueOf(message.getId())});

                event.success = true;

            } catch (SmackException.NotConnectedException e) {
                Util.LogStackTrace(e);
                event.success = false;
            }

            event.message = message;
            mEventBus.post(event);
        } else {
            sendGroupMessage(message.getBody(), message.getOtherUser());
        }
    }

    private void sendGroupMessage(String body, String to) {
        MultiUserChat multiUserChat = mMultiUserChatManager.getMultiUserChat(to);

        try {
            multiUserChat.sendMessage(new Message(to, body));
        } catch (SmackException e) {
            Util.LogStackTrace(e);
        }
    }

    public void rebuildRoster() {
        Roster roster = Roster.getInstanceFor(mXMPPConnection);

        mContacts = roster.getEntries();

        RosterEntriesUpdatedEvent event = new RosterEntriesUpdatedEvent();
        event.mContactList = mContacts;
        mEventBus.post(event);
    }


    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener(this);
        mUserChatMap.put(XmppStringUtils.parseBareJid(chat.getParticipant()), chat);

        ChatCreatedEvent event = new ChatCreatedEvent();
        event.chat = chat;
        mEventBus.post(event);
    }

    @Override
    public void connected(XMPPConnection connection) {
        mConnectionState = ConnectionState.Connected;
        notifyConnectionState();
    }

    private void notifyConnectionState() {
        ConnectionStateChanged event = new ConnectionStateChanged();
        event.connectionState = mConnectionState;
        mEventBus.post(event);
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        mConnectionState = ConnectionState.Connected;
        notifyConnectionState();
        if (mXMPPConnection.isAuthenticated()) {
            Roster roster = Roster.getInstanceFor(mXMPPConnection);
            try {
                roster.reloadAndWait();
                rebuildRoster();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connectionClosed() {
        mConnectionState = ConnectionState.Disconnected;
        notifyConnectionState();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        mConnectionState = ConnectionState.Disconnected;
        notifyConnectionState();
    }

    @Override
    public void reconnectionSuccessful() {
        mConnectionState = ConnectionState.Connected;
        notifyConnectionState();
    }

    @Override
    public void reconnectingIn(int seconds) {
        mConnectionState = ConnectionState.Reconnecting;
        notifyConnectionState();
    }

    @Override
    public void reconnectionFailed(Exception e) {
        mConnectionState = ConnectionState.Disconnected;
        notifyConnectionState();
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {
        rebuildRoster();
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
        rebuildRoster();
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        rebuildRoster();
    }

    @Override
    public void presenceChanged(Presence presence) {
        rebuildRoster();
    }

    @Override
    public void stateChanged(Chat chat, ChatState state) {
        Util.Log(TAG, "Chat state for " + chat.getParticipant() + " has changed state to " + state.name());
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        ContentValues values = Util.messageToContentValues(message, false);
        Uri uri = mApplicationContext.getContentResolver().insert(SmackChatContract.MessageContract.CONTENT_URI, values);
        MessageReceivedEvent event = new MessageReceivedEvent();
        event.message = message;
        mEventBus.post(event);
    }

    @Override
    public void processMessage(Message message) {
        Log.d(TAG, message.getBody());
    }

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
        try {
            room.join(mSessionManager.getXmppJid());
        } catch (SmackException.NoResponseException |
                SmackException.NotConnectedException |
                XMPPException.XMPPErrorException e) {
            Util.LogStackTrace(e);
        }
    }

    @Override
    public void pingFailed() {
        Util.Log(TAG, "ping to the server failed");
    }

    @Override
    public void invitationDeclined(String invitee, String reason) {
        Util.Log(TAG, "rejected ... " + invitee + ":" + reason);
    }

    @Subscribe
    public void onSendMessageEvent(SendMessageEvent sendMessageEvent) {
        MessageModel message = sendMessageEvent.message;
        sendMessage(message, false);
    }

    @Subscribe
    public void onCreateChatEvent(CreateChatEvent event) {
        mChatManager.createChat(event.to);
    }
}
