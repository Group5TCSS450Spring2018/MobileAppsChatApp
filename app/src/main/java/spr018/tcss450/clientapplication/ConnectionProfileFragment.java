package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


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
    private static final String BUNDLE_TYPE ="friend";


    public static final int FRIEND = 0;
    public static final int PENDING = 1;
    public static final int STRANGER = 2;

    private String mFullName;
    private String mUsername;
    private String mEmail;
    private int mType;
    private View mView;
    private ImageButton mButton;
    private ImageButton mAcceptButton;
    private ImageButton mDeclineButton;

    private OnFragmentInteractionListener mListener;

    public ConnectionProfileFragment() {
        // Required empty public constructor
    }

    public static ConnectionProfileFragment newInstance(String fullName, String username, String email, int connectionType) {
        ConnectionProfileFragment fragment = new ConnectionProfileFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FULL_NAME, fullName);
        args.putString(BUNDLE_USERNAME, username);
        args.putString(BUNDLE_EMAIL, email);
        args.putInt(BUNDLE_TYPE, connectionType);
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
            mType = getArguments().getInt(BUNDLE_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_connection_profile, container, false);
        TextView name = mView.findViewById(R.id.profileName);
        name.setText(mFullName);
        TextView username = mView.findViewById(R.id.profileUsername);
        username.setText(mUsername);
        TextView email = mView.findViewById(R.id.profileEmail);
        email.setText(mEmail);
        mButton = mView.findViewById(R.id.profileAddButton);
        mAcceptButton = mView.findViewById(R.id.profileAcceptButton);
        mDeclineButton = mView.findViewById(R.id.profileDeclineButton);
        setUpButtons();
        return mView;
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

    private void setUpButtons() {
        if (mType == FRIEND) {
            mButton.setImageResource(R.drawable.ic_connection_remove_red);
            mButton.setOnClickListener(this::onRemoveButtonClicked);
        } else if (mType == PENDING) {
            mButton.setVisibility(View.GONE);
            mAcceptButton.setVisibility(View.VISIBLE);
            mAcceptButton.setOnClickListener(this::onAcceptButtonClicked);
            mView.findViewById(R.id.profileDeclineButton).setVisibility(View.VISIBLE);
            mDeclineButton.setOnClickListener(this::onDeclineButtonClicked);
        } else if (mType == STRANGER){
            mButton.setOnClickListener(this::onAddButtonClicked);
        }
    }

    private void onAddButtonClicked(View button) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_addConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username_a", u); //pass in their username
            msg.put("username_b", mUsername);

        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleAddPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleAddPost(String result) {
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                mButton.setEnabled(false);
                Toast.makeText(getActivity().getApplicationContext(), "Request sent", Toast.LENGTH_LONG).show();
            } else {
                Log.e("JSONOBJECT", result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onRemoveButtonClicked(View button) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_removeConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("me", u);
            msg.put("username", mUsername);

        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRemovePost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleRemovePost(String result) {
        Log.e("JSON", result);
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                mType = STRANGER;
                setUpButtons();
            } else {
                Log.e("JSONOBJECT", result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onAcceptButtonClicked(View button) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_acceptConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username_a", u);
            msg.put("username_b", mUsername);

        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleAcceptPost)
                .onCancelled(this::handleError)
                .build().execute();
    }

    private void handleAcceptPost(String result) {
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                Toast.makeText(getActivity().getApplicationContext(), "New connection added", Toast.LENGTH_LONG).show();
                mType = FRIEND;
                setUpButtons();
            } else {
                Log.e("JSONOBJECT", result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onDeclineButtonClicked(View button) {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_acceptConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username_a", u); //pass in their username
            msg.put("username_b", mUsername);

        } catch(JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(getActivity().getApplicationContext(), "Awaiting endpoint. I'm in ConnectionProfileFragment", Toast.LENGTH_SHORT).show();
//        new SendPostAsyncTask.Builder(uri.toString(), msg)
//                .onPostExecute(this::handleDeclinePost)
//                .onCancelled(this::handleError)
//                .build().execute();
    }

    private void handleDeclinePost(String result) {
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                Toast.makeText(getActivity().getApplicationContext(), "Request removed", Toast.LENGTH_LONG).show();
                mType = STRANGER;
                setUpButtons();
            } else {
                Log.e("JSONOBJECT", result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleError(String result) {
        Log.e("JSONOBJECT", result);
    }

    public interface OnFragmentInteractionListener { //change to send bundle later.
        void onAddNewConnectionAttempt(String mFullName, String mUsername, String mEmail);
    }
}
