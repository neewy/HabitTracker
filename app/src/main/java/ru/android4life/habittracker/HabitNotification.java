package ru.android4life.habittracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.reciever.AlarmReceiver;
import ru.android4life.habittracker.reciever.HabitPerformReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Class, which helps to create notifications
 * for all habit schedules
 *
 * Created by neewy on 30.10.16.
 */

public class HabitNotification {

    private Context context;
    private HabitDAO habitDAO;
    private AlarmManager alarmManager;
    private HabitScheduleDAO habitScheduleDAO;

    private HashMap<Habit, List<HabitSchedule>> habitsAndSchedules = new HashMap<>();

    public HabitNotification(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
    }

    public static void createNotification(Context context, HabitSchedule habitSchedule, Habit habit) {
        Intent skipNotificationIntent = new Intent(context, HabitPerformReceiver.class);
        skipNotificationIntent.setAction(habitSchedule.getId() + " | " + habitSchedule.getHabitId());

        skipNotificationIntent.putExtra("habitScheduleId", habitSchedule.getId());
        skipNotificationIntent.putExtra("isDone", false);

        PendingIntent skipPendingIntent = PendingIntent.getBroadcast(context,
                0, skipNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent doneNotificationIntent = new Intent(context, HabitPerformReceiver.class);
        doneNotificationIntent.setAction(habitSchedule.getId() + " | " + habitSchedule.getHabitId());

        doneNotificationIntent.putExtra("habitScheduleId", habitSchedule.getId());
        doneNotificationIntent.putExtra("isDone", true);

        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context,
                1, doneNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.ic_add_habit_reminder)
                .addAction(R.drawable.skip, context.getString(R.string.skip), skipPendingIntent)
                .addAction(R.drawable.done, context.getString(R.string.done), donePendingIntent)
                .setTicker(String.format(context.getString(R.string.did_i_question),
                        habit.getQuestion()))
                .setWhen(habitSchedule.getDatetime().getTime())
                .setAutoCancel(true)
                .setContentTitle(habit.getName())
                .setContentText(String.format(context.getString(R.string.did_i_question),
                        habit.getQuestion()));

        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_ALL;

        //TODO: Add custom tune and vibration!
        //notification.sound = Uri.parse(habit.getAudioResource());

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(habitSchedule.getId(), notification);
    }

    private void populateHabitList() {
        List<Habit> habits = (List<Habit>) habitDAO.findAll();
        for (Habit habit : habits) {
            List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(habit.getId());
            habitsAndSchedules.put(habit, habitSchedules);
        }
    }

    public void createAllAlarms() {
        if (habitsAndSchedules.isEmpty()) {
            populateHabitList();
        }
        Iterator<Habit> habitIterator = habitsAndSchedules.keySet().iterator();
        while (habitIterator.hasNext()) {
            Habit habit = habitIterator.next();
            for (HabitSchedule schedule : habitsAndSchedules.get(habit)) {
                PendingIntent pendingIntent = createPendingIntent(schedule.getId());
                alarmManager.cancel(pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.getDatetime().getTime(), pendingIntent);
            }
        }
    }

    public void deleteHabitAlarms(int habitId) {
        List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(habitId);
        for (HabitSchedule schedule : habitSchedules) {
            deleteHabitScheduleAlarm(schedule.getId());
        }
    }

    public void deleteHabitScheduleAlarm(int habitScheduleId) {
        alarmManager.cancel(createPendingIntent(habitScheduleId));
    }

    private PendingIntent createPendingIntent(int habitScheduleId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("habitScheduleId", habitScheduleId);
        intent.setAction(String.valueOf(habitScheduleId));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, habitScheduleId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
