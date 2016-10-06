package ru.android4life.habittracker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.DatabaseHelper;
import ru.android4life.habittracker.db.DatabaseManager;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.fragment.DrawerSelectionMode;
import ru.android4life.habittracker.fragment.HabitListFragment;
import ru.android4life.habittracker.fragment.SettingsFragment;
import ru.android4life.habittracker.fragment.StatisticsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Context context;
    private static DrawerSelectionMode drawerSelectionMode;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fragmentManager;
    private SharedPreferences prefs = null;
    private DatabaseHelper database;

    public static Context getContext() {
        return context;
    }

    public static DrawerSelectionMode getDrawerSelectionMode() {
        return drawerSelectionMode;
    }

    /**
     * <b>onCreate</b> is invoked when Activity is first created (and not visible yet to the user)
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        //Init the database manager
        DatabaseManager.setHelper(this);

        // run method forFirstRun only if the application wasn't run after installation
        prefs = getSharedPreferences("firstRun", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            forFirstRun();
        }

        context = this.getApplicationContext();
        // Initiate db
        DatabaseManager.setHelper(context);
        database = DatabaseManager.getHelper();
        database.onUpgrade(database.getReadableDatabase(), database.getConnectionSource(),
                Constants.DATABASE_VERSION, Constants.DATABASE_VERSION);
        drawerSelectionMode = DrawerSelectionMode.TODAY;
        fragmentManager.beginTransaction().replace(R.id.container,
                new HabitListFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //TODO: check if this item was selected before

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_today:
                drawerSelectionMode = DrawerSelectionMode.TODAY;
                fragment = new HabitListFragment();
                break;
            case R.id.nav_tomorrow:
                drawerSelectionMode = DrawerSelectionMode.TOMORROW;
                fragment = new HabitListFragment();
                break;
            case R.id.nav_next_month:
                drawerSelectionMode = DrawerSelectionMode.NEXT_MONTH;
                fragment = new HabitListFragment();
                break;
            case R.id.nav_all_tasks:
                drawerSelectionMode = DrawerSelectionMode.ALL_TASKS;
                fragment = new HabitListFragment();
                break;
            case R.id.nav_statistics:
                fragment = new StatisticsFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            default:
                Log.d("Drawer", "Any other was clicked");
                break;
        }

        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        //Set the item as checked in drawer menu
        item.setChecked(true);

        // Set action bar title
        setTitle(item.getTitle());

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        toggle.onConfigurationChanged(newConfig);
    }

    void forFirstRun() {

        // Do first run stuff here then set 'firstrun' as false
        /* Creation of demo data while the creation buttons doesn't work */
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getApplicationContext());
        HabitDAO habitDAO = new HabitDAO(this.getApplicationContext());
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getApplicationContext());
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today = c.getTime();
        c.add(Calendar.DATE, 1);
        Date tomorrow = c.getTime();
        c.add(Calendar.DATE, 1);
        Date twoDaysAfterToday = c.getTime();
        habitDAO.create(new Habit(1, "Privuichka 1", "do privuichka 1", today, 55.75417935,
                48.7440855, 9, Environment.getExternalStorageDirectory().getPath()
                + "/meouing_kittten.mp3", true, 60, 1));
        habitDAO.create(new Habit(2, "Privuichka 2", "do privuichka 2", tomorrow, 55.75417935,
                48.7440855, 9, Environment.getExternalStorageDirectory().getPath()
                + "/meouing_kittten.mp3", true, 60, 1));
        habitDAO.create(new Habit(3, "Privuichka 3", "do privuichka 3", twoDaysAfterToday,
                55.75417935, 48.7440855, 9, Environment.getExternalStorageDirectory().getPath()
                + "/meouing_kittten.mp3", true, 60, 1));
        habitScheduleDAO.create(new HabitSchedule(1, today, false, false, 1));
        habitScheduleDAO.create(new HabitSchedule(2, today, true, false, 2));
        habitScheduleDAO.create(new HabitSchedule(3, today, false, false, 3));
        habitScheduleDAO.create(new HabitSchedule(4, tomorrow, false, false, 1));
        habitScheduleDAO.create(new HabitSchedule(5, tomorrow, false, false, 2));
        habitScheduleDAO.create(new HabitSchedule(6, tomorrow, false, false, 3));
        habitScheduleDAO.create(new HabitSchedule(7, twoDaysAfterToday, false, false, 1));
        habitScheduleDAO.create(new HabitSchedule(8, twoDaysAfterToday, false, false, 2));
        habitScheduleDAO.create(new HabitSchedule(9, twoDaysAfterToday, true, false, 3));

        // using the following line to edit/commit prefs
        prefs.edit().putBoolean("firstrun", false).apply();
    }
}
