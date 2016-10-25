package ru.android4life.habittracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.android4life.habittracker.R;

public class HabitCardViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView question;
    public TextView time;
    public ImageView skip;
    public ImageView done;

    public HabitCardViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.habit_name);
        question = (TextView) itemView.findViewById(R.id.habit_question);
        time = (TextView) itemView.findViewById(R.id.habit_time);
        skip = (ImageView) itemView.findViewById(R.id.habit_skip);
        done = (ImageView) itemView.findViewById(R.id.habit_done);
    }
}