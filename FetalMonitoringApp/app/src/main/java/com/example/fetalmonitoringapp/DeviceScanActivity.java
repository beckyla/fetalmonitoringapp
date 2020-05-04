package com.example.fetalmonitoringapp;

/* Activity for scanning and displaying available BLE devices */

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.bluetooth.BluetoothDevice;

public class DeviceScanActivity extends ListActivity {

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;

    //Stops scanning after 10 seconds
    private static final long SCAN_PERIOD = 10000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            //Stop scanning after defined scan period from "SCAN_PERIOD" variable
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            //Enable is false; stop scanning
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    //Adapter for holding devices found through scanning
    private LeDeviceListAdapter leDeviceListAdapter;

    //Device scan callback.
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    leDeviceListAdapter.addDevice(device);
                    leDeviceListAdapter.notifyDataSetChanged();
                    }
            });
        }
    };

}
