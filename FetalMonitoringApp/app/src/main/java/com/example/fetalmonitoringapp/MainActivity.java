package com.example.fetalmonitoringapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;
    String movementFile1 = "MovementData1.csv";
    String movementFile2 = "MovementData2.csv";
    String movementFile3 = "MovementData3.csv";
    String heartrateFile = "HeartrateData.csv";

    // Variables for storing and retrieving data
    List<String> heartrateValues = new ArrayList<>();
    List<List<kickInfo>> kickInfoData = new ArrayList<>();
    List<kickInfo> kickInfoSeq = new ArrayList<>();
    List<kickInfo> kickInfoSeq2 = new ArrayList<>();
    List<piezoInfo> piezoInfoData = new ArrayList<>();
    List<String> rawPiezoData = new ArrayList<>();

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
        //File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File folder = new File(path, "MeasurementData");
        // Create folders if they do not exist
        if (!folder.exists()) { folder.mkdirs(); }

        File mFile1 = new File(folder + File.separator + movementFile1);
        File mFile2 = new File(folder + File.separator + movementFile2);
        File mFile3 = new File(folder + File.separator + movementFile3);
        File hrFile = new File(folder + File.separator + heartrateFile);

        // Create files and folders if they do not exist
        if (!mFile1.exists()) { mFile1.mkdirs(); }
        if (!mFile2.exists()) { mFile2.mkdirs(); }
        if (!mFile3.exists()) { mFile3.mkdirs(); }
        if (!hrFile.exists()) { hrFile.mkdirs(); }

        readFileInternalStorage(movementFile1);
        readFileInternalStorage(movementFile2);
        readFileInternalStorage(movementFile3);
        readFileInternalStorage(heartrateFile);

        if (kickInfoData.size() == 0) {
            // Add temp data for kick information
            kickInfoData = ((MeasurementData) this.getApplication()).getKickInfoData();
            kickInfoSeq.add(new kickInfo(1, 1, "15:29:10", "15:29:12", 2));
            kickInfoSeq.add(new kickInfo(2, 6, "15:29:20", "15:30:21", 1));
            kickInfoSeq.add(new kickInfo(3, 2, "18:30:16", "18:30:21", 5));
            kickInfoSeq.add(new kickInfo(4, 4, "1:29:20", "15:30:21", 1));
            kickInfoSeq.add(new kickInfo(5, 4, "1:57:35", "15:30:43", 8));
            kickInfoSeq.add(new kickInfo(6, 5, "3:28:25", "15:30:29", 4));
            kickInfoSeq2.add(new kickInfo(3, 4, "15:29:20", "15:30:21", 1));
            kickInfoSeq2.add(new kickInfo(4, 3, "15:29:20", "15:30:21", 1));

            // Add 3 temp copies
            kickInfoData.add(kickInfoSeq);
            kickInfoData.add(kickInfoSeq2);
            kickInfoData.add(kickInfoSeq2);
            ((MeasurementData) this.getApplication()).setKickInfoData(kickInfoData);

        }

        if (piezoInfoData.size() == 0) {
            // Add temp data for piezo information
            piezoInfoData = ((MeasurementData) this.getApplication()).getPiezoInfoData();
            // Add 3 temp copies
            piezoInfoData.add(new piezoInfo(15, "14/07/2020", "15:00:00", "17:00:35"));
            piezoInfoData.add(new piezoInfo(16, "15/07/2020", "15:00:00", "17:00:35"));
            piezoInfoData.add(new piezoInfo(17, "16/07/2020", "15:00:00", "17:00:35"));
            ((MeasurementData) this.getApplication()).setPiezoInfoData(piezoInfoData);
        }

        if (rawPiezoData.size() == 0) {
            // Add temp data for raw piezo information
            rawPiezoData = ((MeasurementData) this.getApplication()).getRawPiezoData();
            rawPiezoData.add("*35,1023,40,30,20,0,25,36,40,90*21,1023,40,30,48,200,20,33,0,10*35,1023,40,30,20,80,60,80,66,99*35,1023,40,30,20,60,80,66,99,70*35,1023,40,30,20,25,24,26,29,10*35,1023,40,30,20,90,66,10,29,17");
            ((MeasurementData) this.getApplication()).setRawPiezoData(rawPiezoData);
        }

        if (heartrateValues.size() == 0) {
            // Add temp data for heartrate values
            heartrateValues = ((MeasurementData) this.getApplication()).getHeartrateValues();
            heartrateValues.add("123");
            heartrateValues.add("125");
            heartrateValues.add("120");
            heartrateValues.add("124");
            heartrateValues.add("122");
            heartrateValues.add("125");
            heartrateValues.add("123");
            heartrateValues.add("124");
            ((MeasurementData) this.getApplication()).setHeartrateValues(heartrateValues);

            //Save to storage for the first time
            writeFileInternalStorage(movementFile1);
            writeFileInternalStorage(movementFile2);
            writeFileInternalStorage(movementFile3);
            writeFileInternalStorage(heartrateFile);
        }

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();
        ((TextView) findViewById(R.id.toolbar_title)).setText("HOME");
        navigationView = findViewById(R.id.menu_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
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
                    case R.id.nav_export:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new ExportDataFragment());
                        fragmentTransaction.commit();
                        fragmentTransaction.addToBackStack("EXPORT_FRAGMENT");
                        ((TextView) findViewById(R.id.toolbar_title)).setText("Export Data");
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

    //Create class for kick info
    static class kickInfo {
        kickInfo(int id, int location, String startTime, String endTime, int duration) {
            this.ID = id;
            this.location = location;
            this.startTime = startTime;
            this.endTime = endTime;
            this.duration = duration;
        }
        int ID;
        int location;
        String startTime;
        String endTime;
        int duration;
    }

    //Create class for piezoelectric sensor data
    static class piezoInfo {
        piezoInfo(int totalKicks, String date, String startTime, String endTime) {
            this.totalKicks = totalKicks;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        int totalKicks;
        String date;
        String startTime;
        String endTime;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDestroy() {
        super.onDestroy();

        writeFileInternalStorage(movementFile1);
        writeFileInternalStorage(movementFile2);
        writeFileInternalStorage(movementFile3);
        writeFileInternalStorage(heartrateFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStop() {
        super.onStop();
        //writeFileInternalStorage(movementFile1);
        //writeFileInternalStorage(movementFile2);
        //writeFileInternalStorage(movementFile3);
        //writeFileInternalStorage(heartrateFile);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readFileInternalStorage(String filenameInternal) {

        String contents = null;

        try {
            InputStream inputStream = getApplicationContext().openFileInput(filenameInternal);

            if ( inputStream != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {

                    if (filenameInternal.equals("HeartrateData.csv")){
                        stringBuilder.append("").append(receiveString);
                    }
                    else{
                        stringBuilder.append("").append(receiveString).append("*"); }

                }

                inputStream.close();
                contents = stringBuilder.toString();

                if (filenameInternal.equals("HeartrateData.csv")){
                    heartrateValues = new ArrayList<>(Arrays.asList(contents.split(",")));
                    ((MeasurementData) this.getApplication()).setHeartrateValues(heartrateValues);
                }

                if (filenameInternal.equals("MovementData1.csv")){
                    ArrayList<String> splitParent = new ArrayList<>(Arrays.asList(contents.split("[*]")));
                    for (int i = 1; i < splitParent.size(); i++){
                        ArrayList<String> splitChild = new ArrayList<>(Arrays.asList(splitParent.get(i).split(",")));
                        piezoInfoData.add(new piezoInfo(Integer.parseInt(splitChild.get(0)),splitChild.get(1),splitChild.get(2), splitChild.get(3)));
                    }
                    ((MeasurementData) this.getApplication()).setPiezoInfoData(piezoInfoData);
                }

                if (filenameInternal.equals("MovementData2.csv")){
                    int prev_ID = 1;

                    ArrayList<String> splitParent = new ArrayList<>(Arrays.asList(contents.split("[*]")));
                    for (int i = 1; i < splitParent.size(); i++) {
                        ArrayList<String> splitChild = new ArrayList<>(Arrays.asList(splitParent.get(i).split(",")));

                        int new_ID = Integer.parseInt(splitChild.get(0));
                        if (new_ID == prev_ID) {
                            kickInfoSeq.add(new kickInfo(Integer.parseInt(splitChild.get(1)), Integer.parseInt(splitChild.get(2)), splitChild.get(3), splitChild.get(4), Integer.parseInt(splitChild.get(5))));

                        }else{
                            List<kickInfo> copy = new ArrayList<kickInfo>(kickInfoSeq);
                            kickInfoData.add(copy);
                            kickInfoSeq.clear();
                            kickInfoSeq.add(new kickInfo(Integer.parseInt(splitChild.get(1)), Integer.parseInt(splitChild.get(2)), splitChild.get(3), splitChild.get(4), Integer.parseInt(splitChild.get(5))));
                        }
                        prev_ID = new_ID;

                    }
                    List<kickInfo> copy = new ArrayList<kickInfo>(kickInfoSeq);
                    kickInfoData.add(copy);

                    ((MeasurementData) this.getApplication()).setKickInfoData(kickInfoData);
                }

                if (filenameInternal.equals("MovementData3.csv")){
                    int prev_ID = 0;
                    String concatString = "";
                    ArrayList<String> splitParent = new ArrayList<>(Arrays.asList(contents.split("[*]")));
                    for (int i = 0; i < splitParent.size(); i++){
                        ArrayList<String> splitChild = new ArrayList<>(Arrays.asList(splitParent.get(i).split(",")));
                        int new_ID = Integer.parseInt(splitChild.get(0));
                        if (new_ID == prev_ID) {
                            concatString = concatString + "*" + splitChild.get(2);
                            for (int j = 3; j < splitChild.size(); j++){
                                concatString = concatString + "," + splitChild.get(j);
                            }
                        }
                        else{
                            rawPiezoData.add(concatString);
                            //clear concatString
                            concatString = "";
                            concatString = concatString + "*"+ splitChild.get(2);
                            for (int j = 3; j < splitChild.size(); j++){
                                concatString = concatString + "," + splitChild.get(j);
                            }
                        }
                        prev_ID = new_ID;
                    }
                    rawPiezoData.add(concatString);

                    ((MeasurementData) this.getApplication()).setRawPiezoData(rawPiezoData);
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("error", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("error", "Can not read file: " + e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeFileInternalStorage(String filenameInternal) {
        try {

            StringBuilder data = new StringBuilder();

            //Generate data values
            if (filenameInternal.equals("MovementData1.csv")){
                data.append("Total,Date,Start Time,End Time");
                piezoInfoData = ((MeasurementData) this.getApplication()).getPiezoInfoData();
                for(int i = 0; i < piezoInfoData.size(); i ++){
                    data.append("\n").append(piezoInfoData.get(i).totalKicks).append(",").append(piezoInfoData.get(i).date).append(",").append(piezoInfoData.get(i).startTime).append(",").append(piezoInfoData.get(i).endTime);
                }
            }

            if (filenameInternal.equals("MovementData2.csv")){
                data.append("Identifier,ID,Location,Start Time,Stop Time,Duration");
                kickInfoData = ((MeasurementData) this.getApplication()).getKickInfoData();

                for(int i = 0; i < kickInfoData.size(); i ++){
                    kickInfoSeq = kickInfoData.get(i);
                    for(int j = 0; j < kickInfoSeq.size(); j ++){
                        data.append("\n").append(i + 1).append(",").append(kickInfoSeq.get(j).ID).append(",");
                        data.append(kickInfoSeq.get(j).location).append(",").append(kickInfoSeq.get(j).startTime).append(",");
                        data.append(kickInfoSeq.get(j).endTime).append(",").append(kickInfoSeq.get(j).duration).append(",");
                    }
                }
            }

            if (filenameInternal.equals("MovementData3.csv")){
                List<String> rawPiezoData = ((MeasurementData) this.getApplication()).getRawPiezoData();
                for(int i = 0; i < rawPiezoData.size(); i ++){
                    ArrayList<String> splitValues = new ArrayList<>(Arrays.asList(rawPiezoData.get(i).split("\\*")));
                    for(int j = 1; j < splitValues.size(); j++){
                        data.append(i).append(",sensor").append(j-1).append(",").append(splitValues.get(j)).append("\n");
                    }
                }
            }

            if (filenameInternal.equals("HeartrateData.csv")){
                String strHeartrateValues = TextUtils.join(",", heartrateValues);
                data.append(strHeartrateValues);
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filenameInternal, Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(data));
            outputStreamWriter.close();

            //Saving file onto device
            FileOutputStream output = openFileOutput(filenameInternal,Context.MODE_PRIVATE);
            output.write(data.toString().getBytes());
            output.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        catch(NumberFormatException ex){ // handle your exception
        }
    }
}
