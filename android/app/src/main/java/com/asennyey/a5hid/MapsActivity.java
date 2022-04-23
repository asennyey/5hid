package com.asennyey.a5hid;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.objects.read.Event;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.asennyey.a5hid.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private ApiController controller;

    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        controller = new ApiController(this);
        onLoginClick();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Request permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            // Main code
            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((task) -> {
                    String result = "";

                    if (task.isSuccessful()) {
                        // Task completed successfully
                        Location location = task.getResult();
                        result = "Location (success): " +
                                location.getLatitude() +
                                ", " +
                                location.getLongitude();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                ),
                                10
                        ));
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;
                    }
            });
        } else {
            // TODO: Request fine location permission
        }

        controller.getEvents(
                (page)->{
                    for(Event event: page.result.records) {
                        mMap.addMarker(new MarkerOptions().position(event.location).title(event.description));
                    }
                },
                (err)->{
                    System.out.println(err);
                }
        );
    }

    public void onLoginClick(){
        com.asennyey.a5hid.api.objects.write.User user = new com.asennyey.a5hid.api.objects.write.User();
        user.username = "asennyey@email.arizona.edu";
        user.password = "";
        controller.login(
                user,
                (res)->{
                    System.out.println(res);
                },
                (err)->{
                    System.out.println(err);
                }
        );
    }

    public void onCreateEventClick(){
        com.asennyey.a5hid.api.objects.write.Event event = new com.asennyey.a5hid.api.objects.write.Event();
        event.description = "test from android";
        event.location = new LatLng(1, 1);
        controller.createEvent(
                event,
                (res)->{
                    System.out.println(res);
                },
                (err)->{
                    System.out.println(err);
                }
        );
    }
}