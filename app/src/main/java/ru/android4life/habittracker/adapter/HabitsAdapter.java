package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.viewholder.HabitViewHolder;

public class HabitsAdapter extends RecyclerSwipeAdapter<HabitViewHolder> {

    private Context mContext;
    private List<Habit> habits;
    private HabitDAO habitDAO;
    private HabitScheduleDAO habitScheduleDAO;
    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public HabitsAdapter(Context context) {
        mContext = context;
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        habits = (List<Habit>) habitDAO.findAll();
    }

    @Override
    public HabitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_item, parent, false);

        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HabitViewHolder viewHolder, final int position) {
        final Habit habit = habits.get(position);
        viewHolder.getSwipeLayout().setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.getSwipeLayout().addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        viewHolder.getButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.getSwipeLayout());
                habitScheduleDAO.deleteByHabitId(habit.getId());
                habitDAO.delete(habit);
                habits.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, habits.size());
                mItemManger.closeAllItems();
            }
        });
        viewHolder.getName().setText(habit.getName());
        viewHolder.getQuestion().setText(mContext.getResources().getText(R.string.did_i) + " " + habit.getQuestion() + "?");
        //     mItemManger.bind(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public int getItemViewType(int position) {
        Habit habit = habits.get(position);
        return habit.getId();
    }
}
