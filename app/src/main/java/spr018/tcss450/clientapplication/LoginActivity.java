package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import spr018.tcss450.clientapplication.utility.Pages;

public class LoginActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener {
    private Credentials mCredentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {

//            if(findViewById(R.id.fragmentContainer)!= null){
//                SharedPreferences prefs = getSharedPreferences(
//                        getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
//                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false)) {
//                    showMainActivity();
//                } else {
//
//                    getSupportFragmentManager().beginTransaction().
//                            add(R.id.loginFragmentContainer, new LoginFragment(),
//                                    getString(R.string.keys_fragment_login)).
//                            commit();
//                    //loadFragment(new LoginFragment(), Pages.LOGIN);
//                }
//            }
            SharedPreferences prefs = getSharedPreferences(
                        getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
                if (prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false)) {
                    showMainActivity();
                    Log.d("CHECK IN WAS SAVED","SHARED PREFS");
                } else {

                    getSupportFragmentManager().beginTransaction().
                            add(R.id.loginFragmentContainer, new LoginFragment(),
                                    getString(R.string.keys_fragment_login)).
                            commit();
                    //loadFragment(new LoginFragment(), Pages.LOGIN);
                }


        }


    }

    @Override
    public void onLoginInteraction(Credentials c, int type) {
        switch (type) {
            case 0:
                //showMainActivity(); //USER HAS SUCCESSFULLY LOGGED IN
                //step 51 in lab 4 track 1.
                //build
                Uri uri = new Uri.Builder().scheme("https").
                        appendPath(getString(R.string.ep_base_url)).
                        appendPath(getString(R.string.ep_login)).build();
                //build
                JSONObject msg = c.asJSONObject();
                mCredentials = c;
                Log.d("ONLOGININTERACTION","CREDENTIALS ASSIGNED TO C.");
                new SendPostAsyncTask.Builder(uri.toString(), msg).
                        onPostExecute(this::handleLoginOnPost)
                        .onCancelled(this::handleErrorsInTask) .build().execute();
                break;
            case 1:
                loadFragment(new RegisterFragment(), Pages.REGISTER);
                break;
            default:
                Log.wtf("Impossible", "How did this happen?");
                break;
        }
    }


    private void checkStayLoggedIn(){
            if(((CheckBox)findViewById(R.id.LogFragCheckBox)).isChecked()) {
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
            Log.e("HANDLELOGINONPOST","TRYING SUCCESS");
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

    @Override
    public void onRegisterInteraction() {
        loadFragment(new LoginFragment(), Pages.LOGIN);
        Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT).show();
    }


    /* Helpers */
    private void loadFragment(Fragment frag, Pages page) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, frag);

        if (page == Pages.REGISTER) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        setTitle(page.toString());
    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
