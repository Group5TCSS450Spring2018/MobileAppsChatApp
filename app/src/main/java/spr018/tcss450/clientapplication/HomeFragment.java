package spr018.tcss450.clientapplication;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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
import spr018.tcss450.clientapplication.utility.ListenManager;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 100000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static final int UPDATE_REQUESTS = 60000;

    private static final int MY_PERMISSIONS_LOCATIONS = 814;
    private static final String HOME_LOCATION = "HOME LOCATION";
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
    private ImageView mImage;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private ListenManager mRequestListen;
    private ListenManager mWeatherListen;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        }
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
        mImage = v.findViewById(R.id.imageView);
        RecyclerView chats = v.findViewById(R.id.chatListContainer);
        mChatList = new ArrayList<>();
        mChatAdapter = new ChatPreviewAdapter(mChatList);
        chats.setAdapter(mChatAdapter);
        chats.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatAdapter.setOnItemClickListener(this::onChatClicked);
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


        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        getRecentChat();
        getRequests();
        super.onStart();
    }

    @Override
    public void onResume() {
        if (mWeatherListen != null) {
            mWeatherListen.startListening();
        }
        if (mRequestListen != null) {
            mRequestListen.startListening();
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        if (mWeatherListen != null) {
            mWeatherListen.stopListening();
        }
        if (mRequestListen != null) {
            mRequestListen.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    private void getCurrentWeather() {
        String coordinates = mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_currentWeather))
                .appendQueryParameter("location", coordinates)
                .build();
        mWeatherListen = new ListenManager.Builder(uri.toString(),
                this::handleHomeCurrentWeather)
                .setExceptionHandler(this::handleWeatherError)
                .setDelay((int) UPDATE_INTERVAL_IN_MILLISECONDS)
                .build();
    }

    private void handleHomeCurrentWeather(JSONObject resultJSON) {
        final String[] currentWeather;
        try {
            JSONArray arrayJ = resultJSON.getJSONArray("array");
            currentWeather = new String[arrayJ.length()];
            for (int i = 0; i < currentWeather.length; i++) {
                currentWeather[i] = arrayJ.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            mWeatherWidget.setText(currentWeather[0]);
            mLocationWidget.setText(currentWeather[1]);
            Bitmap icon = getIconBitmap(currentWeather[2]);
            mImage.setImageBitmap(icon);
        });


        /*
        get temp that is passed back and then setText of weatherTextview.*/
    }

    private void handleWeatherError(final Exception e) {
        Log.e("HOME WEATHER", e.getMessage());
    }


    //Get all requests from database and display.
    private void getRequests() {
        //send get connections the username.
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getConnectionRequests))
                .appendQueryParameter("username", mUsername)
                .build();

        mRequestListen = new ListenManager.Builder(uri.toString(),
                this::handleViewConnectionRequests)
                .setExceptionHandler(this::handleRequestError)
                .setDelay(UPDATE_REQUESTS)
                .build();
    }

    //Create a JSON object and get the connections requests to display.
    private void handleViewConnectionRequests(JSONObject resultJSON) {
        final Connection[] connections;
        try {
            JSONArray jContacts = resultJSON.getJSONArray("recieved_requests");
            connections = new Connection[jContacts.length()];
            for (int i = 0; i < jContacts.length(); i++) {
                JSONObject c = jContacts.getJSONObject(i);
                String username = c.getString("username");
                String firstName = c.getString("firstname");
                String lastName = c.getString("lastname");
                String email = c.getString("email");
                Connection connection = new Connection(username, firstName + " " + lastName, email);
                connections[i] = connection;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            mRequestList.clear();
            if (connections.length > 0) {
                Collections.addAll(mRequestList, connections);
            } else {
                mRequestList.add(null);
            }
            mRequestAdapter.notifyDataSetChanged();
        });
    }

    private void handleRequestError(final Exception e) {
        Log.e("HOME REQUEST", e.getMessage());
    }

    /**
     * Handle errors that may ouccur during the async taks.
     *
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
        try {
            msg.put("username", mUsername);
        } catch (JSONException e) {
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
            if (x.has("message")) {
                try {
                    JSONArray jContacts = x.getJSONArray("message");
                    if (jContacts.length() == 0) {
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
                        while (mChatList.size() > 10) {
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
        try {
            msg.put("username_a", mUsername);
            msg.put("username_b", connection.getUsername());
        } catch (JSONException e) {
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
        try {
            msg.put("username_a", mUsername);
            msg.put("username_b", connection.getUsername());
        } catch (JSONException e) {
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.

        if (mCurrentLocation == null) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                mCurrentLocation =
                        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {
                    Log.i(HOME_LOCATION, mCurrentLocation.toString());
                }
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.i(HOME_LOCATION, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(HOME_LOCATION, "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.i(HOME_LOCATION, location.getLatitude() + ", " + location.getLongitude());
        //Update the weather.
        getCurrentWeather();
        mWeatherListen.startListening();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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

    private Bitmap getIconBitmap(String icon) {
        Bitmap b;
        if (icon.equals("chanceflurries")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.chanceflurries);
        }
        if (icon.equals("chancerain")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.chancerain);
        }
        if (icon.equals("chancesleet")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.chancesleet);
        }
        if (icon.equals("chancesnow")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.chancesnow);
        }
        if (icon.equals("chancetstorms")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.chancetstorms);
        }
        if (icon.equals("clear")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
        }
        if (icon.equals("cloudy")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.cloudy);
        }
        if (icon.equals("flurries")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.flurries);
        }
        if (icon.equals("fog")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.fog);
        }
        if (icon.equals("hazy")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.mostlycloudy);
        }
        if (icon.equals("mostlysunny")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.mostlysunny);
        }
        if (icon.equals("mostlycloudy")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.mostlycloudy);
        }
        if (icon.equals("rain")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.rain);
        }
        if (icon.equals("sleet")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.sleet);
        }
        if (icon.equals("partlycloudy")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.partlycloudy);
        }
        if (icon.equals("partlysunny")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.partlysunny);
        }
        if (icon.equals("snow")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.snow);
        }
        if (icon.equals("sunny")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.sunny);
        }
        if (icon.equals("tstorms")) {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.tstorms);
        } else {
            b = BitmapFactory.decodeResource(getResources(), R.drawable.clear);
        }
        return b;
    }
}
