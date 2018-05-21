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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.Chat;
import spr018.tcss450.clientapplication.model.ChatPreviewAdapter;
import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.model.RequestAdapter;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private ArrayList<Chat> mChatList;
    private ArrayList<Connection> mRequestList;
    private RequestAdapter mRequestAdapter;
    private ChatPreviewAdapter mChatAdapter;
    private Connection mConnection;
    private String mUsername;
    private SharedPreferences mPrefs;
    private TextView mWeatherWidget;
    private TextView mLocationWidget;
    private static final String TAG = "MyHomeFragment";
    private String mCurrentLocation;





    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        mWeatherWidget = v.findViewById(R.id.weatherText);
        mLocationWidget = v.findViewById(R.id.weatherTabCurrentLocation);
        mPrefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mUsername = mPrefs.getString(getString(R.string.keys_prefs_user_name), "");

        RecyclerView chats = v.findViewById(R.id.chatListContainer);
        mChatList = new ArrayList<>();
        mChatAdapter = new ChatPreviewAdapter(mChatList);
        chats.setAdapter(mChatAdapter);
        chats.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatAdapter.setOnItemClickListener(this::onChatClicked);
        if(getArguments()!=null){
//            mCurrentLocation = getArguments().getString("latlng");
//            Log.d("MAIN", "latlng"+ mCurrentLocation);
        }
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



        getCurrentWeather();
        getRecentChat();
        getRequests();
        setHasOptionsMenu(true);
        return v;
    }
    private void getCurrentWeather(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_currentWeather))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("location", "98031");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleHomeCurrentWeather)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }
    private void handleHomeCurrentWeather(String results){
        Log.d("CURRENT", results);
        String[] weather = results.split(":");
        Log.d("CURRENT", ""+ weather[1].split(",")[0]);
        mWeatherWidget.setText(weather[1].split(",")[0]+ "F");

        mLocationWidget.setText(weather[2].substring(1, weather[2].length()-2));


        /*
        get temp that is passed back and then setText of weatherTextview.*/
    }

    //Get all requests from database and display.
    private void getRequests() {
        //send get connections the username.

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getConnectionRequests))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username", mUsername);
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





    private void getRecentChat() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getRecentChats))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username", mUsername);
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRecentChats)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    //Create a JSON object and get the connections requests to display.
    private void handleRecentChats(String results) {
        try {
            mChatList.clear();
            JSONObject x = new JSONObject(results);
            if(x.has("message")) {
                try {
                    JSONArray jContacts = x.getJSONArray("message");
                    if(jContacts.length()==0){
                        mChatList.add(null);
                    } else {
                        HashMap<Integer, Chat> recentMessages = new HashMap<>();
                        for (int i = jContacts.length() - 1; i >= 0; i--) {
                            JSONObject c = jContacts.getJSONObject(i);
                            Chat chat = new Chat(c.getString("name"),
                                    c.getString("message"),
                                    c.getString("timestamp"),
                                    c.getInt("chatid"),
                                    c.getString("username"));
                            recentMessages.put(chat.getChatID(), chat);
                        }

                        for (int key : recentMessages.keySet()) {
                            mChatList.add(recentMessages.get(key));
                        }
                        Collections.sort(mChatList);
                        // ensure that there is never more than 10 recent chats displayed
                        while(mChatList.size() > 10) {
                            mChatList.remove(mChatList.size() - 1);
                        }

                    }
                    mChatAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mChatAdapter.notifyDataSetChanged();
    }


    private void acceptRequest(Connection connection) {
        mConnection = connection;

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_acceptConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username_a", mUsername);
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

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_denyConnection))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("username_a", mUsername);
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

    private void onChatClicked(Chat chat) {
        mListener.onOpenChat(mUsername, chat.getChatID(), chat.getName());
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
        void onOpenChat(String username, int chatID, String chatname);
        void onExpandingRequestAttempt(Connection connection);
    }





}
