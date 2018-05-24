package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.ChatDialogueAdapter;
import spr018.tcss450.clientapplication.utility.ListenManager;
import spr018.tcss450.clientapplication.utility.Pages;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

public class ChatActivity extends AppCompatActivity
    implements ChatFragment.OnFragmentInteractionListener {
    public static final String CONNECTION_USERNAME = "username";
    public static final String CHAT_ID = "chatID";
    public static final String CHAT_NAME ="chatName";
    //private String mTheirUsername;
    private String mUsername;
    private String mChatName;
    private SharedPreferences.Editor editor;
    /*Remembers if user chooses to stay logged in*/
    private SharedPreferences mPrefs;
    private int mChatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        // make sure to set the app theme before setting the view
        //Log.d("MAIN",mCurrentLocation.getLatitude()+"");
        setTheme(mPrefs.getInt(
                getString(R.string.keys_prefs_app_theme), R.style.AppTheme));

        setContentView(R.layout.activity_chat);


        Bundle bundle = getIntent().getExtras();
        mUsername = bundle.getString(CONNECTION_USERNAME);
        mChatID = bundle.getInt(CHAT_ID);
        mChatName = bundle.getString(CHAT_NAME);

        mPrefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        editor = mPrefs.edit();

        if (findViewById(R.id.chatActivity) != null) {
            setTitle("\"" + mChatName + "\"" + " - " + mUsername);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chatActivity,
                            ChatFragment.newInstance(mUsername, mChatID, mChatName),
                            Pages.CHAT.toString())
                    .commit();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        NotificationIntentService.stopServiceAlarm(this);
        editor.putBoolean(getString(R.string.keys_is_foreground), true);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationIntentService.startServiceAlarm(this, false, mUsername);
        editor.putBoolean(getString(R.string.keys_is_foreground), false);
        editor.apply();
    }

    @Override
    public void goBackToMainActivity(String chatname) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("GoToChatList", chatname);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
