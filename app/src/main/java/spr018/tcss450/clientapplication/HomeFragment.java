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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import spr018.tcss450.clientapplication.model.ChatAdapter;
import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.RequestAdapter;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<Connection> mChatList;
    private ArrayList<Connection> mRequestList;
    private RequestAdapter mRequestAdapter;
    private Connection mConnection;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView chats = v.findViewById(R.id.chatListContainer);
        mChatList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            Connection c = new Connection("Username " + i, "Name" + i, "Email");
            c.setRecentMessage("Recent message");
            mChatList.add(c);
        }


        ChatAdapter adapter = new ChatAdapter(mChatList);
        chats.setAdapter(adapter);
        chats.setLayoutManager(new LinearLayoutManager(getActivity()));
        setHasOptionsMenu(true);

        RecyclerView requests = v.findViewById(R.id.RequestListContainer);
        mRequestList = new ArrayList<>();
        mRequestAdapter = new RequestAdapter(mRequestList);
        requests.setAdapter(mRequestAdapter);
        requests.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRequestAdapter.setOnItemClickListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void onAccept(Connection connection) {
                acceptRequest(connection);
            }

            @Override
            public void onDeny(Connection connection) {
                denyRequest(connection);
            }

            @Override
            public void onExpand(Connection connection) {
                expand(connection);
            }
        });
        getRequests();
        setHasOptionsMenu(true);
        return v;
    }



    //Get all requests from database and display.
    private void getRequests() {
        //send get connections the username.
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getConnectionRequests))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username", u);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleViewConnectionRequests)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    //Create a JSON object and get the connections requests to display.
    private void handleViewConnectionRequests(String results) {
        try {
            mRequestList.clear();
            JSONObject x = new JSONObject(results);
            if(x.has("recieved_requests")) {
                try {
                    JSONArray jContacts = x.getJSONArray("recieved_requests");
                    if(jContacts.length()==0){
                        mRequestList.add(null);
                    } else {
                        for (int i = 0; i < jContacts.length(); i++) {
                            JSONObject c = jContacts.getJSONObject(i);
                            String username = c.get("username").toString();
                            String firstName = c.get("firstname").toString();
                            String lastName = c.get("lastname").toString();
                            String email= c.getString("email");
                            Connection u = new Connection(username, firstName + " " + lastName, email);
                            mRequestList.add(u);
                        }
                    }
                    mRequestAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRequestAdapter.notifyDataSetChanged();
    }

    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    private void acceptRequest(Connection connection) {
        mConnection = connection;
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
            msg.put("username_b", connection.getUsername());
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleAcceptDenyPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void denyRequest(Connection connection) {
        mConnection = connection;
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String u = prefs.getString(getString(R.string.keys_prefs_user_name), "");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_denyConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username_a", u);
            msg.put("username_b", connection.getUsername());
        } catch(JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleAcceptDenyPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleAcceptDenyPost(String result) {
        try {
            JSONObject resultJSON = new JSONObject(result);
            boolean success = resultJSON.getBoolean("success");
            if (success) {
                mRequestList.remove(mConnection);
                if (mRequestList.isEmpty()) {
                    mRequestList.add(null);
                }
                mRequestAdapter.notifyDataSetChanged();
            } else {
                Log.e("JSONOBJECT", result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void expand(Connection connection) {
        mListener.onExpandingRequestAttempt(connection);
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
        void onExpandingRequestAttempt(Connection connection);
    }


}
