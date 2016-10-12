package ru.android4life.habittracker.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.views.RippleView;

/**
 * Created by Bulat Mukhutdinov on 28.09.2016.
 */
public class HabitParametersAdapter extends RecyclerView.Adapter<HabitParametersAdapter.ViewHolder> {

    private List<HabitParameter> parameters;
    private TimePickerDialog.OnTimeSetListener timePickerListener;
    private Activity activity;
    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private HabitCategoryDAO habitCategoryDAO;
    private Context context;

    public HabitParametersAdapter(Activity activity, List<HabitParameter> parameters) {
        this.parameters = parameters;
        this.activity = activity;
        context = MainActivity.getContext();
        habitCategoryDAO = new HabitCategoryDAO(context);
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                System.out.println(timePicker.getHour() + " " + timePicker.getMinute());
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_parameter, parent, false);
        HabitParametersAdapter.ViewHolder vh = new ViewHolder(v, new ViewHolder.AddHabitParameterListener() {
            @Override
            public void onCategory(View caller, final TextView hint) {
                //TODO replace items with values from db
                final CharSequence[] items = habitCategoryDAO.getArrayOfAllNames();
                final List<HabitCategory> habitCategories = (List<HabitCategory>) habitCategoryDAO.findAll();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle("Select category");
                alertDialogBuilder
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //TODO save selected value
                                hint.setText(habitCategories.get(item).getName());
                                dialog.cancel();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        parent.getContext(), timePickerListener, 0, 0, true);
                timePickerDialog.show();
            }

            @Override
            public void onFrequency(View caller, final TextView hint) {
                final CharSequence[] items = {" Daily ", " Weekly ", " Monthly ", " Specified days "};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle("Select a frequency");
                alertDialogBuilder
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            public void onClick(DialogInterface dialog, int item) {
                                //TODO save selected value
                                if (item == 0)
                                    hint.setText(context.getResources().getString(R.string.every_day));
                                else
                                    hint.setText("");
                                switch (item) {
                                    case 1:
                                        createFrequencyWeeklyDialog(parent, hint);
                                        break;
                                    case 2:
                                        createFrequencyMonthlyDialog(parent, hint);
                                        break;
                                    case 3:
                                        createFrequencySpecifiedDaysDialog(parent, hint);
                                        break;
                                }
                                dialog.cancel();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            }

            @Override
            public void onConfirmation(View caller, final TextView hint) {

            }
        });
        return vh;
    }

    private void createFrequencySpecifiedDaysDialog(ViewGroup parent, final TextView hint) {
        final boolean[] mCheckedItems = {false, false, false, false, false, false, false};
        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ",
                " Friday ", " Saturday ", " Sunday "};
        final CharSequence[] shortenDaysOfWeek = context.getResources().getStringArray(R.array.shorten_days_of_week);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                parent.getContext());
        alertDialogBuilder.setTitle("Select days");
        alertDialogBuilder
                .setMultiChoiceItems(items, mCheckedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                mCheckedItems[which] = isChecked;
                            }
                        })
                .setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                StringBuilder stringBuilder = new StringBuilder();
                                StringBuilder selectedDaysInTwoLetters = new StringBuilder();
                                for (int i = 0; i < mCheckedItems.length; i++) {
                                    if (mCheckedItems[i]) {
                                        stringBuilder.append(items[i]).append(" ");
                                        selectedDaysInTwoLetters.append(shortenDaysOfWeek[i]).append(" ,");
                                    }
                                }
                                System.out.println(stringBuilder);
                                if (selectedDaysInTwoLetters.length() > 0)
                                    selectedDaysInTwoLetters.deleteCharAt(selectedDaysInTwoLetters.length() - 1);
                                hint.setText(context.getResources().getString(R.string.on_every,
                                        selectedDaysInTwoLetters));
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ", " Friday ", " Saturday ", " Sunday "};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                parent.getContext());
        alertDialogBuilder.setTitle("Select day");
        alertDialogBuilder
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        hint.setText(context.getResources().getString(R.string.two_subsequent_strings,
                                hint.getText(), String.valueOf(items[item]).substring(1, items[item].length() - 1)));
                        dialog.cancel();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

        public ViewHolder(View itemView, AddHabitParameterListener listener) {
            super(itemView);
            mListener = listener;
            title = (TextView) itemView.findViewById(R.id.add_habit_category);
            hint = (TextView) itemView.findViewById(R.id.add_habit_category_hint);
            icon = (ImageView) itemView.findViewById(R.id.add_habit_category_icon);
            block = (RippleView) itemView.findViewById(R.id.add_habit_category_block);
            block.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
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

        public interface AddHabitParameterListener {
            void onCategory(View caller, final TextView hint);

            void onReminder(View caller, final TextView hint);

            void onFrequency(View caller, final TextView hint);

            void onTune(View caller, final TextView hint);

            void onConfirmation(View caller, final TextView hint);

        }
    }

    public static class HabitSettings {
        private String categoryName;
        private int notificationHour;
        private int notificationMinute;
        private NotificationFrequencyType notificationFrequencyType;
        private int notificationFrequencyWeekNumberOrDate;
        private boolean[] notificationFrequencySpecifiedDays;
        private Uri notificationSoundUri;
        private int minutesBeforeConfirmation;

        public HabitSettings(String categoryName, int notificationHour, int notificationMinute,
                             NotificationFrequencyType notificationFrequencyType,
                             int notificationFrequencyWeekNumberOrDate, boolean[] notificationFrequencySpecifiedDays,
                             Uri notificationSoundUri, int minutesBeforeConfirmation) {
            this.categoryName = categoryName;
            this.notificationHour = notificationHour;
            this.notificationMinute = notificationMinute;
            this.notificationFrequencyType = notificationFrequencyType;
            this.notificationFrequencyWeekNumberOrDate = notificationFrequencyWeekNumberOrDate;
            this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
            this.notificationSoundUri = notificationSoundUri;
            this.minutesBeforeConfirmation = minutesBeforeConfirmation;
        }

        public HabitSettings() {
            Context context = MainActivity.getContext();
            HabitCategoryDAO habitCategoryDAO = new HabitCategoryDAO(context);
            CharSequence[] categoryNames = habitCategoryDAO.getArrayOfAllNames();
            this.categoryName = (String) categoryNames[0];
            this.notificationHour = 0;
            this.notificationMinute = 0;
            this.notificationFrequencyType = NotificationFrequencyType.DAILY;
            this.notificationFrequencyWeekNumberOrDate = 1;
            boolean[] notificationFrequencySpecifiedDays = new boolean[7];
            this.notificationFrequencySpecifiedDays = notificationFrequencySpecifiedDays;
            this.notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            this.minutesBeforeConfirmation = 60;
        }
    }
}
