package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.fragment.DrawerSelectionMode;
import ru.android4life.habittracker.fragment.HabitTabsFragment;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListAdapter extends RecyclerView.Adapter<HabitListAdapter.ViewHolder> {

    HabitScheduleDAO habitScheduleDAO;
    HabitDAO habitDAO;
    private List<HabitSchedule> habitSchedules;
    private FragmentManager fragmentManager;
    private Context context;

    public HabitListAdapter(FragmentManager fragmentManager, DrawerSelectionMode drawerSelectionMode) {
        this.fragmentManager = fragmentManager;
        context = MainActivity.getContext();
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        habitSchedules =
                getHabitSchedulesDependOnDrawerSelectionMode(drawerSelectionMode);
    }

    /*public HabitListAdapter(List<Habit> habits) {
        this.habits = habits;
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_card, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container, new HabitTabsFragment()).commit();
            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Habit habit = (Habit) habitDAO.findById(habitSchedules.get(position).getHabitId());
        holder.title.setText(habit.getName());
        holder.question.setText(String.format(getStringFromResources(R.string.did_i_question),
                habit.getQuestion()));
        holder.time.setText(Constants.prettyTime.format(habitSchedules.get(position).getDatetime()));
    }

    @Override
    public int getItemCount() {
        return habitSchedules.size();
    }

    private String getStringFromResources(int resource) {
        return context.getResources().getString(resource);
    }

    private List<HabitSchedule> getHabitSchedulesDependOnDrawerSelectionMode(
            DrawerSelectionMode drawerSelectionMode) {
        List<HabitSchedule> result = new ArrayList<>();
        switch (drawerSelectionMode) {
            case TODAY:
                result = habitScheduleDAO.findHabitSchedulesForToday();
                break;
            case TOMORROW:
                result = habitScheduleDAO.findHabitSchedulesForTomorrow();
                break;
            case NEXT_MONTH:
                result = habitScheduleDAO.findHabitSchedulesForNextMonth();
                break;
            case ALL_TASKS:
                result = (List<HabitSchedule>) habitScheduleDAO.findAll();
                break;
            default:
                break;
        }
        return result;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView question;
        private TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.habit_name);
            question = (TextView) itemView.findViewById(R.id.habit_question);
            time = (TextView) itemView.findViewById(R.id.habit_time);
        }
    }
}
