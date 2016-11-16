package ru.android4life.habittracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

/**
 * Created by neewy on 15.11.16.
 */

public class NoteDialog {

    private String noteText = "";
    private Context context;
    private HabitScheduleDAO habitScheduleDAO;
    private int habitScheduleId;


    public NoteDialog(Context context, int habitScheduleId) {
        this.context = context;
        habitScheduleDAO = new HabitScheduleDAO(context);
        this.habitScheduleId = habitScheduleId;
    }

    public void createNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.add_note));

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);


        //TODO: translate
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noteText = input.getText().toString();
                HabitSchedule habitSchedule = (HabitSchedule) habitScheduleDAO.findById(habitScheduleId);
                habitSchedule.setNote(noteText);
                habitScheduleDAO.update(habitSchedule);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
