package com.bureau.nocomment.exporizon.ble;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bureau.nocomment.exporizon.BuildConfig;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BleNotAvailableException;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by RVB on 03/12/2016.
 */

public class BeaconDetector implements BeaconConsumer {
    private BeaconManager beaconManager;
    private static final String BTAG = "BeaconDetector";
    private Context context;
    private Region allBeaconsRegion;
    private boolean monitoring = false;
    private Map<String, Beacon> beaconMap;
    private List<Beacon> beaconList; // sorted by distance

    public BeaconDetector() {
        allBeaconsRegion = new Region("MonitoringBeacons",
                Identifier.parse("c2ff6633-22ee-4dd9-a668-8666cc99aa88"), null, null);
        beaconMap = new HashMap<>();
        beaconList = new ArrayList<>();
    }

    //region Public API
    public void initialize(Context context) {
        if (beaconManager != null) {
            return;
        }
        beaconManager = BeaconManager.getInstanceForApplication(context);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")); // iBeacons
        beaconManager.addMonitorNotifier(createMonitorNotifier());
        beaconManager.addRangeNotifier(createRangeNotifier());
        this.context = context;
    }

    public void start() throws BleNotAvailableException, IOException {
        if (!beaconManager.checkAvailability()) {
            throw new IOException("Bluetooth unavailable");
        }
        beaconManager.bind(this);
    }

    public void stop() throws RemoteException {
        stopMonitoring(allBeaconsRegion);
        beaconManager.unbind(this);
    }
    //endregion

    //region BeaconConsumer
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

    @Override
    public void onBeaconServiceConnect() {
        try {
            startMonitoring(allBeaconsRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region PRIVATE
    private void updateBeaconListWith(Collection<org.altbeacon.beacon.Beacon> altBeacons) {
        Set<String> visibleBeacons = new HashSet<>();

        // Update de visible ones.
        if (altBeacons != null) {
            for (org.altbeacon.beacon.Beacon altBeacon : altBeacons) {
                String address = altBeacon.getBluetoothAddress();
                Beacon beacon = beaconMap.get(address);
                if (beacon == null) {
                    beacon = new Beacon(address, altBeacon.getId3().toInt());
                    beaconMap.put(address, beacon);
                    beaconList.add(beacon);
                }
                beacon.updateVisibleBeacon(altBeacon.getDistance());
                // get track of the visible ones
                visibleBeacons.add(address);
            }
        }

        // Cancel the invisible ones
        for (Beacon beacon : beaconList) {
            if (!visibleBeacons.contains(beacon.getAddress())) {
                beacon.updateInvisibleBeacon();
            }
        }

        // Sort
        Collections.sort(beaconList, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon lhs, Beacon rhs) {
                return (int)(lhs.getMeanDistance() - rhs.getMeanDistance());
            }
        });
    }

    @NonNull
    private RangeNotifier createRangeNotifier() {
        return new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<org.altbeacon.beacon.Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    updateBeaconListWith(beacons);
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
                Log.i(BTAG, "I no longer see any beacon");
                try {
                    updateBeaconListWith(null);
                    stopMonitoring(region);
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

    private void startMonitoring(Region region) throws RemoteException {
        if (BuildConfig.DEBUG && !region.equals(allBeaconsRegion)) { throw new AssertionError("Trying to monitor an unknown region"); }
        if (BuildConfig.DEBUG && monitoring) { throw new AssertionError("The beacon detector is already monitoring"); }
        beaconManager.startMonitoringBeaconsInRegion(region);
        monitoring = true;
    }

    private void stopMonitoring(Region region) throws RemoteException {
        if (BuildConfig.DEBUG && !region.equals(allBeaconsRegion)) { throw new AssertionError("Trying to monitor an unknown region"); }
        if (monitoring) {
            beaconManager.stopMonitoringBeaconsInRegion(region);
            monitoring = false;
        }
    }
    //endregion
}
