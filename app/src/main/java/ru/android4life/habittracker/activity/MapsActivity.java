package ru.android4life.habittracker.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.android4life.habittracker.R;
import ru.android4life.habittracker.views.RippleView;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AppCompatButton deleteMarkerButton;
    private DiscreteSlider discreteSlider;
    private RippleView markerBack;
    private RippleView markerConfirm;
    private Marker currentMarker;
    private boolean isMarkerVisible;
    private Circle currentCircle;
    private int habitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //FIXME: change when habit id is in intent
        //habitId = getIntent().getExtras().getInt(HABIT_ID);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        markerBack = (RippleView) findViewById(R.id.marker_back);
        markerConfirm = (RippleView) findViewById(R.id.marker_confirm);

        discreteSlider = (DiscreteSlider) findViewById(R.id.range_picker);
        deleteMarkerButton = (AppCompatButton) findViewById(R.id.delete_marker);
        discreteSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                setRange(position); //changes the circle radius if not null
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        //moves camera to current position
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude())));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker != null) currentMarker.remove();
                currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                isMarkerVisible = true;
                deleteMarkerButton.setVisibility(View.VISIBLE);
                setRange(discreteSlider.getPosition());
            }
        });

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

        markerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        markerConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: pass the settings of marker into corresponding habit
                finish();
            }
        });
    }

    private void addCircle(int meters) {
        if (currentMarker != null && isMarkerVisible) {
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

    private void setRange(int position) {
        switch (position) {
            case 0:
                addCircle(10);
                break;
            case 1:
                addCircle(25);
                break;
            case 2:
                addCircle(50);
                break;
            case 3:
                addCircle(100);
                break;
            case 4:
                addCircle(250);
                break;
        }
    }

}
