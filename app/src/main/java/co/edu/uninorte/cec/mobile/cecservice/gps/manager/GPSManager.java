package co.edu.uninorte.cec.mobile.cecservice.gps.manager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import co.edu.uninorte.cec.mobile.cecservice.core.PermissionsActivity;

/**
 * Created by Administrador on 29/04/2017.
 */

public class GPSManager extends LocationCallback
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleApiClient;
    Context serviceContext;
    GPSManagerCallerInterface caller;

    public GPSManager(Context serviceContextParameter,GPSManagerCallerInterface caller) {
        this.serviceContext = serviceContextParameter;
        this.caller=caller;
    }

    public void createGooglePlayServicesClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(serviceContext)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();

        } catch (Exception error) {
            Log.e("GPSManager", "createGooglePlayServicesClient: " + error.toString());
        }
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        caller.newLocationHasBeenReceived(locationResult.getLastLocation());
        super.onLocationResult(locationResult);
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        if(locationAvailability.isLocationAvailable()){
            this.caller.isLocationEnabled(true);
        }else{
            this.caller.isLocationEnabled(false);
        }
        super.onLocationAvailability(locationAvailability);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission
                (serviceContext,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(serviceContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {


            Intent intentToStartActivity=new Intent(this.serviceContext, PermissionsActivity.class);
            this.serviceContext.startActivity(intentToStartActivity);


            return;
        }else{
            startLocationUpdates();
        }


    }

    public void startLocationUpdates(){
        try {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setSmallestDisplacement(0);
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            LocationServices.
                    FusedLocationApi.
                    requestLocationUpdates(
                            this.googleApiClient, locationRequest, this, Looper.getMainLooper());
        }catch (Exception error){
            Log.e("GPSManager","startLocationUpdates: "+error.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
