package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;


/**
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class ConnectionsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private List<Connection> mConnectionsList;

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
            Connection c = new Connection("Username " + i, "Name" + i, "Email");
            mConnectionsList.add(c);
        }
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
