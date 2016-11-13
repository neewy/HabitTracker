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

import java.text.DecimalFormat;
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
import ru.android4life.habittracker.utils.StringConstants;
import ru.android4life.habittracker.views.RippleView;

import static ru.android4life.habittracker.utils.StringConstants.DAILY;
import static ru.android4life.habittracker.utils.StringConstants.LATITUDE;
import static ru.android4life.habittracker.utils.StringConstants.LONGITUDE;
import static ru.android4life.habittracker.utils.StringConstants.MINUTES_BEFORE_CONFIRMATION;
import static ru.android4life.habittracker.utils.StringConstants.MONTHLY;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_1;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_2;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_3;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_4;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_5;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_6;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_SPECIFIED_DAY_7;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_TYPE;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_HOUR;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_MINUTE;
import static ru.android4life.habittracker.utils.StringConstants.PICK_AUDIO_REQUEST;
import static ru.android4life.habittracker.utils.StringConstants.POSITION;
import static ru.android4life.habittracker.utils.StringConstants.RANGE;
import static ru.android4life.habittracker.utils.StringConstants.SPECIFIED_DAYS;
import static ru.android4life.habittracker.utils.StringConstants.WEEKLY;

/**
 * Created by Bulat Mukhutdinov on 24.09.2016.
 */
public class AddHabitActivity extends BaseActivity {


    private HabitParametersAdapter mAdapter;
    private HabitSettings habitSettings;
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private SharedPreferences habitSettingsPrefs = null;
    private HabitNotification notification;
    private boolean notificationSoundChanged;
    private boolean positionChanged;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        setContext(this.getApplicationContext());

        notificationSoundChanged = false;
        positionChanged = false;

        notification = new HabitNotification(getContext());

        // Initialise DAOs, habitSettingsPrefs & notificationSoundUri with default notification uri
        habitSettingsPrefs = getApplicationContext().getSharedPreferences(getApplicationContext()
                .getString(R.string.creating_habit_settings), MODE_PRIVATE);

        habitDAO = new HabitDAO(getApplicationContext());
        habitScheduleDAO = new HabitScheduleDAO(getApplicationContext());

        habitSettings = new HabitSettings();

        // if habit is edited, habit schedule id is passed, else = -1
        final int editedHabitId = getIntent().getIntExtra(getString(R.string.habit_id), -1);

        if (editedHabitId != -1) { // If habit is being edited
            Habit editedHabit = (Habit) habitDAO.findById(editedHabitId);

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

                habitSettings = getHabitSettingsFromPreferences(editedHabitId);
                if (createOrEditHabitAccordingToHabitPreferencesIfDataIsCorrect(editedHabitId)) {
                    mAdapter.notifyDataSetChanged();

                    removeValuesForHabitSettingsFromPreferences();

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

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.habit_parameters_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        if (editedHabitId == -1) // habits creation
            mAdapter = new HabitParametersAdapter(this, HabitParameter.createParameters(getApplicationContext()), true);
        else { // habits edition
            mAdapter = new HabitParametersAdapter(this,
                    HabitParameter.createParametersByHabitId(getApplicationContext(),
                            editedHabitId), true);
            mAdapter.setHabitId(editedHabitId);
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

                mAdapter.updateHintForTuneParameterIfExists(habitSettings.getNotificationSoundName());
                notificationSoundChanged = true;
            }
        } else if (resultCode == Activity.RESULT_OK) {
            //TODO: why request code does not work?
            //FIXME: pass the data in habit parameter!
            boolean hasPosition = resultData.getBooleanExtra(POSITION, false);
            if (hasPosition) {
                double latitude = resultData.getDoubleExtra(LATITUDE, 0);
                double longitude = resultData.getDoubleExtra(LONGITUDE, 0);
                DecimalFormat df = new DecimalFormat("#.##");
                int range = resultData.getIntExtra(RANGE, 0);

                habitSettings.setLatitudeLongitudeAndRange(latitude, longitude, range);
                positionChanged = true;

                //FIXME: this is temporary workaround! fix it
                mAdapter.updatePosition(getString(R.string.position_format, df.format(latitude),
                        df.format(longitude), String.valueOf(range)));
            } else {
                mAdapter.updatePosition(getString(R.string.none));
            }
        }
    }

    private void removeValuesForHabitSettingsFromPreferences() {
        habitSettingsPrefs.edit().remove(StringConstants.CATEGORY_ID).apply();
        habitSettingsPrefs.edit().remove(NOTIFICATION_HOUR).apply();
        habitSettingsPrefs.edit().remove(NOTIFICATION_MINUTE).apply();
        habitSettingsPrefs.edit().remove(NOTIFICATION_FREQUENCY_TYPE).apply();
        for (int i = 0; i <= 7; i++)
            habitSettingsPrefs.edit().remove(getApplicationContext().getResources()
                    .getString(R.string.notification_frequency_specified_day_string,
                            String.valueOf(i))).apply();
        habitSettingsPrefs.edit().remove(NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE).apply();
        habitSettingsPrefs.edit().remove(MINUTES_BEFORE_CONFIRMATION).apply();
    }

    private HabitSettings getHabitSettingsFromPreferences(int editedHabitId) {
        HabitSettings result;

        HabitSettings savedHabitSettings = habitSettings;
        boolean frequencyChanged = habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_TYPE) ||
                habitSettingsPrefs.contains(NOTIFICATION_HOUR) ||
                habitSettingsPrefs.contains(NOTIFICATION_MINUTE);

        if (editedHabitId == -1) { // Habits creation
            result = new HabitSettings();
        } else { // Habits edition
            if (frequencyChanged)
                result = new HabitSettings(editedHabitId, true);
            else
                result = new HabitSettings(editedHabitId, false);
        }

        if (notificationSoundChanged) {
            result.setNotificationSoundUri(savedHabitSettings.getNotificationSoundUri());
            result.setNotificationSoundName(savedHabitSettings.getNotificationSoundName());
        }

        if (positionChanged) {
            result.setLatitude(savedHabitSettings.getLatitude());
            result.setLongitude(savedHabitSettings.getLongitude());
            result.setRange(savedHabitSettings.getRange());
        }

        if (habitSettingsPrefs.contains(StringConstants.CATEGORY_ID))
            result.setCategoryId(habitSettingsPrefs.getInt(StringConstants.CATEGORY_ID, habitSettings.getCategoryId()));
        if (habitSettingsPrefs.contains(NOTIFICATION_HOUR))
            result.setNotificationHour(habitSettingsPrefs.getInt(NOTIFICATION_HOUR, habitSettings.getNotificationHour()));
        if (habitSettingsPrefs.contains(NOTIFICATION_MINUTE))
            result.setNotificationMinute(habitSettingsPrefs.getInt(NOTIFICATION_MINUTE, habitSettings.getNotificationMinute()));
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_TYPE))
            result.setNotificationFrequencyType(parseStringToNotificationFrequencyType(habitSettingsPrefs
                    .getString(NOTIFICATION_FREQUENCY_TYPE, habitSettings.getNotificationFrequencyType().toString())));
        boolean[] notificationFrequencySpecifiedDays = result.getNotificationFrequencySpecifiedDays();

        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_1))
            notificationFrequencySpecifiedDays[0] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_1, habitSettings.getNotificationFrequencySpecifiedDays()[0]);
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_2))
            notificationFrequencySpecifiedDays[1] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_2, habitSettings.getNotificationFrequencySpecifiedDays()[1]);
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_3))
            notificationFrequencySpecifiedDays[2] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_3, habitSettings.getNotificationFrequencySpecifiedDays()[2]);
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_4))
            notificationFrequencySpecifiedDays[3] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_4, habitSettings.getNotificationFrequencySpecifiedDays()[3]);
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_5))
            notificationFrequencySpecifiedDays[4] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_5, habitSettings.getNotificationFrequencySpecifiedDays()[4]);
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_6))
            notificationFrequencySpecifiedDays[5] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_6, habitSettings.getNotificationFrequencySpecifiedDays()[5]);
        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_7))
            notificationFrequencySpecifiedDays[6] = habitSettingsPrefs.getBoolean(NOTIFICATION_FREQUENCY_SPECIFIED_DAY_7, habitSettings.getNotificationFrequencySpecifiedDays()[6]);

        result.setNotificationFrequencySpecifiedDays(notificationFrequencySpecifiedDays);

        if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE))
            result.setNotificationFrequencyWeekNumberOrDate(habitSettingsPrefs.getInt(NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE,
                    habitSettings.getNotificationFrequencyWeekNumberOrDate()));
        if (habitSettingsPrefs.contains(MINUTES_BEFORE_CONFIRMATION))
            result.setMinutesBeforeConfirmation(habitSettingsPrefs.getInt(MINUTES_BEFORE_CONFIRMATION, habitSettings.getMinutesBeforeConfirmation()));

        return result;
    }

    private NotificationFrequencyType parseStringToNotificationFrequencyType(String s) {
        switch (s) {
            case DAILY:
                return NotificationFrequencyType.DAILY;
            case WEEKLY:
                return NotificationFrequencyType.WEEKLY;
            case MONTHLY:
                return NotificationFrequencyType.MONTHLY;
            case SPECIFIED_DAYS:
                return NotificationFrequencyType.SPECIFIED_DAYS;
            default:
                return NotificationFrequencyType.DAILY;
        }
    }

    private boolean createOrEditHabitAccordingToHabitPreferencesIfDataIsCorrect(int editedHabitId) {
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

        if (editedHabitId == -1)
            return createHabitAccordingToHabitPreferencesIfDataIsCorrect(habitDay, habitName, habitQuestion);
        else
            return editHabitAccordingToHabitPreferencesIfDataIsCorrect(habitDay, habitName, habitQuestion,
                    editedHabitId);
    }

    private boolean editHabitAccordingToHabitPreferencesIfDataIsCorrect(Date habitDay, String habitName,
                                                                        String habitQuestion, int editedHabitId) {
        Habit editedHabit = (Habit) habitDAO.findById(editedHabitId);

        int habitsEditionResult = habitDAO.update(new Habit(editedHabit.getId(), habitName, habitQuestion, habitDay,
                habitSettings.getLatitude(), habitSettings.getLongitude(), habitSettings.getRange(),
                habitSettings.getNotificationSoundUri().toString(), true, 60, habitSettings.getCategoryId()));
        if (habitsEditionResult >= 0) {
            if (habitSettingsPrefs.contains(NOTIFICATION_FREQUENCY_TYPE) ||
                    habitSettingsPrefs.contains(NOTIFICATION_HOUR) ||
                    habitSettingsPrefs.contains(NOTIFICATION_MINUTE)) { // If habit schedules should be changed
                habitScheduleDAO.deleteByHabitId(editedHabit.getId());
                createSchedulesForTheHabitByItsId(editedHabit.getId());
            }
            notification.createHabitAlarms(editedHabit);
            return true;
        } else {
            toastMessage(getContext().getString(R.string.habit_name_and_question_should_be_unique));
            return false;
        }
    }

    private boolean createHabitAccordingToHabitPreferencesIfDataIsCorrect(Date habitDay, String habitName, String habitQuestion) {
        Habit habitToCreate = new Habit(habitName, habitQuestion, habitDay,
                habitSettings.getLatitude(), habitSettings.getLongitude(), habitSettings.getRange(),
                habitSettings.getNotificationSoundUri().toString(), true, 60, habitSettings.getCategoryId());
        int habitsCreationResult = habitDAO.create(habitToCreate);
        if (habitsCreationResult > 0) {
            Habit habitWithMaxId = (Habit) habitDAO.getObjectWithMaxId();
            createSchedulesForTheHabitByItsId(habitWithMaxId.getId());
            notification.createHabitAlarms(habitToCreate);
            return true;
        } else {
            toastMessage(getContext().getString(R.string.habit_name_and_question_should_be_unique));
            return false;
        }
    }

    private void createSchedulesForTheHabitByItsId(int habitId) {
        Calendar c = new GregorianCalendar();
        // Get how many days in current month
        Date currentTime = c.getTime();
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
                    if (habitDay.after(currentTime))
                        //TODO delete last parameter from create when adding of notes would be implemented
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId, "Some note"));
                    c.add(Calendar.DATE, 1);
                    habitDay = c.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
                break;
            case WEEKLY:
                c = setDayOfWeekByItsNumber(c, habitSettings.getNotificationFrequencyWeekNumberOrDate());
                habitDay = c.getTime();
                while (habitDayBeforeAfterAMonth) {
                    if (habitDay.after(currentTime))
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
                    c.add(Calendar.DAY_OF_YEAR, 7);
                    habitDay = c.getTime();
                    habitDayBeforeAfterAMonth = habitDay.before(afterAMonth) || habitDay.equals(afterAMonth);
                }
                break;
            case MONTHLY:
                c.set(Calendar.DAY_OF_MONTH, habitSettings.getNotificationFrequencyWeekNumberOrDate());
                habitDay = c.getTime();
                while (habitDayBeforeAfterAMonth) {
                    if (habitDay.after(currentTime))
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
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
                            if (habitDay.after(currentTime))
                                habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
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
                    if (habitDay.after(currentTime))
                        habitScheduleDAO.create(new HabitSchedule(habitDay, null, habitId));
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
