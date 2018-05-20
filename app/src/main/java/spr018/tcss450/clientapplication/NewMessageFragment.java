package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class NewMessageFragment extends Fragment {
    private List<Connection> mConnectionsList;
    private List<Connection> mSelectedMembers;
    private ConnectionAdapter mAdapter;
    private TextView mSelectedLabel;
    private EditText mChatNameInput;
    private Button mCreateChatButton;

    private String tempChatName;

    private OnFragmentInteractionListener mListener;

    public NewMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_message, container, false);

        mConnectionsList = new ArrayList<>();
        mSelectedMembers = new ArrayList<>();

        mSelectedLabel = v.findViewById(R.id.selectedLabel);
        updateSelectedLabel();
        mChatNameInput = v.findViewById(R.id.chatNameInput);
        mCreateChatButton = v.findViewById(R.id.createChatButton);
        mCreateChatButton.setOnClickListener(this::openChat);

        mAdapter = new ConnectionAdapter(mConnectionsList);
        mAdapter.setOnItemClickListener(this::selectChatMember);

        RecyclerView allConnections = v.findViewById(R.id.newMessageConnectionsHolder);
        allConnections.setAdapter(mAdapter);

        allConnections.setLayoutManager(new LinearLayoutManager(getActivity()));

        checkConnections();

        setHasOptionsMenu(true);

        return v;
    }

    private void selectChatMember(Connection c) {
        if (mSelectedMembers.contains(c)) {
            mSelectedMembers.remove(c);
        } else {
            mSelectedMembers.add(c);
        }
        Log.d("SELECTION MADE !", "CURRENT MEMBERS: " + mSelectedMembers.toString());
        updateSelectedLabel();
    }

    private void updateSelectedLabel() {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String display = prefs.getString(getString(R.string.keys_prefs_user_name), "")
                + " (YOU)";
        for (Connection user : mSelectedMembers) {
            display += ", " + user.getUsername();
        }
        mSelectedLabel.setText(display);
    }

    private void openChat(View v){
        mCreateChatButton.setEnabled(false);
        tempChatName = mChatNameInput.getText().toString();
        boolean check = true;
        if (mChatNameInput.getText().toString().isEmpty()) {
            mChatNameInput.setError("Must provide a unique name!");
            mCreateChatButton.setEnabled(true);
            check = false;
        }

        if (mSelectedMembers.size() < 2) {
            mSelectedLabel.setError("Must select at least two members!");
            mCreateChatButton.setEnabled(true);
            check = false;
        }

        if (check) {
            //send get connections the username.
            SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_addChat))
                    .build();

            JSONObject msg = new JSONObject();
            try{
                msg.put("chatname", mChatNameInput.getText().toString());

                ArrayList<String> members = new ArrayList<String>();
                members.add(u);
                for (Connection c : mSelectedMembers) {
                    members.add(c.getUsername().toString());
                }

                msg.put("members", new JSONArray(members));

            } catch(JSONException e) {
                e.printStackTrace();
            }
            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleChatCreationOnPost)
                    .onCancelled(this::handleErrorsInTask)
                    .build().execute();
        }
    }

    private void handleChatCreationOnPost(String results) {
        try {
            JSONObject resultJSON = new JSONObject(results);
            int chatid = resultJSON.getInt("chatid");
            mListener.onChatCreation(chatid, tempChatName);
        } catch (JSONException e) {
            e.printStackTrace();
            //return;
        }
    }

    private void checkConnections() {
        //send get connections the username.
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getConnections))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username", u);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleViewConnections)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleViewConnections(String results) {
        try {
            JSONObject resultJSON = new JSONObject(results);
            if(resultJSON.has("connections_a")) {
                try {
                    JSONArray aArray = resultJSON.getJSONArray("connections_a");
                    JSONArray bArray = resultJSON.getJSONArray("connections_b");
                    mConnectionsList.clear();
                    if(aArray.length() == 0 && bArray.length() == 0) {
                        mConnectionsList.add(null);
                    } else {
                        for (int i = 0; i < aArray.length(); i++) {
                            JSONObject c = aArray.getJSONObject(i);
                            String username = c.getString("username");
                            String firstName = c.getString("firstname");
                            String lastName = c.getString("lastname");
                            String email = c.getString("email");
                            Connection u = new Connection(username, firstName + " " + lastName, email);
                            mConnectionsList.add(u);
                        }

                        for (int i = 0; i < bArray.length(); i++) {
                            JSONObject c = bArray.getJSONObject(i);
                            String username = c.getString("username");
                            String firstName = c.getString("firstname");
                            String lastName = c.getString("lastname");
                            String email = c.getString("email");
                            Connection u = new Connection(username, firstName + " " + lastName, email);
                            mConnectionsList.add(u);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //return;
                }
                Log.d("size of mConnectionsList", ""+ mConnectionsList.size());
                //return;
            }

        } catch (JSONException e) {
            mConnectionsList.clear();
            mAdapter.notifyDataSetChanged();
            e.printStackTrace();
            //return;
        }

    }

    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
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

    public interface OnFragmentInteractionListener {
        void onChatCreation(int chatid, String chatName);
    }
}
