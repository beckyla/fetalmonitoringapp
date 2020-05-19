package com.example.fetalmonitoringapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.fetalmonitoringapp.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Called when the use taps the bluetooth button
    public void bluetoothPage(View view) {
        //Intent constructor takes two parameters => context and class
        Intent intent = new Intent(this, DeviceScanActivity.class);
        //EditText editText = (EditText) findViewById(R.id.editText3);
        //String message = editText.getText().toString();
        //PutExtra() adds to value of EditText to the intent
        //intent.putExtra(EXTRA_MESSAGE, message);

        //Starts an instance of "BluetoothConnection"
        startActivity(intent);
    }

    public void monitoringPage(View view) {
        //Intent constructor takes two parameters => context and class
        Intent intent = new Intent(this, Monitoring.class);
        //EditText editText = (EditText) findViewById(R.id.editText3);
        //String message = editText.getText().toString();
        //PutExtra() adds to value of EditText to the intent
        //intent.putExtra(EXTRA_MESSAGE, message);

        //Starts an instance of "BluetoothConnection"
        startActivity(intent);
    }

}
