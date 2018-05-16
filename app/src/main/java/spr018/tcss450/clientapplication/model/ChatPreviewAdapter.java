package spr018.tcss450.clientapplication.model;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import spr018.tcss450.clientapplication.R;

public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {
    private List<Connection> mConnections;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Connection connection);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mUsernameTextView;
        private TextView mRecentMessageTextView;
        private View mView;

        ViewHolder(View v) {
            super(v);
            mView = v;
            mUsernameTextView = v.findViewById(R.id.chatUsername);
            mRecentMessageTextView = v.findViewById(R.id.chatRecentMessage);
        }

        void bind(Connection connection, OnItemClickListener listener) {
            if (connection == null) {
                mUsernameTextView.setText("No Chats Open");
                mUsernameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mUsernameTextView.setTypeface(null, Typeface.NORMAL);
                mUsernameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mRecentMessageTextView.setVisibility(View.GONE);
            } else {
                mView.setOnClickListener(view -> listener.onItemClick(connection));
            }
        }
    }

    // Parameter could be any type of collection. I'm using list for now. - Tuan
    public ChatPreviewAdapter(List<Connection> connections) {
        mConnections = connections;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View connectionView = inflater.inflate(R.layout.fragment_home_chat_list_item, parent, false);
        return new ViewHolder(connectionView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Connection connection = mConnections.get(position);
        if (connection != null) {
            holder.mUsernameTextView.setText(connection.getUsername());
            holder.mRecentMessageTextView.setText(connection.getRecentMessage());
        }
        holder.bind(mConnections.get(position), mListener);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mConnections.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

