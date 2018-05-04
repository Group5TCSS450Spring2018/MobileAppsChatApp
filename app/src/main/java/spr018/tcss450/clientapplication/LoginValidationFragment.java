package spr018.tcss450.clientapplication;

import android.content.Context;
import android.net.Credentials;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginValidationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_validation, container, false);

        mCode = v.findViewById(R.id.validationNumberInput);
        mCode.setOnFocusChangeListener(this::onCodeFocusChange);

        v.findViewById(R.id.validationButton).setOnClickListener(this::onValidationButtonPressed);

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

    private void onCodeFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (mCode.getText().toString().isEmpty()) {
                mCode.setError("Cannot by empty!");
            }
            if (mCode.getText().toString().length() != 4) {
                mCode.setError("Must be of length 4!");
            }
        } else {
            mCode.setError(null);
        }
    }

    /* *********** */
    /* EXPOSED API */
    /* *********** */
    public void setEnabledAllButtons(boolean state) {
        getActivity().findViewById(R.id.validationButton).setEnabled(state);
    }

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
        //Log in unsuccessful for reason: err. Try again.
        //you may want to add error stuffs for the user here.
        ((EditText) getView().findViewById(R.id.validationNumberInput))
                .setError(getString(R.string.error_empty));
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
        // TODO: Update argument type and name
        void onValidationAttempt(int code);
    }
}
