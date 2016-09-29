package ru.android4life.habittracker.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
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
}