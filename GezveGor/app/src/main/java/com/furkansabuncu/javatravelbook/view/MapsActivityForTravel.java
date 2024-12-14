package com.furkansabuncu.javatravelbook.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.furkansabuncu.javatravelbook.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.furkansabuncu.javatravelbook.databinding.ActivityMapsForTravelBinding;

public class MapsActivityForTravel extends FragmentActivity implements OnMapReadyCallback {

    // Define latitude and longitude variables as class fields
    private Double latitude;
    private Double longitude;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.furkansabuncu.javatravelbook.databinding.ActivityMapsForTravelBinding binding = ActivityMapsForTravelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();

        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        placeName=intent.getStringExtra("place");



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker in a known location.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        LatLng location = new LatLng(latitude,longitude);


        googleMap.addMarker(new MarkerOptions().position(location).title(placeName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15)); // Zoom level set to 10


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                // Set the data of the intent as the coordinates of the clicked location
                intent.setData(Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude));

                // Set the package of the intent as the Google Maps package
                intent.setPackage("com.google.android.apps.maps");

                // Start the intent
                startActivity(intent);
            }
        });





    }


}
