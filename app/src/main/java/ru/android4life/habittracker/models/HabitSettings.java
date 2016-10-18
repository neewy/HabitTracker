package ru.android4life.habittracker.models;

import android.media.RingtoneManager;
import android.net.Uri;

import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.enumeration.NotificationFrequencyType;

public class HabitSettings {
        private int categoryId;
        private int notificationHour;
        private int notificationMinute;
        private NotificationFrequencyType notificationFrequencyType;
        private int notificationFrequencyWeekNumberOrDate;
        private boolean[] notificationFrequencySpecifiedDays;
        private Uri notificationSoundUri;
        private String notificationSoundName;
        private int minutesBeforeConfirmation;

        public HabitSettings(int categoryId, int notificationHour, int notificationMinute,
                             NotificationFrequencyType notificationFrequencyType,
                             int notificationFrequencyWeekNumberOrDate, boolean[] notificationFrequencySpecifiedDays,
                             Uri notificationSoundUri, String notificationSoundName,
                             int minutesBeforeConfirmation) {
            this.categoryId = categoryId;
            this.notificationHour = notificationHour;
            this.notificationMinute = notificationMinute;
            this.notificationFrequencyType = notificationFrequencyType;
            this.notificationFrequencyWeekNumberOrDate = notificationFrequencyWeekNumberOrDate;
            this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
            this.notificationSoundUri = notificationSoundUri;
            this.notificationSoundName = notificationSoundName;
            this.minutesBeforeConfirmation = minutesBeforeConfirmation;
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
            this.notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            this.notificationSoundName = BaseActivity.getContext().getString(R.string.standard_from_capital_letter);
            this.minutesBeforeConfirmation = 60;
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