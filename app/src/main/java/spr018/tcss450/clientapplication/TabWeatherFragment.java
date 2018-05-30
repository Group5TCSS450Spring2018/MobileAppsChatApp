package spr018.tcss450.clientapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import spr018.tcss450.clientapplication.model.WeatherCollectionPagerAdapter;
import spr018.tcss450.clientapplication.utility.ListenManager;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


public class TabWeatherFragment extends Fragment {

    public static final String LOCATION = "Location";
    public static final String CURRENTORSAVED = "c";
    private String mCurrentorSaved;
    private TextView mWeatherWidget;
    private TextView mLocationWidget;
    private LinearLayout m24HoursWidget;
    private LinearLayout mVerticalHolder;
    private ImageView mImage;
    private String mLocation;
    private Button mSave;
    private SharedPreferences mPrefs;
    private ListenManager mWeatherListen;
    private WeatherCollectionPagerAdapter mWeatherPagerAdapter;


    public TabWeatherFragment() {
        // Required empty public constructor
    }

    //Static method to create a new fragment with the specified parameters,
    //Can pass anything here, JSON, String, Coordinate.
    public static TabWeatherFragment newInstance(String location, String current) {
        TabWeatherFragment fragment = new TabWeatherFragment();

        Log.d("TABWEATHERFRAG", location+" "+current);
        Bundle args = new Bundle();
        args.putString(LOCATION, location);
        args.putString(CURRENTORSAVED, current);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (getArguments() != null) {
            mCurrentorSaved = getArguments().getString(CURRENTORSAVED);
            if(mCurrentorSaved.equals("Current")){
                mLocation = mPrefs.getString(getString(R.string.keys_prefs_coordinates), "");

            } else{
                mLocation = getArguments().getString(LOCATION);
            }
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
        mVerticalHolder = v.findViewById(R.id.weatherTabVerticalHolder);
        mImage = v.findViewById(R.id.imageView2);
        mSave = v.findViewById(R.id.saveButton);
        mSave.setOnClickListener(this::save);
        if(mCurrentorSaved == "Saved") {
            mSave.setEnabled(false);
        } else {
            mSave.setEnabled(true);
        }



        getCurrentWeather();
        getHourlyWeather();
        get10DayWeather();
        return v;
    }

    private void save(View v){
        //send username and mLocation to the database.
        //get out in onCreateView weather fragment.
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_saveWeather))
                .build();

        JSONObject msg = new JSONObject();
        try{
            String name = mPrefs.getString(getString(R.string.keys_prefs_user_name),"");
            if(mLocation.contains(",")){
                Log.d("long", mLocation.split(",")[1]);
                msg.put("username", name);
                msg.put("lng", mLocation.split(",")[1]);
                msg.put("lat", mLocation.split(",")[0]);

            } else {
                msg.put("username", name);
                msg.put("zip", mLocation);
            }
            //Log.d("lat", mLocation.split(",")[0]);



        } catch (JSONException e){

        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleSave)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleSave(String results){
        Log.d("RESULTS", results);
        try {
            JSONObject resultJSON = new JSONObject(results);
            boolean success = resultJSON.getBoolean("success");
            Log.d("HANDLE SAVE", ""+success);
            if (success) {
                //alert dialog here.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("This location has been saved.");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                //alert dialog here.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("This location was unable to be saved, please try again..");
                builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", results
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }


    private void getCurrentWeather() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_currentWeather))
                .appendQueryParameter("location", mLocation)
                .build();
        mWeatherListen = new ListenManager.Builder(uri.toString(),
                this::handleHomeCurrentWeather)
                .setExceptionHandler(this::handleWeatherError)
                .setDelay((int) HomeFragment.UPDATE_INTERVAL_IN_MILLISECONDS)
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
            String temperature = currentWeather[0] + (char) 0x00B0 + "F";
            mWeatherWidget.setText(temperature);
            mLocationWidget.setText(currentWeather[1]);
            Bitmap icon = getIconBitmap(currentWeather[2]);
            mImage.setImageBitmap(icon);
        });
    }

    private void handleWeatherError(final Exception e) {
        Log.e("HOME WEATHER", e.getMessage());
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
    private void handle10DayWeather(String result) {
        try {
            JSONObject resultJSON = new JSONObject(result);
            if (resultJSON.has("datearray")&& resultJSON.has("temparray")) {
                try {
                    JSONArray dates = resultJSON.getJSONArray("datearray");
                    JSONArray highTemperatures = resultJSON.getJSONArray("temparray");
                    JSONArray icons = resultJSON.getJSONArray(("iconarray"));
                    JSONArray lowTemperatures = resultJSON.getJSONArray("LOWtemparray");
                    if (dates.length() == 0 && highTemperatures.length() == 0) {
                        Log.e("10 DAY WEATHER", "Empty");
                    } else {
                        for (int i = 0; i < dates.length(); i++) {
                            LinearLayout.LayoutParams textLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            textLayout.setMargins(10,10,10,10);

                            String date = dates.get(i).toString().split("on")[1];
                            TextView dateText = new TextView(getContext());
                            dateText.setText(date);
                            dateText.setLayoutParams(textLayout);
                            dateText.setTextSize(20);
                            dateText.setId(View.generateViewId());

                            TextView highText = new TextView(getContext());
                            String high = "H:"+highTemperatures.get(i).toString() + (char) 0x00B0 + "F";
                            highText.setText(high);
                            highText.setLayoutParams(textLayout);
                            highText.setTextSize(18);
                            highText.setId(View.generateViewId());

                            TextView lowText = new TextView(getActivity());
                            String low = "L:"+lowTemperatures.get(i).toString() + (char) 0x00B0 + "F";
                            lowText.setText(low);
                            lowText.setTextSize(18);
                            lowText.setLayoutParams(textLayout);
                            lowText.setId(View.generateViewId());

                            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(200, 200);
                            ImageView img = new ImageView(getContext());
                            img.setBackgroundColor(Color.TRANSPARENT);
                            img.setImageBitmap(getIconBitmap(icons.get(i).toString()));
                            img.setLayoutParams(imageParams);
                            img.setId(View.generateViewId());

                            ConstraintLayout constraintText = new ConstraintLayout(getActivity());
                            constraintText.setId(View.generateViewId());
                            constraintText.addView(highText);
                            constraintText.addView(lowText);
                            ConstraintSet constraintSet = new ConstraintSet();
                            constraintSet.clone(constraintText);
                            constraintSet.createVerticalChain(ConstraintSet.PARENT_ID, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                                    new int[]{highText.getId(), lowText.getId()}, null, ConstraintSet.CHAIN_PACKED);
                            constraintSet.connect(highText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                            constraintSet.connect(highText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                            constraintSet.connect(lowText.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                            constraintSet.connect(highText.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                            constraintSet.applyTo(constraintText);

                            ConstraintLayout constraintHolder = new ConstraintLayout(getActivity());
                            constraintHolder.setId(View.generateViewId());
                            constraintHolder.addView(img);
                            constraintHolder.addView(dateText);
                            constraintHolder.addView(constraintText);
                            constraintSet = new ConstraintSet();
                            constraintSet.clone(constraintHolder);
                            constraintSet.connect(img.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                            constraintSet.connect(img.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                            constraintSet.connect(dateText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                            constraintSet.connect(dateText.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
                            constraintSet.connect(constraintText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                            constraintSet.connect(constraintText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
                            constraintSet.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                                    ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, new int[] {img.getId(), dateText.getId(), constraintText.getId()},
                                    null, ConstraintSet.CHAIN_SPREAD);
                            constraintSet.applyTo(constraintHolder);

                            mVerticalHolder.addView(constraintHolder);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("10 DAY WEATHER", e.getMessage());
                }
            }
        } catch (JSONException e){
            Log.e("10 DAY WEATHER", e.getMessage());
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
        try{
            JSONObject resultJSON = new JSONObject(results);
            if(resultJSON.has("timearray") &&resultJSON.has("temparray")) {
                try {
                    JSONArray time = resultJSON.getJSONArray("timearray");
                    JSONArray temp = resultJSON.getJSONArray("temparray");
                    JSONArray icons = resultJSON.getJSONArray("iconarray");
                    if(time.length() == 0 && temp.length() == 0) {
                        Log.e("HOURLY WEATHER", "EMPTY");
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
                            hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            TextView temperature = new TextView(getActivity());
                            String s = temp.getString(i) + (char) 0x00B0 + "F";
                            temperature.setText(s);
                            temperature.setTextSize(15);
                            temperature.setLayoutParams(textLayout);
                            temperature.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(200, 200);
                            ImageView img = new ImageView(getContext());
                            img.setBackgroundColor(Color.TRANSPARENT);
                            img.setImageBitmap(getIconBitmap(icons.get(i).toString()));
                            img.setLayoutParams(imageParams);


                            verticalHolder.addView(img);
                            verticalHolder.addView(hour);
                            verticalHolder.addView(temperature);

                            m24HoursWidget.addView(verticalHolder);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("HOURLY WEATHER", e.getMessage());
                }
            }

        } catch (JSONException e) {
            Log.e("HOURLY WEATHER", e.getMessage());
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
            return BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
        }
        //return b;
    }

    @Override
    public void onStart() {
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }

        super.onStart();
    }

    @Override
    public void onResume() {
        if (mWeatherListen != null) {
            mWeatherListen.startListening();
        }
//        if (mRequestListen != null) {
//            mRequestListen.startListening();
//        }
        super.onResume();
    }

    @Override
    public void onStop() {
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.disconnect();
//        }
        if (mWeatherListen != null) {
            mWeatherListen.stopListening();
        }
//        if (mRequestListen != null) {
//            mRequestListen.stopListening();
//        }
        super.onStop();
    }
}
