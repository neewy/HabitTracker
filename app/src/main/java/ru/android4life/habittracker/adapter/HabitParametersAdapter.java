package ru.android4life.habittracker.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import ru.android4life.habittracker.HabitParameter;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.Translator;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.views.RippleView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Bulat Mukhutdinov on 28.09.2016.
 */
public class HabitParametersAdapter extends RecyclerView.Adapter<HabitParametersAdapter.ViewHolder> {

    private HabitCategoryDAO habitCategoryDAO;
    private List<HabitParameter> parameters;
    private Activity activity;
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private Context context;
    private HabitSettings habitSettings;
    private SharedPreferences prefs = null;
    private boolean isForCreation;

    public HabitParametersAdapter(Activity activity, List<HabitParameter> parameters, boolean isForCreation) {
        this.parameters = parameters;
        this.activity = activity;
        this.habitSettings = new HabitSettings();
        this.context = MainActivity.getContext();
        this.habitCategoryDAO = new HabitCategoryDAO(context);
        this.habitDAO = new HabitDAO(context);
        this.habitScheduleDAO = new HabitScheduleDAO(context);
        this.prefs = context.getSharedPreferences(context.getString(R.string.creating_habit_settings), MODE_PRIVATE);
        this.isForCreation = isForCreation;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_parameter, parent, false);
        final HabitParametersAdapter.ViewHolder vh = new ViewHolder(v);
        if (isForCreation) {
            vh.mListener = createOnClickListener(parent);
        } else {
            vh.disableRippleEffect();
        }
        return vh;
    }

    private ViewHolder.AddHabitParameterListener createOnClickListener(final ViewGroup parent){
        return new ViewHolder.AddHabitParameterListener() {
            @Override
            public void onCategory(View caller, final TextView hint) {
                //TODO replace items with values from db
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
                                prefs.edit().putInt("categoryId", habitSettings.getCategoryId()).apply();
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
                        System.out.println(timePicker.getHour() + " " + timePicker.getMinute());
                        habitSettings.setNotificationMinute(timePicker.getMinute());
                        habitSettings.setNotificationHour(timePicker.getHour());
                        if (String.valueOf(habitSettings.getNotificationMinute()).length() < 2) {
                            hint.setText(context.getResources().getString(R.string.string_colon_space_string_zero,
                                    String.valueOf(habitSettings.getNotificationHour()),
                                    String.valueOf(habitSettings.getNotificationMinute())));
                            prefs.edit().putInt("notificationHour", habitSettings.getNotificationHour()).apply();
                            prefs.edit().putInt("notificationMinute", habitSettings.getNotificationMinute()).apply();
                        } else {
                            hint.setText(context.getResources().getString(R.string.string_colon_space_string,
                                    String.valueOf(habitSettings.getNotificationHour()),
                                    String.valueOf(habitSettings.getNotificationMinute())));
                            prefs.edit().putInt("notificationHour", habitSettings.getNotificationHour()).apply();
                            prefs.edit().putInt("notificationMinute", habitSettings.getNotificationMinute()).apply();
                        }
                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        parent.getContext(), timePickerListener, 0, 0, true);
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
                                    prefs.edit().putString("notificationFrequencyType", NotificationFrequencyType.DAILY.toString()).apply();
                                } else
                                    hint.setText("");
                                switch (item) {
                                    case 1:
                                        habitSettings.setNotificationFrequencyType(NotificationFrequencyType.WEEKLY);
                                        createFrequencyWeeklyDialog(parent, hint);
                                        prefs.edit().putString("notificationFrequencyType", NotificationFrequencyType.WEEKLY.toString()).apply();
                                        break;
                                    case 2:
                                        habitSettings.setNotificationFrequencyType(NotificationFrequencyType.MONTHLY);
                                        createFrequencyMonthlyDialog(parent, hint);
                                        prefs.edit().putString("notificationFrequencyType", NotificationFrequencyType.MONTHLY.toString()).apply();
                                        break;
                                    case 3:
                                        habitSettings.setNotificationFrequencyType(NotificationFrequencyType.SPECIFIED_DAYS);
                                        createFrequencySpecifiedDaysDialog(parent, hint);
                                        prefs.edit().putString("notificationFrequencyType", NotificationFrequencyType.SPECIFIED_DAYS.toString()).apply();
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
                activity.startActivityForResult(tmpIntent, AddHabitActivity.PICK_AUDIO_REQUEST);
                hint.setText(prefs.getString(caller.getResources().getString(R.string.notification_sound_name),
                        caller.getResources().getString(R.string.standard_from_capital_letter)));
            }

            @Override
            public void onConfirmation(View caller, final TextView hint) {

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
                                                    String.valueOf(i)), mCheckedItems[i]).apply();
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
                        prefs.edit().putInt("notificationFrequencyWeekNumberOrDate", item + 1).apply();
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
        DatePickerDialog dpd = new DatePickerDialog(parent.getContext(), null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                hint.setText(context.getResources().getString(R.string.every_month_on_space_string,
                        String.valueOf(i2)));
                prefs.edit().putInt("notificationFrequencyWeekNumberOrDate", i2).apply();
            }
        });
        dpd.show();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HabitParameter parameter = parameters.get(position);
        holder.title.setText(parameter.getTitle());
        holder.icon.setBackground(parameter.getIcon());
        holder.hint.setText(parameter.getHint());
    }

    @Override
    public int getItemCount() {
        return parameters.size();
    }


    public enum NotificationFrequencyType {
        DAILY, WEEKLY, MONTHLY, SPECIFIED_DAYS
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public AddHabitParameterListener mListener;
        private TextView title;
        private TextView hint;
        private ImageView icon;
        private RippleView block;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.add_habit_category);
            hint = (TextView) itemView.findViewById(R.id.add_habit_category_hint);
            icon = (ImageView) itemView.findViewById(R.id.add_habit_category_icon);
            block = (RippleView) itemView.findViewById(R.id.add_habit_category_block);
            block.setOnClickListener(this);
        }

        public void disableRippleEffect() {
            block.setRippleAlpha(0);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_category))) {
                    mListener.onCategory(view, hint);
                } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_reminder))) {
                    mListener.onReminder(view, hint);
                } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_frequency))) {
                    mListener.onFrequency(view, hint);
                } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_tune))) {
                    mListener.onTune(view, hint);
                } else {
                    mListener.onConfirmation(view, hint);
                }
            }
        }

        public interface AddHabitParameterListener {
            void onCategory(View caller, final TextView hint);

            void onReminder(View caller, final TextView hint);

            void onFrequency(View caller, final TextView hint);

            void onTune(View caller, final TextView hint);

            void onConfirmation(View caller, final TextView hint);

        }
    }

    public static class HabitSettings {
        private int categoryId;
        private int notificationHour;
        private int notificationMinute;
        private NotificationFrequencyType notificationFrequencyType;
        private int notificationFrequencyWeekNumberOrDate;
        private boolean[] notificationFrequencySpecifiedDays;
        private Uri notificationSoundUri;
        private String notificationSoundName;
        private int minutesBeforeConfirmation;

        public HabitSettings(int categoryId, int notificationHour, int notificationMinute,
                             NotificationFrequencyType notificationFrequencyType,
                             int notificationFrequencyWeekNumberOrDate, boolean[] notificationFrequencySpecifiedDays,
                             Uri notificationSoundUri, String notificationSoundName,
                             int minutesBeforeConfirmation) {
            this.categoryId = categoryId;
            this.notificationHour = notificationHour;
            this.notificationMinute = notificationMinute;
            this.notificationFrequencyType = notificationFrequencyType;
            this.notificationFrequencyWeekNumberOrDate = notificationFrequencyWeekNumberOrDate;
            this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
            this.notificationSoundUri = notificationSoundUri;
            this.notificationSoundName = notificationSoundName;
            this.minutesBeforeConfirmation = minutesBeforeConfirmation;
        }

        public HabitSettings() {
            HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(BaseActivity.getContext());
            final List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();
            this.categoryId = habitCategories.get(0).getId();
            this.notificationHour = 0;
            this.notificationMinute = 0;
            this.notificationFrequencyType = NotificationFrequencyType.DAILY;
            this.notificationFrequencyWeekNumberOrDate = 1;
            boolean[] notificationFrequencySpecifiedDays = {false, false, false, false, false, false, false};
            this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
            this.notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            this.notificationSoundName = BaseActivity.getContext().getString(R.string.standard_from_capital_letter);
            this.minutesBeforeConfirmation = 60;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public int getNotificationHour() {
            return notificationHour;
        }

        public void setNotificationHour(int notificationHour) {
            this.notificationHour = notificationHour;
        }

        public int getNotificationMinute() {
            return notificationMinute;
        }

        public void setNotificationMinute(int notificationMinute) {
            this.notificationMinute = notificationMinute;
        }

        public NotificationFrequencyType getNotificationFrequencyType() {
            return notificationFrequencyType;
        }

        public void setNotificationFrequencyType(NotificationFrequencyType notificationFrequencyType) {
            this.notificationFrequencyType = notificationFrequencyType;
        }

        public int getNotificationFrequencyWeekNumberOrDate() {
            return notificationFrequencyWeekNumberOrDate;
        }

        public void setNotificationFrequencyWeekNumberOrDate(int notificationFrequencyWeekNumberOrDate) {
            this.notificationFrequencyWeekNumberOrDate = notificationFrequencyWeekNumberOrDate;
        }

        public boolean[] getNotificationFrequencySpecifiedDays() {
            return notificationFrequencySpecifiedDays;
        }

        public void setNotificationFrequencySpecifiedDays(boolean[] notificationFrequencySpecifiedDays) {
            this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
        }

        public Uri getNotificationSoundUri() {
            return notificationSoundUri;
        }

        public void setNotificationSoundUri(Uri notificationSoundUri) {
            this.notificationSoundUri = notificationSoundUri;
        }

        public String getNotificationSoundName() {
            return notificationSoundName;
        }

        public void setNotificationSoundName(String notificationSoundName) {
            this.notificationSoundName = notificationSoundName;
        }

        public int getMinutesBeforeConfirmation() {
            return minutesBeforeConfirmation;
        }

        public void setMinutesBeforeConfirmation(int minutesBeforeConfirmation) {
            this.minutesBeforeConfirmation = minutesBeforeConfirmation;
        }
    }
}
