package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class ConnectionProfileFragment extends Fragment {
    private static final String BUNDLE_FULL_NAME = "full name";
    private static final String BUNDLE_USERNAME = "username";
    private static final String BUNDLE_EMAIL = "email";
    private static final String BUNDLE_FRIEND ="friend";

    private String mFullName;
    private String mUsername;
    private String mEmail;
    private boolean mFriend;

    private OnFragmentInteractionListener mListener;

    public ConnectionProfileFragment() {
        // Required empty public constructor
    }

    public static ConnectionProfileFragment newInstance(String fullname, String username, String email, boolean isFriend) {
        ConnectionProfileFragment fragment = new ConnectionProfileFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FULL_NAME, fullname);
        args.putString(BUNDLE_USERNAME, username);
        args.putString(BUNDLE_EMAIL, email);
        args.putBoolean(BUNDLE_FRIEND, isFriend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFullName = getArguments().getString(BUNDLE_FULL_NAME);
            mUsername = getArguments().getString(BUNDLE_USERNAME);
            mEmail = getArguments().getString(BUNDLE_EMAIL);
            mFriend = getArguments().getBoolean(BUNDLE_FRIEND);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_profile, container, false);
        TextView name = view.findViewById(R.id.profileName);
        name.setText(mFullName);
        TextView username = view.findViewById(R.id.profileUsername);
        username.setText(mUsername);
        TextView email = view.findViewById(R.id.profileEmail);
        email.setText(mEmail);
        ImageButton button = view.findViewById(R.id.profileAddButton);
        if (mFriend) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(this::onAddButtonClicked);
        }
        return view;
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

    private void onAddButtonClicked(View button) {
        mListener.onAddNewConnectionAttempt(mFullName, mUsername, mEmail, mFriend);
    }

    public interface OnFragmentInteractionListener { //change to send bundle later.
        void onAddNewConnectionAttempt(String mFullName, String mUsername, String mEmail,Boolean mFriend);
    }
}
