package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.android4life.habittracker.HabitNotification;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.BaseActivity;
import ru.android4life.habittracker.db.Constants;
import ru.android4life.habittracker.db.dataaccessobjects.HabitCategoryDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.dataaccessobjects.HabitScheduleDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.db.tablesrepresentations.HabitCategory;
import ru.android4life.habittracker.db.tablesrepresentations.HabitSchedule;
import ru.android4life.habittracker.enumeration.DrawerSelectionMode;
import ru.android4life.habittracker.fragment.HabitListFragment;
import ru.android4life.habittracker.fragment.HabitTabsFragment;
import ru.android4life.habittracker.viewholder.HabitCardViewHolder;

import static ru.android4life.habittracker.utils.StringConstants.CLEANING;
import static ru.android4life.habittracker.utils.StringConstants.COOKING;
import static ru.android4life.habittracker.utils.StringConstants.HEALTH;
import static ru.android4life.habittracker.utils.StringConstants.READING;
import static ru.android4life.habittracker.utils.StringConstants.SPORT;
import static ru.android4life.habittracker.utils.StringConstants.STUDYING;


public class HabitListAdapter extends RecyclerView.Adapter<HabitCardViewHolder> implements GestureDetector.OnGestureListener {

    private HabitScheduleDAO habitScheduleDAO;
    private HabitCategoryDAO habitCategoryDAO;
    private HabitListFragment listFragment;
    private HabitDAO habitDAO;
    private List<HabitSchedule> habitSchedules;
    private FragmentManager fragmentManager;
    private Context context;
    private DrawerSelectionMode drawerSelectionMode;
    private ImageButton contextMenu;
    private PopupMenu popup;

    public HabitListAdapter(HabitListFragment listFragment, FragmentManager fragmentManager, DrawerSelectionMode drawerSelectionMode) {
        this.listFragment = listFragment;
        this.fragmentManager = fragmentManager;
        context = BaseActivity.getContext();
        habitDAO = new HabitDAO(context);
        habitScheduleDAO = new HabitScheduleDAO(context);
        habitCategoryDAO = new HabitCategoryDAO(context);
        this.drawerSelectionMode = drawerSelectionMode;
        fillDependOnDrawerSelectionMode();
    }

    @Override
    public HabitCardViewHolder onCreateViewHolder(ViewGroup parent, final int habitScheduleId) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_list_card, parent, false);
        SwipeLayout swipeLayout = (SwipeLayout) v.findViewById(R.id.interactive_card);
        swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction().replace(R.id.container,
                        HabitTabsFragment.newInstance(habitScheduleId)).addToBackStack(drawerSelectionMode.stringValue).commit();

            }
        });
        contextMenu = (ImageButton) v.findViewById(R.id.card_context_menu);

        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_2));
        return new HabitCardViewHolder(v);
    }

    public boolean emptyData() {
        return habitSchedules.isEmpty();
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

        //place for the category image
        RelativeLayout imagePlaceholder = (RelativeLayout) holder.itemView.findViewById(R.id.image_placeholder);

        //if image placeholder is empty
        if (imagePlaceholder.getChildCount() == 0) {
            //then add a category picture to the habit card
            addBackgroundImage(imagePlaceholder, habit);
        }

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
                                onPerformClick(habitSchedule, view, true);
                                return true;
                            case R.id.menu_skip:
                                onPerformClick(habitSchedule, view, false);
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

    /**
     * Adding habit category image to the imagePlaceholder
     *
     * @param imagePlaceholder where to add image
     * @param habit            which has a habit category
     */
    private void addBackgroundImage(RelativeLayout imagePlaceholder, Habit habit) {
        HabitCategory category = (HabitCategory) habitCategoryDAO.findById(habit.getCategoryId());
        ImageView image = new ImageView(context);
        switch (category.getName()) {
            case SPORT:
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.card_back_sport));
                break;
            case READING:
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.card_back_reading));
                break;
            case COOKING:
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.card_back_cooking));
                break;
            case CLEANING:
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.card_back_cleaning));
                break;
            case STUDYING:
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.card_back_studying));
                break;
            case HEALTH:
                image.setImageDrawable(context.getResources().getDrawable(R.drawable.card_back_health));
                break;
        }
        imagePlaceholder.addView(image);
    }

    @Override
    public int getItemCount() {
        return habitSchedules.size();
    }

    private String getStringFromResources(int resource) {
        return context.getResources().getString(resource);
    }

    public void fillDependOnDrawerSelectionMode() {
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

    private void onPerformClick(final HabitSchedule habitSchedule, View v, boolean isDone) {
        if (!DateUtils.isToday(habitSchedule.getDatetime().getTime())) {
            Toast toast = Toast.makeText(context,
                    context.getString(R.string.perform_future_habit), Toast.LENGTH_LONG);
            LinearLayout layout = (LinearLayout) toast.getView();
            if (layout.getChildCount() > 0) {
                TextView tv = (TextView) layout.getChildAt(0);
                tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            }
            toast.show();
            return;
        }
        HabitSchedule updatedHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                habitSchedule.getDatetime(), isDone, habitSchedule.getHabitId());
        habitScheduleDAO.update(updatedHabitSchedule);
        fillDependOnDrawerSelectionMode();
        notifyDataSetChanged();
        makeUndoSnackbar(isDone, habitSchedule, v);
        listFragment.switchEmptyView();

        //delete notifications if the habit was performed manually
        HabitNotification notification = new HabitNotification(context);
        notification.deleteHabitScheduleAlarms(habitSchedule.getId());
    }

    private void makeUndoSnackbar(boolean isDone, final HabitSchedule habitSchedule, View v) {

        String message = (isDone) ? getStringFromResources(R.string.was_done) : getStringFromResources(R.string.was_skipped);

        Snackbar snackbar = Snackbar
                .make(v, (context.getString(R.string.empty_delimiter_strings,
                        ((Habit) habitDAO.findById(habitSchedule.getHabitId())).getName(), message)), Snackbar.LENGTH_LONG)
                .setAction(getStringFromResources(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HabitSchedule prevHabitSchedule = new HabitSchedule(habitSchedule.getId(),
                                habitSchedule.getDatetime(), null, habitSchedule.getHabitId());
                        habitScheduleDAO.update(prevHabitSchedule);
                        fillDependOnDrawerSelectionMode();
                        notifyDataSetChanged();
                        listFragment.switchEmptyView();
                    }
                });
        snackbar.show();
    }

    private void setSkipAndDoneListeners(final HabitCardViewHolder holder, final HabitSchedule habitSchedule) {
        holder.skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPerformClick(habitSchedule, v, false);
            }
        });
        holder.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPerformClick(habitSchedule, v, true);
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


    public void sortByTitle(final boolean isAscending) {
        Collections.sort(habitSchedules, new Comparator<HabitSchedule>() {
            @Override
            public int compare(HabitSchedule o1, HabitSchedule o2) {
                Habit habit1 = (Habit) habitDAO.findById(o1.getHabitId());
                Habit habit2 = (Habit) habitDAO.findById(o2.getHabitId());
                if (isAscending) {
                    return habit1.getName().compareTo(habit2.getName());
                } else {
                    return habit2.getName().compareTo(habit1.getName());
                }
            }
        });
        notifyDataSetChanged();
    }

    public void sortByTime(final boolean isAscending) {
        Collections.sort(habitSchedules, new Comparator<HabitSchedule>() {
            @Override
            public int compare(HabitSchedule o1, HabitSchedule o2) {
                if (isAscending) {
                    return o1.getDatetime().compareTo(o2.getDatetime());
                } else {
                    return o2.getDatetime().compareTo(o1.getDatetime());
                }
            }
        });
        notifyDataSetChanged();
    }


}
