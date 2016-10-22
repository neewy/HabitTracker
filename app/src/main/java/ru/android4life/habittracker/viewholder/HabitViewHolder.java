package ru.android4life.habittracker.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import ru.android4life.habittracker.R;

public class HabitViewHolder extends RecyclerView.ViewHolder {
    private SwipeLayout swipeLayout;
    private TextView name;
    private TextView question;
    private Button buttonDelete;

    public HabitViewHolder(View itemView) {
        super(itemView);
        swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
        name = (TextView) itemView.findViewById(R.id.habit_list_item_name);
        question = (TextView) itemView.findViewById(R.id.habit_list_item_question);
        buttonDelete = (Button) itemView.findViewById(R.id.delete);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(getClass().getSimpleName(), "onItemSelected: " + name.getText().toString());
                Toast.makeText(view.getContext(), "onItemSelected: " + name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public SwipeLayout getSwipeLayout() {
        return swipeLayout;
    }


    public TextView getName() {
        return name;
    }


    public Button getButtonDelete() {
        return buttonDelete;
    }

    public TextView getQuestion() {
        return question;
    }

}