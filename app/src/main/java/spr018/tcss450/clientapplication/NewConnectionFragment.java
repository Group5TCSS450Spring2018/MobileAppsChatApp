package spr018.tcss450.clientapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.ConnectionAdapter;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * @author Deepjot Kaur
 * @author Daryan Hanshew
 * @author Tenma Rollins
 * @author Tuan Dinh
 */
public class NewConnectionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SearchView mSearchView;
    private List<Connection> mConnections;
    private ConnectionAdapter mAdapter;

    public NewConnectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_connection, container, false);

        CardView cardView = v.findViewById(R.id.newConnectionCardView);
        cardView.setOnClickListener(this::onCardViewClicked);

        v.findViewById(R.id.newConnectionRecyclerViewContainer).setOnClickListener(this::onContainerClicked);

        mConnections = new ArrayList<>();
        mAdapter = new ConnectionAdapter(mConnections);
        mAdapter.setOnItemClickListener(this::onItemClicked);

        RecyclerView recyclerView = v.findViewById(R.id.newConnectionRecylerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSearchView = v.findViewById(R.id.newConnectionSearchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handleQuery(newText);
                return false;
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewMessageFragment.OnFragmentInteractionListener) {
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


    //Helper methods

    //Open the search when user clicked on the container.
    private void onCardViewClicked(View cardView) {
        mSearchView.setIconified(false);
    }


    //Close the search when user clicked on the bottom container.
    private void onContainerClicked(View container) {
        Log.d("CONTAINER", "CLICKED");
        mSearchView.setIconified(true);
    }

    private void onItemClicked(Connection connection) {
        mSearchView.setIconified(true);
        mListener.onSearchedConnectionClicked(connection);
    }

    private void handleQuery(String input) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search_connection))
                .build();
        JSONObject msg = new JSONObject();
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.keys_prefs_user_name), "");
        //Only search for the first word of the input.
        String trimmedInput = input.split(" ")[0];
        try {
            msg.put("search", trimmedInput);
            msg.put("username", username);
        } catch (JSONException e) {
            Log.e("JSON", e.toString());
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleSearchPost)
                .onCancelled(this::handleSearchError)
                .build()
                .execute();
    }


    private void handleSearchPost(String result) {
        try {
            JSONObject objectJSON = new JSONObject(result);
            Boolean success = objectJSON.getBoolean("success");
            JSONArray arrayJSON = objectJSON.getJSONArray("message");
            if (success) {
                mConnections.clear();
                for (int i = 0; i < arrayJSON.length(); i++ ) {
                    JSONObject connectionJSON = arrayJSON.getJSONObject(i);
                    String fullName = connectionJSON.getString("firstname") + " " + connectionJSON.getString("lastname");
                    String email = connectionJSON.getString("email");
                    Connection c = new Connection(connectionJSON.getString("username"), fullName, email);
                    mConnections.add(c);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                String errorMessage = arrayJSON.getString(0);
                Log.e("JSON", errorMessage);
            }

        } catch (JSONException e) {
            mConnections.clear();
            mAdapter.notifyDataSetChanged();
            Log.e("JSON_PARSE_ERROR", result + System.lineSeparator() + e.getMessage());
        }
    }

    private void handleSearchError(String result) {
        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), getString(R.string.toast_server_down), Toast.LENGTH_LONG).show();
        Log.e("ASYNCT_TASK_ERROR", result);
    }


    public interface OnFragmentInteractionListener {
        void onSearchedConnectionClicked(Connection connection);
    }

}
