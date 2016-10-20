package ru.android4life.habittracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.views.RippleView;

public class HabitParameterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public AddHabitParameterListener mListener;
    public TextView title;
    public TextView hint;
    public ImageView icon;
    private RippleView block;

    public HabitParameterViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.add_habit_category);
        hint = (TextView) itemView.findViewById(R.id.add_habit_category_hint);
        icon = (ImageView) itemView.findViewById(R.id.add_habit_category_icon);
        block = (RippleView) itemView.findViewById(R.id.add_habit_category_block);
        block.setOnClickListener(this);
    }

    public void disableRippleEffect() {
        block.setRippleAlpha(0);
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_category))) {
                mListener.onCategory(view, hint);
            } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_reminder))) {
                mListener.onReminder(view, hint);
            } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_frequency))) {
                mListener.onFrequency(view, hint);
            } else if (title.getText().toString().equals(view.getResources().getString(R.string.add_habit_name_tune))) {
                mListener.onTune(view, hint);
            } else {
                mListener.onConfirmation(view, hint);
            }
        }
    }

    public interface AddHabitParameterListener {
        void onCategory(View caller, final TextView hint);

        void onReminder(View caller, final TextView hint);

        void onFrequency(View caller, final TextView hint);

        void onTune(View caller, final TextView hint);

        void onConfirmation(View caller, final TextView hint);

    }
}