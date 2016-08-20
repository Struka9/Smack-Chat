package presidente.oscar.smackchat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.List;

import presidente.oscar.smackchat.R;

/**
 * Created by oscarr on 8/17/16.
 */
public class RosterEntryAdapter extends RecyclerView.Adapter<RosterEntryAdapter.ViewHolder> {

    private List<RosterEntry> mRosterEntries;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private OnRosterEntryClickListener mOnRosterEntryClickListener;

    public RosterEntryAdapter(@NonNull Context context, @Nullable List<RosterEntry> contacts) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);

        mRosterEntries = contacts != null ? contacts : new ArrayList<RosterEntry>();
    }

    public void setRosterEntries(List<RosterEntry> contacts) {
        if (contacts == null)return;

        mRosterEntries = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RosterEntry item = mRosterEntries.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mRosterEntries.size();
    }

    public void setOnRosterEntryClickListener(OnRosterEntryClickListener onRosterEntryClickListener) {
        mOnRosterEntryClickListener = onRosterEntryClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView displayNameTv;

        public ViewHolder(View itemView) {
            super(itemView);

            displayNameTv = (TextView)itemView.findViewById(R.id.tv_display_name);
        }

        public void setData(final RosterEntry contact) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRosterEntryClickListener != null) {
                        mOnRosterEntryClickListener.onRosterEntryClick(contact);
                    }
                }
            });

            displayNameTv.setText(contact.getName() != null ?
                    contact.getName() : contact.getUser());
        }
    }

    public interface OnRosterEntryClickListener {
        public void onRosterEntryClick(RosterEntry rosterEntry);
    }
}
