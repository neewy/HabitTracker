package ru.android4life.habittracker.utils;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;

/**
 * Created by Bulat Mukhutdinov on 14.10.2016.
 */

public class Translator {
    public static String translate(String word) {
        switch (word) {
            case "Sport":
                return MainActivity.getContext().getResources().getString(R.string.sport);
            case "Reading":
                return MainActivity.getContext().getResources().getString(R.string.reading);
            case "Cooking":
                return MainActivity.getContext().getResources().getString(R.string.cooking);
            case "Cleaning":
                return MainActivity.getContext().getResources().getString(R.string.cleaning);
            case "Studying":
                return MainActivity.getContext().getResources().getString(R.string.studying);
            case "Health":
                return MainActivity.getContext().getResources().getString(R.string.health);
            case "Other":
                return MainActivity.getContext().getResources().getString(R.string.other);
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
