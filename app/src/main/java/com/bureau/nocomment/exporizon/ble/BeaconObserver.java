package com.bureau.nocomment.exporizon.ble;

/**
 * Created by RVB on 11/12/2016.
 */

public interface BeaconObserver {
    void onBeaconDetectedWithinCloseRange(Beacon beacon);
}
