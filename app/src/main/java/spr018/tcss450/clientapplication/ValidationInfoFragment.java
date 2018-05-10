package spr018.tcss450.clientapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import spr018.tcss450.clientapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ValidationInfoFragment extends Fragment {


    public ValidationInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_validation_info, container, false);
    }



}
