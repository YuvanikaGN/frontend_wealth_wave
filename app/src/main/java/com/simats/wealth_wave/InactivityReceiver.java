package com.simats.wealth_wave;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class InactivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("inactivity_reminders", false);
        if (!enabled) return;

        long lastSave = prefs.getLong("last_save_time", -1);
        long now = System.currentTimeMillis();

        long threshold = TimeUnit.DAYS.toMillis(7); // 🔹 7 days of inactivity
//        long threshold = TimeUnit.MINUTES.toMillis(1);


        if (lastSave == -1 || now - lastSave >= threshold) {
            Intent activityIntent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            String channelId = "inactivity_channel";
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId, "Inactivity Reminders", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle("WealthWave")
                    .setContentText("You haven't saved in a while. Update your savings today!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            notificationManager.notify(3001, builder.build());
        }
    }
}
