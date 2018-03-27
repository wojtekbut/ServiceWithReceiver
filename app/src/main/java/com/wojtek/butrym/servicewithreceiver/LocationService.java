package com.wojtek.butrym.servicewithreceiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;

public class LocationService extends Service implements LocationListener {
    double latitude;
    double longtitude;
    long time, curtime;
    Location location = null;
    Context context;
    LocationManager locationMangaer = null;
    private LocationListener locationListener = null;

    public LocationService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        context = getApplicationContext();
        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        try {

            locationMangaer.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, 5000, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast toast = Toast.makeText(context,"Serwis uruchomiony",Toast.LENGTH_LONG);
        toast.show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onstart","intent: " + intent);
        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String rozkaz = extra.getString("rozkaz");
                Log.d("onstart","rozkaz: "+ rozkaz);
                wykonaj(rozkaz);
            } else {
                Log.d("onstart","rozkaz jest null");
            }
        } else {
            Log.d("onstart","intent jest null ");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    void wykonaj(String rozkaz) {
        switch (rozkaz) {
            case "wyslij":
                location = locationMangaer
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longtitude = location.getLongitude();
                    time = location.getTime();
                    curtime = System.currentTimeMillis();
                }
                long czas = (curtime - time)/1000;
                long godziny = czas/3600;
                long minuty = (czas % 3600) / 60;
                long sekundy = (czas % 3600) % 60;
                Toast toast = Toast.makeText(context,"Wykonuje wyslij.\ndlugość: " +  longtitude + "\nszerokość: " + latitude
                        + "\nczas: " + godziny + " godzin " + minuty + " minut " + sekundy + " sekund temu." ,Toast.LENGTH_LONG);
                toast.show();

                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
