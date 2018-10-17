package com.wojtek.butrym.servicewithreceiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Intent permissionintent = new Intent(context, MainActivity.class);
                permissionintent.putExtra("uprawnienia", "location");
                startActivity(permissionintent);
            } else {
                locationManger = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                locationManger.requestLocationUpdates(LocationManager
                        .GPS_PROVIDER, 15000, 0, this);
            }
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String rozkaz = extra.getString("rozkaz");
                try {
                    wykonaj(rozkaz, intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        } else {
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void wykonaj(String rozkaz, Intent intent) throws IOException {
        String dostwiad = intent.getStringExtra("wiadomosc");
        String nadawca = intent.getStringExtra("nadawca");
        switch (dostwiad) {
            case "Gdzie jestes?":
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Intent permissionintent = new Intent(context,MainActivity.class);
                        permissionintent.putExtra("uprawnienia","location");
                        startActivity(permissionintent);
                    } else {
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
                        wyslijSmsData(nadawca, wiadomosc);
                    }
                }
                break;
            case "Zadzwon":
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            Log.e("timer", "zamykam");
                            Runtime.getRuntime().exec(new String[] {"su", "-c", "input keyevent 26"});
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);
                Intent callintent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + nadawca));
                callintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callintent);
                break;
            case "Ustawienia":
                Log.e("Ustawienia", "zaczynam.");
                try {
                    Process p = Runtime.getRuntime().exec(new String[] {"su","-c","settings get global",
                    "wifi_on", "&&", "settings get global mobile_data","&&",
                    "settings get secure location_providers_allowed"});
                    Log.e("Ustawienia", "po exec.");

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));
                    p.getErrorStream().close();
                    Log.e("Ustawienia", "czytam.");

                    String s = null;
                    s= stdInput.readLine();
                    String wifi = s;
                    Log.e("wifi", wifi);

                    s= stdInput.readLine();
                    String siec = s;
                    Log.e("Siec", siec);

                    s= stdInput.readLine();
                    String gps = s;
                    Log.e("GPS", gps);

                    p.getInputStream().close();
                    String wiadomosc = "Stan WiFi: ";
                    if(wifi.equals("1"))
                        wiadomosc += "on\n";
                    else
                        wiadomosc += "off\n";
                    wiadomosc += "Stan Sieci: ";
                    if(siec.equals("1"))
                        wiadomosc += "on\n";
                    else
                        wiadomosc += "off\n";
                    wiadomosc += "Stan GPS: ";
                    if(gps.equals("gps"))
                        wiadomosc += "on\n";
                    else
                        wiadomosc += "off\n";
                    wyslijSmsData(nadawca, wiadomosc);

                } catch (Exception e){
                    e.printStackTrace();
                }
        }
    }

    public void wyslijSms(String adresat, String wiadomosc){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("uprawnienia","sms");
                startActivity(intent);
            } else {
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> wiadomosci = smsManager.divideMessage(wiadomosc);
                smsManager.sendMultipartTextMessage(adresat, null, wiadomosci, null, null);
            }
        }
    }

    public void wyslijSmsData(String adresat, String wiadomosc) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("uprawnienia","sms");
                startActivity(intent);
            } else {
                sendEncryptedMessage(wiadomosc, adresat);
            }
        }
    }

    private void sendEncryptedMessage(String plainMessage, String adresat) throws IOException {

        Integer PDU_SIZE = 130;
        short port = 6544;
        byte[] messageSerialized = plainMessage.getBytes();
        SmsManager smsManager = SmsManager.getDefault();
        byte[] preparedMessage = new byte[messageSerialized.length + 1];
        preparedMessage[0] = 1;
        System.arraycopy(messageSerialized, 0, preparedMessage, 1, messageSerialized.length);
        if (preparedMessage.length > PDU_SIZE) {
            Boolean equalChunks;
            Integer mLength = preparedMessage.length;
            Integer numOfChunks = mLength / PDU_SIZE;
            if (mLength % PDU_SIZE == 0) {
                preparedMessage[0] = numOfChunks.byteValue();
                equalChunks = true;
            } else {
                Integer tmpChunks = numOfChunks + 1;
                preparedMessage[0] = tmpChunks.byteValue();
                equalChunks = false;
            }
            for (int i = 0; i < numOfChunks; i++) {
                byte[] chunkArray = Arrays.copyOfRange(preparedMessage, i * PDU_SIZE, (i + 1) * PDU_SIZE);
                smsManager.sendDataMessage(adresat, null, port, chunkArray, null, null); //todo: dodać jeszcze PendingDeliveryIntent
                Log.e("send data", "wysyłam nr " + i + " do " + adresat + " na " + port+ "[0]: " + chunkArray[0]);
            }
            if (!equalChunks) {
                byte[] chunkArray = Arrays.copyOfRange(preparedMessage, numOfChunks * PDU_SIZE, mLength);
                smsManager.sendDataMessage(adresat, null, port, chunkArray, null, null); //todo: dodać jeszcze PendingDeliveryIntent
                Log.e("send data", "wysyłam bez nru " + " do " + adresat + " na " + port);
            }
        } else {
            smsManager.sendDataMessage(adresat, null, port, preparedMessage, null, null); //todo: dodać jeszcze PendingDeliveryIntent
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
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
