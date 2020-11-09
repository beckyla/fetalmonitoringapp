package com.example.fetalmonitoringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    public static final String POSITION = "POSITION";
    List<List<MainActivity.kickInfo>> kickInfoData;
    List<MainActivity.kickInfo> kickInfoSeq;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        final Intent intent = getIntent();

        ((TextView) findViewById(R.id.toolbar_title)).setText("Individual Kick Information");

        // Get the position
        int mPosition = intent.getIntExtra(POSITION, 0);

        // Retrieve the kickInfoSeq for the position
        kickInfoData = ((MeasurementData) this.getApplication()).getKickInfoData();

        Collections.reverse(kickInfoData); //Reverse list
        kickInfoSeq = kickInfoData.get(mPosition);

        Collections.reverse(kickInfoData); //Reverse list

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.information);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        InformationDataAdapter adapter = new InformationDataAdapter(this, kickInfoSeq);
        recyclerView.setAdapter(adapter);
    }

    public void backClick(View view) {
        //((TextView) findViewById(R.id.toolbar_title)).setText("Fetal Kicks");
        finish();
    }
}
