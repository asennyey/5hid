package com.asennyey.a5hid;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.AuthenticationController;
import com.asennyey.a5hid.api.objects.read.Event;
import com.asennyey.a5hid.viewmodels.EventViewModel;
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
    private ApiController controller;

    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private LocationController locationController;
    private AuthenticationController authController;
    public boolean logged_in = false;
    public SupportMapFragment mapFragment;
    public Fragment otherFragment;
    public String STATE = "MAP";

    public Button settingsButton;
    public Button leaderboardButton;
    public Button helpButton;
    private boolean flag;

    private EventViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationController = LocationController.getInstance(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        controller = ApiController.getInstance(this);
        authController = AuthenticationController.getInstance(this);
        model = new ViewModelProvider(this).get(EventViewModel.class);
        model.getEvents().observe(this, events -> {
            if(mMap != null) {
                // update UI
                for (Event event : events) {
                    mMap.addMarker(new MarkerOptions().position(event.location).title(event.description));
                }
            }else {
                flag = true;
            }
        });
        settingsButton = findViewById(R.id.settings_button);
        leaderboardButton = findViewById(R.id.leaderboard_button);
        helpButton = findViewById(R.id.help_button);
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
        if(flag){
            flag = false;
            model.getEvents();
        }
        mMap = googleMap;
        if(locationController.isLocationEnabled()){
            mMap.setMyLocationEnabled(true);
            locationController.getPreciseLocation(cancellationTokenSource, (location)->{
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        location.result,
                        1
                ));
            }, (err)->{

            });
        }else{
            // TODO: request location.
        }
    }


    public void onToSettings(View v) {
        FragmentTransaction ft =
                getSupportFragmentManager().beginTransaction();

        if (STATE.equals("MAP")) {
            this.STATE = "SETTINGS";
            settingsButton.setText("Return");
            //mapFragment.getView().setVisibility(View.INVISIBLE);
            otherFragment = new Settings();
            //someFragment.setContainerActivity(this);
            //someFragment.setArguments(args);
            ft.replace(R.id.fragmentContainerView, new Settings());
            ft.commit();
        } else {
            this.STATE = "MAP";
            settingsButton.setText("Settings");
            otherFragment.onDestroy();
            ft.replace(R.id.fragmentContainerView, mapFragment);
            ft.commit();
        }
    }
    public void onToLeaderboard(View v) {
        FragmentTransaction ft =
                getSupportFragmentManager().beginTransaction();

        if (STATE.equals("MAP")) {
            this.STATE = "LEADER";
            leaderboardButton.setText("Return");
            //mapFragment.getView().setVisibility(View.INVISIBLE);
            //otherFragment.setContainerActivity(this);
            //otherFragment.setArguments(args);
            ft.replace(R.id.fragmentContainerView, leaderboard_list_fragment.class, null);
            ft.commit();
        } else {
            this.STATE = "MAP";
            leaderboardButton.setText("Leaderboard");
            ft.replace(R.id.fragmentContainerView, mapFragment, null);
            ft.commit();
        }
    }
    public void onToHelp(View v) {

    }

    // button will be within maps fragment
    public void onNewEvent(View V) {

    }
    //TODO: implement login/credentials feature

    //TODO: implment create_event_page

    public void onLoginClick(){
        com.asennyey.a5hid.api.objects.write.User user = new com.asennyey.a5hid.api.objects.write.User();
        user.username = "asennyey@email.arizona.edu";
        user.password = "Araclasen6893.";
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
        locationController.getPreciseLocation(cancellationTokenSource, (location)->{
            event.location = location.result;
            controller.createEvent(
                    event,
                    (res)->{
                        System.out.println(res);
                    },
                    (err)->{
                        System.out.println(err);
                    }
            );
        }, (err)->{
            System.out.println(err.result);
        });
    }
}