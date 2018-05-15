package spr018.tcss450.clientapplication;

import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

public class ChatActivity extends AppCompatActivity {
    public static final String CONNECTION_KEY = "connection";

    private String mUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        mUsername = b.getString(CONNECTION_KEY);
        ImageView sendButton = findViewById(R.id.chatSendButton);
        sendButton.setOnClickListener(this::sendMessage);
    }
    private void sendMessage(final View theButton) {
        Log.d("send message", "button clicked");
        String mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_sendMessage))
                .build().toString();
        JSONObject messageJson = new JSONObject();
        //UNCOMMENT THIS WHEN YOU HAVE A CHAT TEXT BOX.
//        String msg = ((EditText) getView().findViewById(R.id.chatInputEditText))
//                .getText().toString();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), "-----");
            messageJson.put(getString(R.string.keys_json_chat_id), 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleError(String s) {
        Log.e("LISTEN ERROR!!!", s);
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get("success").toString()
                    .equals(true)) {
                //display the messages on the screen.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


}
