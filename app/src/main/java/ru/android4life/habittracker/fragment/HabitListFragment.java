package ru.android4life.habittracker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AddHabitActivity;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.adapter.HabitListAdapter;
import ru.android4life.habittracker.views.BottomOffsetDecoration;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListFragment extends Fragment {

    private HabitListAdapter mAdapter;
    private RelativeLayout view;
    private Menu sortCategory;
    private Menu sortDirection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void invalidateDataSet() {
        mAdapter.fillDependOnDrawerSelectionMode();
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //creating view from layout, attachToRoot false — so the parent cannot listen to events of inflated view
        view = (RelativeLayout) inflater.inflate(R.layout.habit_list, container, false);
        mAdapter = new HabitListAdapter(this, getActivity(), getFragmentManager(), MainActivity.drawerSelectionMode, view);
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.habits_list);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Process floating action bar
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View c) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
            }
        });

        listView.setHasFixedSize(true);
        listView.setLayoutManager(mLinearLayoutManager);
        listView.setAdapter(mAdapter);

        //adding 32dp space to the bottom of the list
        float offsetPx = getResources().getDimension(R.dimen.recycler_view_padding);
        BottomOffsetDecoration bottomOffsetDecoration = new BottomOffsetDecoration((int) offsetPx);
        listView.addItemDecoration(bottomOffsetDecoration);

        switchEmptyView();

        return view;
    }

    public void switchEmptyView() {
        ViewFlipper switcher = (ViewFlipper) view.findViewById(R.id.flipper);
        if (mAdapter.emptyData()) {
            switcher.setDisplayedChild(1);
        } else {
            switcher.setDisplayedChild(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sorting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        sortCategory = menu.getItem(0).getSubMenu();
        sortDirection = menu.getItem(1).getSubMenu();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // if the item was checked - then uncheck it and vice versa
        item.setChecked(!item.isChecked());

        // check if ascending menu item was checked
        boolean isAscending = sortDirection.getItem(0).isChecked();

        switch (item.getItemId()) {
            case R.id.sort_time:
                mAdapter.sortByTime(isAscending);
                return true;

            case R.id.sort_title:
                mAdapter.sortByTitle(isAscending);
                return true;

            case R.id.sort_asc:
                // if we sort based on the time
                if (sortCategory.getItem(0).isChecked()) {
                    mAdapter.sortByTime(true);
                } else {
                    mAdapter.sortByTitle(true);
                }
                return true;

            case R.id.sort_desc:
                if (sortCategory.getItem(0).isChecked()) {
                    mAdapter.sortByTime(false);
                } else {
                    mAdapter.sortByTitle(false);
                }
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateDataSet();
        switchEmptyView();
    }
}
