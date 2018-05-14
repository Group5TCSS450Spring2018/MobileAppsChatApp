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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;
import spr018.tcss450.clientapplication.model.Credentials;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class ConnectionsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private List<Connection> mConnectionsList;
    private ConnectionAdapter adapter;
    public ConnectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections, container, false);


        mConnectionsList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            Connection c = new Connection("Username " + i, "Name" + i, "Email");
//            mConnectionsList.add(c);
//        }
        adapter = new ConnectionAdapter(mConnectionsList);
        checkConnections();
        Log.d("mConnectionsList size", ""+mConnectionsList.size());



        RecyclerView connections = (RecyclerView) v.findViewById(R.id.connectionsListContainer);

        connections.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setOnItemClickListener(this::onItemClicked);
        connections.setAdapter(adapter);
        //v.findViewById(R.id.connectionsListContainer);

        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        checkConnections();
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
        adapter.notifyDataSetChanged();
        mConnectionsList.clear();
        Log.d("viewConnections", results);
        try {
            JSONObject x = new JSONObject(results);
//            Log.d("handleViewConnections", x.toString());
            if(x.has("recieved_requests")) {
                try {
                    JSONArray jContacts = x.getJSONArray("recieved_requests");
//                    Log.d("display the contacts", "length is: " + jContacts.length());
                    if(jContacts.length() == 0) {
                        mConnectionsList.add(new Connection("No Contacts", "", ""));
                    } else {
                        for (int i = 0; i < jContacts.length(); i++) {
                            JSONObject c = jContacts.getJSONObject(i);
                            String username = c.get("username").toString();
                            String firstname = c.get("firstname").toString();
                            String lastname = c.get("lastname").toString();
                            //String email = c.get("email").toString();
                            Connection u = new Connection(username, firstname+" "+lastname, "");
                            mConnectionsList.add(u);
                            Log.d("CONNECTIONSFRAG", username);
                        }
                    }//return;
                } catch (JSONException e) {
                    e.printStackTrace();
                    //return;
                }
                Log.d("size of mConnectionsList", ""+ mConnectionsList.size());
                //return;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //return;
        }
        adapter.notifyDataSetChanged();
        return;
    }
    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }


    private void onItemClicked(Connection connection) {
        adapter.notifyDataSetChanged();
        mListener.onFriendConnectionClicked(connection);
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
        void onFriendConnectionClicked(Connection connection);
    }
}
