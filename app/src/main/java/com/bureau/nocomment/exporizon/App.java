package com.bureau.nocomment.exporizon;

/**
 * Created by RVB on 03/12/2016.
 */

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.bureau.nocomment.exporizon.ble.BeaconDetector;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;


public class App extends Application {

    private static final String PREFS_NAME = "com.bureau.nocomment.bets";
    private static final String NOTIFICATION_PREFS = App.PREFS_NAME + ".notifications";

    private static App sInstance;
    private BackgroundPowerSaver backgroundPowerSaver;

    private BeaconDetector beaconDetector;

    public App() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        backgroundPowerSaver = new BackgroundPowerSaver(this);
        beaconDetector = new BeaconDetector();
    }

    public static App getContext() {
        return sInstance;
    }

    public BeaconDetector getBeaconDetector() {
        return beaconDetector;
    }

    public static boolean isRunningOnPhone() {
        int screenCategory = getContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenCategory == Configuration.SCREENLAYOUT_SIZE_SMALL || screenCategory == Configuration.SCREENLAYOUT_SIZE_NORMAL;
    }

    public static boolean isPortrait() {
        return sInstance.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public boolean getNotificationPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getBoolean(NOTIFICATION_PREFS, true);
    }

    public void setNotificationPreferences(boolean notify) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(NOTIFICATION_PREFS, notify);
        editor.apply();
    }

    public void initNotificationPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!preferences.contains(NOTIFICATION_PREFS)) {
            // ask question here if need be
            setNotificationPreferences(true);
        }
    }

}
