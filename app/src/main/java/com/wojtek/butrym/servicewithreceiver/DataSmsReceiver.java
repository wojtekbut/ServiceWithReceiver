package com.wojtek.butrym.servicewithreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class DataSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast toast = Toast.makeText(context,"DataReceiver - uruchamianie" ,Toast.LENGTH_LONG);
        //toast.show();
        //Log.e("DataSmsReceiver", "start");
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        String nadawca ="";
        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            byte[] data = null;
            msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
            nadawca = msgs[0].getOriginatingAddress();
            data = msgs[0].getUserData();
            for (int index=0; index < data.length; index++) {
                str += Character.toString((char) data[index]);
            }
            str += "\n";
        }
        //Toast toast1 = Toast.makeText(context,"Dostalem Data Smsa\n" + str,Toast.LENGTH_LONG);
        //toast1.show();
        //Log.e("dostałem i wyswietlam: ", str);
        Intent serviceintent = new Intent(context,LocationService.class);
        serviceintent.putExtra("rozkaz","wyslij");
        serviceintent.putExtra("nadawca", nadawca);
        serviceintent.putExtra("wiadomosc", str);
        context.startService(serviceintent);
    }
}
