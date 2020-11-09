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
    private List<String> mDate;
    private int unitIdent;
    private LayoutInflater mInflater;
    ItemClickListener clickListener;

    //Data is passed into the constructor
    MyMeasurementRecyclerViewAdapter(Context context, List<String> data, int unitIdent, List<String> date) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDate = date;
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

        String date = mDate.get(position);
        holder.mDateView.setText(date);

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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextView;
        TextView mUnitView;
        TextView mDateView;

        ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.measurement_values);
            mUnitView = itemView.findViewById(R.id.measurement_unit);
            mDateView = itemView.findViewById(R.id.date_value);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
