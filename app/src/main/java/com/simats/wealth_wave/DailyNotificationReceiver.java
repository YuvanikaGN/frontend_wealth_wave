package com.simats.wealth_wave;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;



public class DailyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DailyNotification", "Receiver triggered!"); // debug log

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "daily_channel")
                .setSmallIcon(R.drawable.notif) // your icon
                .setContentTitle("Wealth Wave")
                .setContentText("Test notification: Don’t forget to update your expenses!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
    }
}
