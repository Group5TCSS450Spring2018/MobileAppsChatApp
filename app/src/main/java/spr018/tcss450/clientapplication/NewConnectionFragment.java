package spr018.tcss450.clientapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewConnectionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private EditText mSearchText;

    public NewConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_new_connection, container, false);
        mSearchText = v.findViewById(R.id.newConnectionSearchText);
        mSearchText.setOnFocusChangeListener(this::onSearchTextFocusChange);
        ImageButton searchButton = v.findViewById(R.id.newConnectionSearchButton);
        searchButton.setOnClickListener(this::onSearchClicked);


        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewMessageFragment.OnFragmentInteractionListener) {
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
    public void onSearchClicked(View button) {
        String username = mSearchText.getText().toString();
        onSearchTextFocusChange(mSearchText, false);
        if(mSearchText.getError() == null) {
            mListener.onSearchAttempt(username);
        }
    }

    public void onSearchTextFocusChange(View searchText, boolean hasFocus) {
        String username = mSearchText.getText().toString();
        if (!hasFocus) {
            if (username.isEmpty()) {
                mSearchText.setError(getString(R.string.error_empty));
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onSearchAttempt(String username);
    }

}
