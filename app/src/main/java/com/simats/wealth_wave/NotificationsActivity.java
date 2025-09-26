package com.simats.wealth_wave;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class NotificationsActivity extends AppCompatActivity {

    private Switch toggleSwitch2, toggleSwitch3;
    private SharedPreferences sharedPreferences;
    private CompoundButton.OnCheckedChangeListener dailyListener, inactivityListener;
    private static final int REQ_POST_NOTIFICATIONS = 1001;
    private static final String CHANNEL_IMMEDIATE = "immediate_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        toggleSwitch2 = findViewById(R.id.toggleSwitch2);
        toggleSwitch3 = findViewById(R.id.toggleSwitch3);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // restore
        boolean dailyEnabled = sharedPreferences.getBoolean("daily_notifications", false);
        boolean inactivityEnabled = sharedPreferences.getBoolean("inactivity_reminders", false);

        // create channels up front (if API >= 26)
        createNotificationChannels();

        // define listeners (re-usable so we can temporarily remove when programmatically changing the switch)
        dailyListener = (buttonView, isChecked) -> {
            if (isChecked) {
                // user turned ON daily reminders
                if (!isNotificationPermissionAndEnabled()) {
                    // request permission OR ask user to enable notifications in settings
                    requestNotificationPermissionOrOpenSettings();
                } else {
                    showImmediateNotification(); // welcome notification
                    scheduleDailyReminder();
                    sharedPreferences.edit().putBoolean("daily_notifications", true).apply();
                }
            } else {
                // user turned OFF
                cancelDailyReminder();
                sharedPreferences.edit().putBoolean("daily_notifications", false).apply();
            }
        };

        inactivityListener = (buttonView, isChecked) -> {
            if (isChecked) {
                if (!isNotificationPermissionAndEnabled()) {
                    requestNotificationPermissionOrOpenSettings();
                } else {
                    scheduleInactivityReminder();
                    sharedPreferences.edit().putBoolean("inactivity_reminders", true).apply();
                }
            } else {
                cancelInactivityReminder();
                sharedPreferences.edit().putBoolean("inactivity_reminders", false).apply();
            }
        };

        // attach
//        toggleSwitch2.setOnCheckedChangeListener(dailyListener);
//        toggleSwitch3.setOnCheckedChangeListener(inactivityListener);
//
//        // set UI state AFTER listeners attached (so toggles represent saved state)
//        toggleSwitch2.setChecked(dailyEnabled);
//        toggleSwitch3.setChecked(inactivityEnabled);

        // set UI state first (so toggles represent saved state)
        toggleSwitch2.setChecked(dailyEnabled);
        toggleSwitch3.setChecked(inactivityEnabled);

// now attach listeners
        toggleSwitch2.setOnCheckedChangeListener(dailyListener);
        toggleSwitch3.setOnCheckedChangeListener(inactivityListener);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home_bot);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home_bot) startActivity(new Intent(this, HomeActivity.class));
            if (id == R.id.nav_savings_plan_bot) startActivity(new Intent(this, SavingsActivity.class));
            else if (id == R.id.nav_investment_bot) startActivity(new Intent(this, ProgressTrackingActivity.class));
            else if (id == R.id.nav_profile_bot) startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(0, 0);
            return true;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }

    // ---------- Permission + settings helpers ----------

    private boolean isNotificationPermissionAndEnabled() {
        // For Android 13+ both runtime permission and notifications enabled are required.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
            boolean perm = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            boolean enabled = NotificationManagerCompat.from(this).areNotificationsEnabled();
            return perm && enabled;
        } else {
            // older devices: check whether notifications are enabled by OS for the app
            return NotificationManagerCompat.from(this).areNotificationsEnabled();
        }
    }

    private void requestNotificationPermissionOrOpenSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // runtime permission is required on Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                    // show rationale and then request
                    new AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("We need permission to send you reminders. Please allow notifications.")
                            .setPositiveButton("Allow", (d, w) -> ActivityCompat.requestPermissions(NotificationsActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTIFICATIONS))
                            .setNegativeButton("Cancel", (d, w) -> revertPendingSwitches())
                            .show();
                } else {
                    // direct request
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTIFICATIONS);
                }
                return;
            }
        }
        // If we reach here either runtime permission was already granted OR we are on older Android.
        // But notifications may still be disabled in Settings. If disabled -> open settings.
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Notifications disabled")
                    .setMessage("Notifications for this app are disabled in system settings. Open settings to enable?")
                    .setPositiveButton("Open settings", (d, w) -> openNotificationSettings())
                    .setNegativeButton("Cancel", (d, w) -> revertPendingSwitches())
                    .show();
        } else {
            // Shouldn't normally reach here (we already checked earlier), but safe fallback
//            showImmediateNotification();
//            scheduleDailyReminder();
//            sharedPreferences.edit().putBoolean("daily_notifications", true).apply();
        }
    }

    private void revertPendingSwitches() {
        // revert UI & prefs if user declined the permission prompt / cancelled the dialog
        if (toggleSwitch2.isChecked()) {
            toggleSwitch2.setOnCheckedChangeListener(null);
            toggleSwitch2.setChecked(false);
            toggleSwitch2.setOnCheckedChangeListener(dailyListener);
            cancelDailyReminder();
            sharedPreferences.edit().putBoolean("daily_notifications", false).apply();
        }
        if (toggleSwitch3.isChecked()) {
            toggleSwitch3.setOnCheckedChangeListener(null);
            toggleSwitch3.setChecked(false);
            toggleSwitch3.setOnCheckedChangeListener(inactivityListener);
            cancelInactivityReminder();
            sharedPreferences.edit().putBoolean("inactivity_reminders", false).apply();
        }
    }

    private void openNotificationSettings() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26+ has dedicated app notification settings action (works well on modern devices)
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // older devices - fallback with package + uid extras
            intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS")
                    .putExtra("app_package", getPackageName())
                    .putExtra("app_uid", getApplicationInfo().uid);
        } else {
            // very old fallback: open application details
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", getPackageName(), null));
        }
        startActivity(intent);
    }

    // handle the runtime permission callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_POST_NOTIFICATIONS) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                // permission granted — confirm that notifications are enabled at OS level
                if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Enable notifications")
                            .setMessage("Notifications are still disabled for this app in system settings. Would you like to open settings now?")
                            .setPositiveButton("Open settings", (d, w) -> openNotificationSettings())
                            .setNegativeButton("Cancel", (d, w) -> revertPendingSwitches())
                            .show();
                } else {
                    // schedule those reminders that the user has toggled ON
                    if (toggleSwitch2.isChecked()) {
                        showImmediateNotification();
                        scheduleDailyReminder();
                        sharedPreferences.edit().putBoolean("daily_notifications", true).apply();
                    }
                    if (toggleSwitch3.isChecked()) {
                        scheduleInactivityReminder();
                        sharedPreferences.edit().putBoolean("inactivity_reminders", true).apply();
                    }
                }
            } else {
                // denied — offer to open settings or revert toggles
                new AlertDialog.Builder(this)
                        .setTitle("Permission denied")
                        .setMessage("You denied notification permission. To receive reminders, allow notifications in Settings.")
                        .setPositiveButton("Open settings", (d, w) -> openNotificationSettings())
                        .setNegativeButton("Cancel", (d, w) -> revertPendingSwitches())
                        .show();
            }
        }
    }

    // ---------- Notification channel + immediate notification ----------
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_IMMEDIATE,
                    "Immediate Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
            // (create other channels here if you have different channel categories)
        }
    }

    private void showImmediateNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = CHANNEL_IMMEDIATE;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("WealthWave")
                .setContentText("From now on you'll get daily notifications about your savings")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(2001, builder.build());
    }

    // ---------- Existing scheduling / cancel methods (kept from your original file) ----------

    void scheduleDailyReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 20);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void cancelDailyReminder() {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    void scheduleInactivityReminder() {
        Intent intent = new Intent(this, InactivityReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11); // e.g., 8 PM (adjust if you want 6 PM)
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void cancelInactivityReminder() {
        Intent intent = new Intent(this, InactivityReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
