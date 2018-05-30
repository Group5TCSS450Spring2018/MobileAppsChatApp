package spr018.tcss450.clientapplication;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.WeatherCollectionPagerAdapter;
import spr018.tcss450.clientapplication.utility.ListenManager;
import spr018.tcss450.clientapplication.utility.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private List<TabWeatherFragment> mWeatherTabs;
    private List<String> mTabNames;
    private WeatherCollectionPagerAdapter mWeatherPagerAdapter;
    private SharedPreferences mPrefs;
    private ListenManager mTabListener;
    public WeatherFragment() {
        // Required empty public constructor
    }
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        mWeatherTabs = new ArrayList<>();
        mTabNames = new ArrayList<>();
        mWeatherPagerAdapter =
                new WeatherCollectionPagerAdapter(getChildFragmentManager(), mWeatherTabs, mTabNames);
        mPrefs =
                Objects.requireNonNull(getActivity()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String mLocation = mPrefs.getString(getString(R.string.keys_prefs_coordinates), "");
        Log.d("LATLNG mPREFS", mLocation);
        addTab(TabWeatherFragment.newInstance(mLocation, "Current"), "Current");

        String newLocation = mPrefs.getString(getString(R.string.keys_prefs_NEWCOORDINATES),"");
        Log.e("WEATHER", "LOCATION USED FOR UNSAVED IS: " + newLocation);
        Log.d("NEW LOCATION", newLocation);
        addTab(TabWeatherFragment.newInstance(newLocation, "Unsaved"), "Unsaved");

        getSavedWeatherTabs();

        ViewPager mViewPager = view.findViewById(R.id.weatherTabPager);
        mViewPager.setAdapter(mWeatherPagerAdapter);
        //mViewPager.setOffscreenPageLimit(100);
        mWeatherPagerAdapter.notifyDataSetChanged();
        return view;
    }
    private void getSavedWeatherTabs(){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_getSavedWeather))
                .build();

        JSONObject msg = new JSONObject();
        try {
            String name = mPrefs.getString(getString(R.string.keys_prefs_user_name),"");
            msg.put("username", name);
        } catch (JSONException e){

        }
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleCreateTabs)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleCreateTabs(String results) {
        Log.d("ARRAYT", results);
        final String[] tabs;
        try {
            JSONObject r = new JSONObject(results);
            if(r.has("recieved_requests")){
                try{
                    JSONArray arrayT = r.getJSONArray("recieved_requests");
                    tabs = new String[arrayT.length()];
                    for (int i = 0; i < tabs.length; i++) {
                        tabs[i] = arrayT.getString(i);
                        String n = arrayT.get(i).toString().split(",")[0].split(":")[1];
                        String lat = arrayT.get(i).toString().split(",")[1].
                                split(":")[1].substring(1,arrayT.get(i).toString().split(",")[1].
                                split(":")[1].length()-1);
                        String lg = arrayT.get(i).toString().split(",")[2].
                                split(":")[1].substring(1,arrayT.get(i).toString().split(",")[1].
                                        split(":")[1].length()-1);

                        if(n.equals("null")){
                            //if zipcode is null
                            //user latitude and longitude concatinated

                            String latlng = lat+","+lg;
                            addTab(TabWeatherFragment.newInstance(latlng, "Saved"), "Saved");
                            mWeatherPagerAdapter.notifyDataSetChanged();

                        } else {
                            //send in zip code.
                            addTab(TabWeatherFragment.newInstance(arrayT.get(i).toString().
                                    split(",")[0].split(":")[1], "Saved"), "Saved");
                            mWeatherPagerAdapter.notifyDataSetChanged();

                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        mWeatherPagerAdapter.notifyDataSetChanged();

    }
    private void handleError(final Exception e) {
        Log.e("HOME WEATHER", e.getMessage());
    }



    /**Handle errors that may ouccur during the async taks.
     * @param result the error message provided from the async task
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
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

    private void addTab(TabWeatherFragment fragment, String name) {
        mWeatherTabs.add(fragment);
        mTabNames.add(name);
        mWeatherPagerAdapter.notifyDataSetChanged();
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
        //  TODO: Update argument type and name
        void onWeatherInteraction(Uri uri);
    }
}
