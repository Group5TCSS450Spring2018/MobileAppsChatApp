package spr018.tcss450.clientapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.ChatDialogueAdapter;
import spr018.tcss450.clientapplication.utility.ListenManager;
import spr018.tcss450.clientapplication.utility.Pages;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

import static spr018.tcss450.clientapplication.ChatActivity.CHAT_ID;
import static spr018.tcss450.clientapplication.ChatActivity.CHAT_NAME;
import static spr018.tcss450.clientapplication.ChatActivity.CONNECTION_USERNAME;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Fragment that holds the messages that you recieve and send.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class ChatFragment extends Fragment {

    private String mTheirUsername;
    private String mUsername;
    private String mChatName;
    private int mChatID;
    private String mSendUrl;
    private ListenManager mListenManager;
    private ChatDialogueAdapter mAdapter;
    private List<ChatDialogueAdapter.ChatHolder> mChatDialogue;
    private RecyclerView mRecyclerView;
    private OnFragmentInteractionListener mListener;


    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor.
     * @param username; the username of the user you are talking to.
     * @param chatID; chat id of the chat you are viewing.
     * @param chatName; the name of the chat you are viewing.
     * @return the fragment.
     */
    public static ChatFragment newInstance(String username, int chatID, String chatName) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(CONNECTION_USERNAME, username);
        args.putInt(CHAT_ID, chatID);
        args.putString(CHAT_NAME, chatName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mTheirUsername = getArguments().getString(CONNECTION_USERNAME);
            mUsername = getArguments().getString(CONNECTION_USERNAME);
            mChatID = getArguments().getInt(CHAT_ID);
            SharedPreferences prefs =
                    Objects.requireNonNull(getActivity()).getSharedPreferences(
                            getString(R.string.keys_shared_prefs) + mChatID,
                            Context.MODE_PRIVATE);
            //mUsername = prefs.getString(getString(R.string.keys_prefs_user_name), "");
            mChatName = getArguments().getString(CHAT_NAME);
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

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.participants, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_participants) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_getChatMembers))
                    .build();

            JSONObject msg = new JSONObject();
            try{
                msg.put("chatid", mChatID);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::showParticipants)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        } else if (id == R.id.action_leave_chat) {
            //send get connections the username.
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String user = prefs.getString(getString(R.string.keys_prefs_user_name), "");

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_leaveChat))
                    .build();

            JSONObject msg = new JSONObject();
            try{
                msg.put("username", user);
                msg.put("chatname", mChatName);
            } catch(JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::leaveChat)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Can the user leave chat? Success- it can. else print out stack trace.
     * @param results; passed in from Database.
     */
    private void leaveChat(String results) {
        try {
            JSONObject response = new JSONObject(results);
            if (response.has("success")) {
                mListener.goBackToMainActivity(mChatName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the users the participants in the chat. 
     * @param results
     */
    private void showParticipants(String results) {
        try {
            JSONObject resultJSON = new JSONObject(results);
            if (resultJSON.has("recieved_requests")) {
                JSONArray participantsList = resultJSON.getJSONArray("recieved_requests");
                ArrayList<String> displayList = new ArrayList<>();
                for (int i = 0; i < participantsList.length(); i++) {
                    JSONObject obj = participantsList.getJSONObject(i);
                    displayList.add(obj.getString("username"));
                }

                String result = displayList.toString().substring(1, displayList.toString().length() - 1);
                Log.e("LIST CONTENTS", displayList.toString() + "\n" + result);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chat Participants of \"" + mChatName + "\"!")
                        .setMessage(result)
                        .show();
            } else {
                Log.e("ERROR", "No participants?");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
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
        if (prefs.contains(getString(R.string.keys_prefs_chat_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_chat_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(5000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(5000)
                    .build();
        }
    }

    private void sendMessage(final View theButton) {
        Log.e("SEND", "ATTEMPTING TO SEND MESSAGE");
        JSONObject messageJSON = new JSONObject();
        String msg = ((EditText) Objects.requireNonNull(getView()).findViewById(R.id.chatSendText))
                .getText().toString();
        Log.e("SEND", "USING:\nUSERNAME: " + mUsername
                + "\nMESSAGE: " + msg
                + "\nCHATID: " + mChatID);
        try {
            messageJSON.put(getString(R.string.keys_json_username), mUsername);
            messageJSON.put(getString(R.string.keys_json_message), msg);
            messageJSON.put(getString(R.string.keys_json_chat_id), mChatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!msg.isEmpty()) {
            Log.e("SEND", "SENDING MESSAGE IN ASYNC");
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
            Log.e("END SEND MESSAGE", result);
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
                getString(R.string.keys_prefs_chat_time_stamp) + mChatID,
                latestMessage)
                .apply();
    }

    public interface OnFragmentInteractionListener {
        void goBackToMainActivity(String chatname);
    }
}
