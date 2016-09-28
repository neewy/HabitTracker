package si.modrajagoda.didi.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import si.modrajagoda.didi.HabitParameter;
import si.modrajagoda.didi.R;
import si.modrajagoda.didi.activity.AddHabitActivity;
import si.modrajagoda.didi.activity.MainActivity;
import si.modrajagoda.didi.adapter.HabitParametersAdapter;

/**
 * This class represents a fragment of left tab,
 * which contains confirmation card of habit,
 * and list of habit settings as well.
 *
 * Created by Nikolay Yushkevich on 27.09.16.
 */

public class HabitCardFragment extends Fragment {

    private HabitParametersAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adapter for habits parameters (thank you, Bulat)
        mAdapter = new HabitParametersAdapter(HabitParameter.createParameters(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View habitCard = inflater.inflate(R.layout.habit_card, container, false);

        //list of settings
        RecyclerView recyclerView = (RecyclerView) habitCard.findViewById(R.id.habit_settings);

        //as we need a vertical list, the layout manager is vertical
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton editHabit = (FloatingActionButton) habitCard.findViewById(R.id.fab_edit);
        editHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
            }
        });

        return habitCard;
    }

}
