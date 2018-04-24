package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
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
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private EditText mUsername;
    private EditText mPassword;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mUsername = v.findViewById(R.id.usernameText);
        mUsername.setOnFocusChangeListener(this::onUsernameFocusChange);
        mPassword = v.findViewById(R.id.passwordText);
        mPassword.setOnFocusChangeListener(this::onPasswordFocusChange);
        Button login = v.findViewById(R.id.loginButton);
        login.setOnClickListener(this::getLoginInfo);
        Button register = v.findViewById(R.id.registerButton);
        register.setOnClickListener(this::onRegisterClicked);
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

    private void getLoginInfo(View v) {
        onUsernameFocusChange(null, false);
        onPasswordFocusChange(null, false);

        if (mUsername.getError() == null && mPassword.getError() == null) {
            Credentials loginCredentials = new Credentials.Builder(mUsername.getText().toString(),
                    mPassword.getText()).build();
            mListener.onLoginAttempt(loginCredentials);
        }
    }

    private void onUsernameFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mUsername.getText().toString().isEmpty()) {
                mUsername.setError("Cannot be empty");
            }
        }
    }

    private void onPasswordFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mPassword.getText().toString().isEmpty()) {
                mPassword.setError("Cannot be empty");
            }
        }
    }

    public void onRegisterClicked(View v) {
        mListener.onRegisterClicked();
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
                .setError("Login Unsuccessful");
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
        void onLoginAttempt(Credentials loginCredentials);

        void onRegisterClicked();
    }
}
