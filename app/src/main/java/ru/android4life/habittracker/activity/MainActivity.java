package ru.android4life.habittracker.activity;

import android.content.Context;
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

import java.text.ParseException;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.DatabaseManager;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.fragment.HabitListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Context context;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fragmentManager;

    public static Context getContext() {
        return context;
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

        /* Creation of demo data while the creation buttons doesn't work */
        HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(this.getApplicationContext());
        HabitDAO habitDAO = new HabitDAO(this.getApplicationContext());
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(this.getApplicationContext());
        try {
            habitDAO.create(new Habit(1, "Privuichka 1", "do privuichka 1", "2015-01-02 03:04:05.6",
                    55.75417935, 48.7440855, 9, Environment.getExternalStorageDirectory().getPath()
                    + "/meouing_kittten.mp3", true, 60, 1));
        } catch (ParseException e) {
            Log.e(Constants.ERROR, e.getMessage(), e.fillInStackTrace());
        }

        context = this.getApplicationContext();

        fragmentManager.beginTransaction().replace(R.id.container, new HabitListFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //TODO: check if this item was selected before

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_today:
                fragment = new HabitListFragment();
                break;
            case R.id.nav_all_tasks:
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
}