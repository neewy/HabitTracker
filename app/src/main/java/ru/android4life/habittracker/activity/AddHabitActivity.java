package ru.android4life.habittracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.android4life.habittracker.HabitParameter;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.adapter.HabitParametersAdapter;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.views.RippleView;

/**
 * Created by Bulat Mukhutdinov on 24.09.2016.
 */
public class AddHabitActivity extends BaseActivity {

    public static final int PICK_AUDIO_REQUEST = 0;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private HabitParametersAdapter.HabitSettings habitSettings;
    private HabitCategoryDAO habitCategoryDAO;
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private SharedPreferences habitSettingsPrefs = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        // Initialise DAOs, prefs & notificationSoundUri with default notification uri
        habitSettingsPrefs = getApplicationContext().getSharedPreferences(getApplicationContext()
                .getString(R.string.creating_habit_settings), MODE_PRIVATE);

        habitCategoryDAO = new HabitCategoryDAO(getApplicationContext());
        habitDAO = new HabitDAO(getApplicationContext());
        habitScheduleDAO = new HabitScheduleDAO(getApplicationContext());

        habitSettings = new HabitParametersAdapter.HabitSettings();

        final RippleView textView = (RippleView) findViewById(R.id.add_habit_back_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, (int) (textView.getRippleDuration() * 1.1d));

            }
        });

        final RippleView confirmButton = (RippleView) findViewById(R.id.add_habit_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Get data from HabitParametersAdapter, create Habits according to it
                getHabitSettingsFromPreferences();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, (int) (textView.getRippleDuration() * 1.1d));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.habit_parameters_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HabitParametersAdapter(this, HabitParameter.createParameters(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AUDIO_REQUEST) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                habitSettings.setNotificationSoundUri(uri);
                Log.i("AUDIO", "Uri: " + habitSettings.getNotificationSoundUri().toString());
                Ringtone r = RingtoneManager.getRingtone(MainActivity.getContext(),
                        habitSettings.getNotificationSoundUri());
                habitSettings.setNotificationSoundName(r.getTitle(MainActivity.getContext()));
                Log.i("AUDIO", "Name: " + habitSettings.getNotificationSoundName());

                habitSettingsPrefs.edit().putString(MainActivity.getContext().getResources()
                        .getString(R.string.notification_sound_name), habitSettings.getNotificationSoundName()).apply();
            }
        }
    }

    private void removeValuesForHabitSettingsFromPreferences() {
        habitSettingsPrefs.edit().remove("categoryId").apply();
        habitSettingsPrefs.edit().remove("notificationHour").apply();
        habitSettingsPrefs.edit().remove("notificationMinute").apply();
        habitSettingsPrefs.edit().remove("notificationFrequencyType").apply();
        for (int i = 0; i <= 7; i++)
            habitSettingsPrefs.edit().remove(getApplicationContext().getResources()
                    .getString(R.string.notification_frequency_specified_day_string,
                            String.valueOf(i))).apply();
        habitSettingsPrefs.edit().remove("notificationFrequencyWeekNumberOrDate").apply();
    }

    private void getHabitSettingsFromPreferences() {
        HabitParametersAdapter.HabitSettings result = new HabitParametersAdapter.HabitSettings();
        if (prefs.contains("categoryId"))
            result.setCategoryId(prefs.getInt("categoryId", habitSettings.getCategoryId()));
        if (prefs.contains("notificationHour"))
            result.setNotificationHour(prefs.getInt("notificationHour", habitSettings.getNotificationHour()));
        if (prefs.contains("notificationMinute"))
            result.setNotificationMinute(prefs.getInt("notificationMinute", habitSettings.getNotificationMinute()));
        if (prefs.contains("notificationFrequencyType"))
            result.setNotificationFrequencyType(parseStringToNotificationFrequencyType(prefs
                    .getString("notificationFrequencyType", habitSettings.getNotificationFrequencyType().toString())));
        boolean[] notificationFrequencySpecifiedDays = {false, false, false, false, false, false, false};

        if (prefs.contains("notificationFrequencySpecifiedDay1"))
            notificationFrequencySpecifiedDays[0] = prefs.getBoolean("notificationFrequencySpecifiedDay1", habitSettings.getNotificationFrequencySpecifiedDays()[0]);
        if (prefs.contains("notificationFrequencySpecifiedDay2"))
            notificationFrequencySpecifiedDays[1] = prefs.getBoolean("notificationFrequencySpecifiedDay2", habitSettings.getNotificationFrequencySpecifiedDays()[1]);
        if (prefs.contains("notificationFrequencySpecifiedDay3"))
            notificationFrequencySpecifiedDays[2] = prefs.getBoolean("notificationFrequencySpecifiedDay3", habitSettings.getNotificationFrequencySpecifiedDays()[2]);
        if (prefs.contains("notificationFrequencySpecifiedDay4"))
            notificationFrequencySpecifiedDays[3] = prefs.getBoolean("notificationFrequencySpecifiedDay4", habitSettings.getNotificationFrequencySpecifiedDays()[3]);
        if (prefs.contains("notificationFrequencySpecifiedDay5"))
            notificationFrequencySpecifiedDays[4] = prefs.getBoolean("notificationFrequencySpecifiedDay5", habitSettings.getNotificationFrequencySpecifiedDays()[4]);
        if (prefs.contains("notificationFrequencySpecifiedDay6"))
            notificationFrequencySpecifiedDays[5] = prefs.getBoolean("notificationFrequencySpecifiedDay6", habitSettings.getNotificationFrequencySpecifiedDays()[5]);
        if (prefs.contains("notificationFrequencySpecifiedDay7"))
            notificationFrequencySpecifiedDays[6] = prefs.getBoolean("notificationFrequencySpecifiedDay7", habitSettings.getNotificationFrequencySpecifiedDays()[6]);

        result.setNotificationFrequencySpecifiedDays(notificationFrequencySpecifiedDays);

        if (prefs.contains("notificationFrequencyWeekNumberOrDate"))
            result.setNotificationFrequencyWeekNumberOrDate(prefs.getInt("notificationFrequencyWeekNumberOrDate",
                    habitSettings.getNotificationFrequencyWeekNumberOrDate()));
        if (prefs.contains("minutesBeforeConfirmation"))
            result.setMinutesBeforeConfirmation(prefs.getInt("minutesBeforeConfirmation", habitSettings.getMinutesBeforeConfirmation()));

        habitSettings = result;
    }

    private HabitParametersAdapter.NotificationFrequencyType parseStringToNotificationFrequencyType(String s) {
        switch (s) {
            case "DAILY":
                return HabitParametersAdapter.NotificationFrequencyType.DAILY;
            case "WEEKLY":
                return HabitParametersAdapter.NotificationFrequencyType.WEEKLY;
            case "MONTHLY":
                return HabitParametersAdapter.NotificationFrequencyType.MONTHLY;
            case "SPECIFIED_DAYS":
                return HabitParametersAdapter.NotificationFrequencyType.SPECIFIED_DAYS;
            default:
                return HabitParametersAdapter.NotificationFrequencyType.DAILY;
        }
    }

    private void createHabitsAccordingToHabitPreferences() {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, habitSettings.getNotificationHour());
        c.set(Calendar.MINUTE, habitSettings.getNotificationMinute());
        c.set(Calendar.SECOND, 0);
        Date habitDay = c.getTime();
        c.add(Calendar.DATE, 1);
        Date habitDayPlusDay = c.getTime();

        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.add_habit_title_edit_text);

        int result = habitDAO.create(new Habit(1, "privuichkaaa 1", "do privuichka 1", habitDay, 55.75417935,
                48.7440855, 9, Environment.getExternalStorageDirectory().getPath()
                + "/meouing_kittten.mp3", true, 60, 1));
    }

}
