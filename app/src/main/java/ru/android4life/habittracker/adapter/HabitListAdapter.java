package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.fragment.HabitTabsFragment;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListAdapter extends RecyclerView.Adapter<HabitListAdapter.ViewHolder> {

    private List<Habit> habits;
    private FragmentManager fragmentManager;
    private Context context;

    public HabitListAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        context = MainActivity.getContext();
        HabitDAO habitDAO = new HabitDAO(context);
        habits = (List<Habit>) habitDAO.findAll();
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
        holder.title.setText(habits.get(position).getName());
        holder.question.setText(String.format(getStringFromResources(R.string.did_i_question),
                habits.get(position).getQuestion()));
        holder.time.setText(Constants.prettyTime.format(habits.get(position).getNotificationTime()));
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    private String getStringFromResources(int resource) {
        return context.getResources().getString(resource);
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
