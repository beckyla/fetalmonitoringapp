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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovementFragment extends Fragment {

    private List<String> movementValues;
    private TextView kicks_home;

    public MovementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        movementValues = ((MeasurementData) getActivity().getApplication()).getMovementValues();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_movement, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.movementData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Collections.reverse(movementValues); //Reverse list
        MyMeasurementRecyclerViewAdapter adapter = new MyMeasurementRecyclerViewAdapter(getActivity(), movementValues.subList(1, movementValues.size()), 1);
        recyclerView.setAdapter(adapter);

        //Check for back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ((TextView) getActivity().findViewById(R.id.toolbar_title)).setText("HOME");
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                }
                return false;
            }
        });

        kicks_home = view.findViewById(R.id.kicks_recent_text);

        kicks_home.setText(movementValues.get(0));

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Collections.reverse(movementValues); //Undo reversed list
    }

}
