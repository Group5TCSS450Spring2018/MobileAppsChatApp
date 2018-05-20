package spr018.tcss450.clientapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;

import spr018.tcss450.clientapplication.model.Connection;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


public class TabWeatherFragment extends Fragment {

    public static final String TAB_NAME = "Tab name";

    private String mTabName;
    private TextView mWeatherWidget;
    private TextView mLocationWidget;
    private LinearLayout m24HoursWidget;
    private LinearLayout m10DayWidget;
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
        m24HoursWidget = v.findViewById(R.id.horizontal);
        m10DayWidget = v.findViewById(R.id.tenforecast);
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
                            TextView tempreture = new TextView(getContext());

                            String[] dates = date.get(i).toString().split("on");
                            tempreture.setText(dates[1]+ " \n \t\tHigh: " +temp.get(i).toString()+"F");

                            tempreture.setTextSize(30);
                            m10DayWidget.addView(tempreture);


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
            JSONObject R = new JSONObject(results);
            if(R.has("timearray") &&R.has("temparray")) {
                Log.d("TAB WEATHER FRAG", "has.");
                try {
                    JSONArray time = R.getJSONArray("timearray");
                    JSONArray temp = R.getJSONArray("temparray");
                    if(time.length() == 0 && temp.length() == 0) {

                    } else {
                        for (int i = 0; i < time.length(); i++) {
                            TextView hourly = new TextView(getContext());
                            TextView tempreture = new TextView(getContext());
                            //hourly.setText(time.get(i).toString());
                            tempreture.setText(temp.get(i).toString()+ "F \n" + time.get(i).toString());
                            //hourly.setTextSize(24);
                            tempreture.setTextSize(30);
                            m24HoursWidget.addView(tempreture);
                            m24HoursWidget.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                            m24HoursWidget.getShowDividers();
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

    public String getTabName() {
        return mTabName;
    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
