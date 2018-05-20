package spr018.tcss450.clientapplication;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


public class TabWeatherFragment extends Fragment {

    public static final String LOCATION = "Location";
    private TextView mWeatherWidget;
    private TextView mLocationWidget;
    private LinearLayout m24HoursWidget;
    private LinearLayout m10DayWidget;
    private String mLocation;

    public TabWeatherFragment() {
        // Required empty public constructor
    }

    //Static method to create a new fragment with the specified parameters,
    //Can pass anything here, JSON, String, Coordinate.
    public static TabWeatherFragment newInstance(String location) {
        TabWeatherFragment fragment = new TabWeatherFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLocation = getArguments().getString(LOCATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab_weather, container, false);
        mLocationWidget = v.findViewById(R.id.weatherTabCurrentLocation);
        mWeatherWidget = v.findViewById(R.id.weatherTabCurrentTemp);
        m24HoursWidget = v.findViewById(R.id.weatherTabHourlyContainer);
        m10DayWidget = v.findViewById(R.id.weatherTabDailyContainer);
        getCurrentWeather();
        getHourlyWeather();
        get10DayWeather();
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
                .onPostExecute(this::handleCurrentWeather)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }
    private void handleCurrentWeather(String results){
        Log.d("CURRENT", results);
        String[] weather = results.split(":");
        Log.d("CURRENT", ""+ weather[1].split(",")[0]);
        mWeatherWidget.setText(weather[1].split(",")[0]+ "F");

        mLocationWidget.setText(weather[2].substring(1, weather[2].length()-2));


        /*
        get temp that is passed back and then setText of weatherTextview.*/
    }

    private void get10DayWeather(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_forecast10Weather))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("location", "98031");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAB WEATHER", "GET10 DAY");
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handle10DayWeather)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }
    private void handle10DayWeather(String r) {
        Log.d("TAB WEATHER", "10 days" + r);
        try {
            JSONObject R = new JSONObject(r);
            if (R.has("datearray")&& R.has("temparray")) {
                Log.d("TAB WEATHER FRAG", "has.");
                try {
                    JSONArray date = R.getJSONArray("datearray");
                    JSONArray temp = R.getJSONArray("temparray");
                    if (date.length() == 0 && temp.length() == 0) {

                    } else {
                        for (int i = 0; i < date.length(); i++) {
                            TextView temperature = new TextView(getContext());
                            String[] dates = date.get(i).toString().split("on");
                            temperature.setText(dates[1]+ " \n \t\tHigh: " +temp.get(i).toString()+"F");

                            temperature.setTextSize(30);
                            m10DayWidget.addView(temperature);


                        }


                    }
                } catch (JSONException e) {

                }

            }

        } catch (JSONException e){

        }
    }
    private void getHourlyWeather() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_hourlyWeather))
                .build();

        JSONObject msg = new JSONObject();
        try{
            msg.put("location", "98031");
        } catch(JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleHourlyWeather)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }
    private void handleHourlyWeather(String results){
        Log.d("TAB WEATHER FRAG",results); //displays in console. timearray and temparray
        try{
            JSONObject resultJSON = new JSONObject(results);
            if(resultJSON.has("timearray") &&resultJSON.has("temparray")) {
                Log.d("TAB WEATHER FRAG", "has.");
                try {
                    JSONArray time = resultJSON.getJSONArray("timearray");
                    JSONArray temp = resultJSON.getJSONArray("temparray");
                    if(time.length() == 0 && temp.length() == 0) {

                    } else {
                        for (int i = 0; i < time.length(); i++) {

                            LinearLayout verticalHolder = new LinearLayout(getActivity());
                            verticalHolder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            verticalHolder.setOrientation(LinearLayout.VERTICAL);

                            //Margins for the TextView.
                            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            textLayout.setMargins(10,10,10,10);

                            TextView hour = new TextView(getActivity());
                            hour.setText(time.get(i).toString());
                            hour.setTextSize(15);
                            hour.setLayoutParams(textLayout);

                            TextView temperature = new TextView(getActivity());
                            temperature.setText(temp.get(i).toString());
                            temperature.setTextSize(15);
                            temperature.setLayoutParams(textLayout);

                            verticalHolder.addView(hour);
                            verticalHolder.addView(temperature);
                            m24HoursWidget.addView(verticalHolder);
                        }



                    }
                } catch (JSONException e) {

                }
            }

        } catch (JSONException e) {

        }
    }

    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
