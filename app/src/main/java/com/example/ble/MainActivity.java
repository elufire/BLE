package com.example.ble;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter  = null;
    private BluetoothLeScanner mBluetoothLeScanner = null;

    public static final int REQUEST_BT_PERMISSIONS = 0;
    public static final int REQUEST_BT_ENABLE = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private boolean mScanning = false;
    ListView listView;
    BluetoothDeviceAdapter bluetoothAdapter;
    List<BluetoothDevice> bluetoothDeviceList;
    private Button btnScan = null;

    @RequiresApi(23)
    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    Log.d("BLE", result.getDevice().getAddress());
                    BluetoothDevice bluetoothDevice = result.getDevice();
                    if (!bluetoothDeviceList.contains(bluetoothDevice)){
                        bluetoothDeviceList.add(bluetoothDevice);
                        BluetoothDevice [] deviceArray = bluetoothDeviceList.toArray(new BluetoothDevice[bluetoothDeviceList.size()]);
                        bluetoothAdapter = new BluetoothDeviceAdapter(getBaseContext(), deviceArray);
                        listView.setAdapter(bluetoothAdapter);
                    }

                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.d("BLE", "error");
                }
            };



    @RequiresApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lvBluetoothDevices);
        btnScan = findViewById(R.id.btnScan);
        bluetoothDeviceList = new ArrayList<>();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        checkBtPermissions();
        enableBt();

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Access is necessary.");
            builder.setMessage("Loaction needed for detecting nearby devices.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }



    @RequiresApi(23)
    public void onBtnScan(View v){
        if (mScanning){
            mScanning = false;
            scanLeDevice(false);
            btnScan.setText("Stop Scan");
        } else {
            mScanning = true;
            scanLeDevice(true);
            btnScan.setText("Start SCAN");
            bluetoothDeviceList.clear();
        }
    }

    @RequiresApi(23)
    public void checkBtPermissions() {
        this.requestPermissions(
                new String[]{
                        Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN
                },
                REQUEST_BT_PERMISSIONS);
    }

    public void enableBt(){
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "YOUR DEVICE DOES NOT SUPPORT BLUETOOTH", Toast.LENGTH_LONG).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
        }
    }

    @RequiresApi(23)
    public void scanLeDevice(final boolean enable) {

        if (enable) {
            mScanning = true;
            Log.d("TAG", "start");
            mBluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            Log.d("TAG", "stop");
            mScanning = false;
            mBluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission has been granted.");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Functionality Limited without location Access.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
