package com.wojtek.butrym.servicewithreceiver;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    Button send;
    String extra = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startintent = getIntent();
        //Log.e("main", "intent : " + startintent);
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

//    public void sendSms(View view) {
//
//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage("604442591", null, " test wiadomosci", null, null);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                //Log.e("onClick", "kliknieto");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Log.e("onClick", "sprawdzam uprawnienia");
                    if (checkSelfPermission(Manifest.permission_group.SMS) != PackageManager.PERMISSION_GRANTED) {
                        //Log.e("onClick", "żądam uprawnienien");
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
        //Log.e("onResult", "dostałem odpowiedz");
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
}

