package ru.android4life.habittracker.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MapsActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.enumeration.NotificationFrequencyType;
import ru.android4life.habittracker.models.HabitParameter;
import ru.android4life.habittracker.models.HabitSettings;
import ru.android4life.habittracker.utils.Translator;
import ru.android4life.habittracker.viewholder.HabitParameterViewHolder;

import static android.content.Context.MODE_PRIVATE;
import static ru.android4life.habittracker.utils.StringConstants.CATEGORY_ID;
import static ru.android4life.habittracker.utils.StringConstants.HABIT_ID;
import static ru.android4life.habittracker.utils.StringConstants.MINUTES_BEFORE_CONFIRMATION;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_TYPE;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_HOUR;
import static ru.android4life.habittracker.utils.StringConstants.NOTIFICATION_MINUTE;
import static ru.android4life.habittracker.utils.StringConstants.PICK_AUDIO_REQUEST;
import static ru.android4life.habittracker.utils.StringConstants.REQUEST_POSITION;

/**
 * Created by Bulat Mukhutdinov on 28.09.2016.
 */
public class HabitParametersAdapter extends RecyclerView.Adapter<HabitParameterViewHolder> {

    private HabitCategoryDAO habitCategoryDAO;
    private List<HabitParameter> parameters;
    private Activity activity;
    private Context context;
    private HabitSettings habitSettings;
    private SharedPreferences prefs = null;
    private boolean isForCreation;
    private int habitId = -1;


    public HabitParametersAdapter(Activity activity, List<HabitParameter> parameters, boolean isForCreation) {
        this.parameters = parameters;
        this.activity = activity;
        this.habitSettings = new HabitSettings();
        this.context = activity;
        this.habitCategoryDAO = new HabitCategoryDAO(context);
        this.prefs = context.getSharedPreferences(context.getString(R.string.creating_habit_settings), MODE_PRIVATE);
        this.isForCreation = isForCreation;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public void updateParameters(List<HabitParameter> parameters) {
        this.parameters = parameters;
        notifyDataSetChanged();
    }

    @Override
    public HabitParameterViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_parameter, parent, false);
        final HabitParameterViewHolder vh = new HabitParameterViewHolder(v);
        if (isForCreation) {
            vh.mListener = createOnClickListener(parent);
        } else {
            vh.disableRippleEffect();
        }
        return vh;
    }

    //FIXME: this is temporary workaround!
    //change it as we figure out how to process data
    //in order to change habit settings

    public void updateHintForParameterByItsNameIfExists(String parametersName, String hint) {
        boolean tuneParameterExists = false;
        int i = 0;
        while (!tuneParameterExists && i < parameters.size()) {
            if (parameters.get(i).getTitle().equals(parametersName)) {
                tuneParameterExists = true;
            } else {
                i++;
            }
        }

        if (tuneParameterExists) {
            parameters.get(i).setHint(hint);
            this.notifyItemChanged(i);
        }
    }

    private HabitParameterViewHolder.AddHabitParameterListener createOnClickListener(final ViewGroup parent) {
        return new HabitParameterViewHolder.AddHabitParameterListener() {
            @Override
            public void onCategory(View caller, final TextView hint) {
                final CharSequence[] items = Translator.translate(habitCategoryDAO.getArrayOfAllNames());
                final List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle(context.getResources().getString(R.string.select_category));
                alertDialogBuilder
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //TODO save selected value
                                habitSettings.setCategoryId(habitCategories.get(item).getId());
                                hint.setText(items[item]);
                                prefs.edit().putInt(CATEGORY_ID, habitSettings.getCategoryId()).apply();
                                dialog.cancel();
                            }
                        }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }

            @Override
            public void onReminder(View caller, final TextView hint) {
                TimePickerDialog.OnTimeSetListener timePickerListener;
                timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            habitSettings.setNotificationMinute(timePicker.getMinute());
                            habitSettings.setNotificationHour(timePicker.getHour());
                        } else {
                            habitSettings.setNotificationMinute(timePicker.getCurrentMinute());
                            habitSettings.setNotificationHour(timePicker.getCurrentHour());
                        }
                        if (String.valueOf(habitSettings.getNotificationMinute()).length() < 2) {
                            hint.setText(context.getResources().getString(R.string.string_colon_space_string_zero,
                                    String.valueOf(habitSettings.getNotificationHour()),
                                    String.valueOf(habitSettings.getNotificationMinute())));
                            prefs.edit().putInt(NOTIFICATION_HOUR, habitSettings.getNotificationHour()).apply();
                            prefs.edit().putInt(NOTIFICATION_MINUTE, habitSettings.getNotificationMinute()).apply();
                        } else {
                            hint.setText(context.getResources().getString(R.string.string_colon_space_string,
                                    String.valueOf(habitSettings.getNotificationHour()),
                                    String.valueOf(habitSettings.getNotificationMinute())));
                            prefs.edit().putInt(NOTIFICATION_HOUR, habitSettings.getNotificationHour()).apply();
                            prefs.edit().putInt(NOTIFICATION_MINUTE, habitSettings.getNotificationMinute()).apply();
                        }
                    }
                }

                ;
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        parent.getContext(), timePickerListener, 10, 0, true);
                timePickerDialog.show();
            }

            @Override
            public void onFrequency(View caller, final TextView hint) {
                final CharSequence[] items = {context.getResources().getString(R.string.daily), context.getResources().getString(R.string.weekly), context.getResources().getString(R.string.monthly), context.getResources().getString(R.string.specified_days)};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle(context.getResources().getString(R.string.select_frequency));
                alertDialogBuilder
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            public void onClick(DialogInterface dialog, int item) {
                                //TODO save selected value
                                if (item == 0) {
                                    habitSettings.setNotificationFrequencyType(NotificationFrequencyType.DAILY);
                                    hint.setText(context.getResources().getString(R.string.every_day));
                                    prefs.edit().putString(NOTIFICATION_FREQUENCY_TYPE, NotificationFrequencyType.DAILY.toString()).apply();
                                } else
                                    hint.setText("");
                                switch (item) {
                                    case 1:
                                        habitSettings.setNotificationFrequencyType(NotificationFrequencyType.WEEKLY);
                                        createFrequencyWeeklyDialog(parent, hint);
                                        prefs.edit().putString(NOTIFICATION_FREQUENCY_TYPE, NotificationFrequencyType.WEEKLY.toString()).apply();
                                        break;
                                    case 2:
                                        habitSettings.setNotificationFrequencyType(NotificationFrequencyType.MONTHLY);
                                        createFrequencyMonthlyDialog(parent, hint);
                                        prefs.edit().putString(NOTIFICATION_FREQUENCY_TYPE, NotificationFrequencyType.MONTHLY.toString()).apply();
                                        break;
                                    case 3:
                                        habitSettings.setNotificationFrequencyType(NotificationFrequencyType.SPECIFIED_DAYS);
                                        createFrequencySpecifiedDaysDialog(parent, hint);
                                        prefs.edit().putString(NOTIFICATION_FREQUENCY_TYPE, NotificationFrequencyType.SPECIFIED_DAYS.toString()).apply();
                                        break;
                                }
                                dialog.cancel();
                            }
                        }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }

            @Override
            public void onTune(View caller, final TextView hint) {
                Intent tmpIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                activity.startActivityForResult(tmpIntent, PICK_AUDIO_REQUEST);
            }

            @Override
            public void onPosition(View caller, TextView hint) {
                Intent openPosition = new Intent(activity, MapsActivity.class);
                openPosition.putExtra(HABIT_ID, habitId);
                activity.startActivityForResult(openPosition, REQUEST_POSITION);
            }

            @Override
            public void onConfirmation(View caller, final TextView hint) {
                Resources res = context.getResources();
                int resId = res.getIdentifier(context.getString(R.string.minutes_to_confirmation_text),
                        context.getString(R.string.array), context.getPackageName());
                final String[] textLabelsForVariantsToSelectList = res.getStringArray(resId);
                resId = res.getIdentifier(context.getString(R.string.minutes_to_confirmation),
                        context.getString(R.string.array), context.getPackageName());
                final int[] integersForVariantsToSelectList = res.getIntArray(resId);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle(context.getResources().getString(R.string.confirmation));
                alertDialogBuilder
                        .setSingleChoiceItems(textLabelsForVariantsToSelectList, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                habitSettings.setMinutesBeforeConfirmation(integersForVariantsToSelectList[item]);
                                hint.setText(textLabelsForVariantsToSelectList[item]);
                                prefs.edit().putInt(MINUTES_BEFORE_CONFIRMATION, integersForVariantsToSelectList[item]).apply();
                                dialog.cancel();
                            }
                        }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        };
    }


    private void createFrequencySpecifiedDaysDialog(ViewGroup parent, final TextView hint) {
        final boolean[] mCheckedItems = {false, false, false, false, false, false, false};
        final CharSequence[] items = {context.getResources().getString(R.string.monday), context.getResources().getString(R.string.tuesday),
                context.getResources().getString(R.string.wednesday), context.getResources().getString(R.string.thursday),
                context.getResources().getString(R.string.friday), context.getResources().getString(R.string.saturday),
                context.getResources().getString(R.string.sunday)};
        final CharSequence[] shortenDaysOfWeek = context.getResources().getStringArray(R.array.shorten_days_of_week);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                parent.getContext());
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.select_days));
        alertDialogBuilder
                .setMultiChoiceItems(items, mCheckedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                mCheckedItems[which] = isChecked;
                            }
                        })
                .setPositiveButton(context.getResources().getString(R.string.habit_done),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                StringBuilder stringBuilder = new StringBuilder();
                                StringBuilder selectedDaysInTwoLetters = new StringBuilder();
                                for (int i = 0; i < mCheckedItems.length; i++) {
                                    if (mCheckedItems[i]) {
                                        stringBuilder.append(items[i]).append(" ");
                                        selectedDaysInTwoLetters.append(shortenDaysOfWeek[i]).append(", ");
                                    }
                                    prefs.edit().putBoolean(context.getResources()
                                            .getString(R.string.notification_frequency_specified_day_string,
                                                    String.valueOf(i + 1)), mCheckedItems[i]).apply();
                                }
                                System.out.println(stringBuilder);
                                if (selectedDaysInTwoLetters.length() > 1)
                                    selectedDaysInTwoLetters.deleteCharAt(selectedDaysInTwoLetters.length() - 2);
                                hint.setText(context.getResources().getString(R.string.on_every,
                                        selectedDaysInTwoLetters));
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void createFrequencyWeeklyDialog(ViewGroup parent, final TextView hint) {
        final CharSequence[] items = {context.getResources().getString(R.string.monday), context.getResources().getString(R.string.tuesday),
                context.getResources().getString(R.string.wednesday), context.getResources().getString(R.string.thursday),
                context.getResources().getString(R.string.friday), context.getResources().getString(R.string.saturday),
                context.getResources().getString(R.string.sunday)};
        final CharSequence[] shortenDaysOfWeek = context.getResources().getStringArray(R.array.shorten_days_of_week);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                parent.getContext());
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.select_days));
        alertDialogBuilder
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        hint.setText(context.getResources().getString(R.string.on_every,
                                shortenDaysOfWeek[item]));
                        prefs.edit().putInt(NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE, item + 1).apply();
                        dialog.cancel();
                    }
                }).setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createFrequencyMonthlyDialog(ViewGroup parent, final TextView hint) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                hint.setText(context.getResources().getString(R.string.every_month_on_space_string,
                        String.valueOf(dayOfMonth)));
                prefs.edit().putInt(NOTIFICATION_FREQUENCY_WEEK_NUMBER_OR_DATE, dayOfMonth).apply();
            }
        };
        DatePickerDialog dpd = new DatePickerDialog(parent.getContext(), myCallBack, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        dpd.show();
    }


    @Override
    public void onBindViewHolder(HabitParameterViewHolder holder, int position) {
        HabitParameter parameter = parameters.get(position);
        holder.title.setText(parameter.getTitle());
        holder.icon.setBackground(parameter.getIcon());
        holder.hint.setText(parameter.getHint());
    }

    @Override
    public int getItemCount() {
        return parameters.size();
    }
}
