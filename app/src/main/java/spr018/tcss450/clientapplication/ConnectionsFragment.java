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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;
import spr018.tcss450.clientapplication.model.Credentials;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
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


        mConnectionsList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Connection c = new Connection("Username " + i, "Name" + i);
            mConnectionsList.add(c);
        }
        checkConnections();

        ConnectionAdapter adapter = new ConnectionAdapter(mConnectionsList);
        RecyclerView connections = v.findViewById(R.id.connectionsListContainer);
        connections.setAdapter(adapter);
        connections.setLayoutManager(new LinearLayoutManager(getActivity()));
        v.findViewById(R.id.connectionsListContainer);

        return v;
    }
    private void checkConnections() {
        Uri uri = new Uri.Builder().scheme("https").
                appendPath(getString(R.string.ep_base_url)).
                appendPath(getString(R.string.ep_getConnections)).build();
        //JSONObject msg =
//        new SendPostAsyncTask.Builder(uri.toString(), msg).onPostExecute(this::handleViewConnections)
//                .onCancelled(this::handleErrorsInTask).build().execute();
    }
    private void handleViewConnections(String results) {
        Log.d("Results", "ss");
    }
    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onConnectionsInteraction(uri);
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
        void onConnectionsInteraction(Uri uri);
    }
}
