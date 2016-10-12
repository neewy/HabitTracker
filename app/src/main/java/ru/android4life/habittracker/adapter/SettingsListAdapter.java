package ru.android4life.habittracker.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.coreorb.selectiondialogs.data.SelectableColor;
import pl.coreorb.selectiondialogs.dialogs.ColorSelectDialog;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.fragment.SettingsFragment;

import static android.content.Context.MODE_PRIVATE;
import static ru.android4life.habittracker.activity.MainActivity.SHARED_PREF;
import static ru.android4life.habittracker.activity.MainActivity.getContext;

/**
 * This adapter populates the list of settings in the application.
 * Currently, it implements the feature of style selection by the user.
 * <p>
 * Created by Nikolay Yushkevich on 09.10.16.
 */

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.SettingsViewHolder> {

    // in order to create dialogs of settings
    public static FragmentManager fragmentManager;
    // list of settings
    private List<SettingsFragment.Setting> applicationSettings;
    // what is their type (are they personal or global/in-app settings)
    private SettingsFragment.SettingsType type;
    // for access to shared prefs and in order to change the style
    private AppCompatActivity mainActivity;


    public SettingsListAdapter(List<SettingsFragment.Setting> applicationSettings, SettingsFragment.SettingsType type) {
        this.applicationSettings = applicationSettings;
        this.type = type;
    }

    /**
     * Set the activity for the settings which need it.
     * e.g. a color change demand the activity
     */
    public void setMainActivity(AppCompatActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // select the layout depending on the type of the settings
        int layout = 0;
        switch (type) {
            case INAPP:
                layout = android.R.layout.simple_list_item_1;
                break;
            case PERSONAL:
                layout = android.R.layout.simple_list_item_2;
                break;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        SettingsViewHolder vh = new SettingsViewHolder(v);
        vh.setListener(new SettingsViewHolder.SettingsListener() {
            @Override
            public void onPrimaryColor(View caller) {
                createPrimaryColorPickerDialog();
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {
        SettingsFragment.Setting setting = applicationSettings.get(position);
        holder.settingTitle.setText(setting.title);

        // set the text for second line, if the type of elements are personal settings
        if (type == SettingsFragment.SettingsType.PERSONAL) {
            holder.settingSelection.setText(setting.selection);
        }
    }

    @Override
    public int getItemCount() {
        return applicationSettings.size();
    }

    private void createPrimaryColorPickerDialog() {
        if (mainActivity != null) {
            final ColorSelectDialog dialog = new ColorSelectDialog.Builder(getContext())
                    .setColors(R.array.primary_ids,
                            R.array.primary_names,
                            R.array.primary_colors)
                    .setTitle(R.string.color_select)
                    .setSortColorsByName(true)
                    .build();
            dialog.show(fragmentManager, "primary_color_dialog");
            dialog.setOnColorSelectedListener(new ColorSelectDialog.OnColorSelectedListener() {
                @Override
                public void onColorSelected(SelectableColor selectedItem) {
                    mainActivity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString("color", selectedItem.getName()).apply();
                    dialog.dismiss();
                    mainActivity.recreate();
                }
            });
        } else {
            Log.e("No activity", "No activity in Setting List Adapter", new OperationCanceledException());
        }
    }

    static class SettingsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SettingsListener mListener;
        private TextView settingTitle;
        private TextView settingSelection;

        SettingsViewHolder(View itemView) {
            super(itemView);
            settingTitle = (TextView) itemView.findViewById(android.R.id.text1);
            settingSelection = (TextView) itemView.findViewById(android.R.id.text2);
            itemView.setOnClickListener(this);
        }

        public void setListener(SettingsListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            if (settingTitle.getText().toString().equals(v.getResources().getString(R.string.primary_color))) {
                mListener.onPrimaryColor(v);
            }
        }

        interface SettingsListener {
            void onPrimaryColor(View caller);
        }
    }
}
