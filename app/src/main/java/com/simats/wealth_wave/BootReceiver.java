package com.simats.wealth_wave;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
            boolean isEnabled = prefs.getBoolean("daily_reminder_enabled", false);

            if (isEnabled) {
                // Reschedule daily reminder
                new NotificationsActivity().scheduleDailyReminder();
            }
        }
    }
}
