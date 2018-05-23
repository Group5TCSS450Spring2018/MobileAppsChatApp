package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


public class TabWeatherFragment extends Fragment {

    public static final String LOCATION = "Location";
    public static final String CURRENTORSAVED = "c";
    private String mCurrentorSaved;
    private TextView mWeatherWidget;
    private TextView mLocationWidget;
    private LinearLayout m24HoursWidget;
    private LinearLayout m10DayWidget;
    private ImageView mImage;
    private String mLocation;
    private ImageButton mReload;
    private SharedPreferences mPrefs;


    public TabWeatherFragment() {
        // Required empty public constructor
    }

    //Static method to create a new fragment with the specified parameters,
    //Can pass anything here, JSON, String, Coordinate.
    public static TabWeatherFragment newInstance(String location, String current) {
        TabWeatherFragment fragment = new TabWeatherFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION, location);
        args.putString(CURRENTORSAVED, current);
        Log.d("LATLNG NEW INSTANCE", location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = mPrefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (getArguments() != null) {
            mCurrentorSaved = getArguments().getString(CURRENTORSAVED);
            if(mCurrentorSaved.equals("Current")){
                mLocation = mPrefs.getString(getString(R.string.keys_prefs_coordinates), "");

            } else{
                mLocation = getArguments().getString(LOCATION);
            }


            Log.d("LATLNG TAB WEATHER", mLocation);
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

        mImage = v.findViewById(R.id.imageView2);
        mReload = v.findViewById(R.id.reloadButton);
        mReload.setOnClickListener(this::Reload);
        getCurrentWeather();
        getHourlyWeather();
        get10DayWeather();
        return v;
    }

    private void Reload(View v){
        getCurrentWeather();
        getHourlyWeather();
        get10DayWeather();
    }


    private void getCurrentWeather() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_currentWeather))
                .build();

        JSONObject msg = new JSONObject();
        try{
                msg.put("location", mLocation);
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
        try {
            JSONObject res = new JSONObject(results);
            if (res.has("array")) {
                Log.d("TAB WEATHER FRAG", "has.");
                try {
                    JSONArray arrayJ = res.getJSONArray("array");
                    if (arrayJ.length() == 0 ) {

                    } else {
                        mWeatherWidget.setText(arrayJ.get(0).toString());
                        mLocationWidget.setText(arrayJ.get(1).toString());


                        mImage.setImageBitmap(getIconBitmap(arrayJ.get(2).toString()));
                    }
                } catch (JSONException e) {

                }

            }

        } catch (JSONException e){

        }
    }

    private void get10DayWeather(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_forecast10Weather))
                .build();

        JSONObject msg = new JSONObject();
        try{


            msg.put("location", mLocation);

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
                    JSONArray icons = R.getJSONArray(("iconarray"));
                    if (date.length() == 0 && temp.length() == 0) {

                    } else {
                        for (int i = 0; i < date.length(); i++) {
                            LinearLayout horizontalHolder = new LinearLayout(getActivity());
                            horizontalHolder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            horizontalHolder.setOrientation(LinearLayout.HORIZONTAL);

                            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            textLayout.setMargins(10,10,10,10);

                            TextView temperature = new TextView(getContext());
                            String[] dates = date.get(i).toString().split("on");
                            temperature.setText(dates[1]+ " \n \t\tHigh: " +temp.get(i).toString()+"F");
                            temperature.setLayoutParams(textLayout);
                            ImageButton img = new ImageButton(getContext());
                            img.setBackgroundColor(Color.TRANSPARENT);
                            img.setImageBitmap(getIconBitmap(icons.get(i).toString()));
//                            img.setScaleY(SIZE/2);
//                            img.setScaleX(SIZE/2);
                            horizontalHolder.addView(img);
                            temperature.setTextSize(15);
                            horizontalHolder.addView(temperature);
                            m10DayWidget.addView(horizontalHolder);

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

            msg.put("location", mLocation);

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
                    JSONArray icons = resultJSON.getJSONArray("iconarray");
                    if(time.length() == 0 && temp.length() == 0) {

                    } else {
                        for (int i = 0; i < time.length(); i++) {
                            LinearLayout verticalHolder = new LinearLayout(getActivity());
                            verticalHolder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            verticalHolder.setOrientation(LinearLayout.VERTICAL);

                            //Margins for the TextView.
                            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            textLayout.setMargins(10,10,10,10);

                            TextView hour = new TextView(getActivity());
                            hour.setText(time.get(i).toString());
                            hour.setTextSize(15);
                            hour.setLayoutParams(textLayout);

                            TextView temperature = new TextView(getActivity());
                            temperature.setText(temp.get(i).toString());
                            temperature.setTextSize(15);
                            temperature.setLayoutParams(textLayout);

                            ImageButton img = new ImageButton(getContext());
                            img.setBackgroundColor(Color.TRANSPARENT);
                            img.setImageBitmap(getIconBitmap(icons.get(i).toString()));
//                            img.setScaleY(SIZE);
//                            img.setScaleX(SIZE);
                            verticalHolder.addView(img);
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


    private Bitmap getIconBitmap(String icon) {
        Bitmap b;
        if(icon.equals("chanceflurries")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.chanceflurries);
        } if(icon.equals("chancerain")) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.chancerain);
        } if(icon.equals("chancesleet")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.chancesleet);
        } if(icon.equals("chancesnow")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.chancesnow);
        } if(icon.equals("chancetstorms")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.chancetstorms);
        } if(icon.equals("clear")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.clear);
        } if(icon.equals("cloudy")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.cloudy);
        } if(icon.equals("flurries")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.flurries);
        } if(icon.equals("fog")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.fog);
        } if(icon.equals("hazy")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.mostlycloudy);
        } if(icon.equals("mostlysunny")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.mostlysunny);
        } if(icon.equals("mostlycloudy")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.mostlycloudy);
        } if(icon.equals("rain")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.rain);
        } if(icon.equals("sleet")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.sleet);
        } if(icon.equals("partlycloudy")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.partlycloudy);
        } if(icon.equals("partlysunny")){
            return  BitmapFactory.decodeResource(getResources(), R.drawable.partlysunny);
        } if(icon.equals("snow")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.snow);
        } if(icon.equals("sunny")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.sunny);
        }  if(icon.equals("tstorms")){
            return BitmapFactory.decodeResource(getResources(), R.drawable.tstorms);
        } else {
            return BitmapFactory.decodeResource(getResources(), R.drawable.clear);
        }
        //return b;
    }
}
