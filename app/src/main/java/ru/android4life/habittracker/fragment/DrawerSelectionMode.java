package ru.android4life.habittracker.fragment;

/**
 * Created by alnedorezov on 10/5/16.
 */

public enum DrawerSelectionMode {
    TODAY("Today"), TOMORROW("Tomorrow"), NEXT_MONTH("Next Month"), ALL_TASKS("All Tasks"), STATISTICS("Statistics"), SETTINGS("Settings");

    public String stringValue;

    DrawerSelectionMode(String s) {
        this.stringValue = s;
    }

    public static DrawerSelectionMode findDrawerSelectionMode (String tag) {
        switch (tag){
            case "Today":
                return TODAY;
            case "Tomorrow":
                return TOMORROW;
            case "Next Month":
               return NEXT_MONTH;
            case "All Tasks":
                return ALL_TASKS;
            case "Statistics":
                return STATISTICS;
            case "Settings":
                return STATISTICS;
        }
        return null;
    }
}
