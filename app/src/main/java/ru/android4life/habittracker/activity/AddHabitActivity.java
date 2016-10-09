package ru.android4life.habittracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import ru.android4life.habittracker.HabitParameter;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.adapter.HabitParametersAdapter;
import ru.android4life.habittracker.views.RippleView;

/**
 * Created by Bulat Mukhutdinov on 24.09.2016.
 */
public class AddHabitActivity extends AppCompatActivity {//implements View.OnClickListener {

    public static final int PICK_AUDIO_REQUEST = 0;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        final RippleView textView = (RippleView) findViewById(R.id.add_habit_back_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, (int) (textView.getRippleDuration() * 1.1d));

            }
        });

        final RippleView confirmButton = (RippleView) findViewById(R.id.add_habit_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: Get data from HabitParametersAdapter, create Habits according to it

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }, (int) (textView.getRippleDuration() * 1.1d));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.habit_parameters_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HabitParametersAdapter(this, HabitParameter.createParameters(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AUDIO_REQUEST) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                Log.i("AUDIO", "Uri: " + uri.toString());
            }
        }
    }
}
