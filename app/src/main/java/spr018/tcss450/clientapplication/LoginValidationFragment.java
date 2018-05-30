package spr018.tcss450.clientapplication;

import android.content.Context;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Objects;


/**
 * Fragment that displays the first time the user logins in. prompts for a 4 digit code.
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class LoginValidationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText mCode;
    public LoginValidationFragment() {
        // Required empty public constructor
    }

    /* ****************************************** */
    /* OVERRIDES FOR CALLBACK AND FACTORY METHODS */
    /* ****************************************** */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_validation, container, false);

        mCode = v.findViewById(R.id.validationNumberInput);
        mCode.setOnFocusChangeListener(this::onCodeFocusChange);

        v.findViewById(R.id.validationButton).setOnClickListener(this::onValidationButtonPressed);
        v.findViewById(R.id.validationResendButton).setOnClickListener(this::onResendPressed);
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

    /**
     * If the button is pressed, checks if the 4 digits are entered.
     * @param v: view on which the button is located
     */
    private void onValidationButtonPressed(View v) {
        if (mListener != null) {
            // check for errors in input
            onCodeFocusChange(null, false);

            // if no errors, attempt to verify
            if (mCode.getError() == null) {
                int code = Integer.parseInt(mCode.getText().toString());
                setEnabledAllButtons(false);
                mListener.onValidationAttempt(code);
            }
        }
    }

    /**
     * Checks to see if the user input a valid 4 digit code.
     * @param v: view on which the button is on.
     * @param hasFocus: if the user has clicked away from the edit text.
     */
    private void onCodeFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mCode.getText().toString().isEmpty()) {
                Log.d("HELLO", "in input rn: " + mCode.getText().toString());
                mCode.setError(getString(R.string.error_empty));
            } else if (mCode.getText().toString().length() != 4) {
                mCode.setError(getString(R.string.error_verification_code));
            } else {
                mCode.setError(null);
            }
        }
    }

    private void onResendPressed(View button) {
        mListener.onResendCodeAttempt();
    }

    /* *********** */
    /* EXPOSED API */
    /* *********** */
    public void setEnabledAllButtons(boolean state) {
        Objects.requireNonNull(getActivity()).findViewById(R.id.validationButton).setEnabled(state);
    }

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
        //Log in unsuccessful for reason: err. Try again.
        //you may want to add error stuffs for the user here.
        ((EditText) Objects.requireNonNull(getView()).findViewById(R.id.validationNumberInput))
                .setError("Unable to validate! Is the code correct?");
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
        void onValidationAttempt(int code);
        void onResendCodeAttempt();
    }
}
