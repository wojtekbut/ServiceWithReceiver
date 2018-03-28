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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;

public class LocationService extends Service implements LocationListener {
    double latitude;
    double longtitude;
    long time, curtime;
    float dokl;
    Location location = null;
    Context context;
    LocationManager locationManger = null;


    public LocationService() {
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Log.e("service", "sprawdzam uprawnienia");
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Log.e("service", "żądam uprawnienien");
                Intent permissionintent = new Intent(context, MainActivity.class);
                permissionintent.putExtra("uprawnienia", "location");
                startActivity(permissionintent);
                //this.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 200);
            } else {
                locationManger = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                locationManger.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 15000, 0, this);
            }
        }
        //Toast toast = Toast.makeText(context,"Serwis uruchomiony",Toast.LENGTH_LONG);
        //toast.show();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("onstart","intent: " + intent);
        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String rozkaz = extra.getString("rozkaz");
                //Log.e("onstart","rozkaz: "+ rozkaz);
                wykonaj(rozkaz, intent);
            } else {
                //Log.e("onstart","rozkaz jest null");
            }
        } else {
            //Log.e("onstart","intent jest null ");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void wykonaj(String rozkaz, Intent intent) {
        switch (rozkaz) {
            case "wyslij":
                String nadawca = intent.getStringExtra("nadawca");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Log.e("service", "sprawdzam uprawnienia");
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //Log.e("service", "żądam uprawnienien loc");
                        Intent permissionintent = new Intent(context,MainActivity.class);
                        permissionintent.putExtra("uprawnienia","location");
                        startActivity(permissionintent);
                        //this.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 200);
                    } else {
                        //Log.e("service", "mam uprawnienienia location - wysylam");

                        //SmsManager smsManager = SmsManager.getDefault();
                        //ArrayList<String> wiadomosci = smsManager.divideMessage(wiadomosc);
                        //smsManager.sendMultipartTextMessage(adresat, null, wiadomosci, null, null);
                        location = locationManger
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longtitude = location.getLongitude();
                            time = location.getTime();
                            dokl = location.getAccuracy();
                            curtime = System.currentTimeMillis();
                        }
                        long czas = (curtime - time)/1000;
                        long godziny = czas/3600;
                        long minuty = (czas % 3600) / 60;
                        long sekundy = (czas % 3600) % 60;
                        String wiadomosc = "Moja lokalizacja:\n";
                        wiadomosc +=   "dlugość geograficzna  : " + String.valueOf(longtitude);
                        wiadomosc += "\nszerokość geograficzna: " + String.valueOf(latitude);
                        wiadomosc += "\ndokładność: " + String.valueOf(dokl) + " m";
                        wiadomosc += "\ndane z przed  : ";
                        wiadomosc += String.valueOf(godziny) + " godzin " + String.valueOf(minuty) + " minut " + String.valueOf(sekundy) + " sekund.";
                        wiadomosc += "\nLink do Google Maps:\n";
                        wiadomosc += "\nhttps://www.google.pl/maps/?q=" + String.valueOf(latitude) + "," + String.valueOf(longtitude);
                        //nadawca = nadawca.substring(3)
                        //Toast toast = Toast.makeText(context,"Wykonuje wyslij.\n" +  wiadomosc ,Toast.LENGTH_LONG);
                        //toast.show();
                        wyslijSms(nadawca, wiadomosc);
                    }
                }
//                location = locationManger
//                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (location != null) {
//                    latitude = location.getLatitude();
//                    longtitude = location.getLongitude();
//                    time = location.getTime();
//                    dokl = location.getAccuracy();
//                    curtime = System.currentTimeMillis();
//                }
//                long czas = (curtime - time)/1000;
//                long godziny = czas/3600;
//                long minuty = (czas % 3600) / 60;
//                long sekundy = (czas % 3600) % 60;
//                String wiadomosc = "Moja lokalizacja:\n";
//                wiadomosc +=   "dlugość geograficzna  : " + String.valueOf(longtitude);
//                wiadomosc += "\nszerokość geograficzna: " + String.valueOf(latitude);
//                wiadomosc += "\ndokładność: " + String.valueOf(dokl) + " m";
//                wiadomosc += "\ndane z przed  : ";
//                wiadomosc += String.valueOf(godziny) + " godzin " + String.valueOf(minuty) + " minut " + String.valueOf(sekundy) + " sekund.";
//                wiadomosc += "\nLink do Google Maps:\n";
//                wiadomosc += "\nhttps://www.google.pl/maps/?q=" + String.valueOf(latitude) + "," + String.valueOf(longtitude);
//                //nadawca = nadawca.substring(3)
//
//                Toast toast = Toast.makeText(context,"Wykonuje wyslij.\n" +  wiadomosc ,Toast.LENGTH_LONG);
//                toast.show();
//                wyslijSms(nadawca, wiadomosc);
                break;
        }
    }

    public void wyslijSms(String adresat, String wiadomosc){
        //String adres = adresat;
        //Log.e("wyslijSms", "wysyłam wiadomosc: " + adresat + "\n" + wiadomosc);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Log.e("service wyslij", "sprawdzam uprawnienia sms");
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                //Log.e("service wyslij", "żądam uprawnienien sms");
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("uprawnienia","sms");
                startActivity(intent);
                //this.requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 200);
            } else {
                //Log.e("service", "mam uprawnienienia sms- wysylam");
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> wiadomosci = smsManager.divideMessage(wiadomosc);
                smsManager.sendMultipartTextMessage(adresat, null, wiadomosci, null, null);
            }
        }
        //SmsManager smsManager = SmsManager.getDefault();
        //smsManager.sendTextMessage("+48604442591", null, wiadomosc, null, null);
        //Log.e("wyslijSms", "wiadomosc powinna byc wyslana.");
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
