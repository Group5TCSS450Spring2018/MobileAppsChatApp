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
/**
 * Adapter for chat preview page. Displays all of the current chats that you are a part of.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class ChatPreviewAdapter extends RecyclerView.Adapter<ChatPreviewAdapter.ViewHolder> {
    private List<Chat> mChats;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Chat chat);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mChatRecentNameTextView;
        private TextView mRecentMessageTextView;
        private View mView;

        ViewHolder(View v) {
            super(v);
            mView = v;
            mChatRecentNameTextView = v.findViewById(R.id.chatRecentName);
            mRecentMessageTextView = v.findViewById(R.id.chatRecentMessage);
        }

        void bind(Chat chat, OnItemClickListener listener) {
            if (chat == null) {
                mChatRecentNameTextView.setText("No Chats Open");
                mChatRecentNameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mChatRecentNameTextView.setTypeface(null, Typeface.NORMAL);
                mChatRecentNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                mRecentMessageTextView.setVisibility(View.GONE);
            } else {
                mView.setOnClickListener(view -> listener.onItemClick(chat));
            }
        }
    }

    // Parameter could be any type of collection. I'm using list for now. - Tuan
    public ChatPreviewAdapter(List<Chat> chats) {
        mChats = chats;
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
        Chat chat = mChats.get(position);
        if (chat != null) {
            holder.mChatRecentNameTextView.setText("\"" + chat.getName() + "\"");
            if (chat.getRecentMessage() != null) {
                holder.mRecentMessageTextView.setText(chat.getUsername() + " : "+ chat.getRecentMessage());
            } else {
                String members = chat.getMembers().toString();
                holder.mRecentMessageTextView.setText("Members: " + members.substring(1, members.length()-1));
            }
        }
        holder.bind(mChats.get(position), mListener);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}

