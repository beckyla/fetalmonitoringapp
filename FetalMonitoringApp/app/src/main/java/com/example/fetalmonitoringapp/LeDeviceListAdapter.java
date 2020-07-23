package com.example.fetalmonitoringapp;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    //Data is passed into the constructor
    public LeDeviceListAdapter(Context context) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.mLeDevices = new ArrayList<BluetoothDevice>();
    }
    //Add Bluetooth device
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }
    //Get Bluetooth device
    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }
    //Clear devices
    public void clear() {
        mLeDevices.clear();
    }
    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    BluetoothDevice getItem(int i) {
        return mLeDevices.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i;
    }

    //Inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listitem_device, parent, false);
        return new ViewHolder(view);
    }

    //Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        BluetoothDevice device = mLeDevices.get(position);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());
    }

    //Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName;
        TextView deviceAddress;

        ViewHolder mViewHolder;

        ViewHolder(View itemView) {
            super(itemView);
            // General ListView optimization code.
            if (mViewHolder == null) {
                //itemView = mInflater.inflate(R.layout.listitem_device, null);
                //mViewHolder = new ViewHolder(itemView);
                deviceAddress = itemView.findViewById(R.id.device_address);
                deviceName = itemView.findViewById(R.id.device_name);
                itemView.setTag(mViewHolder);
                itemView.setOnClickListener(this);
            } else {
                mViewHolder = (ViewHolder) itemView.getTag();
            }
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // Allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
