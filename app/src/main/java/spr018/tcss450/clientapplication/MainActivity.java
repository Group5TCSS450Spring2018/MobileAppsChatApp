package spr018.tcss450.clientapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import spr018.tcss450.clientapplication.utility.Pages;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment(), Pages.HOME);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

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
            loadFragment(new SettingsFragment(), Pages.SETTINGS);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragment(new HomeFragment(), Pages.HOME);
        } else if (id == R.id.nav_connections) {
            loadFragment(new ConnectionsFragment(), Pages.CONNECTIONS);
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment(), Pages.WEATHER);
        } else if (id == R.id.nav_settings) {
            loadFragment(new SettingsFragment(), Pages.SETTINGS);
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
    public void onSettingsInteration(Uri uri) {

    }

    /* Helpers */
    private void loadFragment(Fragment frag, Pages page) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, frag)
                .commit();

        modifyFab(page);

        setTitle(page.toString());
    }

    private void showLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void modifyFab(Pages page) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        switch (page) {
            case HOME:
                fab.hide();
                break;
            case CONNECTIONS:
                fab.show();
                fab.setOnClickListener(view -> {
                    Toast.makeText(this, "Add button clicked!", Toast.LENGTH_SHORT).show();
                });
                fab.setImageResource(R.drawable.ic_fab_add);
                break;
            case WEATHER:
                fab.hide();
                break;
            case SETTINGS:
                fab.hide();
                break;
            default:
                Log.wtf("Impossible", "How did this happen?");
                break;
        }
    }
}
