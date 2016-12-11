package com.bureau.nocomment.exporizon.ble;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by RVB on 09/12/2016.
 */

public class Beacon {
    private static int MEMORY = 3;
    private static double THRESHOLD = 3.0;

    private String address;
    private int zone;
    private Queue<Double> observations;

    private double meanDistance;

    Beacon(String address, int zone) {
        this.address = address;
        this.observations = new LinkedList<Double>();
        this.meanDistance = Double.MAX_VALUE;
        this.zone = zone;
    }

    public double getMeanDistance() {
        return meanDistance;
    }

    public String getStatusString() {
        return (meanDistance > 100.0 ? "+inf" : String.format("%.2fm.", meanDistance)) + "&T=" + observations.size();
    }

    public String getAddress() {
        return address;
    }

    public int getZone() {
        return zone;
    }

    void updateVisibleBeacon(double distance) {
        // Append value
        observations.add(distance);
        if (observations.size() > MEMORY) {
            observations.remove();
        }
        // Compute new floating mean
        meanDistance = 0.;
        for (Double value : observations) {
            meanDistance += value;
        }
        meanDistance /= (double)(observations.size());
    }

    void updateInvisibleBeacon() {
        observations.clear();
        meanDistance = Double.MAX_VALUE;
    }

    boolean isReliablyInSight() {
        return meanDistance < THRESHOLD && (observations.size() == MEMORY);
    }

    void makeOutOfSight() {
        updateInvisibleBeacon();
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof Beacon) ) return false;
        return address.equals(((Beacon) o).address);
    }
}
