package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InformationDataAdapter extends RecyclerView.Adapter<InformationDataAdapter.ViewHolder> {

    private List<MainActivity.kickInfo> mData;
    private LayoutInflater mInflater;

    //Data is passed into the constructor
    InformationDataAdapter(Context context, List<MainActivity.kickInfo> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    //Inflates the row layout from xml when needed
    @NonNull
    @Override
    public InformationDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listitem_info, parent, false);
        return new InformationDataAdapter.ViewHolder(view);
    }

    //Binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(InformationDataAdapter.ViewHolder holder, int position) {
        MainActivity.kickInfo data = mData.get(position);

        holder.mLocationView.setText("Location: Sensor " + data.location);
        holder.mStartTimeView.setText("Start Time: " + data.startTime);
        holder.mEndTimeView.setText("End Time: " + data.endTime);
        holder.mDurationView.setText("Duration: " + data.duration + " Seconds");
    }

    //Gets the total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    //Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mLocationView;
        TextView mStartTimeView;
        TextView mEndTimeView;
        TextView mDurationView;

        ViewHolder(View itemView) {
            super(itemView);
            mLocationView = itemView.findViewById(R.id.location);
            mStartTimeView = itemView.findViewById(R.id.start_time);
            mEndTimeView = itemView.findViewById(R.id.end_time);
            mDurationView = itemView.findViewById(R.id.duration);
        }
    }
}