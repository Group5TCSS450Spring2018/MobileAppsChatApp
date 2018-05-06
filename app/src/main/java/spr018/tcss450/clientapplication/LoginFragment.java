package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import spr018.tcss450.clientapplication.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class LoginFragment extends Fragment {

    /* Listener to be attached */
    private OnFragmentInteractionListener mListener;

    /* Text Fields */
    private EditText mUsername;
    private EditText mPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    /* ****************************************** */
    /* OVERRIDES FOR CALLBACK AND FACTORY METHODS */
    /* ****************************************** */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mUsername = v.findViewById(R.id.usernameText);
        mUsername.setOnFocusChangeListener(this::onUsernameFocusChange);
        mPassword = v.findViewById(R.id.passwordText);
        mPassword.setOnFocusChangeListener(this::onPasswordFocusChange);
        Button login = v.findViewById(R.id.loginButton);
        login.setOnClickListener(this::handleLoginAttempt);
        Button register = v.findViewById(R.id.registerButton);
        register.setOnClickListener(view -> mListener.onRegisterClicked());
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
    private void onUsernameFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mUsername.getText().toString().isEmpty()) {
                mUsername.setError(getString(R.string.error_empty));
            }
        }
    }

    private void onPasswordFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mPassword.getText().toString().isEmpty()) {
                mPassword.setError(getString(R.string.error_empty));
            }
        }
    }

    private void handleLoginAttempt(View v) {
        //These two methods call are NECESSARY.
        //They simulate the views losing focus.
        onUsernameFocusChange(null, false);
        onPasswordFocusChange(null, false);

        if (mPassword.getError() == null && mUsername.getError() == null) {
            Credentials loginCredentials = new Credentials.Builder(
                    mUsername.getText().toString(), mPassword.getText())
                    .build();
            setEnabledAllButtons(false);
            mListener.onLoginAttempt(loginCredentials);
        }
    }


    /* *********** */
    /* EXPOSED API */
    /* *********** */
    public void setEnabledAllButtons(boolean state) {
        getActivity().findViewById(R.id.loginButton).setEnabled(state);
        getActivity().findViewById(R.id.registerButton).setEnabled(state);
    }

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
        //Log in unsuccessful for reason: err. Try again.
        //you may want to add error stuffs for the user here.
        ((EditText) getView().findViewById(R.id.usernameText))
                .setError(getString(R.string.error_empty));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     **/
    public interface OnFragmentInteractionListener {
        void onLoginAttempt(Credentials loginCredentials);
        void onRegisterClicked();
    }
}
