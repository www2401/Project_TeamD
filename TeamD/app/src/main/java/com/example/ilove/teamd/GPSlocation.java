package com.example.ilove.teamd;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hyewonkim on 2017. 8. 9..
 */

public class GPSlocation {

    public static LatLng latLng = null;


    public LatLng get_LatLng() {
        return latLng;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // arrayPoints.add(new LatLng(Double.parseDouble(intent.getStringExtra("LAT")),Double.parseDouble(intent.getStringExtra("LANG"))));
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}

