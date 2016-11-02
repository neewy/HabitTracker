package ru.android4life.habittracker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Method;

import ru.android4life.habittracker.R;

import static ru.android4life.habittracker.utils.StringConstants.BLUE;
import static ru.android4life.habittracker.utils.StringConstants.COLOR;
import static ru.android4life.habittracker.utils.StringConstants.FIRSTRUN;
import static ru.android4life.habittracker.utils.StringConstants.PURPLE;
import static ru.android4life.habittracker.utils.StringConstants.RED;
import static ru.android4life.habittracker.utils.StringConstants.SHARED_PREF;
import static ru.android4life.habittracker.utils.StringConstants.TEAL;

/**
 * This activity is for setting style for the activities,
 * if the user checks different style in settings.
 * <p>
 * Created by Nikolay Yushkevich on 12.10.16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static int themeID;
    private static Context context;
    protected SharedPreferences prefs = null;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        BaseActivity.context = context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //any activity which inherits now can use shared preferences
        prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if (prefs.getBoolean(FIRSTRUN, true)) {
            prefs.edit().putString(COLOR, BLUE).apply();
        }

        setApplicationStyle();
        themeID = getThemeId();
    }

    private void setApplicationStyle() {
        //we check if the style was selected in settings:
        switch (prefs.getString(COLOR, "")) {
            case RED:
                setTheme(R.style.AppThemeRedAndBlue);
                break;
            case BLUE:
                setTheme(R.style.AppThemeBlueAndPink);
                break;
            case PURPLE:
                setTheme(R.style.AppThemePurpleAndGreen);
                break;
            case TEAL:
                setTheme(R.style.AppThemeTealAndLime);
                break;
            default:
                setTheme(R.style.AppThemeBlueAndPink);
                break;
        }
    }

    private int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
