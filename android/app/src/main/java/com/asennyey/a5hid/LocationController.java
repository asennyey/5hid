package com.asennyey.a5hid;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.asennyey.a5hid.api.Callback;
import com.asennyey.a5hid.api.Result;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

public class LocationController {
    private static LocationController instance;
    private Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    public LocationController(Context context){
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }
    public void getPreciseLocation(CancellationTokenSource cancelTokenSource, Callback<LatLng> onSuccess, Callback<Exception> onError){
        // Request permission
        if (isLocationEnabled()) {
            // Main code
            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancelTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    // Task completed successfully
                    Location location = task.getResult();
                    context.getMainExecutor().execute(()->onSuccess.onResult(
                            new Result<>(
                                    new LatLng(
                                            location.getLatitude(),
                                            location.getLongitude()
                                    )
                            ))
                    );
                } else {
                    // Task failed with an exception
                    Exception exception = task.getException();
                    context.getMainExecutor().execute(()->onError.onResult(new Result<>(exception)));
                }
            });
        } else {
            // TODO: Request fine location permission
        }
    }

    public boolean isLocationEnabled(){
        // Request permission
        return ActivityCompat.checkSelfPermission(
                this.context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(){

    }

    public static LocationController getInstance(Context context){
        if(instance == null){
            instance = new LocationController(context);
        }
        return instance;
    }
}
