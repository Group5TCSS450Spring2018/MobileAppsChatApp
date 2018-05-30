package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Displays fragment to reenter email if you forgot password.
 * @author  Tuan Dinh Tenma Rollins Daryan Hanshew Deepjot Kaur
 */
public class ForgotPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText mEmail;
    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        Button send = v.findViewById(R.id.forgotPasswordButton);
        send.setOnClickListener(this::onSendClicked);
        mEmail = v.findViewById(R.id.forgotPasswordEmailText);
        mEmail.setOnFocusChangeListener(this::onEmailFocusChange);
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
     *if the user has entered their email address. Then send the reset code.
     * @param button: view on which button is on.
     */
    private void onSendClicked(View button) {
        onEmailFocusChange(mEmail, false);
        if (mEmail.getError() == null) {
            mListener.onSendResetCodeAttempt(mEmail.getText().toString());
        }
    }

    /**
     *
     * @param editText: the edit text field that we will be getting the email address from.
     * @param hasFocus: if the user has clicked on it.
     */
    private void onEmailFocusChange(View editText, boolean hasFocus) {
        String email = mEmail.getText().toString();
        if (!hasFocus) {
            // must not be empty
            if (email.isEmpty()) {
                mEmail.setError(getString(R.string.error_empty));
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmail.setError(getString(R.string.error_email_invalid));
            }
            if (email.length() > getResources().getInteger(R.integer.too_long_email)) {
                mEmail.setError(getString(R.string.error_too_long_email));
            }
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
        void onSendResetCodeAttempt(String email);
    }
}
