package ru.android4life.habittracker.adapter;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.coreorb.selectiondialogs.data.SelectableColor;
import pl.coreorb.selectiondialogs.dialogs.ColorSelectDialog;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.enumeration.SettingsType;
import ru.android4life.habittracker.models.Setting;
import ru.android4life.habittracker.viewholder.SettingsViewHolder;

import static android.content.Context.MODE_PRIVATE;
import static ru.android4life.habittracker.activity.MainActivity.SHARED_PREF;
import static ru.android4life.habittracker.activity.MainActivity.getContext;

/**
 * This adapter populates the list of settings in the application.
 * Currently, it implements the feature of style selection by the user.
 * <p>
 * Created by Nikolay Yushkevich on 09.10.16.
 */

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsViewHolder> {

    // in order to create dialogs of settings
    public static FragmentManager fragmentManager;
    // list of settings
    private List<Setting> applicationSettings;
    // what is their type (are they personal or global/in-app settings)
    private SettingsType type;
    // for access to shared prefs and in order to change the style
    private AppCompatActivity mainActivity;

    private Integer selectedLanguage;

    public SettingsListAdapter(List<Setting> applicationSettings, SettingsType type) {
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

            @Override
            public void onLanguage(View caller) {
                createLanguageDialog(parent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {
        Setting setting = applicationSettings.get(position);
        holder.settingTitle.setText(setting.title);

        // set the text for second line, if the type of elements are personal settings
        if (type == SettingsType.PERSONAL) {
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

    public void createLanguageDialog(final ViewGroup parent) {
        final CharSequence[] items = {parent.getResources().getString(R.string.english), parent.getResources().getString(R.string.russian)};
        if (selectedLanguage == null) {
            selectedLanguage = getSelectedLocale(mainActivity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE).getString("locale", parent.getResources().getString(R.string.locale_en)), parent);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                parent.getContext());
        alertDialogBuilder.setTitle(parent.getResources().getString(R.string.select_language));
        alertDialogBuilder
                .setSingleChoiceItems(items, selectedLanguage, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                mainActivity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString("locale", parent.getResources().getString(R.string.locale_en)).apply();
                                break;
                            case 1:
                                mainActivity.getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString("locale", parent.getResources().getString(R.string.locale_ru)).apply();
                                break;
                        }
                        if (selectedLanguage != item) {
                            selectedLanguage = item;
                            mainActivity.recreate();
                        }
                        dialog.cancel();
                    }
                }).setNegativeButton(parent.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private int getSelectedLocale(String locale, ViewGroup parent) {
        if (parent.getResources().getString(R.string.locale_en).equals(locale)) {
            return 0;
        } else {
            return 1;
        }
    }
}
