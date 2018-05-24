package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import spr018.tcss450.clientapplication.model.WeatherCollectionPagerAdapter;
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
    public WeatherFragment() {
        // Required empty public constructor
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
        Log.d("NEW LOCATION", newLocation);
        addTab(TabWeatherFragment.newInstance(newLocation, "Unsaved"), "Unsaved");

        ViewPager mViewPager = view.findViewById(R.id.weatherTabPager);
        mViewPager.setAdapter(mWeatherPagerAdapter);
        mWeatherPagerAdapter.notifyDataSetChanged();
        return view;
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
