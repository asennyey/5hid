package com.asennyey.a5hid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.AuthenticationController;
import com.asennyey.a5hid.api.objects.read.Event;
import com.asennyey.a5hid.viewmodels.EventViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();

    private EventViewModel model;
    private GoogleMap mMap;
    private LocationController locationController;

    private SupportMapFragment mapFragment;

    private boolean flag = false;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        locationController = LocationController.getInstance();
        model = new ViewModelProvider(this).get(EventViewModel.class);
        model.getData().observe(this, events -> {
            System.out.println(mMap);
            if(mMap != null) {
                // update UI
                for (Event event : events) {
                    addMarker(event);
                }
            }else {
                flag = true;
            }
        });
    }

    private void addMarker(Event event){
        Marker marker = mMap.addMarker(new MarkerOptions().position(event.location).title(event.description));
        if(event.user.isFriend){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }else if(event.user.name.equals("You")){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        }
        marker.setTag(event);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        view.findViewById(R.id.create_event_trigger).setOnClickListener((v)->{
            if(AuthenticationController.getInstance().isLoggedIn()) {
                new CreateEventFragment().show(getChildFragmentManager(), "create-event");
            }else{
                new LoginFragment().show(getChildFragmentManager(), "login");
            }
        });
        return view;
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
        if(flag){
            flag = false;
            for (Event event : model.getData().getValue()) {
                System.out.println(event);
                addMarker(event);
            }
        }
        mMap.setPadding(0, 0, 0, this.getActivity().findViewById(R.id.create_event_trigger).getHeight());
        if(locationController.isLocationEnabled()){
            mMap.setMyLocationEnabled(true);
            locationController.getPreciseLocation(cancellationTokenSource, (location)->{
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        location.result,
                        5
                ));
            }, (err)->{

            });
        }else{
            // TODO: request location.
        }

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Event event = (Event) marker.getTag();
        System.out.println(event);
        // Check if a click count was set, then display the click count.
        if (event != null && event.user.email != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            LatLng pos = marker.getPosition();
            intent.setType("vnd.android.cursor.dir/email");
            intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { event.user.email });
            intent.putExtra(Intent.EXTRA_TEXT, "Let's start a game of thrones at: " + pos.longitude + "," + pos.latitude );
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
}