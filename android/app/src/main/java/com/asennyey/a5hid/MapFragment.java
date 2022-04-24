package com.asennyey.a5hid;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asennyey.a5hid.api.ApiController;
import com.asennyey.a5hid.api.AuthenticationController;
import com.asennyey.a5hid.api.objects.read.Event;
import com.asennyey.a5hid.viewmodels.EventViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationTokenSource;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        locationController = LocationController.getInstance();
        model = new ViewModelProvider(this).get(EventViewModel.class);
        model.getEvents().observe(this, events -> {
            if(mMap != null) {
                // update UI
                for (Event event : events) {
                    System.out.println(event);
                    mMap.addMarker(new MarkerOptions().position(event.location).title(event.description)).setTag(event);
                }
            }else {
                flag = true;
            }
        });
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
}