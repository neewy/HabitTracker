package si.modrajagoda.didi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Dummy class, which is to be replaced by habit DAO
 */

public class HabitParameter {
    private String title;
    private String hint;
    private Drawable icon;

    public HabitParameter(String title, String hint, Drawable icon) {
        this.title = title;
        this.hint = hint;
        this.icon = icon;
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

    public static List<HabitParameter> createParameters(Context context) {
        List<HabitParameter> habitParameters = new ArrayList<>();
        HabitParameter parameter = new HabitParameter("Category", "Fitness", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Remind at", "None", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Frequency", "Daily", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Tune", "Standard", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Confirmation", "After 30 min", ContextCompat.getDrawable(context, R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }
}