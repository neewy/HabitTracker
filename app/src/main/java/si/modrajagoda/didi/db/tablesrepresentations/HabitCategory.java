package si.modrajagoda.didi.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 9/26/16.
 */

@DatabaseTable(tableName = "HabitCategories")
public class HabitCategory {
    @DatabaseField(id = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;

    public HabitCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // For deserialization with Jackson
    public HabitCategory() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HabitCategory))
            return false;

        HabitCategory that = (HabitCategory) o;

        if (getId() != that.getId())
            return false;
        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}
