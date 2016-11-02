package ru.android4life.habittracker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ru.android4life.habittracker.HabitNotification;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Integer habitScheduleId = bundle.getInt("habitScheduleId", -1);
        if (habitScheduleId != -1) {
            HabitDAO habitDAO = new HabitDAO(context);
            HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
            HabitSchedule schedule = (HabitSchedule) habitScheduleDAO.findById(habitScheduleId);
            Habit habit = (Habit) habitDAO.findById(schedule.getHabitId());
            boolean isConfirmation = bundle.getBoolean("confirmation", false);
            if (isConfirmation) {
                HabitNotification.createConfirmation(context, schedule, habit);
            } else {
                HabitNotification.createReminder(context, schedule, habit);
            }
        }
    }
}