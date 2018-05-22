package spr018.tcss450.clientapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.Chat;
import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.utility.Pages;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        NewConnectionFragment.OnFragmentInteractionListener,
        ConnectionProfileFragment.OnFragmentInteractionListener,
        ChatListFragment.OnFragmentInteractionListener,
        NewMessageFragment.OnFragmentInteractionListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /*Remembers if user chooses to stay logged in*/
    private SharedPreferences mPrefs;

    /*Floating action button in Main Activity*/
    private FloatingActionButton mFab;
    private static final String TAG = "MyLocationsActivity";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 814;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        // make sure to set the app theme
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Log.d("MAIN",mCurrentLocation.getLatitude()+"");
        setTheme(mPrefs.getInt(
                getString(R.string.keys_prefs_app_theme_no_actionbar), R.style.AppTheme_NoActionBar));

        setContentView(R.layout.activity_main);

        mFab = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // locations-related task you need to do.

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
        onOpenChat(mPrefs.getString(getString(R.string.keys_prefs_user_name), ""), chatid, chatName);
    }

    @Override
    public void onWeatherInteraction(Uri uri) {

    }

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
    public void onOpenChatAttempt(String username, int chatID, String chatname) {
        onOpenChat(username, chatID, chatname);
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
        recreate();

        //Empties back stack because it doesn't work properly after recreate() is called.
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        updateFABandNV(null);
    }


    /* Helpers */
    private void loadFragmentWithBackStack(Fragment fragment, Pages page) {
        if(fragment instanceof HomeFragment) {
            Log.d("MAIN", "HOME2");
            Bundle b = new Bundle();
            String latlng = mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude();
            b.putString(HomeFragment.COORDINATES, latlng);
            fragment.setArguments(b);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment, page.toString())
                .addToBackStack(page.toString())
                .commit();
        updateFABandNV(fragment);
    }

    private void loadFragmentNoBackStack(Fragment fragment) {
        if(fragment instanceof HomeFragment) {
            Log.d("MAIN", "HOME");
            Bundle b = new Bundle();
            Log.d("MAIN",mCurrentLocation.getLatitude()+"");
            String latlng = mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude();
            b.putString(HomeFragment.COORDINATES, latlng);
            fragment.setArguments(b);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .commit();

        updateFABandNV(fragment);
    }

    private void showLoginActivity() {
        // clear stay logged in regardless of whether it is set or not
        mPrefs.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in), false).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    //Sets the Floating Action Button and NavigationView to the correct state.
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
            mFab.hide();
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
            mFab.hide();
            nv.setCheckedItem(R.id.nav_chat_list);
            setTitle(Pages.CHATLIST.toString());
        }
        else {
            Log.wtf("Main Activity", "YOU SHOULD NOT SEE THIS");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
                    Log.d(TAG, "on connected"+mCurrentLocation.toString());
                }

                loadFragmentNoBackStack(new HomeFragment());
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                                    , Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_LOCATIONS);
                }
                startLocationUpdates();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, mCurrentLocation.toString());

    }
    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Log.d(TAG, mCurrentLocation.toString());
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

}
