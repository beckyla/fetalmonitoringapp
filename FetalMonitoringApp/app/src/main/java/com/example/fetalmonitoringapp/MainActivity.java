package com.example.fetalmonitoringapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    String movementFile = "MovementData.txt";
    String heartrateFile = "HeartrateData.txt";
    String dateFile = "DateData.txt";
    List<String> heartrateValues;
    List<String> movementValues;
    List<CharSequence> dateValues;

    //Any app-defined int constant random number
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Request permission to access location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        File path = getApplicationContext().getFilesDir();

        File folder = new File(path, "MeasurementData");
        File mFile = new File(folder + File.separator + movementFile);
        File hrFile = new File(folder + File.separator +  heartrateFile);

        if (!folder.exists()){
            mFile.mkdir();
        }

        if (!mFile.exists()){
            mFile.mkdir();
        }

        if (!hrFile.exists()){
            hrFile.mkdir();
        }

        String strMovementValues = readFileInternalStorage(movementFile);
        String strHeartrateValues = readFileInternalStorage(heartrateFile);

        if(strMovementValues == null || strHeartrateValues == null) {
            heartrateValues = ((MeasurementData) this.getApplication()).getHeartrateValues();
            movementValues = ((MeasurementData) this.getApplication()).getMovementValues();
            dateValues = ((MeasurementData) this.getApplication()).getDateValues();

            // Add temporary data
            movementValues.add("15");
            movementValues.add("13");
            movementValues.add("13");
            movementValues.add("14");
            movementValues.add("12");
            movementValues.add("18");
            movementValues.add("15");

            heartrateValues.add("123");
            heartrateValues.add("125");
            heartrateValues.add("120");
            heartrateValues.add("124");
            heartrateValues.add("122");
            heartrateValues.add("125");
            heartrateValues.add("123");
            heartrateValues.add("124");

            Date date = new Date();
            CharSequence today  = DateFormat.format("MMMM d, yyyy ", date.getTime());
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);

            ((MeasurementData) this.getApplication()).setHeartrateValues(heartrateValues);
            ((MeasurementData) this.getApplication()).setMovementValues(movementValues);
            ((MeasurementData) this.getApplication()).setDateValues(dateValues);

        }else{
            dateValues = ((MeasurementData) this.getApplication()).getDateValues();

            movementValues = new ArrayList<>(Arrays.asList(strMovementValues.split(",")));
            heartrateValues = new ArrayList<>(Arrays.asList(strHeartrateValues.split(",")));

            ((MeasurementData) this.getApplication()).setHeartrateValues(heartrateValues);
            ((MeasurementData) this.getApplication()).setMovementValues(movementValues);

            Date date = new Date();
            CharSequence today  = DateFormat.format("MMMM d, yyyy ", date.getTime());
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);
            dateValues.add(today);

            ((MeasurementData) this.getApplication()).setDateValues(dateValues);
        }

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();
        ((TextView) findViewById(R.id.toolbar_title)).setText("HOME");
        navigationView = (NavigationView)findViewById(R.id.menu_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HomeFragment()).addToBackStack("HOME_FRAGMENT").commit();
                        ((TextView) findViewById(R.id.toolbar_title)).setText("HOME");
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_import:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new ImportDataFragment());
                        fragmentTransaction.commit();
                        fragmentTransaction.addToBackStack("IMPORT_FRAGMENT");
                        ((TextView) findViewById(R.id.toolbar_title)).setText("Import Data");
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_heart:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HeartRateFragment());
                        fragmentTransaction.commit();
                        fragmentTransaction.addToBackStack("HEART_FRAGMENT");
                        ((TextView) findViewById(R.id.toolbar_title)).setText("Fetal Heart Rate");
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_movement:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new MovementFragment());
                        fragmentTransaction.commit();
                        fragmentTransaction.addToBackStack("KICK_FRAGMENT");
                        ((TextView) findViewById(R.id.toolbar_title)).setText("Fetal Kicks");
                        drawerLayout.closeDrawers();
                        break;

                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String strMovementValues = TextUtils.join(",", movementValues);
        String strHeartrateValues = TextUtils.join(",", heartrateValues);

        writeFileInternalStorage(movementFile, strMovementValues);
        writeFileInternalStorage(heartrateFile, strHeartrateValues);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String readFileInternalStorage(String filenameInternal) {

        String contents = null;

        try {
            InputStream inputStream = getApplicationContext().openFileInput(filenameInternal);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("").append(receiveString);
                }

                inputStream.close();
                contents = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("error", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("error", "Can not read file: " + e.toString());
        }

        return contents;
    }

    private void writeFileInternalStorage(String filenameInternal , String dataValues) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filenameInternal, Context.MODE_PRIVATE));
            outputStreamWriter.write(dataValues);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
