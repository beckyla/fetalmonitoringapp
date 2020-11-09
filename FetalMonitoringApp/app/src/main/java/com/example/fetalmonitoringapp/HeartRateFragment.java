package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeartRateFragment extends Fragment {

    private List<String> heartrateValues = new ArrayList<>();
    private List<String> heartrateDates = new ArrayList<>();

    public HeartRateFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // data to populate the RecyclerView with
        heartrateValues = ((MeasurementData) getActivity().getApplication()).getHeartrateValues();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_heartrate, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.heartRateData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Collections.reverse(heartrateValues); //Reverse list

        // Add temporary dates for heart rate values
        heartrateDates.add("07/09/2020");
        heartrateDates.add("08/09/2020");
        heartrateDates.add("09/09/2020");
        heartrateDates.add("10/09/2020");
        heartrateDates.add("11/09/2020");
        heartrateDates.add("12/09/2020");
        heartrateDates.add("13/09/2020");
        heartrateDates.add("14/09/2020");
        Collections.reverse(heartrateDates);

        MyMeasurementRecyclerViewAdapter adapter = new MyMeasurementRecyclerViewAdapter(getActivity(), heartrateValues.subList(1,heartrateValues.size()), 0, heartrateDates.subList(1,heartrateDates.size()));
        recyclerView.setAdapter(adapter);

        //Check for back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((TextView) Objects.requireNonNull(getActivity()).findViewById(R.id.toolbar_title)).setText("HOME");
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

        TextView heartrate_home = view.findViewById(R.id.heartrate_recent_text);
        TextView heartrate_date = view.findViewById(R.id.date_recent_hr);

        heartrate_home.setText(heartrateValues.get(0));
        heartrate_date.setText(heartrateDates.get(0));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Collections.reverse(heartrateValues);
        Collections.reverse(heartrateDates); //Undo reversed list
    }
}