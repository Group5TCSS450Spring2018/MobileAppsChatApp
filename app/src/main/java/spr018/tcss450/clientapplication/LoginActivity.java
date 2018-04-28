package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import spr018.tcss450.clientapplication.model.Credentials;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

/**
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class LoginActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {

    /* Credentials for POST to webservice */
    private Credentials mCredentials;

    private SharedPreferences mPrefs;


    /* ****************************************** */
    /* OVERRIDES FOR CALLBACK AND FACTORY METHODS */
    /* ****************************************** */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            mPrefs = getSharedPreferences(
                        getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            // make sure to set the app theme
            setTheme(mPrefs.getInt(
                    getString(R.string.keys_prefs_app_theme), R.style.AppTheme));
            // check which landing we should go to
            if (mPrefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false)) {
                showMainActivity();
            } else {
                getSupportFragmentManager().beginTransaction().
                        add(R.id.loginFragmentContainer, new LoginFragment(),
                                getString(R.string.keys_fragment_login)).
                        commit();
            }
        }
    }

    @Override
    public void onLoginAttempt(Credentials loginCredentials) {
            //build
            Uri uri = new Uri.Builder()
                    .scheme("https").
                    appendPath(getString(R.string.ep_base_url)).
                    appendPath(getString(R.string.ep_login))
                    .build();
            //build
            JSONObject msg = loginCredentials.asJSONObject();
            mCredentials = loginCredentials;

            Log.i("LOG", "LOGGING IN: " + mCredentials.toString());

            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleLoginOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
    }

    @Override
    public void onRegisterClicked() {
        RegisterFragment r = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, r, getString(R.string.keys_fragment_register))
                .addToBackStack(getString(R.string.keys_fragment_register));
        transaction.commit();
    }

    @Override
    public void onRegisterAttempt(Credentials loginCredentials) {
        //build the web service URL
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();
        //build the JSONObject
        JSONObject msg = loginCredentials.asJSONObject();
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }


    /* *************** */
    /* PRIVATE HELPERS */
    /* *************** */
    private void showMainActivity() {
       Intent intent = new Intent(this, MainActivity.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(intent);
    }

    private void checkStayLoggedIn() {
        if (((CheckBox) findViewById(R.id.logCheckBox)).isChecked()) {
            Log.e("CHECK BOX", "CHECKED");
            SharedPreferences p = getSharedPreferences
                    (getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            //Save the username for later usage
            p.edit().putString(getString(R.string.keys_shared_prefs),
                    mCredentials.getUsername()).apply();
            //save the users "want" to stay logged in
            p.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in), true).apply();
        }
    }


    /* ******************* */
    /* ASYNC TASK HANDLERS */
    /* ******************* */
    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is * a JSON formatted String. Parse it for success or failure.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try{
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if(success){
                //login was successful so open the main activity
                checkStayLoggedIn();
                showMainActivity();
            } else {
                //login failed.
                LoginFragment frag = (LoginFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.keys_fragment_login));
                frag.setError("Log in unsuccessful");
            }
        } catch(JSONException e){
            Log.e("JSON_PARSE_ERROR", result+System.lineSeparator()+e.getMessage());
        }
    }

    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                getSupportFragmentManager().popBackStack();
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            } else {
                //register was unsuccessful. Don’t switch fragments and inform the user
                RegisterFragment frag =
                        (RegisterFragment) getSupportFragmentManager()
                                .findFragmentByTag(
                                        getString(R.string.keys_fragment_register));
                frag.setError(resultsJSON.getJSONObject("error"));
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

}
