package ru.android4life.habittracker;

import android.support.v7.widget.AppCompatCheckedTextView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.hamcrest.Matchers;

import ru.android4life.habittracker.activity.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


public class DrawerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public DrawerTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SuppressWarnings("unchecked")
    public void testOpenCloseDrawer() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        closeDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }


    public void testItemDrawer() {
        String[] rowContents = {"Today", "Tomorrow", "Next month", "Habits", "Statistics", "Settings"};
        String[] namedAction = {"longClick()", "scrollTo()", "doubleClick()"};
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        for (int i = 0; i < rowContents.length; i++) {
            onView(Matchers.allOf(withText(rowContents[i]), is(instanceOf(AppCompatCheckedTextView.class)))).perform(click());
            for (int j = 0; j < 3; j++) {
                openDrawer(R.id.drawer_layout);
                try {
                    switch (j) {
                        case 0: {
                            onView(withText(rowContents[i])).perform(longClick());
                            break;
                        }
                        case 1: {
                            onView(withText(rowContents[i])).perform(scrollTo());
                            break;
                        }
                        case 2: {
                            onView(withText(rowContents[i])).perform(doubleClick());
                            break;
                        }
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.d("MyTestLog", "action " + namedAction[j] + " is not exist for " + rowContents[i]);
                    openDrawer(R.id.drawer_layout);
                }
            }

        }
    }


}
