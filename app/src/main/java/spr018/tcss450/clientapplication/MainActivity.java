package spr018.tcss450.clientapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Objects;

import spr018.tcss450.clientapplication.model.Chat;
import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.utility.Pages;

/**
 *  The starting activity for the entire app.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        NewConnectionFragment.OnFragmentInteractionListener,
        ConnectionProfileFragment.OnFragmentInteractionListener,
        ChatListFragment.OnFragmentInteractionListener,
        NewMessageFragment.OnFragmentInteractionListener {

    public static final String INTENT_EXTRA_NOTIFICATION = "notificationExtra";

    private static final int MY_PERMISSIONS_LOCATIONS = 814;

    private SharedPreferences.Editor editor;
    /*Remembers if user chooses to stay logged in*/
    private SharedPreferences mPrefs;
    private String mUsername;
    /*Floating action button in Main Activity*/
    private FloatingActionButton mFab;

    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        setTheme(mPrefs.getInt(
                getString(R.string.keys_prefs_app_theme_no_actionbar), R.style.AppTheme_NoActionBar));

        setContentView(R.layout.activity_main);

        editor = mPrefs.edit();

        mFab = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUsername = mPrefs.getString(getString(R.string.keys_prefs_user_name), "");
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        editor.putString(getString(R.string.keys_editor_username), mUsername);
        editor.apply();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadFragmentNoBackStack(new HomeFragment());

        if (getIntent().hasExtra("GoToChatList")) {
            Toast.makeText(getApplicationContext(), "Left \""
                    + getIntent().getExtras().getString("GoToChatList")
                    + "\"!", Toast.LENGTH_LONG).show();
            loadFragmentWithBackStack(new ChatListFragment(), Pages.CHATLIST);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationIntentService.startServiceAlarm(this, true, mUsername);
        NotificationIntentService.stopServiceAlarm(this);
        editor.putBoolean(getString(R.string.keys_is_foreground), true);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationIntentService.stopServiceAlarm(this);
        NotificationIntentService.startServiceAlarm(this, false, mUsername);
        editor.putBoolean(getString(R.string.keys_is_foreground), false);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NotificationIntentService.stopServiceAlarm(this);
        NotificationIntentService.startServiceAlarm(this, false, mUsername);
        editor.putBoolean(getString(R.string.keys_is_foreground), false);
        editor.apply();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFragmentNoBackStack(new HomeFragment());
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED.", "");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down…maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.mainFragmentContainer);
            if (fragment != null) {
                updateFABandNV(fragment);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // check from which page ?
            loadFragmentWithBackStack(new SettingsFragment(), Pages.SETTINGS);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragmentWithBackStack(new HomeFragment(), Pages.HOME);
        } else if (id == R.id.nav_chat_list) {
            loadFragmentWithBackStack(new ChatListFragment(), Pages.CHATLIST);
        } else if (id == R.id.nav_connections) {
            loadFragmentWithBackStack(new ConnectionsFragment(), Pages.CONNECTIONS);
        } else if (id == R.id.nav_weather) {
            loadFragmentWithBackStack(new WeatherFragment(), Pages.WEATHER);
        } else if (id == R.id.nav_log_out) {
            NotificationIntentService.stopServiceAlarm(this);
            showLoginActivity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onOpenChat(String username, int chatID, String chatname) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CONNECTION_USERNAME, username);
        intent.putExtra(ChatActivity.CHAT_ID, chatID);
        intent.putExtra(ChatActivity.CHAT_NAME, chatname);
        startActivity(intent);
    }

    @Override
    public void onExpandingRequestAttempt(Connection connection) {
        String name = connection.getName();
        String username = connection.getUsername();
        String email = connection.getEmail();
        loadFragmentWithBackStack(ConnectionProfileFragment.newInstance(name, username, email, ConnectionProfileFragment.PENDING), Pages.PROFILE);
    }

    @Override
    public void onFriendConnectionClicked(Connection connection) {
        String name = connection.getName();
        String username = connection.getUsername();
        String email = connection.getEmail();
        loadFragmentWithBackStack(ConnectionProfileFragment.newInstance(name, username, email, ConnectionProfileFragment.FRIEND), Pages.PROFILE);
    }

    @Override
    public void onChatCreation(int chatid, String chatName) {
        getSupportFragmentManager().popBackStack();
        mFab.show();
        onOpenChat(mPrefs.getString(getString(R.string.keys_prefs_user_name), ""), chatid, chatName);
    }

    /*@Override
    public void onWeatherInteraction(Uri uri) {

    }*/

    @Override
    public void onSearchedConnectionClicked(Connection connection) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        String name = connection.getName();
        String username = connection.getUsername();
        String email = connection.getEmail();
        loadFragmentWithBackStack(ConnectionProfileFragment.newInstance(name, username, email, ConnectionProfileFragment.STRANGER), Pages.PROFILE);
    }

    @Override
    public void onUpdateFragmentAttempt() {
        Fragment frg = getSupportFragmentManager().findFragmentByTag(Pages.PROFILE.toString());
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    @Override
    public void onChatListSelection(Chat chat) {
        onOpenChat(mPrefs.getString(getString(R.string.keys_prefs_user_name), ""), chat.getChatID(), chat.getName());
    }

    @Override
    public void settings_ToggleStayLoggedIn(Switch v) {
        boolean currentState = mPrefs.getBoolean(
                getString(R.string.keys_prefs_stay_logged_in), false);

        mPrefs.edit().putBoolean(
                getString(R.string.keys_prefs_stay_logged_in), !currentState).apply();

        v.setChecked(!currentState);
    }

    @Override
    public void settings_ChangeTheme(String styleID) {
        int themeID = R.style.AppTheme; // default
        int themeID_no_actionbar = R.style.AppTheme_NoActionBar;

        if (styleID.equals(getString(R.string.washedOutThemeName))) {
            themeID = R.style.AppTheme_WashedOut;
            themeID_no_actionbar = R.style.AppTheme_WashedOut_NoActionBar;
        } else if (styleID.equals(getString(R.string.coralThemeName))) {
            themeID = R.style.AppTheme_Coral;
            themeID_no_actionbar = R.style.AppTheme_Coral_NoActionBar;
        }

        mPrefs.edit().putInt(
                getString(R.string.keys_prefs_app_theme), themeID).apply();
        mPrefs.edit().putInt(
                getString(R.string.keys_prefs_app_theme_no_actionbar), themeID_no_actionbar).apply();

        setTheme(themeID_no_actionbar);


        //Empties back stack because it doesn't work properly after recreate() is called.
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        recreate();
    }

    /* Helpers */

    /**
     *  Loads a fragment from previous  use
     * @param fragment - current fragment
     * @param page - current page
     */
    private void loadFragmentWithBackStack(Fragment fragment, Pages page) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment, page.toString())
                .addToBackStack(page.toString())
                .commit();
        updateFABandNV(fragment);
    }

    /**
     * Load back to home if no fragment is on back stack.
     * @param fragment - current fragment.
     */
    private void loadFragmentNoBackStack(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .commit();

        updateFABandNV(fragment);
    }

    /**
     *  Shows whether user is chose to stay logged in or not.
     */
    private void showLoginActivity() {
        // clear stay logged in regardless of whether it is set or not
        mPrefs.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in), false).apply();
        mNotificationManager.cancelAll();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Loads the map for weather.
     */
    private void loadMap() {
        Intent i = new Intent(this, MapActivity.class);
        Double latitude = Double.parseDouble(mPrefs.getString(getString(R.string.keys_prefs_latitude),""));
        Double longitude = Double.parseDouble(mPrefs.getString(getString(R.string.keys_prefs_longitude),""));
        String.format("%.1f", latitude);
        String.format("%.1f", longitude);
        i.putExtra(MapActivity.LATITUDE, latitude);
        i.putExtra(MapActivity.LONGITUDE, longitude);
        startActivity(i);
    }

    /**
     * Sets the floating action button and navigation view to the correct state.
     * @param fragment - current fragment
     */
    private void updateFABandNV(@Nullable Fragment fragment) {
        NavigationView nv = findViewById(R.id.nav_view);
        if (fragment instanceof HomeFragment) {
            mFab.show();
            mFab.setOnClickListener(view -> loadFragmentWithBackStack(new NewMessageFragment(), Pages.NEWMESSAGE));
            mFab.setImageResource(R.drawable.ic_fab_send);
            nv.setCheckedItem(R.id.nav_home);
            setTitle(Pages.HOME.toString());
        } else if (fragment instanceof ConnectionsFragment) {
            mFab.show();
            mFab.setOnClickListener(view -> loadFragmentWithBackStack(new NewConnectionFragment(), Pages.NEWCONNECTION));
            mFab.setImageResource(R.drawable.ic_fab_add);
            nv.setCheckedItem(R.id.nav_connections);
            setTitle(Pages.CONNECTIONS.toString());
        } else if (fragment instanceof WeatherFragment) {
            mFab.show();
            mFab.setImageResource(R.drawable.ic_fab_map);
            mFab.setOnClickListener(view -> loadMap());
            nv.setCheckedItem(R.id.nav_weather);
            setTitle(Pages.WEATHER.toString());
        } else if (fragment instanceof SettingsFragment) {
            mFab.hide();
            setTitle(Pages.SETTINGS.toString());
        } else if (fragment instanceof NewMessageFragment) {
            mFab.hide();
            nv.setCheckedItem(R.id.nav_home);
            setTitle(Pages.NEWMESSAGE.toString());
        } else if (fragment instanceof NewConnectionFragment) {
            mFab.hide();
            nv.setCheckedItem(R.id.nav_connections);
            setTitle(Pages.NEWCONNECTION.toString());
        } else if (fragment instanceof ConnectionProfileFragment) {
            mFab.hide();
            setTitle(Pages.PROFILE.toString());
        } else if (fragment instanceof  ChatListFragment) {
            mFab.show();
            mFab.setOnClickListener(view -> loadFragmentWithBackStack(new NewMessageFragment(), Pages.NEWMESSAGE));
            mFab.setImageResource(R.drawable.ic_fab_send);
            nv.setCheckedItem(R.id.nav_chat_list);
            setTitle(Pages.CHATLIST.toString());
        }
        else {
            Log.wtf("Main Activity", "YOU SHOULD NOT SEE THIS");
        }
    }
}
