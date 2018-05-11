package spr018.tcss450.clientapplication;

import android.content.Context;
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
    Connection mConnections;

    public ConnectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections, container, false);


//        mConnectionsList = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            Connection c = new Connection("Username " + i, "Name" + i, i, "Email");
//            mConnectionsList.add(c);
//        }
        checkConnections();

        ConnectionAdapter adapter = new ConnectionAdapter(mConnectionsList);
        adapter.setOnItemClickListener(this::onItemClicked);
        RecyclerView connections = v.findViewById(R.id.connectionsListContainer);
        connections.setAdapter(adapter);
        connections.setLayoutManager(new LinearLayoutManager(getActivity()));
        v.findViewById(R.id.connectionsListContainer);

        return v;
    }

    private void checkConnections() {
        //send get connections the username.
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getConnections))
                .build();
        JSONObject msg = new JSONObject();
        try{
            msg.put(getString(R.string.keys_prefs_user_name), "Username");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleViewConnections)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
        Log.d("checkConnections", "inside");

    }

    private void handleViewConnections(String results) {
        try{
            JSONObject x = new JSONObject(results);
            if(x.has(getString(R.string.keys_json_contacts))) {
                try{
                    JSONArray jContacts = x.getJSONArray(getString(R.string.keys_json_contacts));
                    mConnectionsList = new ArrayList<>();
                    for(int i =0; i<jContacts.length(); i++){
                        JSONObject c = jContacts.getJSONObject(i);
                        String username = c.get("username").toString();
                        Connection u = new Connection("Username " + i, "Name" + i, i, "Email");
                        mConnectionsList.add(u);
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

    }
    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }


    private void onItemClicked(Connection connection) {
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
