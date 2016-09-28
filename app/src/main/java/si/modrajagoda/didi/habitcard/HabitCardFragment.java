package si.modrajagoda.didi.habitcard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import si.modrajagoda.didi.HabitParameter;
import si.modrajagoda.didi.R;
import si.modrajagoda.didi.adapter.HabitParametersAdapter;

/**
 * This class represents a fragment of left tab,
 * which contains confirmation card of habit,
 * and list of habit settings as well.
 *
 * Created by Nikolay Yushkevich on 27.09.16.
 */

public class HabitCardFragment extends Fragment {

    private View habitCard;

    private RecyclerView view;
    private LinearLayoutManager mLinearLayoutManager;
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
        habitCard = inflater.inflate(R.layout.habit_card, container, false);

        //list of settings
        view = (RecyclerView) habitCard.findViewById(R.id.habit_settings);

        //as we need a vertical list, the layout manager is vertical
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        view.setHasFixedSize(true);
        view.setLayoutManager(mLinearLayoutManager);
        view.setAdapter(mAdapter);

        return habitCard;
    }
}
