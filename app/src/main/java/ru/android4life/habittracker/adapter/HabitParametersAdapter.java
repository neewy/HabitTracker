package ru.android4life.habittracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.android4life.habittracker.HabitParameter;
import ru.android4life.habittracker.R;

/**
 * Created by Bulat Mukhutdinov on 28.09.2016.
 */
public class HabitParametersAdapter extends RecyclerView.Adapter<HabitParametersAdapter.ViewHolder> {

    private List<HabitParameter> parameters;

    public HabitParametersAdapter(List<HabitParameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_parameter, parent, false);
        return new ViewHolder(v);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView hint;
        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.add_habit_category);
            hint = (TextView) itemView.findViewById(R.id.add_habit_category_hint);
            icon = (ImageView) itemView.findViewById(R.id.add_habit_category_icon);
        }
    }
}
