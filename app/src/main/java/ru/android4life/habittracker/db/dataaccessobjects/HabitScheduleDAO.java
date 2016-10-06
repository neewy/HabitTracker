package ru.android4life.habittracker.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.DatabaseHelper;
import ru.android4life.habittracker.db.DatabaseManager;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.legacy.Main;

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
            helper.getHabitScheduleDao().update(habitSchedule);
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
            helper.getHabitScheduleDao().delete(habitSchedule);
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
        Date today = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();

        List<HabitSchedule> items = new ArrayList<>();

        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, today).and().lt(Constants.DATETIME, tomorrow)
                    .and().eq(Constants.IS_PERFORMED, false).and().eq(Constants.IS_SKIPPED, false);
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
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date twoDaysAfterToday = c.getTime();

        List<HabitSchedule> items = new ArrayList<>();

        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, tomorrow).and().lt(Constants.DATETIME, twoDaysAfterToday)
                    .and().eq(Constants.IS_PERFORMED, false).and().eq(Constants.IS_SKIPPED, false);
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
        Date today = c.getTime();
        c.add(Calendar.MONTH, 1);
        Date monthAfterToday = c.getTime();

        List<HabitSchedule> items = new ArrayList<>();

        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, today).and().lt(Constants.DATETIME, monthAfterToday)
                    .and().eq(Constants.IS_PERFORMED, false).and().eq(Constants.IS_SKIPPED, false);
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return items;
    }

    public List<HabitSchedule> findHabitSchedulesForCurrentMonth() {
        List<HabitSchedule> items = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDay = c.getTime();
        c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
        Date lastDay = c.getTime();
        try {
            QueryBuilder<HabitSchedule, Integer> qBuilder = helper.getHabitScheduleDao().queryBuilder();
            qBuilder.where().ge(Constants.DATETIME, firstDay).and().lt(Constants.DATETIME, lastDay);
            items = qBuilder.query();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitScheduleDAO.class.getSimpleName());
        }
        return items;
    }
}