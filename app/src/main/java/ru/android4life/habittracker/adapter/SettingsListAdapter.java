package ru.android4life.habittracker.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.List;
import java.util.Locale;

import pl.coreorb.selectiondialogs.data.SelectableColor;
import pl.coreorb.selectiondialogs.dialogs.ColorSelectDialog;
import ru.android4life.habittracker.R;
import ru.android4life.habittracker.activity.AppIntroActivity;
import ru.android4life.habittracker.enumeration.SettingsType;
import ru.android4life.habittracker.models.Setting;
import ru.android4life.habittracker.viewholder.SettingsViewHolder;

import static android.content.Context.MODE_PRIVATE;
import static ru.android4life.habittracker.activity.MainActivity.getContext;
import static ru.android4life.habittracker.utils.StringConstants.COLOR;
import static ru.android4life.habittracker.utils.StringConstants.HIDE_SETTINGS;
import static ru.android4life.habittracker.utils.StringConstants.LOCALE;
import static ru.android4life.habittracker.utils.StringConstants.PRIMARY_COLOR_DIALOG;
import static ru.android4life.habittracker.utils.StringConstants.SHARED_PREF;
import static ru.android4life.habittracker.utils.StringConstants.USER_NAME;

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
                layout = R.layout.list_item_1;
                break;
            case PERSONAL:
                layout = R.layout.list_item_2;
                break;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        SettingsViewHolder vh = new SettingsViewHolder(v);
        setListeners(parent, vh);

        return vh;
    }

    private void setListeners(final ViewGroup parent, SettingsViewHolder vh) {
        vh.setListener(new SettingsViewHolder.SettingsListener() {
            @Override
            public void onFirstName(View caller) {
                createFirstNameDialog(caller);
            }

            @Override
            public void onPrimaryColor(View caller) {
                createPrimaryColorPickerDialog();
            }

            @Override
            public void onLanguage(View caller) {
                createLanguageDialog(parent);
            }

            @Override
            public void onAbout() {
                Intent aboutIntro = new Intent(getContext(), AppIntroActivity.class);
                aboutIntro.putExtra(HIDE_SETTINGS, true);
                getContext().startActivity(aboutIntro);
            }

            @Override
            public void onContributors() {
                new LibsBuilder()
                        //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        //start the activity
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withLicenseShown(true)
                        .withActivityTitle(getContext().getString(R.string.contributors))
                        .withAboutDescription(getContext().getString(R.string.contributors_text))
                        .start(getContext());
            }

            @Override
            public void onVersion() {

            }
        });
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {
        Setting setting = applicationSettings.get(position);
        holder.settingTitle.setText(setting.title);

        // set the text for second line, if the type of elements is "personal settings"
        if (type == SettingsType.PERSONAL) {
            holder.settingSelection.setText(setting.selection);
        }
    }

    @Override
    public int getItemCount() {
        return applicationSettings.size();
    }

    private void createFirstNameDialog(final View caller) {
        Context baseContext = getContext();
        final SharedPreferences prefs = baseContext.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        AlertDialog.Builder firstNameDialog = new AlertDialog.Builder(baseContext);
        firstNameDialog.setTitle(baseContext.getResources().getString(R.string.first_name_title));

        final EditText input = getEditText(baseContext, prefs);
        firstNameDialog.setView(input);

        firstNameDialog.setNegativeButton(android.R.string.cancel, null);
        firstNameDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TextView settingSelection = (TextView) caller.findViewById(R.id.text2);
                String newFirstName = input.getText().toString();
                settingSelection.setText(newFirstName);
                prefs.edit().putString(USER_NAME, newFirstName).apply();
            }
        });
        firstNameDialog.show();
    }

    @NonNull
    private EditText getEditText(Context baseContext, SharedPreferences prefs) {
        final EditText input = new EditText(baseContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(prefs.getString(USER_NAME, mainActivity.getString(R.string.username)));
        return input;
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
            dialog.show(fragmentManager, PRIMARY_COLOR_DIALOG);
            dialog.setOnColorSelectedListener(new ColorSelectDialog.OnColorSelectedListener() {
                @Override
                public void onColorSelected(SelectableColor selectedItem) {
                    getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString(COLOR, selectedItem.getId()).apply();
                    dialog.dismiss();
                    mainActivity.recreate();
                }
            });
        } else {
            Log.e("No activity", "No activity in Setting List Adapter", new OperationCanceledException());
        }
    }

    private void createLanguageDialog(final ViewGroup parent) {
        final CharSequence[] items = {parent.getResources().getString(R.string.english), parent.getResources().getString(R.string.russian)};
        AlertDialog.Builder languageSelection = new AlertDialog.Builder(
                parent.getContext());
        languageSelection.setTitle(parent.getResources().getString(R.string.select_language));
        languageSelection
                .setSingleChoiceItems(items, getSelectedLocale(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString(LOCALE, getContext().getResources().getString(R.string.locale_en)).apply();
                                break;
                            case 1:
                                getContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString(LOCALE, getContext().getResources().getString(R.string.locale_ru)).apply();
                                break;
                        }
                        if (getSelectedLocale() != item) {
                            mainActivity.recreate();
                        }
                        dialog.cancel();
                    }
                }).setNegativeButton(parent.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        languageSelection.show();
    }

    private int getSelectedLocale() {
        if (getContext().getResources().getConfiguration().locale.getLanguage().equals(new Locale("en").getLanguage())) {
            return 0;
        } else {
            return 1;
        }
    }
}
