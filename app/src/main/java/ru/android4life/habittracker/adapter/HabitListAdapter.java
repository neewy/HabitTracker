package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.enumeration.DrawerSelectionMode;
import ru.android4life.habittracker.fragment.HabitTabsFragment;
import ru.android4life.habittracker.viewholder.HabitCardViewHolder;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListAdapter extends RecyclerView.Adapter<HabitCardViewHolder> {

    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private List<HabitSchedule> habitSchedules;
    private FragmentManager fragmentManager;
    private Context context;
    private DrawerSelectionMode drawerSelectionMode;

    public HabitListAdapter(FragmentManager fragmentManager, DrawerSelectionMode drawerSelectionMode) {
        this.fragmentManager = fragmentManager;
        context = BaseActivity.getContext();
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        this.drawerSelectionMode = drawerSelectionMode;
        habitSchedules =
                getHabitSchedulesDependOnDrawerSelectionMode(drawerSelectionMode);
    }

    @Override
    public HabitCardViewHolder onCreateViewHolder(ViewGroup parent, final int habitScheduleId) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_card, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container,
                        HabitTabsFragment.newInstance(habitScheduleId)).addToBackStack(drawerSelectionMode.stringValue).commit();
            }
        });
        return new HabitCardViewHolder(v);
    }

    // To assure that viewType parameter of onCreateViewHolder method will represent an id of the clicked habitSchedule
    @Override
    public int getItemViewType(int position) {
        final HabitSchedule habitSchedule = habitSchedules.get(position);
        return habitSchedule.getId();
    }

    @Override
    public void onBindViewHolder(final HabitCardViewHolder holder, int position) {
        final HabitSchedule habitSchedule = habitSchedules.get(position);
        final Habit habit = (Habit) habitDAO.findById(habitSchedule.getHabitId());
        holder.title.setText(habit.getName());
        holder.question.setText(String.format(getStringFromResources(R.string.did_i_question),
                habit.getQuestion()));
        holder.time.setText(Constants.prettyTime.format(habitSchedule.getDatetime()));

        if (habitSchedule.isDone() != null) {
            holder.done.setEnabled(false);
            holder.skip.setEnabled(false);
            holder.done.setVisibility(View.GONE);
            holder.skip.setVisibility(View.GONE);
        }

        setSkipAndDoneListeners(holder, habitSchedule);
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

    private void setSkipAndDoneListeners(final HabitCardViewHolder holder, final HabitSchedule habitSchedule) {
        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                        habitSchedule.getDatetime(), false, habitSchedule.getHabitId());
                habitScheduleDAO.update(updatedHabitSchedule);
                habitSchedules = getHabitSchedulesDependOnDrawerSelectionMode(drawerSelectionMode);
                if (drawerSelectionMode != DrawerSelectionMode.ALL_TASKS)
                    notifyItemRemoved(holder.getAdapterPosition());
                else
                    notifyItemChanged(holder.getAdapterPosition());
            }
        });

        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                        habitSchedule.getDatetime(), true, habitSchedule.getHabitId());
                habitScheduleDAO.update(updatedHabitSchedule);
                habitSchedules = getHabitSchedulesDependOnDrawerSelectionMode(drawerSelectionMode);
                if (drawerSelectionMode != DrawerSelectionMode.ALL_TASKS)
                    notifyItemRemoved(holder.getAdapterPosition());
                else
                    notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }
}
