package com.trf.dscxplore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {



    //vars
    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 15;

    //google
    private Boolean mLocationGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location currentLocation;

    //widgets
    TextView address;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        address = findViewById(R.id.address);
        initMap();
        Intent intent = getIntent();
        mLocationGranted = intent.getBooleanExtra("mLocationGranted",false);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Toast.makeText(this, "Map is ready.", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
        !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        getDeviceLocation();
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    private void initMap(){
        //to initialize the map if permissions are granted
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting device current location.");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(
                        new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG, "onComplete: Found Location.");
                                    currentLocation = (Location)task.getResult();
                                    addMarker(currentLocation);
                                    moveCamera(new LatLng(currentLocation.getLatitude()
                                            ,currentLocation.getLongitude()),DEFAULT_ZOOM);



                                }
                                else{
                                    Log.d(TAG, "onComplete: Current Location is null.");
                                    Toast.makeText(MapActivity.this,
                                            "Cannot get current Location.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }

        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: Security Exception.",e );
        }

    }

    private void addMarker(Location currentLocation){
        Toast.makeText(this, getAddress(), Toast.LENGTH_LONG).show();
        address.setText(getAddress());
        LatLng sydney = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marking You"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        moveCamera(sydney,DEFAULT_ZOOM);
    }

    private void moveCamera(LatLng latLng,float zoom){
        Log.d(TAG, "moveCamera: moving the camera to lat: "+latLng.latitude+
                " long: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    private String getAddress() {
        Log.d(TAG, "getAddress: Called.");
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), 1);
            if (addresses != null) {

                strAdd = addresses.get(0).getLocality();//gives the city
                //Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                //Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;

    }
}
