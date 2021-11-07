package com.mroxny.mobilemapapp;

import android.location.Address;

import org.osmdroid.util.GeoPoint;

import java.util.Comparator;

public class SortPlaces implements Comparator<Address> {
    GeoPoint currentPos;

    public SortPlaces(GeoPoint current){
        currentPos = current;
    }

    @Override
    public int compare(Address place1, Address place2) {
        double lat1 = place1.getLatitude();
        double lon1 = place1.getLongitude();
        double lat2 = place2.getLatitude();
        double lon2 = place2.getLongitude();

        double distanceToPlace1 = distance(currentPos.getLatitude(), currentPos.getLongitude(), lat1, lon1);
        double distanceToPlace2 = distance(currentPos.getLatitude(), currentPos.getLongitude(), lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }


    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}
