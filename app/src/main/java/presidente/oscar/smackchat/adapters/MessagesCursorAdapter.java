package presidente.oscar.smackchat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import presidente.oscar.smackchat.R;
import presidente.oscar.smackchat.Util;
import presidente.oscar.smackchat.models.MessageModel;

/**
 * Created by oscarr on 8/18/16.
 */
public class MessagesCursorAdapter extends RecyclerView.Adapter<MessagesCursorAdapter.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MessageModel> mMessageModelList;

    public MessagesCursorAdapter(Context context, Cursor cursor) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mMessageModelList = MessageModel.listFromCursor(cursor);
    }

    public void swapCursor(@Nullable Cursor cursor) {
        mMessageModelList = MessageModel.listFromCursor(cursor);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = mLayoutInflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageModel messageModel = mMessageModelList.get(position);
        holder.setData(messageModel);
    }

    @Override
    public int getItemCount() {
        return mMessageModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBody;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBody = (TextView)itemView.findViewById(R.id.tv_body);
            tvDate = (TextView)itemView.findViewById(R.id.tv_date);
        }

        public void setData(MessageModel message) {
            tvBody.setText(message.getBody());
            tvBody.setGravity(message.isMine() ? Gravity.RIGHT : Gravity.LEFT);

            tvDate.setText(Util.formatDate(message.getDate()));
        }
    }
}
