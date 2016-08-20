package presidente.oscar.smackchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import presidente.oscar.smackchat.R;
import presidente.oscar.smackchat.activities.ConversationActivity;
import presidente.oscar.smackchat.adapters.RosterEntryAdapter;
import presidente.oscar.smackchat.events.RosterEntriesUpdatedEvent;

/**
 * Created by oscarr on 8/17/16.
 */
public class ContactsFragment extends Fragment implements RosterEntryAdapter.OnRosterEntryClickListener {
    public static ContactsFragment newInstance() {
        ContactsFragment f = new ContactsFragment();

        return f;
    }

    private RecyclerView mContactsRv;
    private LinearLayoutManager mLinearLayoutManager;
    private RosterEntryAdapter mContactsAdapter;

    private EventBus mEventBus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_contacts, container, false);

        mContactsAdapter = new RosterEntryAdapter(getContext(), null);
        mContactsAdapter.setOnRosterEntryClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mContactsRv = (RecyclerView)view.findViewById(R.id.rv_contacts);

        mContactsRv.setAdapter(mContactsAdapter);
        mContactsRv.setLayoutManager(mLinearLayoutManager);

        mEventBus = EventBus.getDefault();
        mEventBus.register(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEventBus.unregister(this);
    }

    @Subscribe
    public void onRosterEntriesUpdated(RosterEntriesUpdatedEvent event) {
        Iterator<RosterEntry> iterator = event.mContactList.iterator();

        final List<RosterEntry> rosterEntryList = new ArrayList<>();

        while (iterator.hasNext()) {
            rosterEntryList.add(iterator.next());
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContactsAdapter.setRosterEntries(rosterEntryList);
            }
        });

    }

    @Override
    public void onRosterEntryClick(RosterEntry rosterEntry) {
        Intent convoIntent = new Intent(getContext(), ConversationActivity.class);
        convoIntent.putExtra(ConversationActivity.EXTRA_TO, rosterEntry.getUser());
        getContext().startActivity(convoIntent);
    }
}
