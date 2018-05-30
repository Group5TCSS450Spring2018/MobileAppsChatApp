package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import spr018.tcss450.clientapplication.model.Credentials;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;

/**
 * Activity that holds fragments involved in loging in. register, login, validiation, forgot password
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class LoginActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        LoginValidationFragment.OnFragmentInteractionListener,
        ForgotPasswordFragment.OnFragmentInteractionListener,
        ResetPasswordFragment.OnFragmentInteractionListener {

    /* Credentials for POST to webservice */
    private Credentials mCredentials;

    private boolean stayLoggedIn = false;
    private String emailTemp = "";

    private SharedPreferences mPrefs;


    /* ****************************************** */
    /* OVERRIDES FOR CALLBACK AND FACTORY METHODS */
    /* ****************************************** */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mPrefs = getSharedPreferences(
                    getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            // make sure to set the app theme
            setTheme(mPrefs.getInt(
                    getString(R.string.keys_prefs_app_theme_no_actionbar), R.style.AppTheme_NoActionBar));
            // check which landing we should go to
            if (mPrefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false)) {
                showMainActivity();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.loginFragmentContainer, new LoginFragment(),
                                getString(R.string.keys_fragment_login))
                        .commit();
            }
        }

        setContentView(R.layout.activity_login);

    }

    /**
     *sends your credentials to login database.
     * @param loginCredentials: your username and password saved.
     */
    @Override
    public void onLoginAttempt(Credentials loginCredentials) {
        //build
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();
        //build
        JSONObject msg = loginCredentials.asJSONObject();
        mCredentials = loginCredentials;

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleLoginErrorsInTask)
                .build().execute();
    }

    /**
     * If you click register, the register fragment will appear.
     */
    @Override
    public void onRegisterClicked() {
        RegisterFragment r = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, r, getString(R.string.keys_fragment_register))
                .addToBackStack(getString(R.string.keys_fragment_register));
        transaction.commit();
    }

    /**
     * if you click forgot password then the forgot password fragment will display.
     */
    @Override
    public void onForgotPasswordClicked() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, new ForgotPasswordFragment(), getString(R.string.keys_fragment_forgot_password))
                .addToBackStack(getString(R.string.keys_fragment_forgot_password))
                .commit();
    }

    /**
     * sends a new password to the database
     * @param email: users email
     */
    @Override
    public void onSendResetCodeAttempt(String email) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_reset_password))
                .build();
        JSONObject msg = new JSONObject();
        try {
            msg.put("email", email);
        } catch (JSONException e) {
            Log.e("JSON", e.toString());
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleSendValidationPre)
                .onPostExecute(this::handleSendValidationPost)
                .onCancelled(this::handleSendValidationError)
                .build()
                .execute();
    }

    /**
     * sends to register database users registration information.
     * @param loginCredentials: users information
     */
    @Override
    public void onRegisterAttempt(Credentials loginCredentials) {
        emailTemp = loginCredentials.getEmail();
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
                .onCancelled(this::handleRegisterErrorsInTask)
                .build().execute();
    }

    /**
     * sends to the database the code to verify the user
     * @param code: code to verify with
     */
    @Override
    public void onValidationAttempt(int code) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_verify))
                .build();

        JSONObject msg = new JSONObject();
        try {
            msg.put("verifyCode", code);
            msg.put("username", mCredentials.getUsername());
        } catch (JSONException e) {
            Log.wtf("VERIFICATION OBJECT", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginVerificationOnPost)
                .onCancelled(this::handleValidationErrorsInTask)
                .build().execute();
    }

    /**
     * sends to the database a new code.
     */
    @Override
    public void onResendCodeAttempt() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_resendCode))
                .build();
        JSONObject msg = new JSONObject();
        try {
            msg.put("username", mCredentials.getUsername());
        } catch (JSONException e) {
            Log.wtf("VERIFICATION OBJECT", "Error creating JSON: " + e.getMessage());
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleResendCodePre)
                .onPostExecute(this::handleResendCodePost)
                .onCancelled(this::handleResendCodeError)
                .build().execute();

    }

    /**
     * sends to the database a new password
     * @param credentials: users information
     * @param resetCode: the new code the user was given.
     */
    @Override
    public void onResetPasswordAttempt(Credentials credentials, int resetCode) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_update_password))
                .build();

        JSONObject msg = new JSONObject();
        try {
            msg.put("verifyCode", resetCode);
            msg.put("username", credentials.getUsername());
            msg.put("newPassword", credentials.getPassword());
        } catch (JSONException e) {
            Log.wtf("VERIFICATION OBJECT", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPreExecute(this::handleUpdatePasswordPre)
                .onPostExecute(this::handleUpdatePasswordPost)
                .onCancelled(this::handleUpdatePasswordError)
                .build().execute();
    }

    /* *************** */
    /* PRIVATE HELPERS */
    /* *************** */

    /**
     * show main activity
     */
    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * stay logged in until user manually logs out.
     */
    private void checkStayLoggedIn() {
        if (((CheckBox) findViewById(R.id.logCheckBox)).isChecked()) {
            Log.e("CHECK BOX", "CHECKED");
            stayLoggedIn = true;
        }
    }

    /**
     * save the users information
     */
    private void saveUserInfo() {
        //Save the username for later usage
        mPrefs.edit().putString(getString(R.string.keys_prefs_user_name),
                mCredentials.getUsername()).apply();
        Log.d("USERNAME IS: ", getString(R.string.keys_prefs_user_name));
        //save the users "want" to stay logged in
        mPrefs.edit().putBoolean(getString(R.string.keys_prefs_stay_logged_in),
                stayLoggedIn).apply();
        String timestamp = mPrefs.getString(getString(R.string.keys_timestamp) + mCredentials.getUsername(), "");
        String chatTimestamp = mPrefs.getString(getString(R.string.keys_chatTimestamp) + mCredentials.getUsername(), "");
        if(timestamp.isEmpty()) {
            mPrefs.edit().putString(getString(R.string.keys_timestamp) + mCredentials.getUsername(),  "1970-01-01T00:00:01.000Z").apply();
            mPrefs.edit().putString(getString(R.string.keys_chatTimestamp) + mCredentials.getUsername(),  "1970-01-01T00:00:01.000Z").apply();
        }
    }

    /**
     * show a verification page
     */
    private void showVerificationPage() {
        LoginValidationFragment l_v_frag = new LoginValidationFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, l_v_frag,
                        getString(R.string.keys_fragment_login_validation))
                .addToBackStack(getString(R.string.keys_fragment_login));
        transaction.commit();
    }

    /* ******************* */
    /* ASYNC TASK HANDLERS */
    /* ******************* */

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleLoginErrorsInTask(String result) {
        Toast.makeText(getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("ASYNCT_TASK_ERROR", result);
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.keys_fragment_login));
        if (loginFragment != null) {
            loginFragment.setEnabledAllButtons(true);
        }
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is * a J  SON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.keys_fragment_login));
        if (loginFragment != null) {
            loginFragment.setEnabledAllButtons(true);
        }
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            boolean isVerified = resultsJSON.getBoolean("verify");
            //boolean isVerified = false;
            if (success) {
                checkStayLoggedIn();
                if (isVerified) { // login completely successful
                    saveUserInfo();
                    showMainActivity();
                } else { // login was successful, but verification wasnt
                    // force verification
                    showVerificationPage();
                }
            } else {
                //login failed.
                Objects.requireNonNull(loginFragment).setError("Log in unsuccessful");
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
        }
    }
    /**
     * Handle registration errors
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterErrorsInTask(String result) {
        Toast.makeText(getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("ASYNCT_TASK_ERROR", result);
        RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.keys_fragment_register));
        if (registerFragment != null) {
            registerFragment.setEnabledAllButtons(true);
        }
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is * a J  SON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleRegisterOnPost(String result) {
        RegisterFragment registerFragment = (RegisterFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.keys_fragment_register));
        if (registerFragment != null) {
            registerFragment.setEnabledAllButtons(true);
        }
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                getSupportFragmentManager().popBackStack();
                Toast.makeText(this, "Registered successfully! Verification code sent to: " + emailTemp, Toast.LENGTH_SHORT).show();
            } else {
                //register was unsuccessful. Don’t switch fragments and inform the user

                Objects.requireNonNull(registerFragment).setError(resultsJSON.getJSONObject("error"));
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void handleValidationErrorsInTask(String result) {
        Toast.makeText(getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("ASYNCT_TASK_ERROR", result);
        RegisterFragment validationFragment = (RegisterFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.keys_fragment_login_validation));
        if (validationFragment != null) {
            validationFragment.setEnabledAllButtons(true);
        }
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is * a J  SON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginVerificationOnPost(String result) {
        LoginValidationFragment validationFragment = (LoginValidationFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.keys_fragment_login_validation));

        validationFragment.setEnabledAllButtons(true);
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                saveUserInfo();
                showMainActivity();
                Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();

            } else {
                //register was unsuccessful. Don’t switch fragments and inform the user
                validationFragment.setError("Verification unsuccessful!");
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * set the forgot password button to disabled.
     */
    private void handleSendValidationPre() {
        findViewById(R.id.forgotPasswordButton).setEnabled(false);
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is * a J  SON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleSendValidationPost(String result) {
        findViewById(R.id.forgotPasswordButton).setEnabled(true);
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.loginFragmentContainer, new ResetPasswordFragment(), getString(R.string.keys_fragment_reset_password))
                        .addToBackStack(getString(R.string.keys_fragment_reset_password))
                        .commit();
            } else {
                Toast.makeText(getApplicationContext(), resultJSON.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }

    }

    /**
     * handle error from validation
     * @param result: the error that is thrown
     */
    private void handleSendValidationError(String result) {
        findViewById(R.id.forgotPasswordButton).setEnabled(true);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * set the resend code button to disabled.
     */
    private void handleResendCodePre() {
        findViewById(R.id.validationResendButton).setEnabled(false);
    }

    /**
     * checks to see if the code was resent successfully
     * @param result: result that is passed in from the webservice.
     */
    private void handleResendCodePost(String result) {
        findViewById(R.id.validationResendButton).setEnabled(true);
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if(success) {
                Toast.makeText(getApplicationContext(), "Please check your email for new verification code.", Toast.LENGTH_LONG).show();
            } else {
                Log.e("RESEND CODE", resultJSON.getString("message"));
                Toast.makeText(getApplicationContext(), "Unable to resend verification code. Please try again later.", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * handle resend code errors
     * @param result: error that is thrown.
     */
    private void handleResendCodeError(String result) {
        findViewById(R.id.validationResendButton).setEnabled(true);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("RESEND CODE", result);
    }

    /**
     * set update password to disabled.
     */
    private void handleUpdatePasswordPre() {
        findViewById(R.id.resetPasswordButton).setEnabled(false);
    }
    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is * a J  SON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleUpdatePasswordPost(String result) {
        findViewById(R.id.resetPasswordButton).setEnabled(true);
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                FragmentManager fm = getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            } else {
                ResetPasswordFragment fragment = (ResetPasswordFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.keys_fragment_reset_password));
                fragment.setError();
                Toast.makeText(getApplicationContext(), "Incorrect username or validation code", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * throw error if resetting password fails.
     * @param result
     */
    private void handleUpdatePasswordError(String result) {
        findViewById(R.id.resetPasswordButton).setEnabled(true);
        Toast.makeText(getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("ASYNCT_TASK_ERROR", result);
    }
}
