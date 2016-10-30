package ru.android4life.habittracker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.android4life.habittracker.HabitNotification;

public class BootReceiver extends BroadcastReceiver {

    /**
     * By default, all alarms are canceled when a device shuts down.
     * To prevent this from happening, you can design your application to automatically restart a repeating alarm if the user reboots the device.
     * This ensures that the AlarmManager will continue doing its task without the user needing to manually restart the alarm.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            HabitNotification notification = new HabitNotification(context);
            notification.createAllAlarms();
        }
    }
}