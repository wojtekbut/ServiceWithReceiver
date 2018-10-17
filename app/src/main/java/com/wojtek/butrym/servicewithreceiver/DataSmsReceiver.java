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
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        String nadawca ="";
        if (bundle != null){
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            byte[] data = null;
            msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
            nadawca = msgs[0].getOriginatingAddress().toString();
            data = msgs[0].getUserData();
            for (int index=0; index < data.length; index++) {
                str += Character.toString((char) data[index]);
            }
        }
        Intent serviceintent = new Intent(context,LocationService.class);
        serviceintent.putExtra("rozkaz","wyslij");
        serviceintent.putExtra("nadawca", nadawca);
        serviceintent.putExtra("wiadomosc", str);
        context.startService(serviceintent);
    }
}
