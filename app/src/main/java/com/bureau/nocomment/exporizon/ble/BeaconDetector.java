package com.bureau.nocomment.exporizon.ble;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BleNotAvailableException;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by RVB on 03/12/2016.
 */

public class BeaconDetector implements BeaconConsumer {
    private BeaconManager beaconManager;
    private static final String BTAG = "BeaconDetector";
    private Context context;
    private Region allBeaconsRegion;

  public void initialize(Context context) {
        if (beaconManager != null) {
            return;
        }
        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        allBeaconsRegion = new Region("MonitoringBeacons",
                Identifier.parse("c2ff6633-22ee-4dd9-a668-8666cc99aa88"), null, null);
        beaconManager.addMonitorNotifier(createMonitorNotifier());
        beaconManager.addRangeNotifier(createRangeNotifier());
        this.context = context;
    }

    @Override
    public Context getApplicationContext() {
        return context.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }

    public void start() throws BleNotAvailableException, IOException {
        if (!beaconManager.checkAvailability()) {
            throw new IOException("Bluetooth unavailable");
        }
        beaconManager.bind(this);
    }

    public void stop() throws RemoteException {
        beaconManager.stopMonitoringBeaconsInRegion(allBeaconsRegion);
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.startMonitoringBeaconsInRegion(allBeaconsRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private RangeNotifier createRangeNotifier() {
        return new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(BTAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }
        };
    }

    @NonNull
    private MonitorNotifier createMonitorNotifier() {
        return new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(BTAG, "I just saw an beacon for the first time!");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    // TODO : do something more clever
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(BTAG, "I no longer see an beacon");
                try {
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {
                    // TODO : do something more clever
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(BTAG, "I have just switched from seeing/not seeing beacons: " + state);
            }
        };
    }
}