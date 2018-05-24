package spr018.tcss450.clientapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import spr018.tcss450.clientapplication.utility.Pages;

public class ChatActivity extends AppCompatActivity
        implements ChatFragment.OnFragmentInteractionListener {
    public static final String CONNECTION_USERNAME = "username";
    public static final String CHAT_ID = "chatID";
    public static final String CHAT_NAME = "chatName";
    private NotificationManager mNotificationManager;
    private String mUsername;
    private String mChatName;
    private SharedPreferences.Editor editor;
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
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
        mNotificationManager.cancel(mChatID);
//        NotificationIntentService.startServiceAlarm(this, true, mUsername);
//        NotificationIntentService.stopServiceAlarm(this);
        editor.putBoolean(getString(R.string.keys_is_foreground), true);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        NotificationIntentServiceice.stopServiceAlarm(this);
//        NotificationIntentServiceicece.startServiceAlarm(this, false, mUsername);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
