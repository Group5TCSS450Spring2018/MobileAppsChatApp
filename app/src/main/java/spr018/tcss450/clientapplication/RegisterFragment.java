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

import spr018.tcss450.clientapplication.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RegisterFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mRePassword;
    private boolean canRegister;

    public RegisterFragment() {
        canRegister = true;
    }

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

    private void onFirstNameFocusChange(View firstNameET, boolean hasFocus) {
        if (!hasFocus) {
            if (mFirstName.getText().toString().isEmpty()) {
                mFirstName.setError("Cannot be empty");
            }
        }

        canRegister = canRegister && mFirstName.getError() == null;
    }

    private void onLastNameFocusChange(View lastNameET, boolean hasFocus) {
        if (!hasFocus) {
            if (mLastName.getText().toString().isEmpty()) {
                mLastName.setError("Cannot be empty");
            }
        }

        canRegister = canRegister && mLastName.getError() == null;
    }

    private void onEmailFocusChange(View emailET, boolean hasFocus) {
        if (!hasFocus) {
            if (mEmail.getText().toString().isEmpty()) {
                mEmail.setError("Cannot be empty");
            }
        }

        canRegister = canRegister && mEmail.getError() == null;
    }

    private void onUsernameFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mUsername.getText().toString().isEmpty()) {
                mUsername.setError("Cannot be empty");
            }
        }

        canRegister = canRegister && mUsername.getError() == null;
    }

    private void onPasswordFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            String password = mPassword.getText().toString();
            if (password.isEmpty()) {
                mPassword.setError("Cannot be empty");
            } else if (password.length() < 6) {
                mPassword.setError("Cannot be less than 6 characters");
            }
        }

        canRegister = canRegister && mPassword.getError() == null;
    }

    private void onRePasswordFocusChange(View v, boolean hasFocus) {
        String rePassword = mRePassword.getText().toString();
        if (!hasFocus) {
            if (rePassword.isEmpty()) {
                mRePassword.setError("Cannot be empty");
            } else if (!rePassword.equals(mPassword.getText().toString())) {
                mRePassword.setError("Password does not match");
            }
        } else if (!rePassword.isEmpty() && rePassword.equals(mPassword.getText().toString())) {
            mRePassword.setError(null);
        }

        canRegister = canRegister && mRePassword.getError() == null;
    }

    private void onRegisterClicked(View v) {
        canRegister = true;
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

    public void setError(JSONObject errorJSON) {
        try {
            String error = errorJSON.getString("constraint");
            if (error.equals(getString(R.string.keys_email_error))) {
                mEmail.setError("This email address has already been used");
            } else if (error.equals(getString(R.string.keys_username_error))) {
                mUsername.setError("This username already exists");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onRegisterAttempt(Credentials loginCredentials);
    }
}