package ru.android4life.habittracker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import ru.android4life.habittracker.HabitNotification;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

import static ru.android4life.habittracker.utils.StringConstants.CONFIRMATION;
import static ru.android4life.habittracker.utils.StringConstants.HABIT_SCHEDULE_ID;
import static ru.android4life.habittracker.utils.StringConstants.SHARED_PREF;
import static ru.android4life.habittracker.utils.StringConstants.SILENT_MODE;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Integer habitScheduleId = bundle.getInt(HABIT_SCHEDULE_ID, -1);
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        // if there is a valid habit schedule in bundle and silent mode is off
        if (habitScheduleId != -1 && !sharedPreferences.getBoolean(SILENT_MODE, false)) {
            HabitDAO habitDAO = new HabitDAO(context);
            HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
            HabitSchedule schedule = (HabitSchedule) habitScheduleDAO.findById(habitScheduleId);
            Habit habit = (Habit) habitDAO.findById(schedule.getHabitId());
            boolean isConfirmation = bundle.getBoolean(CONFIRMATION, false);
            if (isConfirmation) {
                HabitNotification.createConfirmation(context, schedule, habit);
            } else {
                HabitNotification.createReminder(context, schedule, habit);
            }
        }
    }
}