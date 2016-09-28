package si.modrajagoda.didi;

import android.os.Environment;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import si.modrajagoda.didi.db.dataaccessobjects.HabitCategoryDAO;
import si.modrajagoda.didi.db.dataaccessobjects.HabitDAO;
import si.modrajagoda.didi.db.tablesrepresentations.Habit;
import si.modrajagoda.didi.db.tablesrepresentations.HabitCategory;

/**
 * Created by alnedorezov on 9/28/16.
 */

public class DataAccessObjectsTest extends AndroidTestCase {
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

    @Before
    public void setUp() throws Exception {
        // TODO: increment DATABASE_VERSION in db/Constants.java or delete current db version
        // through adb shell before running the test
        id = 1;
        modifiedDateTime = "2015-01-02 03:04:05.6";
        name = "sleep";
        question = "sleep";
        latitude = 55.75417935;
        longitude = 48.7440855;
        range = 9;

        String filename = "/meouing_kittten.mp3";
        // Environment.getExternalStorageDirectory().getPath() represents /sdcard/
        if (Environment.isExternalStorageEmulated())
            audioResource = Environment.getExternalStorageDirectory().getPath() + filename;
        else
            audioResource = filename;
        usesConfirmation = true;
        confirmAfterMinutes = 60;
        categoryId = 1;
    }

    @Test
    public void testWritingNewHabitDataToMobileDB() throws ParseException {
        HabitDAO habitDAO = new HabitDAO(this.getContext());

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
        assertEquals(habitFromMobileDatabaseWithMaxId.getConfirmAfterMinutes(), 90);
        assertEquals(newHabit, habitFromMobileDatabaseWithMaxId);
        habitDAO.delete(newHabit);
        assertFalse(habitDAO.findAll().size() > 0 && newHabit == habitDAO.getObjectWithMaxId());

        habitDAO.create(newHabit);
        // confirmAfterMinutes are equal to 90
        newHabit = new Habit(id, name, question, modifiedDateTime, latitude, longitude,
                range, audioResource, usesConfirmation, confirmAfterMinutes, categoryId);
        habitDAO.update(newHabit);
        habitFromMobileDatabaseWithMaxId = (Habit) habitDAO.getObjectWithMaxId();
        // check update()
        assertEquals(habitFromMobileDatabaseWithMaxId.getConfirmAfterMinutes(), 90);
        assertEquals(newHabit, habitFromMobileDatabaseWithMaxId);
        habitDAO.delete(newHabit);
        assertFalse(habitDAO.findAll().size() > 0 && newHabit == habitDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewHabitCategoryDataToMobileDB() throws ParseException {
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getContext());

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
}