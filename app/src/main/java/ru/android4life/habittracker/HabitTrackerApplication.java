package ru.android4life.habittracker;

import android.app.Application;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jonathanfinerty.once.Once;
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

        Date maxOfCurrentTimeAndNewestScheduleTime;
        Date newestHabitScheduleDate = habitScheduleDAO.getDateOfNewestHabitScheduleForDistinctHabitByHabitId(habitId);
        if (newestHabitScheduleDate != null) {
            maxOfCurrentTimeAndNewestScheduleTime = new Date(Math.max(habitScheduleDateTimeCalendar.getTime().getTime(),
                    newestHabitScheduleDate.getTime()));

            habitScheduleDateTimeCalendar.setTime(newestHabitScheduleDate);

            // Get how many days in current month
            int monthMaxDays = habitScheduleDateTimeCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Date habitDay = habitScheduleDateTimeCalendar.getTime();
            Calendar anotherDateTimeCalendar = new GregorianCalendar();
            anotherDateTimeCalendar.add(Calendar.DAY_OF_YEAR, monthMaxDays);
            Date afterAMonth = anotherDateTimeCalendar.getTime();
            boolean habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
            boolean[] mCheckedItems = {false, false, false, false, false, false, false};

            if (habitSchedules.size() <= 2) { // MONTHLY
                while (habitDayBeforeAfterAMonth) {
                    if (habitDay.after(maxOfCurrentTimeAndNewestScheduleTime))
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                    habitScheduleDateTimeCalendar.add(Calendar.MONTH, 1);
                    habitDay = habitScheduleDateTimeCalendar.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
            } else if (habitSchedules.size() >= 28) { // DAILY
                while (habitDayBeforeAfterAMonth) {
                    if (habitDay.after(maxOfCurrentTimeAndNewestScheduleTime))
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                    habitScheduleDateTimeCalendar.add(Calendar.DATE, 1);
                    habitDay = habitScheduleDateTimeCalendar.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
            } else if (habitSchedules.size() >= 4 && habitSchedules.size() <= 5) { // WEEKLY
                while (habitDayBeforeAfterAMonth) {
                    if (habitDay.after(maxOfCurrentTimeAndNewestScheduleTime))
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
                            if (habitDay.after(maxOfCurrentTimeAndNewestScheduleTime))
                                habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                            habitScheduleDateTimeCalendar.add(Calendar.DAY_OF_YEAR, 7);
                            habitDay = habitScheduleDateTimeCalendar.getTime();
                            habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Once.initialise(this);
        // this method fires once as well as constructor
        // but also application has context here
        createSchedulesForExistingHabitsForTheNextMonthAndDeleteHabitSchedulesOlderThanThirtyOneDay(getApplicationContext());
    }
}
