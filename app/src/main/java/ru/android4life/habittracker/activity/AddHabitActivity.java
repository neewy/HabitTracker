package ru.android4life.habittracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.android4life.habittracker.HabitNotification;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.adapter.HabitParametersAdapter;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.enumeration.NotificationFrequencyType;
import ru.android4life.habittracker.models.HabitParameter;
import ru.android4life.habittracker.models.HabitSettings;
import ru.android4life.habittracker.views.RippleView;

/**
 * Created by Bulat Mukhutdinov on 24.09.2016.
 */
public class AddHabitActivity extends BaseActivity {

    public static final int PICK_AUDIO_REQUEST = 0;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private HabitSettings habitSettings;
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private SharedPreferences habitSettingsPrefs = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        setContext(this.getApplicationContext());

        // Initialise DAOs, habitSettingsPrefs & notificationSoundUri with default notification uri
        habitSettingsPrefs = getApplicationContext().getSharedPreferences(getApplicationContext()
                .getString(R.string.creating_habit_settings), MODE_PRIVATE);

        habitDAO = new HabitDAO(getApplicationContext());
        habitScheduleDAO = new HabitScheduleDAO(getApplicationContext());

        habitSettings = new HabitSettings();

        // if habit is edited, habit schedule id is passed, else = -1
        final int editedHabitScheduleId = getIntent().getIntExtra(getString(R.string.habit_schedule_id), -1);

        if (editedHabitScheduleId != -1) { // If habit is being edited
            HabitSchedule editedHabitsSchedule = (HabitSchedule) habitScheduleDAO.findById(editedHabitScheduleId);
            Habit editedHabit = (Habit) habitDAO.findById(editedHabitsSchedule.getHabitId());

            TextInputLayout habitNameTextInputLayout = (TextInputLayout) findViewById(R.id.add_habit_title_edit_text);
            habitNameTextInputLayout.getEditText().setText(editedHabit.getName());
            TextInputLayout habitQuestionTextInputLayout = (TextInputLayout) findViewById(R.id.add_habit_question_edit_text);
            habitQuestionTextInputLayout.getEditText().setText(editedHabit.getQuestion());
        }

        final RippleView textView = (RippleView) findViewById(R.id.add_habit_back_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, (int) (textView.getRippleDuration() * 1.1d));

            }
        });

        final RippleView confirmButton = (RippleView) findViewById(R.id.add_habit_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmButton.setEnabled(false); // disable button for the time while habits are created

                habitSettings = getHabitSettingsFromPreferences(editedHabitScheduleId);
                if (createOrEditHabitAccordingToHabitPreferencesIfDataIsCorrect(editedHabitScheduleId)) {
                    mAdapter.notifyDataSetChanged();

                    removeValuesForHabitSettingsFromPreferences();

                    //create (or update) alarms for habit notifications
                    HabitNotification notification = new HabitNotification(getContext());
                    notification.createAllAlarms();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, (int) (textView.getRippleDuration() * 1.1d));
                }

                confirmButton.setEnabled(true); // make the button available again
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
        if (editedHabitScheduleId == -1) // habits creation
            mAdapter = new HabitParametersAdapter(this, HabitParameter.createParameters(getApplicationContext()), true);
        else { // habits edition
            mAdapter = new HabitParametersAdapter(this,
                    HabitParameter.createParametersByHabitScheduleId(getApplicationContext(),
                            editedHabitScheduleId), true);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    private boolean isHabitNameEntered() {
        TextInputLayout habitNameTextInputLayout = (TextInputLayout) findViewById(R.id.add_habit_title_edit_text);
        String habitName = habitNameTextInputLayout.getEditText().getText().toString();

        return !(habitName.length() == 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AUDIO_REQUEST) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                habitSettings.setNotificationSoundUri(uri);
                Log.i("AUDIO", "Uri: " + habitSettings.getNotificationSoundUri().toString());
                Ringtone r = RingtoneManager.getRingtone(getContext(),
                        habitSettings.getNotificationSoundUri());
                habitSettings.setNotificationSoundName(r.getTitle(getContext()));
                Log.i("AUDIO", "Name: " + habitSettings.getNotificationSoundName());

                habitSettingsPrefs.edit().putString(getContext().getResources()
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
        habitSettingsPrefs.edit().remove(getContext().getResources()
                .getString(R.string.notification_sound_name)).apply();
        habitSettingsPrefs.edit().remove("minutesBeforeConfirmation").apply();
    }

    private HabitSettings getHabitSettingsFromPreferences(int editedHabitScheduleId) {
        HabitSettings result;
        if (editedHabitScheduleId == -1) { // Habits creation
            if (habitSettingsPrefs.contains(getContext().getString(R.string.notification_sound_name)))
                result = new HabitSettings(habitSettings.getNotificationSoundUri(),
                        habitSettings.getNotificationSoundName());
            else
                result = new HabitSettings();
        } else { // Habits edition
            if (habitSettingsPrefs.contains("notificationFrequencyType") &&
                    habitSettingsPrefs.contains(getContext().getString(R.string.notification_sound_name)))
                result = new HabitSettings(editedHabitScheduleId, true, habitSettings.getNotificationSoundUri(),
                        habitSettings.getNotificationSoundName());
            else if (!habitSettingsPrefs.contains("notificationFrequencyType") &&
                    habitSettingsPrefs.contains(getContext().getString(R.string.notification_sound_name)))
                result = new HabitSettings(editedHabitScheduleId, false, habitSettings.getNotificationSoundUri(),
                        habitSettings.getNotificationSoundName());
            else if (habitSettingsPrefs.contains("notificationFrequencyType") &&
                    !habitSettingsPrefs.contains(getContext().getString(R.string.notification_sound_name)))
                result = new HabitSettings(editedHabitScheduleId, true);
            else
                result = new HabitSettings(editedHabitScheduleId, false);
        }

        if (habitSettingsPrefs.contains("categoryId"))
            result.setCategoryId(habitSettingsPrefs.getInt("categoryId", habitSettings.getCategoryId()));
        if (habitSettingsPrefs.contains("notificationHour"))
            result.setNotificationHour(habitSettingsPrefs.getInt("notificationHour", habitSettings.getNotificationHour()));
        if (habitSettingsPrefs.contains("notificationMinute"))
            result.setNotificationMinute(habitSettingsPrefs.getInt("notificationMinute", habitSettings.getNotificationMinute()));
        if (habitSettingsPrefs.contains("notificationFrequencyType"))
            result.setNotificationFrequencyType(parseStringToNotificationFrequencyType(habitSettingsPrefs
                    .getString("notificationFrequencyType", habitSettings.getNotificationFrequencyType().toString())));
        boolean[] notificationFrequencySpecifiedDays = result.getNotificationFrequencySpecifiedDays();

        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay1"))
            notificationFrequencySpecifiedDays[0] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay1", habitSettings.getNotificationFrequencySpecifiedDays()[0]);
        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay2"))
            notificationFrequencySpecifiedDays[1] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay2", habitSettings.getNotificationFrequencySpecifiedDays()[1]);
        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay3"))
            notificationFrequencySpecifiedDays[2] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay3", habitSettings.getNotificationFrequencySpecifiedDays()[2]);
        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay4"))
            notificationFrequencySpecifiedDays[3] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay4", habitSettings.getNotificationFrequencySpecifiedDays()[3]);
        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay5"))
            notificationFrequencySpecifiedDays[4] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay5", habitSettings.getNotificationFrequencySpecifiedDays()[4]);
        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay6"))
            notificationFrequencySpecifiedDays[5] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay6", habitSettings.getNotificationFrequencySpecifiedDays()[5]);
        if (habitSettingsPrefs.contains("notificationFrequencySpecifiedDay7"))
            notificationFrequencySpecifiedDays[6] = habitSettingsPrefs.getBoolean("notificationFrequencySpecifiedDay7", habitSettings.getNotificationFrequencySpecifiedDays()[6]);

        result.setNotificationFrequencySpecifiedDays(notificationFrequencySpecifiedDays);

        if (habitSettingsPrefs.contains("notificationFrequencyWeekNumberOrDate"))
            result.setNotificationFrequencyWeekNumberOrDate(habitSettingsPrefs.getInt("notificationFrequencyWeekNumberOrDate",
                    habitSettings.getNotificationFrequencyWeekNumberOrDate()));
        if (habitSettingsPrefs.contains("minutesBeforeConfirmation"))
            result.setMinutesBeforeConfirmation(habitSettingsPrefs.getInt("minutesBeforeConfirmation", habitSettings.getMinutesBeforeConfirmation()));

        return result;
    }

    private NotificationFrequencyType parseStringToNotificationFrequencyType(String s) {
        switch (s) {
            case "DAILY":
                return NotificationFrequencyType.DAILY;
            case "WEEKLY":
                return NotificationFrequencyType.WEEKLY;
            case "MONTHLY":
                return NotificationFrequencyType.MONTHLY;
            case "SPECIFIED_DAYS":
                return NotificationFrequencyType.SPECIFIED_DAYS;
            default:
                return NotificationFrequencyType.DAILY;
        }
    }

    private boolean createOrEditHabitAccordingToHabitPreferencesIfDataIsCorrect(int editedHabitScheduleId) {
        if (!isHabitNameEntered()) {
            toastMessage(getContext().getString(R.string.habit_name_should_be_filled));
            return false;
        }

        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, habitSettings.getNotificationHour());
        c.set(Calendar.MINUTE, habitSettings.getNotificationMinute());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date habitDay = c.getTime();

        TextInputLayout habitNameTextInputLayout = (TextInputLayout) findViewById(R.id.add_habit_title_edit_text);
        String habitName = habitNameTextInputLayout.getEditText().getText().toString();
        TextInputLayout habitQuestionTextInputLayout = (TextInputLayout) findViewById(R.id.add_habit_question_edit_text);
        String habitQuestion = habitQuestionTextInputLayout.getEditText().getText().toString();

        if (habitQuestion.length() == 0)
            habitQuestion = habitName;

        if (editedHabitScheduleId == -1)
            return createHabitAccordingToHabitPreferencesIfDataIsCorrect(habitDay, habitName, habitQuestion);
        else
            return editHabitAccordingToHabitPreferencesIfDataIsCorrect(habitDay, habitName, habitQuestion,
                    editedHabitScheduleId);
    }

    private boolean editHabitAccordingToHabitPreferencesIfDataIsCorrect(Date habitDay, String habitName,
                                                                        String habitQuestion, int editedHabitScheduleId) {
        HabitSchedule editedHabitsSchedule = (HabitSchedule) habitScheduleDAO.findById(editedHabitScheduleId);
        Habit editedHabit = (Habit) habitDAO.findById(editedHabitsSchedule.getHabitId());
        int habitsEditionResult = habitDAO.update(new Habit(editedHabit.getId(), habitName, habitQuestion, habitDay, 55.75417935,
                48.7440855, 9, habitSettings.getNotificationSoundUri().toString(), true, 60, habitSettings.getCategoryId()));
        if (habitsEditionResult >= 0) {
            if (habitSettingsPrefs.contains("notificationFrequencyType")) { // If habit schedules should be changed
                habitScheduleDAO.deleteByHabitId(editedHabit.getId());
                createSchedulesForTheHabitByItsId(editedHabit.getId());
            }
            return true;
        } else {
            toastMessage(getContext().getString(R.string.habit_name_and_question_should_be_unique));
            return false;
        }
    }

    private boolean createHabitAccordingToHabitPreferencesIfDataIsCorrect(Date habitDay, String habitName, String habitQuestion) {
        int habitsCreationResult = habitDAO.create(new Habit(1, habitName, habitQuestion, habitDay, 55.75417935,
                48.7440855, 9, habitSettings.getNotificationSoundUri().toString(), true, 60, habitSettings.getCategoryId()));
        if (habitsCreationResult > 0) {
            Habit habitWithMaxId = (Habit) habitDAO.getObjectWithMaxId();
            createSchedulesForTheHabitByItsId(habitWithMaxId.getId());
            return true;
        } else {
            toastMessage(getContext().getString(R.string.habit_name_and_question_should_be_unique));
            return false;
        }
    }

    private void createSchedulesForTheHabitByItsId(int habitId) {
        Calendar c = new GregorianCalendar();
        // Get how many days in current month
        int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.set(Calendar.HOUR_OF_DAY, habitSettings.getNotificationHour());
        c.set(Calendar.MINUTE, habitSettings.getNotificationMinute());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date habitDay = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, monthMaxDays);
        Date afterAMonth = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, monthMaxDays * (-1));
        boolean habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
        switch (habitSettings.getNotificationFrequencyType()) {
            case DAILY:
                while (habitDayBeforeAfterAMonth) {
                    habitScheduleDAO.create(new HabitSchedule(1, habitDay, null, habitId));
                    c.add(Calendar.DATE, 1);
                    habitDay = c.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
                break;
            case WEEKLY:
                c = setDayOfWeekByItsNumber(c, habitSettings.getNotificationFrequencyWeekNumberOrDate());
                habitDay = c.getTime();
                while (habitDayBeforeAfterAMonth) {
                    habitScheduleDAO.create(new HabitSchedule(1, habitDay, null, habitId));
                    c.add(Calendar.DAY_OF_YEAR, 7);
                    habitDay = c.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
                break;
            case MONTHLY:
                c.set(Calendar.DAY_OF_MONTH, habitSettings.getNotificationFrequencyWeekNumberOrDate());
                habitDay = c.getTime();
                while (habitDayBeforeAfterAMonth) {
                    habitScheduleDAO.create(new HabitSchedule(1, habitDay, null, habitId));
                    c.add(Calendar.MONTH, 1);
                    habitDay = c.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
                break;
            case SPECIFIED_DAYS:
                for (int i = 0; i < 7; i++) {
                    if (habitSettings.getNotificationFrequencySpecifiedDays()[i]) {
                        c = setDayOfWeekByItsNumber(c, i + 1);
                        habitDay = c.getTime();
                        habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                        while (habitDayBeforeAfterAMonth) {
                            habitScheduleDAO.create(new HabitSchedule(1, habitDay, null, habitId));
                            c.add(Calendar.DAY_OF_YEAR, 7);
                            habitDay = c.getTime();
                            habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                        }
                        c = new GregorianCalendar();
                        c.set(Calendar.HOUR_OF_DAY, habitSettings.getNotificationHour());
                        c.set(Calendar.MINUTE, habitSettings.getNotificationMinute());
                        c.set(Calendar.SECOND, 0);
                    }
                }
                break;
            default:
                while (habitDayBeforeAfterAMonth) {
                    habitScheduleDAO.create(new HabitSchedule(1, habitDay, null, habitId));
                    c.add(Calendar.DATE, 1);
                    habitDay = c.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
                break;
        }
    }

    private Calendar setDayOfWeekByItsNumber(Calendar calendar, int weekNumber) {
        switch (weekNumber) {
            case 1:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case 2:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case 3:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case 4:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case 5:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
            case 6:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                break;
            case 7:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            default:
                break;
        }

        return calendar;
    }

    private void toastMessage(String message) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getContext(), message, duration);
        toast.show();
    }

}
