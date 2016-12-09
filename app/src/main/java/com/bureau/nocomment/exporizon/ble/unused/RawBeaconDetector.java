package com.bureau.nocomment.exporizon.ble.unused;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by RVB on 02/12/2016.
 */

public class RawBeaconDetector {
    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    public RawBeaconDetector(Context context) {
        this.context = context;
    }

    public void start() throws IOException {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            throw new IOException("Bluetooth unavailable");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        //mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void stop() {
    }
}
