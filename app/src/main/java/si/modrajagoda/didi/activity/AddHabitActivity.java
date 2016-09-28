package si.modrajagoda.didi.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import si.modrajagoda.didi.R;
import si.modrajagoda.didi.RippleView;
import si.modrajagoda.didi.adapter.HabitParametersAdapter;

/**
 * Created by Bulat Mukhutdinov on 24.09.2016.
 */
public class AddHabitActivity extends AppCompatActivity {
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
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new HabitParametersAdapter(createParameters());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<HabitParameter> createParameters() {
        List<HabitParameter> habitParameters = new ArrayList<>();
        HabitParameter parameter = new HabitParameter("Category", "Fitness", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_habit_category));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Remind at", "None", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_habit_reminder));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Frequency", "Daily", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_habit_frequency));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Tune", "Standard", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_habit_tune));
        habitParameters.add(parameter);
        parameter = new HabitParameter("Confirmation", "After 30 min", ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_add_habit_confirmation));
        habitParameters.add(parameter);
        return habitParameters;
    }

    public class HabitParameter {
        private String title;
        private String hint;
        private Drawable icon;

        public HabitParameter(String title, String hint, Drawable icon) {
            this.title = title;
            this.hint = hint;
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }
    }

}
