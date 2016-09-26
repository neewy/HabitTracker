package si.modrajagoda.didi.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

import si.modrajagoda.didi.db.Constants;

/**
 * Created by alnedorezov on 9/27/16.
 */

@DatabaseTable(tableName = "HabitSchedules")
public class HabitSchedule {
    @DatabaseField(id = true, unique = true)
    private int id;
    @DatabaseField
    private Date datetime = null;
    @DatabaseField
    private boolean isPerformed;
    @DatabaseField
    private int habitId;

    public HabitSchedule(int id, Date datetime, boolean isPerformed, int habitId) {
        this.id = id;
        this.datetime = datetime;
        this.isPerformed = isPerformed;
        this.habitId = habitId;
    }

    public HabitSchedule(int id, String datetimeString, boolean isPerformed,
                         int habitId) throws ParseException {
        this.id = id;
        this.datetime = Constants.dateFormat.parse(datetimeString);
        this.isPerformed = isPerformed;
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

    public boolean isPerformed() {
        return isPerformed;
    }

    public int getHabitId() {
        return habitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HabitSchedule))
            return false;

        HabitSchedule that = (HabitSchedule) o;

        if (getId() != that.getId())
            return false;
        if (isPerformed() != that.isPerformed())
            return false;
        if (getHabitId() != that.getHabitId())
            return false;
        return getDatetime() != null ? getDatetime()
                .equals(that.getDatetime()) : that.getDatetime() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getDatetime() != null ? getDatetime().hashCode() : 0);
        result = 31 * result + (isPerformed() ? 1 : 0);
        result = 31 * result + getHabitId();
        return result;
    }
}