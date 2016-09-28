package si.modrajagoda.didi;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.j256.ormlite.stmt.QueryBuilder;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import si.modrajagoda.didi.db.Constants;
import si.modrajagoda.didi.db.DatabaseHelper;
import si.modrajagoda.didi.db.DatabaseManager;
import si.modrajagoda.didi.db.dataaccessobjects.HabitCategoryDAO;
import si.modrajagoda.didi.db.dataaccessobjects.HabitDAO;
import si.modrajagoda.didi.db.dataaccessobjects.HabitScheduleDAO;
import si.modrajagoda.didi.db.tablesrepresentations.Habit;
import si.modrajagoda.didi.db.tablesrepresentations.HabitCategory;
import si.modrajagoda.didi.db.tablesrepresentations.HabitSchedule;

/**
 * Created by alnedorezov on 9/28/16.
 */

public class DataAccessObjectsTest extends AndroidTestCase {
    private DatabaseHelper helper;
    private int id;
    private String modifiedDateTime;
    private String name;
    private String question;
    private double latitude;
    private double longitude;
    private double range;
    private String audioResource;
    private boolean usesConfirmation;
    private int confirmAfterMinutes;
    private int categoryId;
    private boolean isPerformed;

    @Before
    public void setUp() throws Exception {
        DatabaseManager.setHelper(this.getContext());
        helper = DatabaseManager.getHelper();

        id = 1;
        modifiedDateTime = "2015-01-02 03:04:05.6";
        name = "sleep";
        question = "sleep";
        latitude = 55.75417935;
        longitude = 48.7440855;
        range = 9;

        String filename = "/meouing_kittten.mp3";
        // Environment.getExternalStorageDirectory().getPath() represents /sdcard/extended/0
        if (Environment.isExternalStorageEmulated())
            audioResource = Environment.getExternalStorageDirectory().getPath() + filename;
        else
            audioResource = filename;
        usesConfirmation = true;
        confirmAfterMinutes = 60;
        categoryId = 1;
        isPerformed = false;
    }

    @Test
    public void testWritingNewHabitDataToMobileDB() throws ParseException {
        HabitDAO habitDAO = new HabitDAO(this.getContext());

        /* if table is not empty, id = maxIdInTheTable+1, else id=1 */
        QueryBuilder<Habit, Integer> qBuilder;
        try {
            qBuilder = helper.getHabitDao().queryBuilder();
            if (qBuilder.query().size() > 0) {
                qBuilder.orderBy(Constants.ID, false); // false for descending order
                qBuilder.limit(1);
                id = qBuilder.query().get(0).getId() + 1;
            } else {
                id = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Habit habitWithTheSameId;
        Habit habitFromMobileDatabaseWithMaxId;

        Habit newHabit = new Habit(id, name, question, modifiedDateTime, latitude, longitude,
                range, audioResource, usesConfirmation, confirmAfterMinutes, categoryId);
        habitDAO.create(newHabit);

        // create(), findById() check
        habitWithTheSameId = (Habit) habitDAO.findById(id);
        assertEquals(newHabit, habitWithTheSameId);

        List<Habit> habitList = new ArrayList<>();
        habitList.add(newHabit);

        List<Habit> allHabitsList = (List<Habit>) habitDAO.findAll();

        // findAll() check
        assertEquals(habitList, allHabitsList);

        habitFromMobileDatabaseWithMaxId = (Habit) habitDAO.getObjectWithMaxId();

        // getObjectWithMaxId() check
        assertEquals(newHabit, habitFromMobileDatabaseWithMaxId);
        // delete() check
        habitDAO.delete(newHabit);
        assertFalse(habitDAO.findAll().size() > 0 && newHabit == habitDAO.getObjectWithMaxId());

        habitDAO.createOrUpdateIfExists(newHabit);
        habitFromMobileDatabaseWithMaxId = (Habit) habitDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check creation
        assertEquals(newHabit, habitFromMobileDatabaseWithMaxId);

        confirmAfterMinutes = 90;
        // change confirmAfterMinutes to 90
        newHabit = new Habit(id, name, question, modifiedDateTime, latitude, longitude,
                range, audioResource, usesConfirmation, confirmAfterMinutes, categoryId);
        habitDAO.createOrUpdateIfExists(newHabit);
        habitFromMobileDatabaseWithMaxId = (Habit) habitDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check update
        assertEquals(habitFromMobileDatabaseWithMaxId.getConfirmAfterMinutes(), confirmAfterMinutes);
        assertEquals(newHabit, habitFromMobileDatabaseWithMaxId);
        habitDAO.delete(newHabit);
        assertFalse(habitDAO.findAll().size() > 0 && newHabit == habitDAO.getObjectWithMaxId());

        habitDAO.create(newHabit);
        // confirmAfterMinutes is equal to 90
        newHabit = new Habit(id, name, question, modifiedDateTime, latitude, longitude,
                range, audioResource, usesConfirmation, confirmAfterMinutes, categoryId);
        habitDAO.update(newHabit);
        habitFromMobileDatabaseWithMaxId = (Habit) habitDAO.getObjectWithMaxId();
        // check update()
        assertEquals(habitFromMobileDatabaseWithMaxId.getConfirmAfterMinutes(), confirmAfterMinutes);
        assertEquals(newHabit, habitFromMobileDatabaseWithMaxId);
        habitDAO.delete(newHabit);
        assertFalse(habitDAO.findAll().size() > 0 && newHabit == habitDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewHabitCategoryDataToMobileDB() throws ParseException {
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getContext());

        /* if table is not empty, id = maxIdInTheTable+1, else id=1 */
        QueryBuilder<HabitCategory, Integer> qBuilder;
        try {
            qBuilder = helper.getHabitCategoryDao().queryBuilder();
            if (qBuilder.query().size() > 0) {
                qBuilder.orderBy(Constants.ID, false); // false for descending order
                qBuilder.limit(1);
                id = qBuilder.query().get(0).getId() + 1;
            } else {
                id = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HabitCategory habitCategoryWithTheSameId;
        HabitCategory habitCategoryFromMobileDatabaseWithMaxId;

        HabitCategory newHabitCategory = new HabitCategory(id, name);
        habitCategoryDAO.create(newHabitCategory);

        // create(), findById() check
        habitCategoryWithTheSameId = (HabitCategory) habitCategoryDAO.findById(id);
        assertEquals(newHabitCategory, habitCategoryWithTheSameId);

        List<HabitCategory> habitCategoryList = new ArrayList<>();
        habitCategoryList.add(newHabitCategory);

        List<HabitCategory> allHabitCategorysList = (List<HabitCategory>) habitCategoryDAO.findAll();

        // findAll() check
        assertEquals(habitCategoryList, allHabitCategorysList);

        habitCategoryFromMobileDatabaseWithMaxId = (HabitCategory) habitCategoryDAO.getObjectWithMaxId();

        // getObjectWithMaxId() check
        assertEquals(newHabitCategory, habitCategoryFromMobileDatabaseWithMaxId);
        // delete() check
        habitCategoryDAO.delete(newHabitCategory);
        assertFalse(habitCategoryDAO.findAll().size() > 0 && newHabitCategory == habitCategoryDAO.getObjectWithMaxId());

        habitCategoryDAO.createOrUpdateIfExists(newHabitCategory);
        habitCategoryFromMobileDatabaseWithMaxId = (HabitCategory) habitCategoryDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check creation
        assertEquals(newHabitCategory, habitCategoryFromMobileDatabaseWithMaxId);

        name = "sport";
        // change name to "sport"
        newHabitCategory = new HabitCategory(id, name);
        habitCategoryDAO.createOrUpdateIfExists(newHabitCategory);
        habitCategoryFromMobileDatabaseWithMaxId = (HabitCategory) habitCategoryDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check update
        assertEquals(habitCategoryFromMobileDatabaseWithMaxId.getName(), name);
        assertEquals(newHabitCategory, habitCategoryFromMobileDatabaseWithMaxId);
        habitCategoryDAO.delete(newHabitCategory);
        assertFalse(habitCategoryDAO.findAll().size() > 0 && newHabitCategory == habitCategoryDAO.getObjectWithMaxId());

        habitCategoryDAO.create(newHabitCategory);
        // name currently equals "sport"
        newHabitCategory = new HabitCategory(id, name);
        habitCategoryDAO.update(newHabitCategory);
        habitCategoryFromMobileDatabaseWithMaxId = (HabitCategory) habitCategoryDAO.getObjectWithMaxId();
        // check update()
        assertEquals(habitCategoryFromMobileDatabaseWithMaxId.getName(), name);
        assertEquals(newHabitCategory, habitCategoryFromMobileDatabaseWithMaxId);
        habitCategoryDAO.delete(newHabitCategory);
        assertFalse(habitCategoryDAO.findAll().size() > 0 && newHabitCategory == habitCategoryDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewHabitScheduleDataToMobileDB() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());

        /* if table is not empty, id = maxIdInTheTable+1, else id=1 */
        QueryBuilder<HabitSchedule, Integer> qBuilder;
        try {
            qBuilder = helper.getHabitScheduleDao().queryBuilder();
            if (qBuilder.query().size() > 0) {
                qBuilder.orderBy(Constants.ID, false); // false for descending order
                qBuilder.limit(1);
                id = qBuilder.query().get(0).getId() + 1;
            } else {
                id = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HabitSchedule habitScheduleWithTheSameId;
        HabitSchedule habitScheduleFromMobileDatabaseWithMaxId;

        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);
        habitScheduleDAO.create(newHabitSchedule);

        // create(), findById() check
        habitScheduleWithTheSameId = (HabitSchedule) habitScheduleDAO.findById(id);
        assertEquals(newHabitSchedule, habitScheduleWithTheSameId);

        List<HabitSchedule> habitScheduleList = new ArrayList<>();
        habitScheduleList.add(newHabitSchedule);

        List<HabitSchedule> allHabitSchedulesList = (List<HabitSchedule>) habitScheduleDAO.findAll();

        // findAll() check
        assertEquals(habitScheduleList, allHabitSchedulesList);

        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();

        // getObjectWithMaxId() check
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);
        // delete() check
        habitScheduleDAO.delete(newHabitSchedule);
        assertFalse(habitScheduleDAO.findAll().size() > 0 && newHabitSchedule == habitScheduleDAO.getObjectWithMaxId());

        habitScheduleDAO.createOrUpdateIfExists(newHabitSchedule);
        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check creation
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);

        isPerformed = true;
        // change isPerformed to true
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);
        habitScheduleDAO.createOrUpdateIfExists(newHabitSchedule);
        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check update
        assertEquals(habitScheduleFromMobileDatabaseWithMaxId.isPerformed(), isPerformed);
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);
        habitScheduleDAO.delete(newHabitSchedule);
        assertFalse(habitScheduleDAO.findAll().size() > 0 && newHabitSchedule == habitScheduleDAO.getObjectWithMaxId());

        habitScheduleDAO.create(newHabitSchedule);
        // isPerformed is equal to true
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);
        habitScheduleDAO.update(newHabitSchedule);
        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();
        // check update()
        assertEquals(habitScheduleFromMobileDatabaseWithMaxId.isPerformed(), isPerformed);
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);
        habitScheduleDAO.delete(newHabitSchedule);
        assertFalse(habitScheduleDAO.findAll().size() > 0 && newHabitSchedule == habitScheduleDAO.getObjectWithMaxId());
    }
}