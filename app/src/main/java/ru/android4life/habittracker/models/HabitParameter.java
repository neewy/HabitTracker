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
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.utils.CalendarUtils;
import ru.android4life.habittracker.utils.Translator;

/**
 * Dummy class, which is to be replaced by habit DAO
 * TODO: Check if needed!
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
        HabitParameter parameter = new HabitParameter(context.getString(R.string.add_habit_name_category),
                Translator.translate(habitCategories.get(0).getName()), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getString(R.string.add_habit_name_reminder), context.getString(R.string.zero_colon_zero_zero), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getString(R.string.add_habit_name_frequency), context.getString(R.string.daily), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getString(R.string.add_habit_name_tune), context.getString(R.string.standard_tune), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getString(R.string.add_habit_name_position), context.getString(R.string.none), ContextCompat.getDrawable(context, R.drawable.ic_explore_black_24dp));
        habitParameters.add(parameter);
        parameter = new HabitParameter(context.getString(R.string.add_habit_name_confirmation), context.getString(R.string.after_hour), ContextCompat.getDrawable(context, R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }

    public static List<HabitParameter> createParametersByHabitId(Context context, int habitId) {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        HabitDAO habitDAO = new HabitDAO(context);
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(context);
        List<HabitParameter> habitParameters = new ArrayList<>();
        Habit habit = (Habit) habitDAO.findById(habitId);
        HabitSchedule habitSchedule = habitScheduleDAO.findByHabitId(habitId).get(0);

        HabitCategory habitCategory = (HabitCategory) habitCategoryDAO.findById(habit.getCategoryId());

        HabitParameter parameter = new HabitParameter(context.getString(R.string.add_habit_name_category),
                Translator.translate(habitCategory.getName()),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);

        parameter = new HabitParameter(context.getString(R.string.add_habit_name_reminder),
                getHabitTimeHint(context, habitSchedule.getDatetime()),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);


        parameter = new HabitParameter(context.getString(R.string.add_habit_name_frequency),
                getHabitFrequencyHint(context, habit.getId(), habitScheduleDAO),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);

        parameter = new HabitParameter(context.getString(R.string.add_habit_name_tune),
                getHabitNotificationRingtoneName(context, habit.getAudioResource()),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);

        parameter = new HabitParameter(context.getString(R.string.add_habit_name_position),
                getHabitPositionString(context, habit),
                ContextCompat.getDrawable(context, R.drawable.ic_explore_black_24dp));
        habitParameters.add(parameter);

        parameter = new HabitParameter(context.getString(R.string.add_habit_name_confirmation),
                context.getString(R.string.after_string_minutes, String.valueOf(habit.getConfirmAfterMinutes())),
                ContextCompat.getDrawable(context, R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }

    private static String getHabitFrequencyHint(Context context, int habitId, HabitScheduleDAO habitScheduleDAO) {
        List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(habitId);
        Calendar habitScheduleDateTimeCalendar = new GregorianCalendar();

        // As we delete habits that are older than 1 month, and create habits for the following month
        // on every start of the app, we can conceive frequency type by the number of habit
        // schedules that exist for particular habit
        if (habitSchedules.size() <= 2) { // MONTHLY
            habitScheduleDateTimeCalendar.setTime(habitSchedules.get(0).getDatetime());
            return context.getString(R.string.every_month_on_space_string,
                    String.valueOf(habitScheduleDateTimeCalendar.get(Calendar.DAY_OF_MONTH)));
        } else if (habitSchedules.size() >= 28) { // DAILY
            return context.getString(R.string.every_day);
        } else if (habitSchedules.size() >= 4 && habitSchedules.size() <= 5) { // WEEKLY
            habitScheduleDateTimeCalendar.setTime(habitSchedules.get(0).getDatetime());
            return context.getString(R.string.on_every,
                    habitScheduleDateTimeCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
                            Calendar.LONG, MainActivity.locale));
        } else { // SPECIFIED DAYS
            boolean[] mCheckedItems = {false, false, false, false, false, false, false};
            String[] shortenDaysOfWeek = context.getResources().getStringArray(R.array.shorten_days_of_week);
            for (HabitSchedule habitSchedule : habitSchedules) {
                mCheckedItems[CalendarUtils.getDayOfWeeksNumberFromDate(habitSchedule.getDatetime()) - 1] = true;
            }
            StringBuilder selectedDaysInTwoLetters = new StringBuilder();
            for (int i = 0; i < mCheckedItems.length; i++) {
                if (mCheckedItems[i]) {
                    selectedDaysInTwoLetters.append(shortenDaysOfWeek[i]).append(", ");
                }
            }
            if (selectedDaysInTwoLetters.length() > 1)
                selectedDaysInTwoLetters.deleteCharAt(selectedDaysInTwoLetters.length() - 2);
            return context.getString(R.string.on_every,
                    selectedDaysInTwoLetters);
        }
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
            habitTimeHint = context.getString(R.string.string_colon_space_string,
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

    private static String getHabitPositionString(Context context, Habit habit) {
        return context.getString(R.string.position_format, String.valueOf(habit.getLatitude()),
                String.valueOf(habit.getLongitude()), String.valueOf(habit.getRange()));
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