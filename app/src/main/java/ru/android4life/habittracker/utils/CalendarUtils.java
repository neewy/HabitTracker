package ru.android4life.habittracker.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by alnedorezov on 10/23/16.
 */

public class CalendarUtils {
    public static int getDayOfWeeksNumberFromDate(Date dateTime) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(dateTime);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
            default:
                break;
        }

        return 1;
    }
}
