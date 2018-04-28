package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import spr018.tcss450.clientapplication.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class RegisterFragment extends Fragment {

    /* listener to be attached */
    private OnFragmentInteractionListener mListener;

    /* Text Fields */
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mRePassword;

    /* For determining if registration is possible */
    private boolean canRegister;

    public RegisterFragment() {
        // empty constructor
    }

    /* ****************************************** */
    /* OVERRIDES FOR CALLBACK AND FACTORY METHODS */
    /* ****************************************** */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        mFirstName = v.findViewById(R.id.firstNameRegister);
        mFirstName.setOnFocusChangeListener(this::onFirstNameFocusChange);
        mLastName = v.findViewById(R.id.lastNameRegister);
        mLastName.setOnFocusChangeListener(this::onLastNameFocusChange);
        mEmail = v.findViewById(R.id.emailRegister);
        mEmail.setOnFocusChangeListener(this::onEmailFocusChange);
        mUsername = v.findViewById(R.id.usernameRegister);
        mUsername.setOnFocusChangeListener(this::onUsernameFocusChange);
        mPassword = v.findViewById(R.id.passwordRegister);
        mPassword.setOnFocusChangeListener(this::onPasswordFocusChange);
        mRePassword = v.findViewById(R.id.passwordRegisterConfirm);
        mRePassword.setOnFocusChangeListener(this::onRePasswordFocusChange);
        v.findViewById(R.id.newRegisterButton).setOnClickListener(this::onRegisterClicked);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /* **************** */
    /* PRIVATE HANDLERS */
    /* **************** */
    private void onFirstNameFocusChange(View v, boolean hasFocus) {
        String firstName = mFirstName.getText().toString();

        if (!hasFocus) {
            // empty
            if (firstName.isEmpty()) {
                mFirstName.setError(getString(R.string.error_empty));
            }
            // special chars
            if (firstName.matches(getString(R.string.regex_non_alphanumeric))) {
                mFirstName.setError(getString(R.string.error_special_chars));
            }
            if (firstName.length() > getResources().getInteger(R.integer.too_long)) {
                mFirstName.setError(getString(R.string.error_too_long));
            }
        } else { // never set error until finished writing input and unfocused
            mFirstName.setError(null);
        }

        canRegister = canRegister && mFirstName.getError() == null;
    }

    private void onLastNameFocusChange(View v, boolean hasFocus) {
        String lastName = mLastName.getText().toString();

        if (!hasFocus) {
            // empty
            if (lastName.isEmpty()) {
                mLastName.setError(getString(R.string.error_empty));
            }
            // special chars
            if (lastName.matches(getString(R.string.regex_non_alphanumeric))) {
                mLastName.setError(getString(R.string.error_special_chars));
            }
            if (lastName.length() > getResources().getInteger(R.integer.too_long)) {
                mLastName.setError(getString(R.string.error_too_long));
            }
        } else { // never set error until finished writing input and unfocused
            mLastName.setError(null);
        }

        canRegister = canRegister && mLastName.getError() == null;
    }

    private void onEmailFocusChange(View v, boolean hasFocus) {
        String email = mEmail.getText().toString();

        if (!hasFocus) {
            // must not be empty
            if (email.isEmpty()) {
                mEmail.setError(getString(R.string.error_empty));
            }
            // must contain @
            if (!email.contains("@")){
                mEmail.setError(getString(R.string.error_email_invalid));
            }
            // must contain something to both sides of the @
            if (Arrays.stream(email.split("@")).anyMatch(s -> s.isEmpty())) {
                mEmail.setError(getString(R.string.error_email_invalid));
            }
            // must contain at least one . to the right of @
            if (email.indexOf('@') > email.indexOf('.')) {
                mEmail.setError(getString(R.string.error_email_invalid));
            }
            if (email.length() > getResources().getInteger(R.integer.too_long_email)) {
                mEmail.setError(getString(R.string.error_too_long_email));
            }
        } else {
            mEmail.setError(null);
        }

        canRegister = canRegister && mEmail.getError() == null;
    }

    private void onUsernameFocusChange(View v, boolean hasFocus) {
        String username = mUsername.getText().toString();

        if (!hasFocus) {
            // is empty
            if (username.isEmpty()) {
                mUsername.setError(getString(R.string.error_empty));
            }
            // special characters
            if (username.matches(getString(R.string.regex_non_alphanumeric))) {
                mUsername.setError(getString(R.string.error_special_chars));
            }
            if (username.length() > getResources().getInteger(R.integer.too_long)) {
                mUsername.setError(getString(R.string.error_too_long));
            }
        } else {
            mUsername.setError(null);
        }

        canRegister = canRegister && mUsername.getError() == null;
    }

    private void onPasswordFocusChange(View v, boolean hasFocus) {
        String password = mPassword.getText().toString();

        if (!hasFocus) {
            // is empty
            if (password.isEmpty()) {
                mPassword.setError(getString(R.string.error_empty));
            }
            // is less than 6 characters
            if (password.length() < getResources().getInteger(R.integer.password_minimum_length)) {
                mPassword.setError(getString(R.string.error_password_short));
            }

            // has special characters
            if (password.matches(getString(R.string.regex_non_alphanumeric))) {
                mPassword.setError(getString(R.string.error_special_chars));
            }

            if (password.length() > getResources().getInteger(R.integer.too_long)) {
                mPassword.setError(getString(R.string.error_too_long));
            }
        } else {
            mPassword.setError(null);
        }

        canRegister = canRegister && mPassword.getError() == null;
    }

    private void onRePasswordFocusChange(View v, boolean hasFocus) {
        String rePassword = mRePassword.getText().toString();
        if (!hasFocus) {
            // if empty
            if (rePassword.isEmpty()) {
                mRePassword.setError(getString(R.string.error_empty));
            }
            // if password check matches password
            if (!rePassword.equals(mPassword.getText().toString())) {
                mRePassword.setError(getString(R.string.error_password_not_match));
            }
        } else {
            mRePassword.setError(null);
        }

        canRegister = canRegister && mRePassword.getError() == null;
    }

    private void onRegisterClicked(View v) {
        canRegister = true; // allow base canRegister to be true

        // run the checks via their focus change listeners
        onFirstNameFocusChange(null, false);
        onLastNameFocusChange(null, false);
        onEmailFocusChange(null, false);
        onUsernameFocusChange(null, false);
        onPasswordFocusChange(null, false);
        onRePasswordFocusChange(null, false);

        if (canRegister) {
            String username = mUsername.getText().toString();
            Editable password = mPassword.getText();
            String firstName = mFirstName.getText().toString();
            String lastName = mLastName.getText().toString();
            String email = mEmail.getText().toString();
            Credentials loginCredentials = new Credentials
                    .Builder(username, password)
                    .addFirstName(firstName)
                    .addLastName(lastName)
                    .addEmail(email)
                    .build();
            mListener.onRegisterAttempt(loginCredentials);
        }
    }

    /* *********** */
    /* EXPOSED API */
    /* *********** */
    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param errorJSON the error message(s) to determine what errors were thrown.
     */
    public void setError(JSONObject errorJSON) {
        try {
            String error = errorJSON.getString("constraint");
            if (error.equals(getString(R.string.keys_email_error))) {
                mEmail.setError(getString(R.string.error_email_used));
            } else if (error.equals(getString(R.string.keys_username_error))) {
                mUsername.setError(getString(R.string.error_username_existed));
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", e.getMessage());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onRegisterAttempt(Credentials loginCredentials);
    }
}