package ru.android4life.habittracker.models;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.enumeration.NotificationFrequencyType;
import ru.android4life.habittracker.utils.CalendarUtils;

public class HabitSettings {
    private int categoryId;
    private int notificationHour;
    private int notificationMinute;
    private NotificationFrequencyType notificationFrequencyType;
    private int notificationFrequencyWeekNumberOrDate;
    private boolean[] notificationFrequencySpecifiedDays;
    private double latitude;
    private double longitude;
    private double range;
    private Uri notificationSoundUri;
    private String notificationSoundName;
    private int minutesBeforeConfirmation;

    public HabitSettings(Uri notificationSoundUri, String notificationSoundName) {
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(BaseActivity.getContext());
        final List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();
        this.categoryId = habitCategories.get(0).getId();
        this.notificationHour = 0;
        this.notificationMinute = 0;
        this.notificationFrequencyType = NotificationFrequencyType.DAILY;
        this.notificationFrequencyWeekNumberOrDate = 1;
        boolean[] notificationFrequencySpecifiedDays = {false, false, false, false, false, false, false};
        this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
        this.latitude = 0;
        this.longitude = 0;
        this.range = 0;
        this.notificationSoundUri = notificationSoundUri;
        this.notificationSoundName = notificationSoundName;
        this.minutesBeforeConfirmation = 60;
    }

    public HabitSettings() {
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(BaseActivity.getContext());
        final List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();
        this.categoryId = habitCategories.get(0).getId();
        this.notificationHour = 0;
        this.notificationMinute = 0;
        this.notificationFrequencyType = NotificationFrequencyType.DAILY;
        this.notificationFrequencyWeekNumberOrDate = 1;
        boolean[] notificationFrequencySpecifiedDays = {false, false, false, false, false, false, false};
        this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
        this.latitude = 0;
        this.longitude = 0;
        this.range = 0;
        this.notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        this.notificationSoundName = BaseActivity.getContext().getString(R.string.standard_from_capital_letter);
        this.minutesBeforeConfirmation = 60;
    }

    // Get habit settings for the edited habit, by the habit schedule id if notification sound had not changed
    public HabitSettings(int editedHabitId, boolean frequencyChanged) {
        HabitDAO habitDAO = new HabitDAO(BaseActivity.getContext());
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(BaseActivity.getContext());
        Habit editedHabit = (Habit) habitDAO.findById(editedHabitId);
        Calendar habitScheduleDateTimeCalendar = new GregorianCalendar();
        HabitSchedule habitSchedule = habitScheduleDAO.findByHabitId(editedHabit.getId()).get(0);
        habitScheduleDateTimeCalendar.setTime(habitSchedule.getDatetime());
        this.categoryId = editedHabit.getCategoryId();
        this.notificationHour = habitScheduleDateTimeCalendar.get(Calendar.HOUR_OF_DAY);
        this.notificationMinute = habitScheduleDateTimeCalendar.get(Calendar.MINUTE);

        boolean[] notificationFrequencySpecifiedDays = {false, false, false, false, false, false, false};

        if (frequencyChanged) {
            List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(editedHabit.getId());
            // As we delete habits that are older than 1 month, and create habits for the following month
            // on every start of the app, we can conceive frequency type by the number of habit
            // schedules that exist for particular habit
            if (habitSchedules.size() <= 2) { // MONTHLY
                this.notificationFrequencyType = NotificationFrequencyType.MONTHLY;
                this.notificationFrequencyWeekNumberOrDate = habitScheduleDateTimeCalendar.get(Calendar.DAY_OF_MONTH);
            } else if (habitSchedules.size() >= 28) { // DAILY
                this.notificationFrequencyType = NotificationFrequencyType.DAILY;
                this.notificationFrequencyWeekNumberOrDate = 1;
            } else if (habitSchedules.size() >= 4 && habitSchedules.size() <= 5) { // WEEKLY
                this.notificationFrequencyType = NotificationFrequencyType.WEEKLY;
                this.notificationFrequencyWeekNumberOrDate =
                        CalendarUtils.getDayOfWeeksNumberFromDate(habitSchedule.getDatetime());
            } else { // SPECIFIED DAYS
                this.notificationFrequencyType = NotificationFrequencyType.SPECIFIED_DAYS;
                this.notificationFrequencyWeekNumberOrDate = 1;
                for (HabitSchedule schedule : habitSchedules) {
                    notificationFrequencySpecifiedDays[CalendarUtils.getDayOfWeeksNumberFromDate(schedule.getDatetime()) - 1] = true;
                }
            }
        } else {
            this.notificationFrequencyType = NotificationFrequencyType.DAILY;
            this.notificationFrequencyWeekNumberOrDate = 1;
        }

        this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
        this.latitude = 0;
        this.longitude = 0;
        this.range = 0;
        this.notificationSoundUri = Uri.parse(editedHabit.getAudioResource());
        Ringtone ringtone = RingtoneManager.getRingtone(BaseActivity.getContext(),
                Uri.parse(editedHabit.getAudioResource()));
        this.notificationSoundName = ringtone.getTitle(BaseActivity.getContext());
        this.minutesBeforeConfirmation = editedHabit.getConfirmAfterMinutes();
    }

    // Get habit settings for the edited habit, by the habit schedule id if notification sound changed
    public HabitSettings(int editedHabitId, boolean frequencyChanged,
                         Uri notificationSoundUri, String notificationSoundName) {
        HabitDAO habitDAO = new HabitDAO(BaseActivity.getContext());
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(BaseActivity.getContext());
        Habit editedHabit = (Habit) habitDAO.findById(editedHabitId);
        HabitSchedule editedHabitsSchedule = habitScheduleDAO.findByHabitId(editedHabitId).get(0);
        Calendar habitScheduleDateTimeCalendar = new GregorianCalendar();
        habitScheduleDateTimeCalendar.setTime(editedHabitsSchedule.getDatetime());
        this.categoryId = editedHabit.getCategoryId();
        this.notificationHour = habitScheduleDateTimeCalendar.get(Calendar.HOUR_OF_DAY);
        this.notificationMinute = habitScheduleDateTimeCalendar.get(Calendar.MINUTE);

        boolean[] notificationFrequencySpecifiedDays = {false, false, false, false, false, false, false};

        if (frequencyChanged) {
            List<HabitSchedule> habitSchedules = habitScheduleDAO.findByHabitId(editedHabit.getId());
            // As we delete habits that are older than 1 month, and create habits for the following month
            // on every start of the app, we can conceive frequency type by the number of habit
            // schedules that exist for particular habit
            if (habitSchedules.size() <= 2) { // MONTHLY
                this.notificationFrequencyType = NotificationFrequencyType.MONTHLY;
                this.notificationFrequencyWeekNumberOrDate = habitScheduleDateTimeCalendar.get(Calendar.DAY_OF_MONTH);
            } else if (habitSchedules.size() >= 28) { // DAILY
                this.notificationFrequencyType = NotificationFrequencyType.DAILY;
                this.notificationFrequencyWeekNumberOrDate = 1;
            } else if (habitSchedules.size() >= 4 && habitSchedules.size() <= 5) { // WEEKLY
                this.notificationFrequencyType = NotificationFrequencyType.WEEKLY;
                this.notificationFrequencyWeekNumberOrDate =
                        CalendarUtils.getDayOfWeeksNumberFromDate(editedHabitsSchedule.getDatetime());
            } else { // SPECIFIED DAYS
                this.notificationFrequencyType = NotificationFrequencyType.SPECIFIED_DAYS;
                this.notificationFrequencyWeekNumberOrDate = 1;
                for (HabitSchedule habitSchedule : habitSchedules) {
                    notificationFrequencySpecifiedDays[CalendarUtils.getDayOfWeeksNumberFromDate(habitSchedule.getDatetime()) - 1] = true;
                }
            }
        } else {
            this.notificationFrequencyType = NotificationFrequencyType.DAILY;
            this.notificationFrequencyWeekNumberOrDate = 1;
        }

        this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
        this.latitude = 0;
        this.longitude = 0;
        this.range = 0;
        this.notificationSoundUri = notificationSoundUri;
        this.notificationSoundName = notificationSoundName;
        this.minutesBeforeConfirmation = editedHabit.getConfirmAfterMinutes();
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getNotificationHour() {
        return notificationHour;
    }

    public void setNotificationHour(int notificationHour) {
        this.notificationHour = notificationHour;
    }

    public int getNotificationMinute() {
        return notificationMinute;
    }

    public void setNotificationMinute(int notificationMinute) {
        this.notificationMinute = notificationMinute;
    }

    public NotificationFrequencyType getNotificationFrequencyType() {
        return notificationFrequencyType;
    }

    public void setNotificationFrequencyType(NotificationFrequencyType notificationFrequencyType) {
        this.notificationFrequencyType = notificationFrequencyType;
    }

    public int getNotificationFrequencyWeekNumberOrDate() {
        return notificationFrequencyWeekNumberOrDate;
    }

    public void setNotificationFrequencyWeekNumberOrDate(int notificationFrequencyWeekNumberOrDate) {
        this.notificationFrequencyWeekNumberOrDate = notificationFrequencyWeekNumberOrDate;
    }

    public boolean[] getNotificationFrequencySpecifiedDays() {
        return notificationFrequencySpecifiedDays;
    }

    public void setNotificationFrequencySpecifiedDays(boolean[] notificationFrequencySpecifiedDays) {
        this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setLatitudeLongitudeAndRange(double latitude, double longitude, double range) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
    }

    public Uri getNotificationSoundUri() {
        return notificationSoundUri;
    }

    public void setNotificationSoundUri(Uri notificationSoundUri) {
        this.notificationSoundUri = notificationSoundUri;
    }

    public String getNotificationSoundName() {
        return notificationSoundName;
    }

    public void setNotificationSoundName(String notificationSoundName) {
        this.notificationSoundName = notificationSoundName;
    }

    public int getMinutesBeforeConfirmation() {
        return minutesBeforeConfirmation;
    }

    public void setMinutesBeforeConfirmation(int minutesBeforeConfirmation) {
        this.minutesBeforeConfirmation = minutesBeforeConfirmation;
    }
}