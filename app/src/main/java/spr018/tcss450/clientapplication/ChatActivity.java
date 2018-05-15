package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class ChatActivity extends AppCompatActivity {
    public static final String CONNECTION_USERNAME = "username";
    public static final String CHAT_ID = "chatID";
    private String mTheirUsername;
    private String mUsername;
    private int mChatID;
    private String mSendUrl;
    private ListenManager mListenManager;
    private ChatDialogueAdapter mAdapter;
    private List<ChatDialogueAdapter.ChatHolder> mChatDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        assert b != null;
        mTheirUsername = b.getString(CONNECTION_USERNAME);
        mChatID = b.getInt(CHAT_ID);
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mUsername = prefs.getString(getString(R.string.keys_prefs_user_name), "");
        mChatDialogue = new ArrayList<>();
        mAdapter = new ChatDialogueAdapter(mChatDialogue);
        RecyclerView recyclerView = findViewById(R.id.chatRecylerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle(mTheirUsername);
        ImageView sendButton = findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(this::sendMessage);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getSharedPreferences(
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
                .appendQueryParameter("chatId", Integer.toString(mChatID))
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
        String msg = ((EditText) findViewById(R.id.chatSendText))
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

    private void publishProgress(JSONObject resultJSON) {
        final String[] messages;
        if (resultJSON.has("messages")) {
            try {
                JSONArray jMessages = resultJSON.getJSONArray("messages");
                messages = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {
                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    messages[i] = username + ":" + userMessage;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            runOnUiThread(() -> {
                for (int i = 0; i < messages.length; i++) {
                    ChatDialogueAdapter.ChatHolder chat = new ChatDialogueAdapter.ChatHolder("Unknown", messages[i], ChatDialogueAdapter.DISPLAY_LEFT);
                    mChatDialogue.add(chat);
                    mAdapter.notifyDataSetChanged();
                }

            });
        }
    }

    private void handleError(final Exception e) {
        Log.e("GET", e.getMessage());
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);
            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {
                ((EditText) findViewById(R.id.chatSendText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }

}
