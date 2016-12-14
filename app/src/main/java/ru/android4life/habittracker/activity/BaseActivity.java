package ru.android4life.habittracker.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Method;

import jonathanfinerty.once.Once;
import ru.android4life.habittracker.R;

import static ru.android4life.habittracker.utils.StringConstants.BLUE;
import static ru.android4life.habittracker.utils.StringConstants.COLOR;
import static ru.android4life.habittracker.utils.StringConstants.PURPLE;
import static ru.android4life.habittracker.utils.StringConstants.RED;
import static ru.android4life.habittracker.utils.StringConstants.SHARED_PREF;
import static ru.android4life.habittracker.utils.StringConstants.TEAL;

/**
 * This activity is for setting style for the activities,
 * if the user checks different style in settings.
 * <p>
 * Created by Nikolay Yushkevich on 12.10.16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public static int themeID;
    private static Context context;
    private static AlertDialog locationAlert = null;
    protected SharedPreferences prefs = null;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        BaseActivity.context = context;
    }

    public static boolean isFineLocationServiceEnabled(Context context) {
        boolean locationPermissionsGranted = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        final LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationPermissionsGranted && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isCoarseLocationServiceEnabled(Context context) {
        boolean locationPermissionsGranted = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        final LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationPermissionsGranted && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        getContext().startActivity(new Intent(android
                                .provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        locationAlert = builder.create();
        locationAlert.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //any activity which inherits now can use shared preferences
        prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        if (!Once.beenDone(Once.THIS_APP_INSTALL, "applyColor")) {
            prefs.edit().putString(COLOR, BLUE).apply();
            Once.markDone("applyColor");
        }

        setApplicationStyle();
        themeID = getThemeId();
    }

    private void setApplicationStyle() {
        //we check if the style was selected in settings:
        switch (prefs.getString(COLOR, "")) {
            case RED:
                setTheme(R.style.AppThemeRedAndBlue);
                break;
            case BLUE:
                setTheme(R.style.AppThemeBlueAndPink);
                break;
            case PURPLE:
                setTheme(R.style.AppThemePurpleAndGreen);
                break;
            case TEAL:
                setTheme(R.style.AppThemeTealAndLime);
                break;
            default:
                setTheme(R.style.AppThemeBlueAndPink);
                break;
        }
    }

    private int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationAlert != null) {
            locationAlert.dismiss();
        }
    }
}
