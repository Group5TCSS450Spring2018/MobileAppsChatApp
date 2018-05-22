package spr018.tcss450.clientapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.ChatDialogueAdapter;
import spr018.tcss450.clientapplication.utility.ListenManager;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

import static spr018.tcss450.clientapplication.ChatActivity.CHAT_ID;
import static spr018.tcss450.clientapplication.ChatActivity.CONNECTION_USERNAME;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private String mTheirUsername;
    private String mUsername;
    private int mChatID;
    private String mSendUrl;
    private ListenManager mListenManager;
    private ChatDialogueAdapter mAdapter;
    private List<ChatDialogueAdapter.ChatHolder> mChatDialogue;
    private RecyclerView mRecyclerView;


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String username, int chatID) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(CONNECTION_USERNAME, username);
        args.putInt(CHAT_ID, chatID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mTheirUsername = getArguments().getString(CONNECTION_USERNAME);
            mChatID = getArguments().getInt(CHAT_ID);
            SharedPreferences prefs =
                    Objects.requireNonNull(getActivity()).getSharedPreferences(
                            getString(R.string.keys_shared_prefs) + mChatID,
                            Context.MODE_PRIVATE);
            mUsername = prefs.getString(getString(R.string.keys_prefs_user_name), "");
            mChatDialogue = new ArrayList<>();
            mAdapter = new ChatDialogueAdapter(mChatDialogue);

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        mRecyclerView = v.findViewById(R.id.chatRecylerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
        mRecyclerView.setAdapter(mAdapter);
        ImageView sendButton = v.findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(this::sendMessage);
        return v;
    }

    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_user_name))) {
            throw new IllegalStateException("No username in prefs!");
        }

        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_sendMessage))
                .build()
                .toString();
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getMessages))
                .appendQueryParameter("chatid", Integer.toString(mChatID))
                .build();
        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        }
    }

    private void sendMessage(final View theButton) {
        JSONObject messageJSON = new JSONObject();
        String msg = ((EditText) Objects.requireNonNull(getView()).findViewById(R.id.chatSendText))
                .getText().toString();

        try {
            messageJSON.put(getString(R.string.keys_json_username), mUsername);
            messageJSON.put(getString(R.string.keys_json_message), msg);
            messageJSON.put(getString(R.string.keys_json_chat_id), mChatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!msg.isEmpty()) {
            new SendPostAsyncTask.Builder(mSendUrl, messageJSON)
                    .onPostExecute(this::endOfSendMsgTask)
                    .onCancelled(this::handleError)
                    .build().execute();
        }
    }

    private void handleError(String s) {
        Log.e("SEND", s);
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ((EditText) Objects.requireNonNull(getView()).findViewById(R.id.chatSendText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void publishProgress(JSONObject resultJSON) {
        final String[] messages;
        final String[] usernames;
        if (resultJSON.has("messages")) {
            try {
                JSONArray jMessages = resultJSON.getJSONArray("messages");
                messages = new String[jMessages.length()];
                usernames = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    usernames[i] = username;
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    messages[i] = userMessage;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                if (messages.length > mChatDialogue.size()) {
                    for (int i = mChatDialogue.size(); i < messages.length; i++) {
                        ChatDialogueAdapter.ChatHolder chat;
                        if (usernames[i].equals(mUsername)) {
                            chat = new ChatDialogueAdapter.ChatHolder("You", messages[i], ChatDialogueAdapter.DISPLAY_RIGHT);
                        } else {
                            chat = new ChatDialogueAdapter.ChatHolder(usernames[i], messages[i], ChatDialogueAdapter.DISPLAY_LEFT);
                        }
                        mChatDialogue.add(chat);
                    }
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mChatDialogue.size() - 1);
                }
            });

        }
    }

    private void handleError(final Exception e) {
        Log.e("GET", e.getMessage());
    }


    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = mListenManager.stopListening();
        SharedPreferences prefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp) + mChatID,
                latestMessage)
                .apply();
    }
}
