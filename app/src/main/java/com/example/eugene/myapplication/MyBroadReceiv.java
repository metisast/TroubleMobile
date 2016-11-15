package com.example.eugene.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by eugene on 11/6/16.
 */

public class MyBroadReceiv extends BroadcastReceiver {

    final String LOG_TAG = "myLogs";
    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive " + intent.getAction());

        Toast toast = Toast.makeText(context.getApplicationContext(), "Broadcast", Toast.LENGTH_SHORT);
        toast.show();

        context.startService(new Intent(context, MyService.class));
    }
}
