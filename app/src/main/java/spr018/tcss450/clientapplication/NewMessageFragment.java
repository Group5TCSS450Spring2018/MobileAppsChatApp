package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewMessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewMessageFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewMessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public NewMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_message, container, false);

        //TODO Remove this when there is a real list of connections
        List<Connection> bigList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Connection c = new Connection("Connection " + i);
            bigList.add(c);
        }

        ConnectionAdapter allAdapter = new ConnectionAdapter(bigList);
        RecyclerView allConnections = v.findViewById(R.id.newMessageConnectionsHolder);
        allConnections.setAdapter(allAdapter);
        allConnections.setLayoutManager(new LinearLayoutManager(getActivity()));

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        item = menu.findItem(R.id.search);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
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
        //mListener.onNewChatDetach(this);
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
        void onNewChatDetach(Fragment fragment);
    }
}
