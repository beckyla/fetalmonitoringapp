package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExportDataFragment extends Fragment {

    String heartrateFile = "HeartrateData.csv";

    // Variables for storing and retrieving data
    private List<String> heartrateValues;

    public ExportDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_export_data, container, false);

        // Capture buttons from layout
        Button fab1Button = view.findViewById(R.id.fab1);
        Button fab2Button = view.findViewById(R.id.fab2);
        Button fab3Button = view.findViewById(R.id.fab3);

        // Register the onClick listener with the buttons
        fab1Button.setOnClickListener(mClickListener);
        fab2Button.setOnClickListener(mClickListener);
        fab3Button.setOnClickListener(mClickListener);

        //Check for back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
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

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onClick(View button) {
            String movementFile1 = "MovementData1.csv";
            String movementFile2 = "MovementData2.csv";
            String movementFile3 = "MovementData3.csv";
            switch (button.getId()) {
                case R.id.fab1:
                    writeFileInternalStorage(movementFile3);
                    break;
                case R.id.fab2:
                    writeFileInternalStorage(movementFile2);
                    break;
                case R.id.fab3:
                    writeFileInternalStorage(movementFile1);
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeFileInternalStorage(String filenameInternal) {
        try {

            StringBuilder data = new StringBuilder();

            //Generate data values
            if (filenameInternal.equals("MovementData1.csv")){
                data.append("Total,Date,Start Time,End Time");
                List<MainActivity.piezoInfo> piezoInfoData = ((MeasurementData) Objects.requireNonNull(getActivity()).getApplication()).getPiezoInfoData();
                for(int i = 0; i < piezoInfoData.size(); i ++){
                    data.append("\n").append(piezoInfoData.get(i).totalKicks).append(",").append(piezoInfoData.get(i).date).append(",").append(piezoInfoData.get(i).startTime).append(",").append(piezoInfoData.get(i).endTime);
                }
            }

            if (filenameInternal.equals("MovementData2.csv")){
                data.append("Identifier,ID,Location,Start Time,Stop Time,Duration");
                List<List<MainActivity.kickInfo>> kickInfoData = ((MeasurementData) Objects.requireNonNull(getActivity()).getApplication()).getKickInfoData();

                for(int i = 0; i < kickInfoData.size(); i ++){
                    List<MainActivity.kickInfo> kickInfoSeq = kickInfoData.get(i);
                    for(int j = 0; j < kickInfoSeq.size(); j ++){
                        data.append("\n").append(i + 1).append(",").append(kickInfoSeq.get(j).ID).append(",");
                        data.append(kickInfoSeq.get(j).location).append(",").append(kickInfoSeq.get(j).startTime).append(",");
                        data.append(kickInfoSeq.get(j).endTime).append(",").append(kickInfoSeq.get(j).duration).append(",");
                    }
                }
            }

            if (filenameInternal.equals("MovementData3.csv")){
                List<String> rawPiezoData = ((MeasurementData) Objects.requireNonNull(getActivity()).getApplication()).getRawPiezoData();
                for(int i = 0; i < rawPiezoData.size(); i ++){
                    ArrayList<String> splitValues = new ArrayList<>(Arrays.asList(rawPiezoData.get(i).split("[*]")));
                    for(int j = 1; j < splitValues.size(); j++){
                        data.append(i).append(",sensor").append(j-1).append(",").append(splitValues.get(j)).append("\n");
                    }
                }
            }

            if (filenameInternal.equals("HeartrateData.csv")){
                String strHeartrateValues = TextUtils.join(",", heartrateValues);
                data.append(strHeartrateValues);
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Objects.requireNonNull(getActivity()).openFileOutput(filenameInternal, Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(data));
            outputStreamWriter.close();

            /*//Saving file onto device
            FileOutputStream output = Objects.requireNonNull(getActivity()).openFileOutput(filenameInternal,Context.MODE_PRIVATE);
            output.write(data.toString().getBytes());
            output.close();*/

            //Exporting
            Context context = getActivity().getApplicationContext();
            File filelocation = new File(getActivity().getFilesDir(), filenameInternal);
            Uri path = FileProvider.getUriForFile(context, "com.example.fetalmonitoringapp.fileprovider", filelocation);

            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "test"));
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        catch(NumberFormatException ex){ // handle your exception
        }
    }
}
