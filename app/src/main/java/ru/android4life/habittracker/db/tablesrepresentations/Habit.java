package ru.android4life.habittracker.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

import ru.android4life.habittracker.db.Constants;

/**
 * Created by alnedorezov on 9/26/16.
 */

@DatabaseTable(tableName = "Habits")
public class Habit {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;
    @DatabaseField(unique = true)
    private String question;
    @DatabaseField
    private Date notificationTime = null;
    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;
    @DatabaseField
    private double range;
    @DatabaseField
    private String audioResource;
    @DatabaseField
    private boolean usesConfirmation;
    @DatabaseField
    private int confirmAfterMinutes;
    @DatabaseField
    private int categoryId;

    public Habit(int id, String name, String question, Date notificationTime, double latitude,
                 double longitude, double range, String audioResource, boolean usesConfirmation,
                 int confirmAfterMinutes, int categoryId) {
        this.id = id;
        this.name = name;
        this.question = question;
        this.notificationTime = notificationTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.audioResource = audioResource;
        this.usesConfirmation = usesConfirmation;
        this.confirmAfterMinutes = confirmAfterMinutes;
        this.categoryId = categoryId;
    }

    public Habit(int id, String name, String question, String notificationTimeString,
                 double latitude, double longitude, double range, String audioResource,
                 boolean usesConfirmation, int confirmAfterMinutes,
                 int categoryId) throws ParseException {
        this.id = id;
        this.name = name;
        this.question = question;
        this.notificationTime = Constants.dateFormat.parse(notificationTimeString);
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.audioResource = audioResource;
        this.usesConfirmation = usesConfirmation;
        this.confirmAfterMinutes = confirmAfterMinutes;
        this.categoryId = categoryId;
    }

    // For deserialization with Jackson
    public Habit() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public String getNotificationTimeString() {
        return Constants.dateFormat.format(notificationTime);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRange() {
        return range;
    }

    public String getAudioResource() {
        return audioResource;
    }

    public boolean isUsesConfirmation() {
        return usesConfirmation;
    }

    public int getConfirmAfterMinutes() {
        return confirmAfterMinutes;
    }

    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Habit))
            return false;

        Habit habit = (Habit) o;

        if (getId() != habit.getId())
            return false;
        if (Double.compare(habit.getLatitude(), getLatitude()) != 0)
            return false;
        if (Double.compare(habit.getLongitude(), getLongitude()) != 0)
            return false;
        if (Double.compare(habit.getRange(), getRange()) != 0)
            return false;
        if (isUsesConfirmation() != habit.isUsesConfirmation())
            return false;
        if (getConfirmAfterMinutes() != habit.getConfirmAfterMinutes())
            return false;
        if (getCategoryId() != habit.getCategoryId())
            return false;
        if (getName() != null ? !getName().equals(habit.getName()) : habit.getName() != null)
            return false;
        if (getQuestion() != null ? !getQuestion()
                .equals(habit.getQuestion()) : habit.getQuestion() != null)
            return false;
        if (getNotificationTime() != null ? !getNotificationTime()
                .equals(habit.getNotificationTime()) : habit.getNotificationTime() != null)
            return false;
        return getAudioResource() != null ? getAudioResource()
                .equals(habit.getAudioResource()) : habit.getAudioResource() == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getQuestion() != null ? getQuestion().hashCode() : 0);
        result = 31 * result
                + (getNotificationTime() != null ? getNotificationTime().hashCode() : 0);
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getRange());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getAudioResource() != null ? getAudioResource().hashCode() : 0);
        result = 31 * result + (isUsesConfirmation() ? 1 : 0);
        result = 31 * result + getConfirmAfterMinutes();
        result = 31 * result + getCategoryId();
        return result;
    }

    public static class HabitBuilder {
        private int id;
        private String name;
        private String question;
        private Date notificationTime = null;
        private double latitude;
        private double longitude;
        private double range;
        private String audioResource;
        private boolean usesConfirmation;
        private int confirmAfterMinutes;
        private int categoryId;

        public HabitBuilder() {
            // Empty constructor for builder class
        }

        public HabitBuilder setGeneralParameters(int id, String name, String question,
                                                 Date notificationTime, boolean usesConfirmation,
                                                 int confirmAfterMinutes, int categoryId) {
            this.id = id;
            this.name = name;
            this.question = question;
            this.notificationTime = notificationTime;
            this.usesConfirmation = usesConfirmation;
            this.confirmAfterMinutes = confirmAfterMinutes;
            this.categoryId = categoryId;

            return this;
        }

        public HabitBuilder setLocationAndRange(double latitude, double longitude, double range) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.range = range;

            return this;
        }

        public HabitBuilder setAudioResource(String audioResource) {
            this.audioResource = audioResource;

            return this;
        }

        public Habit build() {
            return new Habit(id, name, question, notificationTime, latitude, longitude, range,
                    audioResource, usesConfirmation, confirmAfterMinutes, categoryId);
        }
    }
}
