package com.example.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothDeviceAdapter  extends ArrayAdapter<BluetoothDevice> {
    private  Context context;
    private  BluetoothDevice[] devices;

    public BluetoothDeviceAdapter(@NonNull Context context, BluetoothDevice[] devices) {
        super(context, -1, devices);
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.device_row_layout, parent, false);

        TextView tvDeviceName = row.findViewById(R.id.tvDeviceName);
        TextView tvDeviceAddress = row.findViewById(R.id.tvDeviceAddress);
        TextView tvBluetoothClass = row.findViewById(R.id.tvBluetoothClass);
        tvDeviceName.setText(Integer.toString(devices[position].getType()));
        tvDeviceAddress.setText(devices[position].getAddress());
        tvBluetoothClass.setText(devices[position].getBluetoothClass().toString());
        return row;
    }
}
