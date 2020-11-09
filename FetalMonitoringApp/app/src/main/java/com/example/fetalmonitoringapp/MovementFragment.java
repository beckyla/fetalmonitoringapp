package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovementFragment extends Fragment implements MyMeasurementRecyclerViewAdapter.ItemClickListener {

    private List<String> movementValues = new ArrayList<>();
    private List<String> movementDates = new ArrayList<>();

    public MovementFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        List<MainActivity.piezoInfo> piezoInfoData = ((MeasurementData) Objects.requireNonNull(getActivity()).getApplication()).getPiezoInfoData();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_movement, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.movementData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Store kick values and date values in a list for recycler adapter
        for(int i = piezoInfoData.size()-1; i >= 0 ; i--)
        {
            movementValues.add(String.valueOf(piezoInfoData.get(i).totalKicks));
            movementDates.add(piezoInfoData.get(i).date);
        }

        MyMeasurementRecyclerViewAdapter adapter = new MyMeasurementRecyclerViewAdapter(getActivity(), movementValues.subList(1, movementValues.size()), 1, movementDates.subList(1, movementDates.size()));
        adapter.setClickListener(this);
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

        TextView kicks_home = view.findViewById(R.id.kicks_recent_text);
        TextView kicks_date = view.findViewById(R.id.date_recent_kick);

        kicks_home.setText(movementValues.get(0));
        kicks_date.setText(movementDates.get(0));

        //Capture layout from view
        View kick_recent = view.findViewById(R.id.kicks_recent);
        kick_recent.setOnClickListener(mClickListener);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //Collections.reverse(movementValues); //Undo reversed list
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked on entry", Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(getActivity(), DataDisplayActivity.class);
        intent.putExtra(DataDisplayActivity.POSITION, position+1);
        startActivity(intent);
    }

    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onClick(View button) {
            Toast.makeText(getActivity(), "You clicked on entry ", Toast.LENGTH_SHORT).show();
            final Intent intent = new Intent(getActivity(), DataDisplayActivity.class);
            intent.putExtra(DataDisplayActivity.POSITION, 0);
            startActivity(intent);
        }
    };
}
