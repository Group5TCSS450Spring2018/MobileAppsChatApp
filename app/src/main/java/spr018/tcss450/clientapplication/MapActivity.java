package spr018.tcss450.clientapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";

    private GoogleMap mGoogleMap;
    private double mLat, mLng;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mPrefs =
                Objects.requireNonNull(getApplication()).getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        mLat = getIntent().getDoubleExtra(LATITUDE, 0.0);
        mLng = getIntent().getDoubleExtra(LONGITUDE, 0.0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Hardcode - Add a marker in Tacoma, WA, and move the camera.
        //LatLng latLng = new LatLng(47.2529, -122.4443);
        LatLng latLng = new LatLng(mLat, mLng);
        mGoogleMap.addMarker(new MarkerOptions().
                position(latLng).
                title("Marker in Tacoma"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        mGoogleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("NEW MARKER. LAT/LONG", latLng.toString());
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
    }
}
