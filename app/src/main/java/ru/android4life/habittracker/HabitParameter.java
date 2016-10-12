package ru.android4life.habittracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;

/**
 * Dummy class, which is to be replaced by habit DAO
 */

public class HabitParameter {
    private String title;
    private String hint;
    private Drawable icon;

    public HabitParameter() {
    }

    public HabitParameter(String title, String hint, Drawable icon) {
        this.title = title;
        this.hint = hint;
        this.icon = icon;
    }

    public static List<HabitParameter> createParameters(Context context) {
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(context);
        List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();
        List<HabitParameter> habitParameters = new ArrayList<>();
        HabitParameter parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_category),
                habitCategories.get(0).getName(), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_reminder), "0:00", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_frequency), "Daily", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_tune), "Standard", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_confirmation), "After 60 minutes", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}