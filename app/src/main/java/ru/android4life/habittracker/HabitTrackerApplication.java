package ru.android4life.habittracker;

import android.app.Application;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

/**
 * Created by alnedorezov on 10/20/16.
 */

/*
This class created for app to execute old habits deletion & new habits creation just once per application start
https://stackoverflow.com/questions/7360846/how-can-i-execute-something-just-once-per-application-start
 */

public class HabitTrackerApplication extends Application {
    public HabitTrackerApplication() {
        // this method fires only once per application start.
        // getApplicationContext returns null here
    }

    private static void createSchedulesForExistingHabitsForTheNextMonthAndDeleteHabitSchedulesOlderThanThirtyOneDay(Context context) {
        createSchedulesForAllExistingHabits(context);

        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        habitScheduleDAO.deleteHabitSchedulesOlderThanThirtyOneDay();
    }

    private static void createSchedulesForAllExistingHabits(Context context) {
        HabitDAO habitDAO = new HabitDAO(context);
        List<Habit> habits = (List<Habit>) habitDAO.findAll();
        for (Habit habit : habits)
            createSchedulesForExistingHabitForTheNextMonthById(context, habit.getId());
    }

    private static void createSchedulesForExistingHabitForTheNextMonthById(Context context, int habitId) {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(habitId);
        Calendar habitScheduleDateTimeCalendar = new GregorianCalendar();
        habitScheduleDateTimeCalendar.setTime(habitSchedules.get(0).getDatetime());
        // Get how many days in current month
        int monthMaxDays = habitScheduleDateTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date habitDay = habitScheduleDateTimeCalendar.getTime();
        habitScheduleDateTimeCalendar.add(Calendar.DAY_OF_YEAR, monthMaxDays);
        Date afterAMonth = habitScheduleDateTimeCalendar.getTime();
        habitScheduleDateTimeCalendar.add(Calendar.DAY_OF_YEAR, monthMaxDays * (-1));
        boolean habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
        boolean[] mCheckedItems = {false, false, false, false, false, false, false};

        if (habitSchedules.size() <= 2) { // MONTHLY
            while (habitDayBeforeAfterAMonth) {
                habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                habitScheduleDateTimeCalendar.add(Calendar.MONTH, 1);
                habitDay = habitScheduleDateTimeCalendar.getTime();
                habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
            }
        } else if (habitSchedules.size() >= 28) { // DAILY
            while (habitDayBeforeAfterAMonth) {
                habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                habitScheduleDateTimeCalendar.add(Calendar.DATE, 1);
                habitDay = habitScheduleDateTimeCalendar.getTime();
                habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
            }
        } else if (habitSchedules.size() >= 4 && habitSchedules.size() <= 5) { // WEEKLY
            while (habitDayBeforeAfterAMonth) {
                habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                habitScheduleDateTimeCalendar.add(Calendar.DAY_OF_YEAR, 7);
                habitDay = habitScheduleDateTimeCalendar.getTime();
                habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
            }
        } else { // SPECIFIED DAYS
            for (HabitSchedule habitSchedule : habitSchedules) {
                habitScheduleDateTimeCalendar.setTime(habitSchedule.getDatetime());
                mCheckedItems[habitScheduleDateTimeCalendar.get(Calendar.DAY_OF_WEEK) - 1] = true;
            }
            for (int i = 0; i < 7; i++) {
                if (mCheckedItems[i]) {
                    habitScheduleDateTimeCalendar.set(Calendar.DAY_OF_WEEK, i + 1);
                    habitDay = habitScheduleDateTimeCalendar.getTime();
                    while (habitDayBeforeAfterAMonth) {
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                        habitScheduleDateTimeCalendar.add(Calendar.DAY_OF_YEAR, 7);
                        habitDay = habitScheduleDateTimeCalendar.getTime();
                        habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // this method fires once as well as constructor
        // but also application has context here
        createSchedulesForExistingHabitsForTheNextMonthAndDeleteHabitSchedulesOlderThanThirtyOneDay(getApplicationContext());
    }
}
