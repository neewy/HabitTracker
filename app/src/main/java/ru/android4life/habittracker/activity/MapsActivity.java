package ru.android4life.habittracker.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RelativeLayout;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.db.dataaccessobjects.HabitDAO;
import ru.android4life.habittracker.db.tablesrepresentations.Habit;
import ru.android4life.habittracker.views.RippleView;

import static ru.android4life.habittracker.utils.StringConstants.HABIT_ID;
import static ru.android4life.habittracker.utils.StringConstants.LATITUDE;
import static ru.android4life.habittracker.utils.StringConstants.LONGITUDE;
import static ru.android4life.habittracker.utils.StringConstants.POSITION;
import static ru.android4life.habittracker.utils.StringConstants.RANGE;

/**
 * User can select the place on map
 * to perform the habit in specified range
 * via MapsActivity
 */

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    public static final int FIRST_RANGE = 10;
    public static final int SECOND_RANGE = 25;
    public static final int THIRD_RANGE = 50;
    public static final int FOURTH_RANGE = 100;
    public static final int FIFTH_RANGE = 250;


    private GoogleMap mMap;

    private RelativeLayout mapWrapper;

    private RippleView back;
    private RippleView confirm;

    private AppCompatButton deleteMarkerButton;
    private DiscreteSlider rangeSlider;

    private Marker currentMarker;
    private boolean isMarkerVisible;
    private Circle currentCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapWrapper = (RelativeLayout) findViewById(R.id.map_wrapper);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        back = (RippleView) findViewById(R.id.marker_back);
        confirm = (RippleView) findViewById(R.id.marker_confirm);

        rangeSlider = (DiscreteSlider) findViewById(R.id.range_picker);
        deleteMarkerButton = (AppCompatButton) findViewById(R.id.delete_marker);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        int habitId = getIntent().getExtras().getInt(HABIT_ID, -1);

        Location myLocation = getLastKnownLocation();

        if (habitId != -1) {
            HabitDAO habitDAO = new HabitDAO(this);
            Habit habit = (Habit) habitDAO.findById(habitId);

            if (isPositionNone(habit)) {
                if (myLocation != null) {
                    moveCameraToLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                }
            } else {
                placeMarker(new LatLng(habit.getLatitude(), habit.getLongitude()));
                setRangeByMeters((int) habit.getRange());
                moveCameraToLatLng(currentMarker.getPosition());
            }
        } else {
            if (myLocation != null) {
                moveCameraToLatLng(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            }
        }

        setupControls();
    }

    private void moveCameraToLatLng(LatLng latLng) {

        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    private boolean isPositionNone(Habit habit) {
        return habit.getLongitude() == 0 && habit.getLongitude() == 0 && habit.getRange() == 0;
    }

    private void placeMarker(LatLng latLng) {
        if (currentMarker != null) currentMarker.remove();
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        isMarkerVisible = true;
        deleteMarkerButton.setVisibility(View.VISIBLE);
        setRangeBySlider(rangeSlider.getPosition());
    }


    private void addCircle(int meters) {
        if (isMarkerVisible) {
            CircleOptions circleOptions = new CircleOptions()
                    .center(currentMarker.getPosition())
                    .radius(meters)
                    .strokeColor(Color.BLUE)
                    .fillColor(Color.argb(100, 88, 87, 255));

            if (currentCircle != null) {
                currentCircle.remove();
            }

            currentCircle = mMap.addCircle(circleOptions);
        }
    }

    private void setRangeByMeters(int meters) {
        switch (meters) {
            case FIRST_RANGE:
                setRangeBySlider(0);
                break;
            case SECOND_RANGE:
                setRangeBySlider(1);
                break;
            case THIRD_RANGE:
                setRangeBySlider(2);
                break;
            case FOURTH_RANGE:
                setRangeBySlider(3);
                break;
            case FIFTH_RANGE:
                setRangeBySlider(4);
                break;
            default:
                setRangeBySlider(1);
                break;
        }
    }

    private void setRangeBySlider(int position) {
        switch (position) {
            case 0:
                addCircle(FIRST_RANGE);
                break;
            case 1:
                addCircle(SECOND_RANGE);
                break;
            case 2:
                addCircle(THIRD_RANGE);
                break;
            case 3:
                addCircle(FOURTH_RANGE);
                break;
            case 4:
                addCircle(FIFTH_RANGE);
                break;
        }
    }

    private int getRange() {
        switch (rangeSlider.getPosition()) {
            case 0:
                return FIRST_RANGE;
            case 1:
                return SECOND_RANGE;
            case 2:
                return THIRD_RANGE;
            case 3:
                return FOURTH_RANGE;
            case 4:
                return FIFTH_RANGE;
        }
        return -1;
    }

    private void setupControls() {
        // if we delete marker from map
        deleteMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMarker != null) {
                    currentMarker.remove();
                    isMarkerVisible = false;
                }
                if (currentCircle != null) currentCircle.remove();
                deleteMarkerButton.setVisibility(View.GONE);
            }
        });

        // if we change the range
        rangeSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                setRangeBySlider(position); //changes the circle radius if not null
            }
        });

        // if we go back
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //if we confirm the selection
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent positionData = new Intent();
                if (isMarkerVisible) {
                    positionData.putExtra(POSITION, true);
                    positionData.putExtra(LATITUDE, currentMarker.getPosition().latitude);
                    positionData.putExtra(LONGITUDE, currentMarker.getPosition().longitude);
                    positionData.putExtra(RANGE, getRange());
                } else {
                    // if nothing was selected
                    positionData.putExtra(POSITION, false);
                }

                setResult(Activity.RESULT_OK, positionData);
                finish();
            }
        });

        //if we click on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                placeMarker(latLng);
            }
        });
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
