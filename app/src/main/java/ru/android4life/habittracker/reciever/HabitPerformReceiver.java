package ru.android4life.habittracker.reciever;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.PopupActivity;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;

import static ru.android4life.habittracker.utils.StringConstants.HABIT_SCHEDULE_ID;
import static ru.android4life.habittracker.utils.StringConstants.IS_DONE;

/**
 * Created by neewy on 29.10.16.
 */

public class HabitPerformReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        HabitScheduleDAO habitScheduleDAO = new HabitScheduleDAO(context);
        HabitDAO habitDAO = new HabitDAO(context);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Integer habitScheduleId = bundle.getInt(HABIT_SCHEDULE_ID, -1);
        Boolean isDone = bundle.getBoolean(IS_DONE, false);

        // if such a habit exists
        if (habitScheduleId != -1) {
            HabitSchedule schedule = (HabitSchedule) habitScheduleDAO.findById(habitScheduleId);
            HabitSchedule updatedHabitSchedule = new HabitSchedule(schedule.getId(),
                    schedule.getDatetime(), isDone, schedule.getHabitId());

            habitScheduleDAO.update(updatedHabitSchedule);
            String habitName = ((Habit) habitDAO.findById(schedule.getHabitId())).getName();

            //TODO: add motivational messages!

            if (isDone) {
                Intent openNoteActivity = new Intent(context, PopupActivity.class);
                openNoteActivity.putExtra(HABIT_SCHEDULE_ID, habitScheduleId);
                context.startActivity(openNoteActivity);
            }

            String message = (isDone) ? context.getString(R.string.was_done) : context.getString(R.string.was_skipped);
            Toast.makeText(context, habitName + " " + message, Toast.LENGTH_LONG).show();

            //cancel the notification to hide it from the screen
            notificationManager.cancel(habitScheduleId * 2 + 1);

            //FIXME: is this possible to update list of habits if a notification action was checked?
            //(probably not)
        }
    }
}
