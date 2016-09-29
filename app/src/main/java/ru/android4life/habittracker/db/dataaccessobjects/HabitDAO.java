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
import ru.android4life.habittracker.db.tablesrepresentations.Habit;

/**
 * Created by alnedorezov on 9/28/16.
 */

public class HabitDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public HabitDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Habit habit = (Habit) item;
        try {
            index = helper.getHabitDao().create(habit);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Habit habit = (Habit) item;

        try {
            helper.getHabitDao().update(habit);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Habit habit = (Habit) item;

        try {
            helper.getHabitDao().delete(habit);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Habit habit = null;
        try {
            habit = helper.getHabitDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }
        return habit;
    }

    @Override
    public List<?> findAll() {

        List<Habit> items = new ArrayList<>();

        try {
            items = helper.getHabitDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        Habit habit = null;
        try {
            QueryBuilder<Habit, Integer> qBuilder = helper.getHabitDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            habit = helper.getHabitDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }
        return habit;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        Habit habit = (Habit) item;
        try {
            if (helper.getHabitDao().idExists(habit.getId())) {
                if (helper.getHabitDao().queryForId(habit.getId()).equals(habit))
                    index = habit.getId();
                else
                    index = helper.getHabitDao().update(habit);
            } else
                index = helper.getHabitDao().create(habit);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitDAO.class.getSimpleName());
        }

        return index;
    }
}