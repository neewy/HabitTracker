package ru.android4life.habittracker.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

import ru.android4life.habittracker.db.Constants;

/**
 * Created by alnedorezov on 9/27/16.
 */

@DatabaseTable(tableName = "HabitSchedules")
public class HabitSchedule {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private Date datetime = null;
    @DatabaseField
    private Boolean isDone;
    @DatabaseField
    private String note;
    @DatabaseField(uniqueCombo = true)
    private int habitId;

    public HabitSchedule(Date datetime, Boolean isDone, int habitId) {
        this.datetime = datetime;
        this.isDone = isDone;
        this.habitId = habitId;
    }

    public HabitSchedule(String datetimeString, Boolean isDone, int habitId) throws ParseException {
        this.datetime = Constants.dateFormat.parse(datetimeString);
        this.isDone = isDone;
        this.habitId = habitId;
    }

    public HabitSchedule(int id, Date datetime, Boolean isDone, int habitId) {
        this.id = id;
        this.datetime = datetime;
        this.isDone = isDone;
        this.habitId = habitId;
    }

    public HabitSchedule(int id, String datetimeString, Boolean isDone, int habitId) throws ParseException {
        this.id = id;
        this.datetime = Constants.dateFormat.parse(datetimeString);
        this.isDone = isDone;
        this.habitId = habitId;
    }

    // For deserialization with Jackson
    public HabitSchedule() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getDatetimeString() {
        return Constants.dateFormat.format(datetime);
    }

    public Boolean isDone() {
        return isDone;
    }

    public int getHabitId() {
        return habitId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HabitSchedule)) return false;

        HabitSchedule that = (HabitSchedule) o;

        if (getId() != that.getId()) return false;
        if (!getNote().equals(that.getNote())) return false;
        if (getHabitId() != that.getHabitId()) return false;
        if (getDatetime() != null ? !getDatetime().equals(that.getDatetime()) : that.getDatetime() != null)
            return false;
        return isDone != null ? isDone.equals(that.isDone) : that.isDone == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + datetime.hashCode();
        result = 31 * result + isDone.hashCode();
        result = 31 * result + note.hashCode();
        result = 31 * result + habitId;
        return result;
    }
}