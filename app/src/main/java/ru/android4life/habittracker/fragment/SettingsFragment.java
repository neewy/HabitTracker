package ru.android4life.habittracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;

/**
 * This class is a controller for in-app settings view.
 * Created by Nikolay Yushkevich on 02.10.16.
 */

public class SettingsFragment extends Fragment {

    // view, which holds whole layout for the fragment
    private RelativeLayout view;

    // list of personal settings
    private RecyclerView personalSettings;

    // list for in-app settings
    private RecyclerView inAppSettings;

    //settings adapters
    private SettingsListAdapter personalAdapter;
    private SettingsListAdapter inAppAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // two types are possible, and each is using different views for their elements (1 or 2 text lines list element)
        personalAdapter = new SettingsListAdapter(createListOfPersonalSettings(), SettingsType.PERSONAL);
        inAppAdapter = new SettingsListAdapter(createListOfInAppSettings(), SettingsType.INAPP);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.settings_list, container, false);
        personalSettings = (RecyclerView) view.findViewById(R.id.personal_settings);
        inAppSettings = (RecyclerView) view.findViewById(R.id.in_app_info);

        setupPersonalSettings();
        setupInAppSettings();

        return view;
    }

    private void setupPersonalSettings() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        personalSettings.setHasFixedSize(true);
        personalSettings.setLayoutManager(mLinearLayoutManager);
        personalSettings.setAdapter(personalAdapter);
    }

    private void setupInAppSettings() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        inAppSettings.setHasFixedSize(true);
        inAppSettings.setLayoutManager(mLinearLayoutManager);
        inAppSettings.setAdapter(inAppAdapter);
    }

    /**
     * Generates a list of personal settings
     *
     * @return personal settings list
     */
    private List<Setting> createListOfPersonalSettings() {
        List<Setting> personalSettingsList = new ArrayList<>();
        personalSettingsList.add(new Setting(getString(R.string.first_name), getString(R.string.username)));
        personalSettingsList.add(new Setting(getString(R.string.favorite_color), getString(R.string.sky_blue)));
        personalSettingsList.add(new Setting(getString(R.string.language), getString(R.string.english)));
        return personalSettingsList;
    }

    /**
     * Generates list of in-app settings
     *
     * @return in-app settings list
     */
    private List<Setting> createListOfInAppSettings() {
        List<Setting> inAppSettingsList = new ArrayList<>();
        inAppSettingsList.add(new Setting(getString(R.string.about)));
        inAppSettingsList.add(new Setting(getString(R.string.contributors)));
        inAppSettingsList.add(new Setting(getString(R.string.language)));
        return inAppSettingsList;
    }

    private enum SettingsType {
        INAPP, PERSONAL
    }

    private class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.SettingsViewHolder> {

        private List<Setting> applicationSettings;
        private SettingsType type;

        public SettingsListAdapter(List<Setting> applicationSettings, SettingsType type) {
            this.applicationSettings = applicationSettings;
            this.type = type;
        }

        @Override
        public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            return new SettingsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(SettingsViewHolder holder, int position) {
            Setting setting = applicationSettings.get(position);
            holder.settingTitle.setText(setting.title);

            // to set the text for second line, if the type of elements are personal settings
            if (type == SettingsType.PERSONAL) {
                holder.settingSelection.setText(setting.selection);
            }
        }

        @Override
        public int getItemCount() {
            return applicationSettings.size();
        }

        protected class SettingsViewHolder extends RecyclerView.ViewHolder {

            private TextView settingTitle;
            private TextView settingSelection;

            public SettingsViewHolder(View itemView) {
                super(itemView);
                settingTitle = (TextView) itemView.findViewById(android.R.id.text1);
                settingSelection = (TextView) itemView.findViewById(android.R.id.text2);
            }
        }
    }

    private class Setting {
        protected String title;
        protected String selection;

        public Setting(String title, String selection) {
            this.title = title;
            this.selection = selection;
        }

        public Setting(String title) {
            this.title = title;
        }
    }
}
