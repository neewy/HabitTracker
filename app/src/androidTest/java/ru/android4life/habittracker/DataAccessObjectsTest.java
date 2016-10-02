package ru.android4life.habittracker;

import android.os.Environment;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.DatabaseHelper;
import ru.android4life.habittracker.db.DatabaseManager;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

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
        helper.onUpgrade(helper.getReadableDatabase(), helper.getConnectionSource(),
                Constants.DATABASE_VERSION, Constants.DATABASE_VERSION);

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

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabit = new Habit(id, name, question, modifiedDateTime, latitude, longitude,
                range, audioResource, usesConfirmation, confirmAfterMinutes, categoryId);

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

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabit = new Habit(id, name, question, modifiedDateTime, latitude, longitude,
                range, audioResource, usesConfirmation, confirmAfterMinutes, categoryId);

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

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabitCategory = new HabitCategory(id, name);

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

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabitCategory = new HabitCategory(id, name);

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

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);

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

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);

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

    @Test
    public void testFindingHabitSchedulesFoundForToday() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);
        // Test schedules for today
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();
        c.add(Calendar.DATE, 2);
        Date twoDaysAfterToday = c.getTime();
        habitScheduleDAO.create(newHabitSchedule);
        HabitSchedule habitScheduleForToday = new HabitSchedule(id, today, isPerformed, id);
        habitScheduleDAO.create(habitScheduleForToday);
        newHabitSchedule = new HabitSchedule(id, twoDaysAfterToday, isPerformed, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findHabitSchedulesForToday();
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0), habitScheduleForToday);
    }

    @Test
    public void testFindingHabitSchedulesFoundForTomorrow() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isPerformed, id);
        // Test schedules for tomorrow
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date twoDaysAfterToday = c.getTime();
        habitScheduleDAO.create(newHabitSchedule);
        newHabitSchedule = new HabitSchedule(id, today, isPerformed, id);
        habitScheduleDAO.create(newHabitSchedule);
        HabitSchedule habitScheduleForTomorrow = new HabitSchedule(id, tomorrow, isPerformed, id);
        habitScheduleDAO.create(habitScheduleForTomorrow);
        newHabitSchedule = new HabitSchedule(id, twoDaysAfterToday, isPerformed, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findHabitSchedulesForTomorrow();
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0), habitScheduleForTomorrow);
    }
}