package com.sedanza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by anthony on 1/10/15.
 */
public class StartReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(DataService.SERVICE_NAME);
        i.setClass(context,DataService.class);
        context.startService(i);
    }
}
