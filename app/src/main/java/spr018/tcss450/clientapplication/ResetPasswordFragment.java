package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import spr018.tcss450.clientapplication.model.Credentials;


/**
 *  Reset the password  fragment for users who have forgotten or want to change there password.
 * {@link ResetPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ResetPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText mUsername;
    private EditText mCodeEditText;
    private EditText mPasswordEditText;
    private EditText mRePasswordEditText;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        mUsername = v.findViewById(R.id.resetPasswordUsername);
        mUsername.setOnFocusChangeListener(this::onUsernameFocusChange);
        mCodeEditText = v.findViewById(R.id.resetPasswordCode);
        mCodeEditText.setOnFocusChangeListener(this::onCodeFocusChange);
        mPasswordEditText = v.findViewById(R.id.resetPasswordPassword);
        mPasswordEditText.setOnFocusChangeListener(this::onPasswordFocusChange);
        mRePasswordEditText = v.findViewById(R.id.resetPasswordRePassword);
        mRePasswordEditText.setOnFocusChangeListener(this::onRePasswordFocusChange);
        Button reset = v.findViewById(R.id.resetPasswordButton);
        reset.setOnClickListener(this::onResetClicked);
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

    /**
     * Sets an error if  any conditions are not met.
     */
    public void setError() {
        mUsername.setError("");
        mCodeEditText.setError("");
    }

    /**
     * Sends errors until username is valid
     * @param view - current view
     * @param hasFocus - whether it's being edited or not.
     */
    //Helper methods
    private void onUsernameFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            if (mUsername.getText().toString().isEmpty()) {
                mUsername.setError(getString(R.string.error_empty));
            }
        }
    }

    /**
     *  Sends  errors until validation code is valid
     * @param view - current view
     * @param hasFocus - whether it's being edited or not.
     */
    private void onCodeFocusChange(View view, boolean hasFocus) {
        EditText codeText = (EditText) view;
        if (!hasFocus) {
            if (codeText.getText().toString().isEmpty()) {
                codeText.setError(getString(R.string.error_empty));
            } else if (codeText.getText().toString().length() != 4) {
                codeText.setError(getString(R.string.error_verification_code));
            }
        }
    }

    private void onPasswordFocusChange(View view, boolean hasFocus) {
        EditText passwordText = (EditText) view;
        String password = passwordText.getText().toString();
        if (!hasFocus) {
            // is empty
            if (password.isEmpty()) {
                passwordText.setError(getString(R.string.error_empty));
            }
            // is less than 6 characters
            if (password.length() < getResources().getInteger(R.integer.password_minimum_length)) {
                passwordText.setError(getString(R.string.error_password_short));
            }

            // has special characters
            if (password.matches(getString(R.string.regex_non_alphanumeric))) {
                passwordText.setError(getString(R.string.error_special_chars));
            }

            if (password.length() > getResources().getInteger(R.integer.too_long)) {
                passwordText.setError(getString(R.string.error_too_long));
            }
        }
    }

    /**
     *  Validates password as user types it.
     * @param view - current view
     * @param hasFocus - whether user is commenting code or not.
     */
    private void onRePasswordFocusChange(View view, boolean hasFocus) {
        EditText rePasswordText = (EditText) view;
        String rePassword = rePasswordText.getText().toString();
        if (!hasFocus) {
            // if empty
            if (rePassword.isEmpty()) {
                rePasswordText.setError(getString(R.string.error_empty));
            }
            // if password check matches password
            if (!rePassword.equals(mPasswordEditText.getText().toString())) {
                rePasswordText.setError(getString(R.string.error_password_not_match));
            }
        }
    }

    /**
     *  Checks for reset clicked to clear boxes.
     * @param button - button to click
     */
    private void onResetClicked(View button) {
        onUsernameFocusChange(mUsername, false);
        onCodeFocusChange(mCodeEditText, false);
        onPasswordFocusChange(mPasswordEditText, false);
        onRePasswordFocusChange(mRePasswordEditText, false);

        if (mUsername.getError() == null
                && mCodeEditText.getError() == null
                && mPasswordEditText.getError() == null
                && mRePasswordEditText.getError() == null) {
            Credentials c = new Credentials.Builder(mUsername.getText().toString(), mPasswordEditText.getText()).build();
            int resetCode = Integer.parseInt(mCodeEditText.getText().toString());
            mListener.onResetPasswordAttempt(c, resetCode);
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
        /**
         *  Sends credentials on the attempt to reset the password.
         * @param credentials - user credentials
         * @param resetCode - code to reset password
         */
        void onResetPasswordAttempt(Credentials credentials, int resetCode);
    }
}
