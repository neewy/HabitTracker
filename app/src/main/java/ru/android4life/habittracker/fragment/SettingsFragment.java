package ru.android4life.habittracker.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.MainActivity;
import ru.android4life.habittracker.adapter.SettingsListAdapter;
import ru.android4life.habittracker.enumeration.SettingsType;
import ru.android4life.habittracker.models.Setting;

import static android.content.Context.MODE_PRIVATE;
import static ru.android4life.habittracker.activity.BaseActivity.SHARED_PREF;

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
        SettingsListAdapter.fragmentManager = getFragmentManager();
        personalAdapter = new SettingsListAdapter(createListOfPersonalSettings(), SettingsType.PERSONAL);
        personalAdapter.setMainActivity((MainActivity) getActivity());
        inAppAdapter = new SettingsListAdapter(createListOfInAppSettings(), SettingsType.INAPP);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_settings_list, container, false);
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
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        List<Setting> personalSettingsList = new ArrayList<>();
        String locale = MainActivity.getContext().getResources().getConfiguration().locale.getDisplayName();
        personalSettingsList.add(new Setting(getString(R.string.first_name), getString(R.string.username)));
        personalSettingsList.add(new Setting(getString(R.string.primary_color), prefs.getString("color", "")));
        personalSettingsList.add(new Setting(getString(R.string.language), locale));
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
        inAppSettingsList.add(new Setting(getString(R.string.version)));
        return inAppSettingsList;
    }
}
