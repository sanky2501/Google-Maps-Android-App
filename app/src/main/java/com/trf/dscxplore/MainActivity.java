package com.trf.dscxplore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    //vars
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //widgets
    ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        if(isServicesOk()){
            getLocationPermission();
        }

    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting location permissions.");
        //get permissions from the user implicitly
        String[] permissons = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                //permissions have been granted already and we are good to go
                mLocationGranted = true;
                Log.d(TAG, "getLocationPermission: Permissions have been granted.");
                //TODO:Good to go... Pass na intent to another activity and hide the progress bar
                showMapActivity(mLocationGranted);

            }else{
                //we need to take the permissions and onRequestPermissionsResult will be called
                ActivityCompat.requestPermissions(this,
                        permissons,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            //we need to take the permissions and onRequestPermissionsResult will be called
            ActivityCompat.requestPermissions(this,
                    permissons,
                    LOCATION_PERMISSION_REQUEST_CODE);}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Taking permissions implicitly.");
        mLocationGranted = false;
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length >0){
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            mLocationGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permissions were not granted.");
                            return;
                        }
                    }
                    mLocationGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permissions have been granted.");
                    //init our map
                    //TODO:Good to go... Pass na intent to another activity and hide the progress bar
                        showMapActivity(mLocationGranted);
                }
            }
        }
    }

    private void showMapActivity(final Boolean  b){
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this,MapActivity.class);
                        intent.putExtra("mLocationGranted",b);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        startActivity(intent);
                        finish();
                    }
                }
       ,1500 );
    }

    public boolean isServicesOk(){
        Log.d(TAG, "isServicesOk: Cheacking Google Services version.");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available== ConnectionResult.SUCCESS){
            //everything is fine and user can amke map request
            Log.d(TAG, "isServicesOk: Google Play Services is wroking");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOk: an error occured but we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "You cant make map request.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
