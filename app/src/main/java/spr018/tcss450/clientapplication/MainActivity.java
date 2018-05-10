package spr018.tcss450.clientapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Switch;

import java.util.Objects;

import spr018.tcss450.clientapplication.utility.Pages;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        NewMessageFragment.OnFragmentInteractionListener,
        NewConnectionFragment.OnFragmentInteractionListener {

    /*Remembers if user chooses to stay logged in*/
    private SharedPreferences mPrefs;

    /*Floating action button in Main Activity*/
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        // make sure to set the app theme
        setTheme(mPrefs.getInt(
                getString(R.string.keys_prefs_app_theme_no_actionbar), R.style.AppTheme_NoActionBar));

        setContentView(R.layout.activity_main);

        mFab = findViewById(R.id.fab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadFragmentNoBackStack(new HomeFragment());
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
                modifyFab(fragment);
            }
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem item = menu.findItem(R.id.search);
//        item.setVisible(false);
//        super.onPrepareOptionsMenu(menu);
//        return true;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.actionBarSearch);
        item.setVisible(false);
        //TODO Implement search
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.actionBarSearch).getActionView();
        searchView.setSearchableInfo(
                Objects.requireNonNull(searchManager).getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
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
    public void onHomeInteraction(Uri uri) {

    }

    @Override
    public void onConnectionsInteraction(Uri uri) {

    }

    @Override
    public void onWeatherInteraction(Uri uri) {

    }

    @Override
    public void onNewChatDetach(Fragment fragment) {

    }

    @Override
    public void onSearchAttempt(String username) {

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
        modifyFab(null);
    }

    /* Helpers */
    private void loadFragmentWithBackStack(Fragment fragment, Pages page) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment, page.toString())
                .addToBackStack(page.toString())
                .commit();
        modifyFab(fragment);
    }

    private void loadFragmentNoBackStack(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .commit();

        modifyFab(fragment);
    }

    private void showLoginActivity() {
        // clear stay logged in regardless of whether it is set or not
        mPrefs.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in), false).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void modifyFab(@Nullable Fragment fragment) {
        NavigationView nv = findViewById(R.id.nav_view);
        if (fragment instanceof HomeFragment) {
            mFab.show();
            mFab.setOnClickListener(view -> loadFragmentWithBackStack(new NewMessageFragment(), Pages.NEWMESSAGE));
            mFab.setImageResource(R.drawable.ic_fab_send);
            nv.getMenu().getItem(0).setChecked(true);
            setTitle(Pages.HOME.toString());
        } else if (fragment instanceof ConnectionsFragment) {
            mFab.show();
            mFab.setOnClickListener(view -> loadFragmentWithBackStack(new NewConnectionFragment(), Pages.NEWCONNECTION));
            mFab.setImageResource(R.drawable.ic_fab_add);
            nv.getMenu().getItem(1).setChecked(true);
            setTitle(Pages.CONNECTIONS.toString());
        } else if (fragment instanceof WeatherFragment) {
            mFab.hide();
            nv.getMenu().getItem(2).setChecked(true);
            setTitle(Pages.WEATHER.toString());
        } else if (fragment instanceof SettingsFragment) {
            mFab.hide();
            nv.getMenu().getItem(3).setChecked(true);
            setTitle(Pages.SETTINGS.toString());
        } else if (fragment instanceof NewMessageFragment) {
            mFab.hide();
            nv.getMenu().getItem(0).setChecked(true);
            setTitle(Pages.NEWMESSAGE.toString());
        } else if (fragment instanceof NewConnectionFragment) {
            mFab.hide();
            nv.getMenu().getItem(1).setChecked(true);
            setTitle(Pages.NEWCONNECTION.toString());
        } else {
            Log.wtf("Main Activity", "YOU SHOULD NOT SEE THIS");
        }
    }
}
