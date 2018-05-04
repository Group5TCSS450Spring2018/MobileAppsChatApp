package spr018.tcss450.clientapplication;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import spr018.tcss450.clientapplication.utility.Pages;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        NewMessageFragment.OnFragmentInteractionListener {

    private SharedPreferences mPrefs;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        loadFragmentNoBackStack(new HomeFragment(), Pages.HOME);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//
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
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
        //TODO Implement search
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragmentNoBackStack(new HomeFragment(), Pages.HOME);
        } else if (id == R.id.nav_connections) {
            loadFragmentNoBackStack(new ConnectionsFragment(), Pages.CONNECTIONS);
        } else if (id == R.id.nav_weather) {
            loadFragmentNoBackStack(new WeatherFragment(), Pages.WEATHER);
        } else if (id == R.id.nav_log_out) {
            showLoginActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public void onNewChatDetach() {
        modifyFab(Pages.HOME);
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
        Log.e("THEME CHOSEN", styleID + " IS THE THEME");
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
    }

    /* Helpers */
    private void loadFragmentWithBackStack(Fragment frag, Pages page) {
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, frag)
                .addToBackStack(null);
        ft.commit();

        modifyFab(page);

        setTitle(page.toString());
    }

    private void loadFragmentNoBackStack(Fragment frag, Pages page) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, frag)
                .commit();

        modifyFab(page);

        setTitle(page.toString());
    }

    private void showLoginActivity() {
        // clear stay logged in regardless of whether it is set or not
        mPrefs.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in), false).apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private View loadNewChat(View v) {
        loadFragmentWithBackStack(new NewMessageFragment(), Pages.NEWCHAT);
        return v;
    }

    private void modifyFab(Pages page) {

        switch (page) {
            case HOME:
                mFab.show();
                mFab.setOnClickListener(this::loadNewChat);
                mFab.setImageResource(R.drawable.ic_fab_send);
                break;
            case CONNECTIONS:
                mFab.show();
                mFab.setOnClickListener(view -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Add New Connection!")
                            .setMessage("Attempted to add new connection! (Placeholder)")
                            .setCancelable(false)
                            .show();
                });
                mFab.setImageResource(R.drawable.ic_fab_add);
                break;
            case WEATHER:
                mFab.hide();
                break;
            case SETTINGS:
                mFab.hide();
                break;
            case NEWCHAT:
                mFab.hide();
                setTitle("Chat name");
                break;
            default:
                Log.wtf("Impossible", "How did this happen?");
                break;
        }
    }


}
