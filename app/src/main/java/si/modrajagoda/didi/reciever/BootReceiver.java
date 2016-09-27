package si.modrajagoda.didi.reciever;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private static final String MINUTES = "minutes";
    private static final String HOURS = "hours";
    private SharedPreferences settings;
    private int minutes;
    private int hours;

    @Override
    public void onReceive(Context context, Intent intent) {

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        minutes = settings.getInt(MINUTES, 0);
        hours = settings.getInt(HOURS, 22);

        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        Calendar reportTime = Calendar.getInstance();
        reportTime.set(Calendar.HOUR_OF_DAY, hours);
        reportTime.set(Calendar.MINUTE, minutes);
        reportTime.set(Calendar.SECOND, 0);
        Intent notification = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringAlarm = PendingIntent.getBroadcast(context,
                0, notification, PendingIntent.FLAG_CANCEL_CURRENT);

        alarms.setRepeating(AlarmManager.RTC, reportTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, recurringAlarm);

    }
}