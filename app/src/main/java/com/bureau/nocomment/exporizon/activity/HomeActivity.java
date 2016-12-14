package com.bureau.nocomment.exporizon.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bureau.nocomment.exporizon.App;
import com.bureau.nocomment.exporizon.R;
import com.bureau.nocomment.exporizon.ble.BeaconDetector;
import com.bureau.nocomment.exporizon.ble.BeaconObserver;
import com.bureau.nocomment.exporizon.view.KeyboardButton;
import com.bureau.nocomment.exporizon.view.KeyboardImageButton;

import org.altbeacon.beacon.BleNotAvailableException;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements BeaconObserver {

    private static final int REQUEST_ENABLE_BT = 1001;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BeaconDetector beaconDetector;
    private boolean snackbar;
    @Bind(R.id.item_number) EditText itemNumber;
    @Bind(R.id.click_blocker) LinearLayout clickBlocker;
    @Bind(R.id.status_bar) TextView statusBar;
    @Bind(R.id.content_main) View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beaconDetector = ((App) getApplicationContext()).getBeaconDetector();

        ButterKnife.bind(this);

        itemNumber.setCursorVisible(true);
        itemNumber.setSelection(itemNumber.getText().length());

        clickBlocker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        snackbar = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                askCoarseLocationPermission();
            } else {
                startDetector(true);
            }
        } else {
            startDetector(true);
        }
    }

    private void askCoarseLocationPermission() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This app needs location access");
        builder.setMessage("Please grant location access so this app can detect beacons.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }

    private void startDetector(boolean askPermission) {
        try {
            beaconDetector.start();
        } catch (IOException e) {
            if (askPermission) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                e.printStackTrace();
            }
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
                startDetector(false);
            } else {
                // TODO
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // coarse location permission granted
                    startDetector(true);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
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
        if (snackbar) {
            Snackbar snack = Snackbar.make(mainLayout, "Théâtre sphérique, Andor Weininger", Snackbar.LENGTH_LONG);
            snack.setAction("Voir ?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, DetailActivity.class));
                }
            });
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
            snack.show();
        } else {
            startActivity(new Intent(HomeActivity.this, DetailActivity.class));
        }
        snackbar = !snackbar;
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

    @OnClick({ R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
               R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9})
    void onKeyboardDigit(KeyboardButton button) {
        itemNumber.getText().append(button.getText());
    }

    @OnClick(R.id.buttonBack)
    void onKeyboardBack(KeyboardImageButton button) {
        if (itemNumber.getText().length() > 0) {
            itemNumber.getText().delete(itemNumber.getText().length() - 1, itemNumber.getText().length());
        }
    }

    @OnClick({ R.id.buttonOK })
    void onKeyboardValidate(KeyboardImageButton button) {
        startActivity(new Intent(HomeActivity.this, DetailActivity.class));
    }
}
