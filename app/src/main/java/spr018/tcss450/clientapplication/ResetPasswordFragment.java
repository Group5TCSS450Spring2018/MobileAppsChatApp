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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResetPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ResetPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
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

    //Helper methods
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

    private void onResetClicked(View button) {
        onCodeFocusChange(mCodeEditText, false);
        onPasswordFocusChange(mPasswordEditText, false);
        onRePasswordFocusChange(mRePasswordEditText, false);

        if (mCodeEditText.getError() == null
                && mPasswordEditText.getError() == null
                && mRePasswordEditText.getError() == null) {
            mListener.onResetPasswordAttempt(null);
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
        void onResetPasswordAttempt(Credentials credentials);
    }
}
