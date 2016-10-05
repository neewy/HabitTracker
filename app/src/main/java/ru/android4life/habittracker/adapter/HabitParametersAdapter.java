package ru.android4life.habittracker.adapter;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

import ru.android4life.habittracker.HabitParameter;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.views.RippleView;

/**
 * Created by Bulat Mukhutdinov on 28.09.2016.
 */
public class HabitParametersAdapter extends RecyclerView.Adapter<HabitParametersAdapter.ViewHolder> {

    private List<HabitParameter> parameters;
    private TimePickerDialog.OnTimeSetListener timePickerListener;
    private Activity activity;

    public HabitParametersAdapter(Activity activity, List<HabitParameter> parameters) {
        this.parameters = parameters;
        this.activity = activity;
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
            public void onCategory(View caller) {
                //TODO replace items with values from db
                final CharSequence[] items = {" Fitness ", " Health ", " Study ", " Other "};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle("Select category");
                alertDialogBuilder
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //TODO save selected value
                                System.out.println(items[item]);
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
            public void onReminder(View caller) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        parent.getContext(), timePickerListener, 0, 0, true);
                timePickerDialog.show();
            }

            @Override
            public void onFrequency(View caller) {
                final CharSequence[] items = {" Daily ", " Weekly ", " Monthly ", " Specified days "};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        parent.getContext());
                alertDialogBuilder.setTitle("Select a frequency");
                alertDialogBuilder
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                //TODO save selected value
                                System.out.println(items[item]);
                                if (item == 3) {
                                    createFrequencySpecifiedDaysDialog(parent);
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
            public void onTune(View caller) {
                Intent tmpIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                tmpIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                activity.startActivityForResult(tmpIntent, AddHabitActivity.PICK_AUDIO_REQUEST);
            }

            @Override
            public void onConfirmation(View caller) {

            }
        });
        return vh;
    }

    private void createFrequencySpecifiedDaysDialog(ViewGroup parent) {
        final boolean[] mCheckedItems = {false, false, false, false, false, false, false};
        final CharSequence[] items = {" Monday ", " Tuesday ", " Wednesday ", " Thursday ", " Friday ", " Saturday ", " Sunday "};
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                parent.getContext());
        alertDialogBuilder.setTitle("Select category");
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
                                for (int i = 0; i < mCheckedItems.length; i++) {
                                    if (mCheckedItems[i]) {
                                        stringBuilder.append(items[i] + " ");
                                    }
                                }
                                System.out.println(stringBuilder);
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
                mListener.onCategory(view);
            } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_reminder))) {
                mListener.onReminder(view);
            } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_frequency))) {
                mListener.onFrequency(view);
            } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_tune))) {
                mListener.onTune(view);
            } else {
                mListener.onConfirmation(view);
            }
        }

        public interface AddHabitParameterListener {
            void onCategory(View caller);

            void onReminder(View caller);

            void onFrequency(View caller);

            void onTune(View caller);

            void onConfirmation(View caller);

        }
    }
}
