package spr018.tcss450.clientapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayRequestFragment extends Fragment {

    private String mFullName;
    private String mUsername;

    private static final String BUNDLE_FULL_NAME = "full name";
    private static final String BUNDLE_USERNAME = "username";

    public DisplayRequestFragment() {
        // Required empty public constructor
    }

    public static DisplayRequestFragment newInstance(String fullname, String username) {
        DisplayRequestFragment req = new DisplayRequestFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_FULL_NAME, fullname);
        args.putString(BUNDLE_USERNAME, username);
        req.setArguments(args);
        return req;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFullName = getArguments().getString(BUNDLE_FULL_NAME);
            mUsername = getArguments().getString(BUNDLE_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_arequest, container, false);
        TextView name = view.findViewById(R.id.fullname);
        name.setText(mFullName);
        TextView username = view.findViewById(R.id.username);
        username.setText(mUsername);
        ImageButton accept = view.findViewById(R.id.accept);
        accept.setOnClickListener(this::AcceptRequest);
        ImageButton reject = view.findViewById(R.id.reject);
        reject.setOnClickListener(this::RejectRequest);
        return view;
    }
    private void RejectRequest(View view){

    }
    private void AcceptRequest(View view){
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
            msg.put("username_a", mUsername);
            msg.put("username_b", u);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleAcceptRequests)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleAcceptRequests(String results){
        Log.d("result is ", results);
    }

    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }
}
