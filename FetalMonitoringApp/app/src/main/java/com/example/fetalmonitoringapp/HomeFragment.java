package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private List<Integer> heartrateValues;
    private List<Integer> movementValues;
    private List<CharSequence> dateValues;
    private TextView heartrate_home, kicks_home;

    public HomeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        heartrateValues = ((MeasurementData) getActivity().getApplication()).getHeartrateValues();
        movementValues = ((MeasurementData) getActivity().getApplication()).getMovementValues();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        heartrate_home = getView().findViewById(R.id.heartrate_home);
        kicks_home = getView().findViewById(R.id.kicks_home);

        heartrate_home.setText(heartrateValues.get(heartrateValues.size()-1).toString());
        kicks_home.setText(movementValues.get(movementValues.size()-1).toString());
    }
}