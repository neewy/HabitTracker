package si.modrajagoda.didi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import si.modrajagoda.didi.R;
import si.modrajagoda.didi.database.Habit;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListAdapter extends RecyclerView.Adapter<HabitListAdapter.ViewHolder> {

    private String[] dummyList = new String[]{"Test1", "Test2", "Test3", "Test4", "Test5"};


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_card, parent, false);
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
