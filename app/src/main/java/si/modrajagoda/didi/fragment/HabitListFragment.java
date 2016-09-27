package si.modrajagoda.didi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import si.modrajagoda.didi.adapter.HabitListAdapter;
import si.modrajagoda.didi.R;

/**
 * Created by Nikolay Yushkevich on 21.09.16.
 */
public class HabitListFragment extends Fragment {

    private RecyclerView view;
    private LinearLayoutManager mLinearLayoutManager;
    private HabitListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HabitListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //creating view from layout, attachToRoot false — so the parent cannot listen to events of inflated view
        view = (RecyclerView) inflater.inflate(R.layout.habit_list, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        view.setHasFixedSize(true);
        view.setLayoutManager(mLinearLayoutManager);
        view.setAdapter(mAdapter);
        return view;
    }
}
