package ru.android4life.habittracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.reciever.AlarmReceiver;
import ru.android4life.habittracker.reciever.HabitPerformReceiver;

import static android.content.Context.ALARM_SERVICE;
import static ru.android4life.habittracker.utils.StringConstants.CONFIRMATION;
import static ru.android4life.habittracker.utils.StringConstants.DONE;
import static ru.android4life.habittracker.utils.StringConstants.HABIT_SCHEDULE_ID;
import static ru.android4life.habittracker.utils.StringConstants.IS_DONE;
import static ru.android4life.habittracker.utils.StringConstants.MIN_IN_MS;
import static ru.android4life.habittracker.utils.StringConstants.OPEN;
import static ru.android4life.habittracker.utils.StringConstants.SKIP;

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

    private List<Habit> habits = new ArrayList<>();

    public HabitNotification(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
    }

    /**
     * Reminder is the push notification,
     * that a user can see the time a habit
     * was scheduled
     * <p>
     * <b>
     * The id of reminder notification is
     * the habitSchedule.id * 2
     * </b>
     *
     * @param context
     * @param habitSchedule
     * @param habit
     */
    public static void createReminder(Context context, HabitSchedule habitSchedule, Habit habit) {
        Intent openHabitsIntent = new Intent(context, MainActivity.class);
        openHabitsIntent.setAction(habitSchedule.getId() + OPEN); //to distinguish between different intents

        // Intent to open the MainActivity is put into pending intent
        PendingIntent openPendingIntent = PendingIntent.getActivity(context,
                0, openHabitsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(openPendingIntent)
                .setSmallIcon(R.drawable.ic_add_habit_reminder)
                .setTicker(String.format(context.getString(R.string.did_i_question), //top line question
                        habit.getQuestion()))
                .setWhen(habitSchedule.getDatetime().getTime())
                .setAutoCancel(true) //auto cancel if clicked
                .setContentTitle(habit.getName())
                .setContentText(String.format(context.getString(R.string.did_i_question),
                        habit.getQuestion()));

        Notification notification = builder.build();
        //notification.defaults |= Notification.DEFAULT_ALL; //all default settings (vibration, tune, etc.)

        //TODO: Add custom tune and vibration!
        notification.sound = Uri.parse(habit.getAudioResource());

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // we multiple by two to distinguish between reminder and confirmation
        notificationManager.notify(habitSchedule.getId() * 2, notification);
    }


    /**
     * Confirmation is the push notification,
     * that a user can see if he selected habit
     * with confirmations, and after some time has
     * passed after reminder
     * <b>
     * The id of confirmation notification is
     * the habitSchedule.id * 2 + 1
     * </b>
     *
     * @param context
     * @param habitSchedule
     * @param habit
     */
    public static void createConfirmation(Context context, HabitSchedule habitSchedule, Habit habit) {
        Intent skipConfirmationIntent = new Intent(context, HabitPerformReceiver.class);
        skipConfirmationIntent.setAction(habitSchedule.getId() + SKIP);

        skipConfirmationIntent.putExtra(HABIT_SCHEDULE_ID, habitSchedule.getId());
        skipConfirmationIntent.putExtra(IS_DONE, false);

        PendingIntent skipPendingIntent = PendingIntent.getBroadcast(context,
                0, skipConfirmationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent doneConfirmationIntent = new Intent(context, HabitPerformReceiver.class);
        doneConfirmationIntent.setAction(habitSchedule.getId() + DONE);

        doneConfirmationIntent.putExtra(HABIT_SCHEDULE_ID, habitSchedule.getId());
        doneConfirmationIntent.putExtra(IS_DONE, true);

        PendingIntent donePendingIntent = PendingIntent.getBroadcast(context,
                1, doneConfirmationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.ic_add_habit_reminder)
                .addAction(R.drawable.ic_skip_icon_24dp, context.getString(R.string.skip), skipPendingIntent)
                .addAction(R.drawable.ic_done_icon_24dp, context.getString(R.string.done), donePendingIntent)
                .setTicker(String.format(context.getString(R.string.did_i_question),
                        habit.getQuestion()))
                .setWhen(habitSchedule.getDatetime().getTime())
                .setAutoCancel(true)
                .setContentTitle(habit.getName())
                .setContentText(String.format(context.getString(R.string.did_i_question),
                        habit.getQuestion()));

        Notification notification = builder.build();
        //notification.defaults |= Notification.DEFAULT_ALL;

        //TODO: Add custom tune and vibration!
        notification.sound = Uri.parse(habit.getAudioResource());

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // we multiple by two and add one to distinguish between reminder and confirmation
        notificationManager.notify(habitSchedule.getId() * 2 + 1, notification);
    }


    public void createAllAlarms() {
        if (habits.isEmpty()) {
            habits = (List<Habit>) habitDAO.findAll();
        }
        for (Habit habit : habits) {
            createHabitAlarms(habit);
        }
    }

    public void deleteHabitAlarms(int habitId) {
        List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(habitId);
        for (HabitSchedule schedule : habitSchedules) {
            deleteHabitScheduleAlarms(schedule.getId());
        }
    }

    public void deleteHabitScheduleAlarms(int habitScheduleId) {
        alarmManager.cancel(createPendingIntent(habitScheduleId, true));
        alarmManager.cancel(createPendingIntent(habitScheduleId, false));
    }

    private PendingIntent createPendingIntent(int habitScheduleId, boolean isReminder) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(HABIT_SCHEDULE_ID, habitScheduleId);
        int alarmId;
        if (isReminder) {
            alarmId = habitScheduleId * 2;
            intent.setAction(String.valueOf(alarmId));
            return PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // it is confirmation
            alarmId = habitScheduleId * 2 + 1;
            intent.setAction(String.valueOf(alarmId));
            intent.putExtra(CONFIRMATION, true);
            return PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    /**
     * Creates and re-creates all alarms for the habit
     * @param habit
     */
    public void createHabitAlarms(Habit habit) {
        List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(habit.getId());
        for (HabitSchedule schedule : habitSchedules) {
            //if it is not done or skipped and the time now is less
            if (schedule.isDone() == null && System.currentTimeMillis() <= schedule.getDatetime().getTime()) {
                PendingIntent reminderIntent = createPendingIntent(schedule.getId(), true); //create intent for reminder
                alarmManager.cancel(reminderIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.getDatetime().getTime(), reminderIntent);

                PendingIntent confirmationIntent = createPendingIntent(schedule.getId(), false); //create intent for confirmation
                alarmManager.cancel(confirmationIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, schedule.getDatetime().getTime() + habit.getConfirmAfterMinutes() * MIN_IN_MS, confirmationIntent);
            }
        }
    }
}
