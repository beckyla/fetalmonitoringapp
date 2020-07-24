package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyMeasurementRecyclerViewAdapter extends RecyclerView.Adapter<MyMeasurementRecyclerViewAdapter.ViewHolder> {

    private List<String> mData;
    private int unitIdent;
    private LayoutInflater mInflater;

    //Data is passed into the constructor
    MyMeasurementRecyclerViewAdapter(Context context, List<String> data, int unitIdent) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.unitIdent = unitIdent;
    }

    //Inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_measurement, parent, false);
        return new ViewHolder(view);
    }

    //Binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String data = mData.get(position);
        holder.mTextView.setText(data);

        if (unitIdent == 1) {
            holder.mUnitView.setText("kicks");
        } else{
            holder.mUnitView.setText("bpm");
        }
    }

    //Gets the total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    //Stores and recycles views as they are scrolled off screen
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView mUnitView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.measurement_values);
            mUnitView = itemView.findViewById(R.id.measurement_unit);
        }

    }
}
