package ru.android4life.habittracker.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.fragment.HabitTabsFragment;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListAdapter extends RecyclerView.Adapter<HabitListAdapter.ViewHolder> {

    private List<Habit> habits;
    private String[] dummyList = new String[]{"Test1", "Test2", "Test3", "Test4", "Test5"};
    private FragmentManager fragmentManager;

    public HabitListAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
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
        String record = dummyList[position];
        holder.title.setText(record);
    }

    @Override
    public int getItemCount() {
        return dummyList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.habit_name);
        }
    }
}
