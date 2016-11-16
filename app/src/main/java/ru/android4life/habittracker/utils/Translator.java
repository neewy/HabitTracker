package ru.android4life.habittracker.utils;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;

import static ru.android4life.habittracker.utils.StringConstants.BLUE;
import static ru.android4life.habittracker.utils.StringConstants.CLEANING;
import static ru.android4life.habittracker.utils.StringConstants.COOKING;
import static ru.android4life.habittracker.utils.StringConstants.HEALTH;
import static ru.android4life.habittracker.utils.StringConstants.OTHER;
import static ru.android4life.habittracker.utils.StringConstants.PURPLE;
import static ru.android4life.habittracker.utils.StringConstants.READING;
import static ru.android4life.habittracker.utils.StringConstants.RED;
import static ru.android4life.habittracker.utils.StringConstants.SPORT;
import static ru.android4life.habittracker.utils.StringConstants.STUDYING;
import static ru.android4life.habittracker.utils.StringConstants.TEAL;

/**
 * Created by Bulat Mukhutdinov on 14.10.2016.
 */

public class Translator {


    public static String translate(String word) {
        switch (word) {
            case SPORT:
                return MainActivity.getContext().getResources().getString(R.string.sport);
            case READING:
                return MainActivity.getContext().getResources().getString(R.string.reading);
            case COOKING:
                return MainActivity.getContext().getResources().getString(R.string.cooking);
            case CLEANING:
                return MainActivity.getContext().getResources().getString(R.string.cleaning);
            case STUDYING:
                return MainActivity.getContext().getResources().getString(R.string.studying);
            case HEALTH:
                return MainActivity.getContext().getResources().getString(R.string.health);
            case OTHER:
                return MainActivity.getContext().getResources().getString(R.string.other);
            default:
                return word;
        }
    }

    public static String translateColor(String word) {
        switch (word) {
            case BLUE:
                return MainActivity.getContext().getResources().getString(R.string.color_setting_name_blue);
            case TEAL:
                return MainActivity.getContext().getResources().getString(R.string.color_setting_name_teal);
            case RED:
                return MainActivity.getContext().getResources().getString(R.string.color_setting_name_red);
            case PURPLE:
                return MainActivity.getContext().getResources().getString(R.string.color_setting_name_purple);
            default:
                return word;
        }
    }

    public static CharSequence[] translate(CharSequence[] items) {
        CharSequence[] translated = new CharSequence[items.length];
        for (int i = 0; i < items.length; i++) {
            translated[i] = translate(items[i].toString());
        }
        return translated;
    }
}
