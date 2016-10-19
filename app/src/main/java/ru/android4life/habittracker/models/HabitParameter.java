package ru.android4life.habittracker.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.utils.Translator;

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
                Translator.translate(habitCategories.get(0).getName()), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_reminder), "0:00", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_frequency), context.getResources().getString(R.string.daily), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_tune), "Standard", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_confirmation), "After 60 minutes", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }

    public static List<HabitParameter> createParametersByHabitScheduleId(Context context, int habitScheduleId) {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        HabitDAO habitDAO = new HabitDAO(context);
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(context);
        List<HabitParameter> habitParameters = new ArrayList<>();
        HabitSchedule habitSchedule = (HabitSchedule) habitScheduleDAO.findById(habitScheduleId);
        Habit habit = (Habit) habitDAO.findById(habitSchedule.getHabitId());
        HabitCategory habitCategory = (HabitCategory) habitCategoryDAO.findById(habit.getCategoryId());

        HabitParameter parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_category),
                Translator.translate(habitCategory.getName()), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);

        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_reminder), getHabitTimeHint(context, habitSchedule.getDatetime()),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_frequency), context.getResources().getString(R.string.daily), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);

        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_tune), getHabitNotificationRingtoneName(context, habit.getAudioResource()),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);

        String habitConfirmAfterMinutesHint = context.getString(R.string.after_string_minutes,
                String.valueOf(habit.getConfirmAfterMinutes()));

        parameter = new HabitParameter(context.getResources().getString(R.string.add_habit_name_confirmation),
                habitConfirmAfterMinutesHint, ContextCompat.getDrawable(context, R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }

    private static String getHabitTimeHint(Context context, Date habitScheduleDateTime) {
        Calendar habitScheduleDateTimeCalendar = new GregorianCalendar();
        habitScheduleDateTimeCalendar.setTime(habitScheduleDateTime);
        int habitMinute = habitScheduleDateTimeCalendar.get(Calendar.MINUTE);
        int habitHour = habitScheduleDateTimeCalendar.get(Calendar.HOUR_OF_DAY);
        String habitTimeHint;

        if (String.valueOf(habitMinute).length() < 2) {
            habitTimeHint = context.getString(R.string.string_colon_space_string_zero,
                    String.valueOf(habitHour),
                    String.valueOf(habitMinute));
        } else {
            habitTimeHint = context.getResources().getString(R.string.string_colon_space_string,
                    String.valueOf(habitHour),
                    String.valueOf(habitMinute));
        }

        return habitTimeHint;
    }

    private static String getHabitNotificationRingtoneName(Context context, String habitAudioResource) {
        Ringtone ringtone = RingtoneManager.getRingtone(context,
                Uri.parse(habitAudioResource));
        return ringtone.getTitle(context);
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