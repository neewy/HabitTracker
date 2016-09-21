package si.modrajagoda.didi;

import android.content.res.Configuration;
import android.os.Bundle;
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

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private FragmentManager fragmentManager;

    /**
     * <b>onCreate</b> is invoked when Activity is first created (and not visible yet to the user)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        fragmentManager.beginTransaction().replace(R.id.container, new HabitListFragment()).commit();
    }

    /**
     * <b>onStart</b> is invoked before Activity is visible to the user
     */
    @Override
    protected void onStart() {
        super.onStart();
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
