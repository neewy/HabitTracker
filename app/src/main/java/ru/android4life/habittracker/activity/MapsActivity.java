package ru.android4life.habittracker.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.etiennelawlor.discreteslider.library.ui.DiscreteSlider;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ru.android4life.habittracker.R;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DiscreteSlider discreteSlider;
    private Marker currentMarker;
    private Circle currentCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        discreteSlider = (DiscreteSlider) findViewById(R.id.range_picker);
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker != null) currentMarker.remove();
                currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                setRange(discreteSlider.getPosition());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                AlertDialog.Builder markerDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                markerDialogBuilder.setTitle(R.string.marker_delete);
                markerDialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove();
                        dialog.cancel();
                    }
                });
                markerDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog markerDialog = markerDialogBuilder.create();

                markerDialog.show();
                return true;
            }
        });
    }

    private void addCircle(int meters) {
        if (currentMarker != null) {
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
                addCircle(1);
                break;
            case 1:
                addCircle(10);
                break;
            case 2:
                addCircle(50);
                break;
            case 3:
                addCircle(100);
                break;
            case 4:
                addCircle(500);
                break;
        }
    }

}
