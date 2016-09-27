package si.modrajagoda.didi.db;

/**
 * Created by alnedorezov on 9/26/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import si.modrajagoda.didi.db.tablesrepresentations.Habit;
import si.modrajagoda.didi.db.tablesrepresentations.HabitCategory;
import si.modrajagoda.didi.db.tablesrepresentations.HabitSchedule;

/**
 * Database helper class used to manage the creation and upgrading of your database.
 * This class also usually provides the DAOs used by the other classes.
 * For more details see https://habrahabr.ru/post/143431/
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application
    private static final String DATABASE_NAME = Constants.DATABASE_NAME;
    // any time you make changes to your database objects,
    // you may have to increase the database version
    private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;

    private Dao<HabitCategory, Integer> habitCategoryDao = null;
    private Dao<Habit, Integer> habitDao = null;
    private Dao<HabitSchedule, Integer> habitScheduleDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created.
     * Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.d(Constants.DB_HELPER, Constants.ON_CREATE);
            // when tables are created and only then alter table fields that need to be altered
            TableUtils.createTable(connectionSource, HabitCategory.class);
            TableUtils.createTable(connectionSource, Habit.class);
            TableUtils.createTable(connectionSource, HabitSchedule.class);
        } catch (SQLException e) {
            Log.d(Constants.DB_HELPER + Constants.UNDERSCORE + Constants.ERROR,
                    Constants.CANNOT_CREATE_DATABASE, e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number.
     * This allows you to adjust the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), Constants.ON_UPGRADE);

            TableUtils.dropTable(connectionSource, HabitSchedule.class, true);
            TableUtils.dropTable(connectionSource, Habit.class, true);
            TableUtils.dropTable(connectionSource, HabitCategory.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), Constants.CANNOT_DROP_DATABASES, e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class.
     * It will create it or just give the cached value.
     */
    public Dao<HabitCategory, Integer> getHabitCategoryDao() throws SQLException {
        if (habitCategoryDao == null) {
            habitCategoryDao = getDao(HabitCategory.class);
        }
        return habitCategoryDao;
    }

    public Dao<Habit, Integer> getHabitDao() throws SQLException {
        if (habitDao == null) {
            habitDao = getDao(Habit.class);
        }
        return habitDao;
    }

    public Dao<HabitSchedule, Integer> getHabitScheduleDao() throws SQLException {
        if (habitScheduleDao == null) {
            habitScheduleDao = getDao(HabitSchedule.class);
        }
        return habitScheduleDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        habitScheduleDao = null;
        habitDao = null;
        habitCategoryDao = null;
    }
}
