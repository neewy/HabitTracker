package ru.android4life.habittracker.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.utils.StringConstants;

/**
 * Created by neewy on 15.11.16.
 */
public class PopupActivity extends Activity {

    private String noteText = "";
    private EditText noteEdit;
    private HabitScheduleDAO habitScheduleDAO;
    private int habitScheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        habitScheduleId = getIntent().getExtras().getInt(StringConstants.HABIT_SCHEDULE_ID, -1);
        setContentView(R.layout.activity_note);

        noteEdit = (EditText) findViewById(R.id.note_edit);
        Button confirm = (Button) findViewById(R.id.confirm);
        Button cancel = (Button) findViewById(R.id.cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (habitScheduleId != -1) {
                    noteText = noteEdit.getText().toString();
                    habitScheduleDAO = new HabitScheduleDAO(PopupActivity.this);
                    HabitSchedule habitSchedule = (HabitSchedule) habitScheduleDAO.findById(habitScheduleId);
                    habitSchedule.setNote(noteText);
                    habitScheduleDAO.update(habitSchedule);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myUid());
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myUid());
            }
        });
    }
}
