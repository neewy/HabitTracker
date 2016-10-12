package ru.android4life.habittracker.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.android4life.habittracker.R;

/**
 * This activity is for setting style for the activities,
 * if the user checks different style in settings.
 *
 * Created by Nikolay Yushkevich on 12.10.16.
 */

public class BaseActivity extends AppCompatActivity {

    protected SharedPreferences prefs = null;
    public final static String SHARED_PREF = "SHARED_PREF";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //any activity which inherits now can use shared preferences
        prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putString("color", "Blue").apply();
        }

        setApplicationStyle();
    }

    private void setApplicationStyle() {
        //we check if the style was selected in settings:
        switch (prefs.getString("color", "")){
            case "Red":
                setTheme(R.style.AppThemeRedAndBlue);
                break;
            case "Blue":
                setTheme(R.style.AppThemeBlueAndPink);
                break;
            case "Purple":
                setTheme(R.style.AppThemePurpleAndGreen);
                break;
            case "Teal":
                setTheme(R.style.AppThemeTealAndLime);
                break;
            default:
                setTheme(R.style.AppThemeBlueAndPink);
                break;
        }
    }
}