package si.modrajagoda.didi.database;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "habits")
public class Habit {

    @ForeignCollectionField
    ForeignCollection<Day> days;
    @DatabaseField(id = true)
    private int id;
    @DatabaseField()
    private String name;
    @DatabaseField()
    private boolean archived;

    public Habit() {
        // ORMLite needs a no-arg constructor
    }

    public Habit(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ForeignCollection<Day> getDays() {
        return days;
    }
}
