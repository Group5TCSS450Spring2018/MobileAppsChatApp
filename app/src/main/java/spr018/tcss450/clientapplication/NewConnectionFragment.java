package spr018.tcss450.clientapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewConnectionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SearchView mSearchView;

    public NewConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_new_connection, container, false);
        mSearchView = v.findViewById(R.id.newConnectionSearchView);
        mSearchView.setOnFocusChangeListener(this::onSearchTextFocusChange);
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

    }

    private void onSearchTextFocusChange(View searchView, boolean hasFocus) {

    }

    public interface OnFragmentInteractionListener {
        void onSearchAttempt(String username);
    }

}
