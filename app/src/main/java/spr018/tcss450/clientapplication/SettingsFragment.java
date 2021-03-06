package spr018.tcss450.clientapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

/**
 *  Allows user to modify settings of look and feel of application.
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private OnFragmentInteractionListener mListener;
    private boolean firstOpen;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     *  Initializes the view with the information.
     * @param inflater - layout to handle XML code.
     * @param container - contains all XML code.
     * @param savedInstanceState - grabs previous information from past activities.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        firstOpen = true;
        Log.e("SETTINGS", "CREATE VIEW");
        Switch stayLogged = v.findViewById(R.id.stayLoggedInSwitch);
        stayLogged.setOnClickListener(this::onStayLoggedInToggle);
        stayLogged.setChecked(getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.keys_prefs_stay_logged_in), false));


        Spinner s = v.findViewById(R.id.themeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.settings_themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);
        s.setSelection(getActivity()
                .getSharedPreferences(
                    getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE)
                .getInt(
                    getString(R.string.keys_prefs_current_theme_pos), 0));

        return v;
    }

    /**
     *  Checks if user checked to stay logged in.
     * @param v - the view of  fragment.
     */
    private void onStayLoggedInToggle(View v) {
        if (mListener != null) {
            mListener.settings_ToggleStayLoggedIn((Switch) v);
        }
    }

    /**
     *  Attaches fragment to current state..
     * @param context the current state of the app.
     */
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

    /**
     *  Overrides on pause for the fragment.
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     *  Detaches the fragment from application.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *  Checks the item clicked by a user.
     * @param parent - the changing view of the parent.
     * @param view - current view of the page.
     * @param position - position inside the list.
     * @param id - id of the selected item.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!firstOpen) {
            String theme = (String) parent.getAdapter().getItem(position);

            getActivity().getSharedPreferences(
                    getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE)
                    .edit().putInt(getString(R.string.keys_prefs_current_theme_pos), position).apply();

            if (theme != null && mListener != null) {
                mListener.settings_ChangeTheme(theme);
            }
        } else {
            firstOpen = false;
        }
    }

    /**
     *  Do nothing if nothing is selected.
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void settings_ToggleStayLoggedIn(Switch v);
        void settings_ChangeTheme(String styleID);
    }
}
