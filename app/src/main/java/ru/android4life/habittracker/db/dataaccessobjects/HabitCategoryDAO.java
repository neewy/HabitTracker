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
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;

/**
 * Created by alnedorezov on 9/28/16.
 */

public class HabitCategoryDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public HabitCategoryDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        HabitCategory habitCategory = (HabitCategory) item;
        try {
            index = helper.getHabitCategoryDao().create(habitCategory);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        HabitCategory habitCategory = (HabitCategory) item;

        try {
            index = helper.getHabitCategoryDao().update(habitCategory);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        HabitCategory habitCategory = (HabitCategory) item;

        try {
            index = helper.getHabitCategoryDao().delete(habitCategory);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        HabitCategory habitCategory = null;
        try {
            habitCategory = helper.getHabitCategoryDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }
        return habitCategory;
    }

    @Override
    public List<?> findAll() {

        List<HabitCategory> items = new ArrayList<>();

        try {
            items = helper.getHabitCategoryDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        HabitCategory habitCategory = null;
        try {
            QueryBuilder<HabitCategory, Integer> qBuilder = helper.getHabitCategoryDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            habitCategory = helper.getHabitCategoryDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }
        return habitCategory;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        HabitCategory habitCategory = (HabitCategory) item;
        try {
            if (helper.getHabitCategoryDao().idExists(habitCategory.getId())) {
                if (helper.getHabitCategoryDao().queryForId(habitCategory.getId()).equals(habitCategory))
                    index = habitCategory.getId();
                else
                    index = helper.getHabitCategoryDao().update(habitCategory);
            } else
                index = helper.getHabitCategoryDao().create(habitCategory);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    HabitCategoryDAO.class.getSimpleName());
        }

        return index;
    }

    public CharSequence[] getArrayOfAllNames() {

        List<HabitCategory> habitCategories = (List<HabitCategory>) findAll();
        CharSequence[] categoryNamesArray = new CharSequence[habitCategories.size()];
        for (int i = 0; i < categoryNamesArray.length; i++)
            categoryNamesArray[i] = habitCategories.get(i).getName();

        return categoryNamesArray;
    }
}