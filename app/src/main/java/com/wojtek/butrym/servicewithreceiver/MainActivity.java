package com.wojtek.butrym.servicewithreceiver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.prefs.Preferences;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String PREFERENCES_NAME = "dane";
    private static final String PREFERENCES_FIELD_TELEFON = "Telefon";
    private static final String PREFERENCES_FIELD_HASLO = "Haslo";
    Button send;
    String extra = null;
    SharedPreferences preferences;
    String telefon, haslo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        telefon = preferences.getString(PREFERENCES_FIELD_TELEFON, null);
        haslo = preferences.getString(PREFERENCES_FIELD_HASLO, null);
        if (telefon == null || haslo == null) {
            pierwszeUruchomienie();
        }
        Intent startintent = getIntent();
        if ((extra = startintent.getStringExtra("uprawnienia")) != null){
            switch (extra) {
                case "location":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                    }
                    break;
                case "sms":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 200);
                    }
                    break;
            }
            finish();
        }
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        send = (Button) findViewById(R.id.send);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission_group.SMS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 200);
                    } else {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("+48604442591", null, " test wiadomosci", null, null);
                    }
                }
                break;
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Log.e("onResult", "mam uprawnienia - wysyłam");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+48604442591", null, " test wiadomosci", null, null);
                } else {
                    //Log.e("onReult", "nie mam uprawnienien");
                }
            }
        }
    }

    void pierwszeUruchomienie(){
        haslo = generujHaslo();

    }

    String generujHaslo(){
        char[] znaki = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            char c = znaki[random.nextInt(znaki.length)];
            sb.append(c);
        }
        return sb.toString();
    }


}

