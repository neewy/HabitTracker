package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
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

    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private List<HabitSchedule> habitSchedules;
    private FragmentManager fragmentManager;
    private Context context;
    private DrawerSelectionMode drawerSelectionMode;

    public HabitListAdapter(FragmentManager fragmentManager, DrawerSelectionMode drawerSelectionMode) {
        this.fragmentManager = fragmentManager;
        context = MainActivity.getContext();
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        this.drawerSelectionMode = drawerSelectionMode;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final HabitSchedule habitSchedule = habitSchedules.get(position);
        final Habit habit = (Habit) habitDAO.findById(habitSchedule.getHabitId());
        holder.title.setText(habit.getName());
        holder.question.setText(String.format(getStringFromResources(R.string.did_i_question),
                habit.getQuestion()));
        holder.time.setText(Constants.prettyTime.format(habitSchedule.getDatetime()));

        if (habitSchedule.isPerformed()) {
            holder.done.setEnabled(false);
            holder.skip.setEnabled(false);
            holder.skip.setTextColor(Color.LTGRAY);
        }
        if (habitSchedule.isSkipped()) {
            holder.skip.setEnabled(false);
            holder.skip.setTextColor(Color.LTGRAY);
        }

        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                        habitSchedule.getDatetime(), habitSchedule.isPerformed(), true,
                        habitSchedule.getHabitId());
                habitScheduleDAO.update(updatedHabitSchedule);
                habitSchedules = getHabitSchedulesDependOnDrawerSelectionMode(drawerSelectionMode);
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                        habitSchedule.getDatetime(), true, habitSchedule.isSkipped(),
                        habitSchedule.getHabitId());
                habitScheduleDAO.update(updatedHabitSchedule);
                habitSchedules = getHabitSchedulesDependOnDrawerSelectionMode(drawerSelectionMode);
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
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
        private AppCompatButton skip;
        private AppCompatButton done;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.habit_name);
            question = (TextView) itemView.findViewById(R.id.habit_question);
            time = (TextView) itemView.findViewById(R.id.habit_time);
            skip = (AppCompatButton) itemView.findViewById(R.id.habit_skip);
            done = (AppCompatButton) itemView.findViewById(R.id.habit_done);
        }
    }
}
