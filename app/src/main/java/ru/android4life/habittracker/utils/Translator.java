package ru.android4life.habittracker.utils;

import ru.android4life.habittracker.R;

import static ru.android4life.habittracker.activity.BaseActivity.getContext;
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
                return getContext().getResources().getString(R.string.sport);
            case READING:
                return getContext().getResources().getString(R.string.reading);
            case COOKING:
                return getContext().getResources().getString(R.string.cooking);
            case CLEANING:
                return getContext().getResources().getString(R.string.cleaning);
            case STUDYING:
                return getContext().getResources().getString(R.string.studying);
            case HEALTH:
                return getContext().getResources().getString(R.string.health);
            case OTHER:
                return getContext().getResources().getString(R.string.other);
            default:
                return word;
        }
    }

    public static String translateColor(String word) {
        switch (word) {
            case BLUE:
                return getContext().getResources().getString(R.string.color_setting_name_blue);
            case TEAL:
                return getContext().getResources().getString(R.string.color_setting_name_teal);
            case RED:
                return getContext().getResources().getString(R.string.color_setting_name_red);
            case PURPLE:
                return getContext().getResources().getString(R.string.color_setting_name_purple);
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
