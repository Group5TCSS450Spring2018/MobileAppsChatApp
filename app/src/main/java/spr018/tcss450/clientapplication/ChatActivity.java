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
import spr018.tcss450.clientapplication.utility.Pages;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

public class ChatActivity extends AppCompatActivity {
    public static final String CONNECTION_USERNAME = "username";
    public static final String CHAT_ID = "chatID";
    private String mTheirUsername;
    private String mUsername;
    private int mChatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle = getIntent().getExtras();
        mTheirUsername = bundle.getString(CONNECTION_USERNAME);
        mChatID = bundle.getInt(CHAT_ID);
        if (findViewById(R.id.chatActivity) != null) {
            setTitle(mTheirUsername);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chatActivity,
                            ChatFragment.newInstance(mTheirUsername, mChatID),
                            Pages.CHAT.toString())
                    .commit();
        }
    }

}
