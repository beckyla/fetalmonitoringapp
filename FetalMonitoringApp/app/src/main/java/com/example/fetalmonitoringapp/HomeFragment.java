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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private List<String> heartrateValues;
    private List<MainActivity.piezoInfo> piezoInfoData;

    public HomeFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        heartrateValues = ((MeasurementData) Objects.requireNonNull(getActivity()).getApplication()).getHeartrateValues();
        piezoInfoData = ((MeasurementData) (getActivity()).getApplication()).getPiezoInfoData();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView heartrate_home = Objects.requireNonNull(getView()).findViewById(R.id.heartrate_home);
        TextView kicks_home = getView().findViewById(R.id.kicks_home);

        heartrate_home.setText(heartrateValues.get(heartrateValues.size()-1));
        kicks_home.setText(String.valueOf(piezoInfoData.get(piezoInfoData.size()-1).totalKicks));
    }
}