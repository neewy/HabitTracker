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
    private Boolean isDone;

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
        isDone = null;

        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getContext());
        habitCategoryDAO.create(new HabitCategory(1, this.getContext().getString(R.string.sport)));
        habitCategoryDAO.create(new HabitCategory(2, this.getContext().getString(R.string.reading)));
        habitCategoryDAO.create(new HabitCategory(3, this.getContext().getString(R.string.cooking)));
        habitCategoryDAO.create(new HabitCategory(4, this.getContext().getString(R.string.cleaning)));
        habitCategoryDAO.create(new HabitCategory(5, this.getContext().getString(R.string.studying)));
        habitCategoryDAO.create(new HabitCategory(6, this.getContext().getString(R.string.health)));
        habitCategoryDAO.create(new HabitCategory(7, this.getContext().getString(R.string.other)));
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

        List<HabitCategory> habitCategoriesToDelete = (List<HabitCategory>) habitCategoryDAO.findAll();
        id = habitCategoriesToDelete.size() + 1;
        for (HabitCategory habitCategory : habitCategoriesToDelete)
            habitCategoryDAO.delete(habitCategory);

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

        habitCategoryDAO.create(new HabitCategory(1, this.getContext().getString(R.string.sport)));
        habitCategoryDAO.create(new HabitCategory(2, this.getContext().getString(R.string.reading)));
        habitCategoryDAO.create(new HabitCategory(3, this.getContext().getString(R.string.cooking)));
        habitCategoryDAO.create(new HabitCategory(4, this.getContext().getString(R.string.cleaning)));
        habitCategoryDAO.create(new HabitCategory(5, this.getContext().getString(R.string.studying)));
        habitCategoryDAO.create(new HabitCategory(6, this.getContext().getString(R.string.health)));
        habitCategoryDAO.create(new HabitCategory(7, this.getContext().getString(R.string.other)));
    }

    @Test
    public void testWritingNewHabitScheduleDataToMobileDB() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());

        HabitSchedule habitScheduleWithTheSameId;
        HabitSchedule habitScheduleFromMobileDatabaseWithMaxId;

        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
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
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);

        habitScheduleDAO.createOrUpdateIfExists(newHabitSchedule);
        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check creation
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);

        isDone = true;
        // change isDone to true
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
        habitScheduleDAO.createOrUpdateIfExists(newHabitSchedule);
        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();
        // createOrUpdateIfExists() check update
        assertEquals(habitScheduleFromMobileDatabaseWithMaxId.isDone(), isDone);
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);
        habitScheduleDAO.delete(newHabitSchedule);
        assertFalse(habitScheduleDAO.findAll().size() > 0 && newHabitSchedule == habitScheduleDAO.getObjectWithMaxId());

        // As auto-increment is used for the ids,
        // the id of the next created element will be bigger on 1
        id++;
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);

        habitScheduleDAO.create(newHabitSchedule);
        // isDone is equal to true
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
        habitScheduleDAO.update(newHabitSchedule);
        habitScheduleFromMobileDatabaseWithMaxId = (HabitSchedule) habitScheduleDAO.getObjectWithMaxId();
        // check update()
        assertEquals(habitScheduleFromMobileDatabaseWithMaxId.isDone(), isDone);
        assertEquals(newHabitSchedule, habitScheduleFromMobileDatabaseWithMaxId);
        habitScheduleDAO.delete(newHabitSchedule);
        assertFalse(habitScheduleDAO.findAll().size() > 0 && newHabitSchedule == habitScheduleDAO.getObjectWithMaxId());
    }

    @Test
    public void testFindingHabitSchedulesFoundForToday() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
        // Test schedules for today
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();
        c.add(Calendar.DATE, 2);
        Date twoDaysAfterToday = c.getTime();
        habitScheduleDAO.create(newHabitSchedule);
        HabitSchedule habitScheduleForToday = new HabitSchedule(id, today, isDone, id);
        habitScheduleDAO.create(habitScheduleForToday);
        newHabitSchedule = new HabitSchedule(id, twoDaysAfterToday, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findHabitSchedulesForToday();
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0), habitScheduleForToday);
    }

    @Test
    public void testFindingHabitSchedulesFoundForTomorrow() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
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
        newHabitSchedule = new HabitSchedule(id, today, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        HabitSchedule habitScheduleForTomorrow = new HabitSchedule(id, tomorrow, isDone, id);
        habitScheduleDAO.create(habitScheduleForTomorrow);
        newHabitSchedule = new HabitSchedule(id, twoDaysAfterToday, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findHabitSchedulesForTomorrow();
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0), habitScheduleForTomorrow);
    }

    @Test
    public void testFindingHabitSchedulesFoundForNextMonth() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
        // Test schedules for next month
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        c.add(Calendar.MONTH, 1);
        Date aDayAndAMonthAfterToday = c.getTime();
        habitScheduleDAO.create(newHabitSchedule);
        HabitSchedule habitScheduleForTomorrow = new HabitSchedule(id, tomorrow, isDone, id);
        habitScheduleDAO.create(habitScheduleForTomorrow);
        newHabitSchedule = new HabitSchedule(id, aDayAndAMonthAfterToday, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findHabitSchedulesForTomorrow();
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0), habitScheduleForTomorrow);
    }

    @Test
    public void testGetArrayOfAllHabitCategoryNames() throws ParseException {
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getContext());
        HabitCategory habitCategory = new HabitCategory(id, name);
        habitCategoryDAO.create(habitCategory);
        // change name to "abrakadabra"
        habitCategory = new HabitCategory(id, "abrakadabra");
        habitCategoryDAO.create(habitCategory);
        CharSequence[] categoryNamesArray = habitCategoryDAO.getArrayOfAllNames();
        List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();
        assertEquals(categoryNamesArray.length, habitCategories.size());
        for (int i = 0; i < categoryNamesArray.length; i++)
            assertEquals(categoryNamesArray[i], habitCategories.get(i).getName());
    }

    @Test
    public void testFindingHabitSchedulesByHabitId() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        HabitSchedule newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        habitScheduleDAO.create(newHabitSchedule);
        habitScheduleDAO.create(newHabitSchedule);
        // changing habitId to 2
        id = 2;
        newHabitSchedule = new HabitSchedule(id, modifiedDateTime, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findByHabitId(id);
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0).getHabitId(), id);
    }

    @Test
    public void testFindingHabitSchedulesInRange() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        Calendar c = new GregorianCalendar();
        Date date = c.getTime();
        HabitSchedule newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        Date fromDate = date;
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        Date toDate = date; // 5 habitSchedules in between fromDate and toDate were created
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        List<HabitSchedule> habitScheduleList = habitScheduleDAO.findInRange(fromDate, toDate);
        assertEquals(habitScheduleList.size(), 5);
    }

    @Test
    public void testDeletionOfHabitSchedulesOlderThanThirtyOneDay() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        // Test schedules for next month
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.MONTH, -2);
        Date date = c.getTime();
        HabitSchedule newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.MONTH, -1);
        c.add(Calendar.DATE, -1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        HabitSchedule notOldHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(notOldHabitSchedule);
        habitScheduleDAO.deleteHabitSchedulesOlderThanThirtyOneDay();
        List<HabitSchedule> habitScheduleList = (List<HabitSchedule>) habitScheduleDAO.findAll();
        assertEquals(habitScheduleList.size(), 1);
        assertEquals(habitScheduleList.get(0), notOldHabitSchedule);
    }

    @Test
    public void testGettingPercentageOfDoneSchedulesForDistinctHabitByHabitId() throws ParseException {
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getContext());
        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, -7);
        Date date = c.getTime();
        HabitSchedule newHabitSchedule = new HabitSchedule(id, date, isDone, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, false, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, true, id);
        habitScheduleDAO.create(newHabitSchedule);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        newHabitSchedule = new HabitSchedule(id, date, true, id);
        habitScheduleDAO.create(newHabitSchedule);
        double percentage = habitScheduleDAO.getPercentageOfDoneSchedulesForDistinctHabitByHabitId(id);
        assertEquals(50.0, percentage);
    }
}