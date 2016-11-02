package ru.android4life.habittracker.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Locale;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.DatabaseManager;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.enumeration.DrawerSelectionMode;
import ru.android4life.habittracker.fragment.HabitListFragment;
import ru.android4life.habittracker.fragment.HabitsFragment;
import ru.android4life.habittracker.fragment.SettingsFragment;
import ru.android4life.habittracker.fragment.StatisticsFragment;

import static ru.android4life.habittracker.enumeration.DrawerSelectionMode.NEXT_MONTH;
import static ru.android4life.habittracker.enumeration.DrawerSelectionMode.TODAY;
import static ru.android4life.habittracker.enumeration.DrawerSelectionMode.TOMORROW;
import static ru.android4life.habittracker.enumeration.DrawerSelectionMode.findDrawerSelectionMode;
import static ru.android4life.habittracker.utils.StringConstants.FIRSTRUN;
import static ru.android4life.habittracker.utils.StringConstants.LOCALE;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Locale locale;
    public static DrawerSelectionMode drawerSelectionMode;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fragmentManager;

    /**
     * <b>onCreate</b> is invoked when Activity is first created (and not visible yet to the user)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContext(this.getApplicationContext());
        locale = new Locale(prefs.getString(LOCALE, getResources().getString(R.string.locale_en)));
        Constants.updatePrettyTime();
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getContext().getResources().updateConfiguration(config,
                getContext().getResources().getDisplayMetrics());
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
        if (prefs.getBoolean(FIRSTRUN, true)) {
            forFirstRun();
        }
        // Initiate db
        DatabaseManager.setHelper(getContext());

        setUpFirstFragment(navigationView);
    }

    // Sets up initial fragment and drawer menu
    private void setUpFirstFragment(NavigationView nv) {
        drawerSelectionMode = TODAY;
        fragmentManager.beginTransaction().replace(R.id.container,
                new HabitListFragment(), drawerSelectionMode.stringValue).commit();
        // set title in actionbar
        setTitle(getString(R.string.today));
        // select first element (today) in drawer's list
        nv.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

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
                fragment = new HabitsFragment();
                break;
            case R.id.nav_statistics:
                drawerSelectionMode = DrawerSelectionMode.STATISTICS;
                fragment = new StatisticsFragment();
                break;
            case R.id.nav_settings:
                drawerSelectionMode = DrawerSelectionMode.SETTINGS;
                fragment = new SettingsFragment();
                break;
        }

        // Emptying fragments in the backstack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Replacing activity content with the content of fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment, drawerSelectionMode.stringValue).commit();

        // Set the item as checked in drawer menu
        item.setChecked(true);

        // Set action bar title
        setTitle(item.getTitle());

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        String fragmentName, title = "";

        // If there are some fragments it backstack
        if (fragmentManager.getBackStackEntryCount() > 0) {
            // then get the name of last element
            fragmentName = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();

            // and then assign the string resource of this name to title
            switch (findDrawerSelectionMode(fragmentName)) {
                case TODAY:
                    title = getString(R.string.today);
                    break;
                case TOMORROW:
                    title = getString(R.string.tomorrow);
                    break;
                case NEXT_MONTH:
                    title = getString(R.string.next_month);
                    break;
                case ALL_TASKS:
                    title = getString(R.string.all_tasks);
                    break;
                case STATISTICS:
                    title = getString(R.string.statistics);
                    break;
                case SETTINGS:
                    title = getString(R.string.settings);
                    break;
            }

            // set the title in actionbar to this title
            setTitle(title);

            // pop the last fragment back on screen
            fragmentManager.popBackStack();

        } else {
            // Finish the Activity
            super.onBackPressed();
        }
    }

    void forFirstRun() {

        // Do first run stuff here then set 'firstrun' as false
        /* Creation of categories */
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getApplicationContext());
        habitCategoryDAO.create(new HabitCategory(1, getResources().getString(R.string.sport)));
        habitCategoryDAO.create(new HabitCategory(2, getResources().getString(R.string.reading)));
        habitCategoryDAO.create(new HabitCategory(3, getResources().getString(R.string.cooking)));
        habitCategoryDAO.create(new HabitCategory(4, getResources().getString(R.string.cleaning)));
        habitCategoryDAO.create(new HabitCategory(5, getResources().getString(R.string.studying)));
        habitCategoryDAO.create(new HabitCategory(6, getResources().getString(R.string.health)));
        habitCategoryDAO.create(new HabitCategory(7, getResources().getString(R.string.other)));

        // using the following line to edit/commit prefs
        prefs.edit().putBoolean(FIRSTRUN, false).apply();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerSelectionMode == TODAY || drawerSelectionMode == TOMORROW || drawerSelectionMode == NEXT_MONTH) {
            HabitListFragment habitList = (HabitListFragment) fragmentManager.findFragmentByTag(drawerSelectionMode.stringValue);
            if (habitList != null) {
                habitList.invalidateDataSet();
                habitList.switchEmptyView();
            }
        }
    }
}
