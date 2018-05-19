package spr018.tcss450.clientapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


public class TabWeatherFragment extends Fragment {

    public static final String TAB_NAME = "Tab name";

    private String mTabName;
    private TextView mWeatherWidget;
    private TextView mLocationWidget;
    public TabWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabName = getArguments().getString(TAB_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_weather, container, false);
        mLocationWidget = v.findViewById(R.id.locationText);
        mWeatherWidget = v.findViewById(R.id.currentWeather);
        getCurrentWeather();
        return v;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    private void getCurrentWeather() {
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
    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    public String getTabName() {
        return mTabName;
    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
