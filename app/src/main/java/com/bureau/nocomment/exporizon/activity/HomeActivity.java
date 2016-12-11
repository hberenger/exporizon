package com.bureau.nocomment.exporizon.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bureau.nocomment.exporizon.App;
import com.bureau.nocomment.exporizon.R;
import com.bureau.nocomment.exporizon.ble.BeaconDetector;
import com.bureau.nocomment.exporizon.ble.BeaconObserver;
import com.bureau.nocomment.exporizon.ble.unused.RawBeaconDetector;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BleNotAvailableException;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements BeaconObserver {

    private static final int REQUEST_ENABLE_BT = 1001;
    private BeaconDetector beaconDetector;
    @Bind(R.id.item_number) EditText itemNumber;
    @Bind(R.id.click_blocker) LinearLayout clickBlocker;
    @Bind(R.id.status_bar) TextView statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beaconDetector = ((App) getApplicationContext()).getBeaconDetector();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, Dummy.class));
            }
        });

        ButterKnife.bind(this);

        itemNumber.setCursorVisible(true);
        itemNumber.setSelection(itemNumber.getText().length());

        clickBlocker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            beaconDetector.start();
        } catch (IOException e) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (BleNotAvailableException e) {
            // TODO : do something more clever
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconDetector.subscribe(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconDetector.unsubscribe(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            beaconDetector.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                try {
                    beaconDetector.start();
                } catch (IOException e) {
                    // TODO : do something more clever
                    e.printStackTrace();
                }
            } else {
                // TODO
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconDetectedWithinCloseRange(com.bureau.nocomment.exporizon.ble.Beacon beacon) {
        startActivity(new Intent(HomeActivity.this, Dummy.class));
    }

    @Override
    public void onBeaconRangeUpdate(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusBar.setText(status);
            }
        });
    }
}
