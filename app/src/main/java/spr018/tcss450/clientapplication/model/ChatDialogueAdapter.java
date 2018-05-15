package spr018.tcss450.clientapplication.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import spr018.tcss450.clientapplication.R;

public class ChatDialogueAdapter extends RecyclerView.Adapter<ChatDialogueAdapter.ViewHolder> {

    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_RIGHT = 1;

    private List<ChatHolder> mChats;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mUsernameTextView;
        private TextView mMessageTextView;

        ViewHolder(View v) {
            super(v);
            mUsernameTextView = v.findViewById(R.id.chatUsername);
            mMessageTextView = v.findViewById(R.id.chatHolderMessage);
        }

        private void setAlignment(int alignment) {
            if (alignment == DISPLAY_RIGHT) {
                mUsernameTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                mMessageTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            }
        }
    }

    public static class ChatHolder {
        private String mUsername;
        private String mMessage;
        private int mAlignment;
        public ChatHolder(String username, String message, int alignment) {
            mUsername = username;
            mMessage = message;
            mAlignment = alignment;
        }
    }

    public ChatDialogueAdapter(List<ChatHolder> chats) {
        mChats = chats;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View connectionView = inflater.inflate(R.layout.fragment_chat_holder, parent, false);
        return new ViewHolder(connectionView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatHolder chat = mChats.get(position);
        holder.mUsernameTextView.setText(chat.mUsername);
        holder.mMessageTextView.setText(chat.mMessage);
        holder.setAlignment(chat.mAlignment);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChats.size();
    }
}

