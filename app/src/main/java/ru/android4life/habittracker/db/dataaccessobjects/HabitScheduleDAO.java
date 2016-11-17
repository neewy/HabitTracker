package ru.android4life.habittracker.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.DatabaseHelper;
import ru.android4life.habittracker.db.DatabaseManager;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

/**
 * Created by alnedorezov on 9/28/16.
 */

public class HabitScheduleDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public HabitScheduleDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        HabitSchedule habitSchedule = (HabitSchedule) item;
        try {
            index = helper.getHabitScheduleDao().create(habitSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        HabitSchedule habitSchedule = (HabitSchedule) item;

        try {
            index = helper.getHabitScheduleDao().update(habitSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        HabitSchedule habitSchedule = (HabitSchedule) item;

        try {
            index = helper.getHabitScheduleDao().delete(habitSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        HabitSchedule habitSchedule = null;
        try {
            habitSchedule = helper.getHabitScheduleDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return habitSchedule;
    }

    @Override
    public List<?> findAll() {

        List<HabitSchedule> items = new ArrayList<>();

        try {
            items = helper.getHabitScheduleDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        HabitSchedule habitSchedule = null;
        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            habitSchedule = helper.getHabitScheduleDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return habitSchedule;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        HabitSchedule habitSchedule = (HabitSchedule) item;
        try {
            if (helper.getHabitScheduleDao().idExists(habitSchedule.getId())) {
                if (helper.getHabitScheduleDao().queryForId(habitSchedule.getId()).equals(habitSchedule))
                    index = habitSchedule.getId();
                else
                    index = helper.getHabitScheduleDao().update(habitSchedule);
            } else
                index = helper.getHabitScheduleDao().create(habitSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return index;
    }

    public List<HabitSchedule> findHabitSchedulesForToday() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        List<HabitSchedule> items = new ArrayList<>();

        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, today).and().lt(Constants.DATETIME, tomorrow)
                    .and().isNull(Constants.IS_DONE);
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return items;
    }

    public List<HabitSchedule> findHabitSchedulesForTomorrow() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date twoDaysAfterToday = c.getTime();

        List<HabitSchedule> items = new ArrayList<>();

        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, tomorrow).and().lt(Constants.DATETIME, twoDaysAfterToday)
                    .and().isNull(Constants.IS_DONE);
            ;
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        return items;
    }

    public List<HabitSchedule> findHabitSchedulesForNextMonth() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date today = c.getTime();
        c.add(Calendar.MONTH, 1);
        Date monthAfterToday = c.getTime();

        List<HabitSchedule> items = new ArrayList<>();

        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, today).and().lt(Constants.DATETIME, monthAfterToday)
                    .and().isNull(Constants.IS_DONE);
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return items;
    }

    public List<HabitSchedule> findInRange(Date from, Date to) {
        List<HabitSchedule> items = new ArrayList<>();
        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, from).and().lt(Constants.DATETIME, to);
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return items;
    }

    public List<HabitSchedule> findByHabitId(int habitId) {
        List<HabitSchedule> items = new ArrayList<>();
        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().eq(Constants.HABIT_ID, habitId);
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return items;
    }

    public int deleteByHabitId(int habitId) {
        int index = -1;
        try {
            DeleteBuilder<HabitSchedule, Integer> deleteBuilder = helper.getHabitScheduleDao().deleteBuilder();
            deleteBuilder.where().eq(Constants.HABIT_ID, habitId);
            index = deleteBuilder.delete();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return index;
    }

    public int deleteHabitSchedulesOlderThanThirtyOneDay() {
        int index = -1;

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, -1);
        Date monthBeforeToday = c.getTime();

        try {
            DeleteBuilder<HabitSchedule, Integer> deleteBuilder = helper.getHabitScheduleDao().deleteBuilder();
            deleteBuilder.where().lt(Constants.DATETIME, monthBeforeToday);
            index = deleteBuilder.delete();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return index; // The number of rows updated in the database.
    }

    public double getPercentageOfDoneSchedulesForDistinctHabitByHabitId(int habitId) {
        double percentage = 0;
        try {
            int overallHabitSchedules;
            int doneHabitSchedules;
            Date now = new Date();
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().eq(Constants.HABIT_ID, habitId).and().lt(Constants.DATETIME, now);
            overallHabitSchedules = qBuilder.query().size();
            qBuilder.reset();
            Calendar c = new GregorianCalendar();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();
            qBuilder.where().eq(Constants.HABIT_ID, habitId).and().eq(Constants.IS_DONE, true)
                    .and().lt(Constants.DATETIME, tomorrow);
            doneHabitSchedules = qBuilder.query().size();
            if (doneHabitSchedules == 0 && overallHabitSchedules == 0) { // if first habit schedule is planned for future
                percentage = 0;
            } else if (doneHabitSchedules >= overallHabitSchedules) { // if habit was done before scheduled time
                percentage = 100;
            } else {
                float percentageFloat = (float) doneHabitSchedules / overallHabitSchedules;
                percentage = percentageFloat * 100;
            }
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return percentage;
    }

    public Date getDateOfNewestHabitScheduleForDistinctHabitByHabitId(int habitId) {
        HabitSchedule habitSchedule = null;
        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().eq(Constants.HABIT_ID, habitId);
            qBuilder.orderBy(Constants.DATETIME, false); // false for descending order
            qBuilder.limit(1);
            if (qBuilder.query().size() > 0) {
                habitSchedule = helper.getHabitScheduleDao().queryForId(qBuilder.query().get(0).getId());
            }
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }

        if (habitSchedule != null)
            return habitSchedule.getDatetime();
        else
            return null;
    }
}