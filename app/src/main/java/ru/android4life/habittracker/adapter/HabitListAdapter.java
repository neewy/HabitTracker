package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.daimajia.swipe.SwipeLayout;

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


public class HabitListAdapter extends RecyclerView.Adapter<HabitCardViewHolder> implements GestureDetector.OnGestureListener {

    private HabitScheduleDAO habitScheduleDAO;
    private HabitDAO habitDAO;
    private List<HabitSchedule> habitSchedules;
    private FragmentManager fragmentManager;
    private Context context;
    private DrawerSelectionMode drawerSelectionMode;
    private ImageButton contextMenu;
    private PopupMenu popup;

    public HabitListAdapter(FragmentManager fragmentManager, DrawerSelectionMode drawerSelectionMode) {
        this.fragmentManager = fragmentManager;
        context = BaseActivity.getContext();
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        this.drawerSelectionMode = drawerSelectionMode;
        fillDependOnDrawerSelectionMode();
    }

    @Override
    public HabitCardViewHolder onCreateViewHolder(ViewGroup parent, final int habitScheduleId) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_card, parent, false);
        SwipeLayout sample = (SwipeLayout) v.findViewById(R.id.interactive_card);
        sample.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container,
                        HabitTabsFragment.newInstance(habitScheduleId)).addToBackStack(drawerSelectionMode.stringValue).commit();

            }
        });
        contextMenu = (ImageButton) v.findViewById(R.id.card_context_menu);

        sample.setShowMode(SwipeLayout.ShowMode.PullOut);
        sample.addDrag(SwipeLayout.DragEdge.Left, sample.findViewById(R.id.bottom_wrapper));
        sample.addDrag(SwipeLayout.DragEdge.Right, sample.findViewById(R.id.bottom_wrapper_2));
        return new HabitCardViewHolder(v);
    }


    // To assure that viewType parameter of onCreateViewHolder method will represent an id of the clicked habitSchedule
    @Override
    public int getItemViewType(int position) {
        HabitSchedule habitSchedule = habitSchedules.get(position);
        return habitSchedule.getId();
    }

    @Override
    public void onBindViewHolder(final HabitCardViewHolder holder, int position) {
        final Habit habit;
        final HabitSchedule habitSchedule = habitSchedules.get(position);
        habit = (Habit) habitDAO.findById(habitSchedule.getHabitId());
        holder.time.setText(Constants.prettyTime.format(habitSchedule.getDatetime()));
        setSkipAndDoneListeners(holder, habitSchedule);
        if (habitSchedule.isDone() != null) {
            holder.done.setEnabled(false);
            holder.skip.setEnabled(false);
            holder.done.setVisibility(View.GONE);
            holder.skip.setVisibility(View.GONE);
        }
        holder.title.setText(habit.getName());
        holder.question.setText(String.format(getStringFromResources(R.string.did_i_question),
                habit.getQuestion()));
        contextMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Context wrapper = new ContextThemeWrapper(context, BaseActivity.themeID);
                popup = new PopupMenu(wrapper, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.list_card_context, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_done:
                                onDoneClick(holder, habitSchedule, view);
                                return true;
                            case R.id.menu_skip:
                                onSkipClick(holder, habitSchedule, view);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
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

    private void fillDependOnDrawerSelectionMode() {
        switch (drawerSelectionMode) {
            case TODAY:
                habitSchedules = habitScheduleDAO.findHabitSchedulesForToday();
                break;
            case TOMORROW:
                habitSchedules = habitScheduleDAO.findHabitSchedulesForTomorrow();
                break;
            case NEXT_MONTH:
                habitSchedules = habitScheduleDAO.findHabitSchedulesForNextMonth();
                break;
            default:
                break;
        }
    }

    private void onDoneClick(final HabitCardViewHolder holder, final HabitSchedule habitSchedule, View v) {
        HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                habitSchedule.getDatetime(), true, habitSchedule.getHabitId());
        habitScheduleDAO.update(updatedHabitSchedule);
        fillDependOnDrawerSelectionMode();
        notifyItemChanged(holder.getAdapterPosition());

    }

    private void onSkipClick(final HabitCardViewHolder holder, final HabitSchedule habitSchedule, View v) {
        HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                habitSchedule.getDatetime(), false, habitSchedule.getHabitId());
        habitScheduleDAO.update(updatedHabitSchedule);
        fillDependOnDrawerSelectionMode();
        notifyItemChanged(holder.getAdapterPosition());
    }

    private void setSkipAndDoneListeners(final HabitCardViewHolder holder, final HabitSchedule habitSchedule) {
        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSkipClick(holder, habitSchedule, v);
            }
        });
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(holder, habitSchedule, v);
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
