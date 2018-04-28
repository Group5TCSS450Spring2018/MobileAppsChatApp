package spr018.tcss450.clientapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import spr018.tcss450.clientapplication.model.ConnectionViewItem;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ConnectionsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayList<ConnectionViewItem> mConnectionsList;

    public ConnectionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connections, container, false);

        RecyclerView connections = v.findViewById(R.id.connectionsListContainer);

        mConnectionsList = ConnectionViewItem.populateConnections(20    );

        ConnectionViewItemAdapter adapter = new ConnectionViewItemAdapter(mConnectionsList);

        connections.setAdapter(adapter);
        connections.setLayoutManager(new LinearLayoutManager(getActivity()));



        v.findViewById(R.id.connectionsListContainer);

        return v;
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

    private class ConnectionViewItemAdapter
            extends RecyclerView.Adapter<ConnectionViewItemAdapter.ViewHolder> {

        private ArrayList<ConnectionViewItem> mConnections;

        public ConnectionViewItemAdapter(ArrayList<ConnectionViewItem> list) {
            this.mConnections = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View connectionViewItem = inflater.inflate(R.layout.fragment_connections_list_item,
                    parent, false);


            ViewHolder viewHolder = new ViewHolder(connectionViewItem);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ConnectionViewItem connection = mConnections.get(position);

            // Set item views based on your views and data model
            TextView textView = holder.mConnectionName;
            textView.setText(connection.getName());
        }

        @Override
        public int getItemCount() {
            return mConnections.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mConnectionIcon;
            public TextView mConnectionName;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View view) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(view);

                mConnectionIcon = view.findViewById(R.id.connectionIcon);
                mConnectionName = view.findViewById(R.id.connectionName);
            }
        }
    }
}
