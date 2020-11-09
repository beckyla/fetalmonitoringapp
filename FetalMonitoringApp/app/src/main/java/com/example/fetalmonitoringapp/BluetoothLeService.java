package com.example.fetalmonitoringapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {

    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;

    // Variables for storing and retrieving data
    private List<MainActivity.piezoInfo> piezoInfoData = new ArrayList<>();
    private String rawPiezoString= "";
    List<List<MainActivity.kickInfo>> kickInfoData;
    List<MainActivity.kickInfo> kickInfoSeq = new ArrayList<>();
    List<String> rawPiezoData = new ArrayList<>();
    int fetalKicks;
    String date;
    String recordStart;
    String recordEnd;
    int syncTime = 0;
    int startSec, stopSec;
    CharSequence currentTime;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.fetalmonitoringapp.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.fetalmonitoringapp.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.fetalmonitoringapp.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.fetalmonitoringapp.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.fetalmonitoringapp.EXTRA_DATA";

    // UUID declaration
    public final static UUID UUID_FETAL_KICK_MEASUREMENT = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1216");
    public final static UUID UUID_RAW_PIEZO_DATA = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1224");
    public final static UUID UUID_TIME_SYNC = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1226");
    //For UUID for sending fetal ECG data; not currently used
    //public final static UUID UUID_FETAL_ECG_MEASUREMENT = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1218");
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                connectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                connectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "Characteristic Read Callback");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "GATT_SUCCESS");
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "Characteristic Changed Callback");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // Special handling of fetal kick measurement information
        if (UUID_FETAL_KICK_MEASUREMENT.equals(characteristic.getUuid())) {
            final byte[] data= characteristic.getValue(); // Extract value from characteristic
            if (data[0] == '*') { // If start of sequence indicated

                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                //intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());

                ArrayList<String> splitValues = new ArrayList<>(Arrays.asList(new String(data).split(","))); // Split and extract information
                fetalKicks = Integer.parseInt((splitValues.get(0)).replaceAll("[^0-9]", ""));
                date = splitValues.get(1).replaceAll("[^0-9/]", "");
                if (date.equals("0")){
                    Date newDate = new Date();
                    CharSequence today  = DateFormat.format("dd/MM/yyyy", newDate.getTime());
                    currentTime  = DateFormat.format("hh:mm:ss", newDate.getTime());
                    date = today.toString();
                    syncTime = 1;
                }

                // Display to user
                intent.putExtra(EXTRA_DATA, String.valueOf(fetalKicks));

            }else if (data[0] == '!'){ //If end of sequence indicated

                ArrayList<String> splitValues = new ArrayList<>(Arrays.asList(new String(data).split(","))); // Split and extract information
                piezoInfoData = ((MeasurementData) this.getApplication()).getPiezoInfoData(); // Get the list of piezoelectric sensor data
                recordStart = (splitValues.get(1)).replaceAll("[^0-9:]", ""); // Remove invalid characters
                recordEnd = (splitValues.get(2)).replaceAll("[^0-9:]", ""); // Remove invalid characters

                if (syncTime == 1){
                    List<String> splitSync = new ArrayList<>(Arrays.asList(new String (currentTime.toString()).split(":")));
                    int syncRecordStart = Integer.parseInt(splitSync.get(0))*3600 + Integer.parseInt(splitSync.get(1))*60 + Integer.parseInt(splitSync.get(2)) + Integer.parseInt(recordStart);
                    int syncRecordEnd = Integer.parseInt(splitSync.get(0))*3600 + Integer.parseInt(splitSync.get(1))*60 + Integer.parseInt(splitSync.get(2)) + Integer.parseInt(recordEnd);

                    int shours = syncRecordStart / 3600;
                    int sminutes = (syncRecordStart % 3600) / 60;
                    int sseconds = syncRecordStart % 60;

                    int ehours = syncRecordEnd / 3600;
                    int eminutes = (syncRecordEnd % 3600) / 60;
                    int eseconds = syncRecordEnd % 60;

                    recordStart = String.format("%02d:%02d:%02d", shours, sminutes, sseconds);
                    recordEnd = String.format("%02d:%02d:%02d", ehours, eminutes, eseconds);

                    //Reset Sync Time
                    syncTime = 0;
                }

                piezoInfoData.add(new MainActivity.piezoInfo(fetalKicks, date, recordStart, recordEnd));
                ((MeasurementData) this.getApplication()).setPiezoInfoData(piezoInfoData);

                kickInfoData = ((MeasurementData) this.getApplication()).getKickInfoData(); // Retrieve the kickInfoData List
                kickInfoData.add(kickInfoSeq); //Add kickInfoSeq to List
                ((MeasurementData) this.getApplication()).setKickInfoData(kickInfoData); //Save new KickInfoData

                Toast.makeText(getApplicationContext(), "Import Complete", Toast.LENGTH_SHORT).show();
            }
            else{
                // Display to user
                if (data.length > 1) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X ", byteChar));
                    //intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                    intent.putExtra(EXTRA_DATA, new String(data) + "" );

                    ArrayList<String> splitValues = new ArrayList<>(Arrays.asList(new String(data).split(","))); // Split and extract information
                    String startTime = (splitValues.get(1)).replaceAll("[^0-9:]", ""); // Remove invalid characters
                    String stopTime = (splitValues.get(2)).replaceAll("[^0-9:]", ""); // Remove invalid characters
                    if (syncTime == 1) {
                        List<String> splitSync = new ArrayList<>(Arrays.asList(new String(currentTime.toString()).split(":")));
                        startSec = Integer.parseInt(splitSync.get(0)) * 3600 + Integer.parseInt(splitSync.get(1)) * 60 + Integer.parseInt(splitSync.get(2)) + Integer.parseInt(startTime);
                        stopSec = Integer.parseInt(splitSync.get(0)) * 3600 + Integer.parseInt(splitSync.get(1)) * 60 + Integer.parseInt(splitSync.get(2)) + Integer.parseInt(stopTime);

                        int shours = startSec / 3600;
                        int sminutes = (startSec % 3600) / 60;
                        int sseconds = startSec % 60;

                        int ehours = stopSec / 3600;
                        int eminutes = (stopSec % 3600) / 60;
                        int eseconds = stopSec % 60;

                        startTime = String.format("%02d:%02d:%02d", shours, sminutes, sseconds);
                        stopTime = String.format("%02d:%02d:%02d", ehours, eminutes, eseconds);

                    } else {
                        List<String> splitStart = new ArrayList<>(Arrays.asList(new String(splitValues.get(1)).replaceFirst("[^0-9:]", "").split(":")));
                        List<String> splitStop = new ArrayList<>(Arrays.asList(new String(splitValues.get(2)).replaceFirst("[^0-9:]", "").split(":")));

                        if (splitStart.size() == 3){
                            startSec = Integer.parseInt(splitStart.get(0).replaceFirst("[^0-9]", ""))* 3600 + Integer.parseInt(splitStart.get(1).replaceFirst("[^0-9]", "")) * 60 + Integer.parseInt(splitStart.get(2).replaceFirst("[^0-9:]", ""));
                        }else if (splitStart.size() == 2){
                            startSec = Integer.parseInt(splitStart.get(0).replaceFirst("[^0-9:]", "")) * 60 + Integer.parseInt(splitStart.get(1).replaceFirst("[^0-9:]", ""));
                        }else{
                            startSec = Integer.parseInt(splitStart.get(0).replaceFirst("[^0-9:]", ""));
                        }

                        if (splitStop.size() == 3){
                            stopSec = Integer.parseInt(splitStop.get(0).replaceFirst("[^0-9:]", "")) * 3600 + Integer.parseInt(splitStop.get(1).replaceFirst("[^0-9:]", "")) * 60 + Integer.parseInt(splitStop.get(2).replaceFirst("[^0-9:]", ""));
                        }else if (splitStop.size() == 2){
                            stopSec = Integer.parseInt(splitStop.get(0).replaceFirst("[^0-9:]", "")) * 60 + Integer.parseInt(splitStop.get(1).replaceFirst("[^0-9:]", ""));
                        }else{
                            stopSec = Integer.parseInt(splitStop.get(0).replaceFirst("[^0-9:]", ""));
                        }
                    }

                    int duration = stopSec - startSec; //Calculate duration
                    kickInfoSeq.add(new MainActivity.kickInfo(kickInfoSeq.size() + 1, Integer.parseInt(splitValues.get(0)), startTime, stopTime, duration));
                }
            }
        }
        else if (UUID_RAW_PIEZO_DATA.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {

                if (data[0] == '!'){ //If end of sequence indicated
                    // Display to user
                    final StringBuilder stringBuilder = new StringBuilder(1);
                    byte byteChar = data[0];
                    stringBuilder.append(String.format("%02X ", byteChar));
                    intent.putExtra(EXTRA_DATA, byteChar +"");
                    //intent.putExtra(EXTRA_DATA, byteChar + "\n" + stringBuilder.toString());

                    // Construct string of raw piezoelectric sensor values
                    //rawPiezoString = rawPiezoString + data[0];
                    // Print final received raw piezoelectric sensor values to logcat for debugging
                    Log.d(TAG, String.format("Received raw data: %s", rawPiezoString));
                    Log.d(TAG, String.format("Received raw data: %c", data[0]));

                    // Retrieve and store in list of raw Piezoelectric sensor data
                    rawPiezoData = ((MeasurementData) this.getApplication()).getRawPiezoData();
                    // Extra formatting for string; replace empty blocks
                    rawPiezoData.add(rawPiezoString.replace(",,", ","));
                    rawPiezoString = "";
                    ((MeasurementData) this.getApplication()).setRawPiezoData(rawPiezoData);

                    Toast.makeText(getApplicationContext(), "Importing Complete", Toast.LENGTH_SHORT).show();

                }else {
                    // Display to user
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    intent.putExtra(EXTRA_DATA, new String(data) );

                    String test = new String(data);
                    rawPiezoString = rawPiezoString + new String(data).replaceAll("[^0-9*,]", "");
                }
            }
        }
        else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }

        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }
    private final IBinder mBinder = new LocalBinder();
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }
    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                connectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, gattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        connectionState = STATE_CONNECTING;
        return true;
    }
    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.d(TAG, "Characteristic Read");
        mBluetoothGatt.readCharacteristic(characteristic);
    }
    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Raw Piezo Data.
        if (UUID_RAW_PIEZO_DATA.equals(characteristic.getUuid()) | UUID_FETAL_KICK_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) throws InterruptedException {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        characteristic.setValue("#");
        Thread.sleep(500);

        if (UUID_RAW_PIEZO_DATA.equals(characteristic.getUuid())) {
            characteristic.setValue("#");
            Thread.sleep(500);
        }

        if (UUID_FETAL_KICK_MEASUREMENT.equals(characteristic.getUuid())) {
            characteristic.setValue("#");
            Thread.sleep(500);
        }

        if (UUID_TIME_SYNC.equals(characteristic.getUuid())) {
            Date date = new Date();
            CharSequence today  = DateFormat.format("hhmmssddMMyyyy", date.getTime());
            Charset charset = StandardCharsets.US_ASCII;
            byte[] bToday = today.toString().getBytes(charset);
            characteristic.setValue(bToday);
            Toast.makeText(getApplicationContext(), "Time Sync Successful", Toast.LENGTH_SHORT).show();
        }

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }
}