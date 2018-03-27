package com.wojtek.butrym.servicewithreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DataSmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Intent intent1 = new Intent(context,LocationService.class);
        intent1.putExtra("rozkaz","wyslij");
        context.startService(intent1);
    }
}
