package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button button = v.findViewById(R.id.loginButton);
        button.setOnClickListener(this::onButtonPressed);

        button = v.findViewById(R.id.registerButton);
        button.setOnClickListener(this::onButtonPressed);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            int id = view.getId();
            if (id == R.id.loginButton) {
                EditText username = getView().findViewById(R.id.LogFragUsername);
                EditText password = getView().findViewById(R.id.LogFragPassword);
                if(username.getText().toString().equals("")){
                    username.setError("Fields cant be empty");
                } if(password.getText().toString().equals("")){
                    password.setError("Field cant be empty.");
                } else {
                    //send to the successful login screen.
                    String u = username.getText().toString();

                    Editable p = password.getText();
                    Credentials c = new Credentials.Builder(u,p).build();
                    mListener.onLoginInteraction(c,0);

                }

            } else if (id == R.id.registerButton) {
                //mListener.onLoginInteraction();
            }
        }
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

    /**
     * Allows an external source to set an error message on this fragment. This may * be needed if an Activity includes processing that could cause login to fail. * @param err the error message to display.
     */
    public void setError(String err) {
        //Log in unsuccessful for reason: err. Try again.
        //you may want to add error stuffs for the user here
        ((TextView) getView().findViewById(R.id.LogFragUsername)).setError("Login Unsuccessul");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onLoginInteraction(Credentials c, int type);
    }
}
