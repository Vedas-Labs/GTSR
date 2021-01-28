package com.gtsr.gtsr.loginModule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gtsr.gtsr.R;
import com.gtsr.gtsr.WifiReceiver;

import java.security.MessageDigest;

public class SplashScreen extends AppCompatActivity {
    private static final int REQUEST = 112;
    final static int REQUEST_LOCATION = 199;
    Context mContext = this;
    Handler handler;
    WifiReceiver exampleBroadcastReceiver = new WifiReceiver();

    // private GoogleApiClient googleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_scree);
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            Log.e("moves", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");
            String[] PERMISSIONS = {
                   // Manifest.permission.READ_PHONE_STATE,
                  //  Manifest.permission.ACCESS_NETWORK_STATE,
                 //   Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
            };
            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.e("ccada", "cc");
                //enableLoc();
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);
            } else {
                checker();
            }
        } else {
            Log.e("ccada", "ccc");
            // startActivity(new Intent(getApplicationContext(), LoginViewController.class));
            checker();

        }
    }
    protected void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(exampleBroadcastReceiver, filter);
    }
    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(exampleBroadcastReceiver);
    }
    public void checkAppFlow() {
        //Get User Location
        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();
        // GET USer Informaion.

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("starting", "startLogin1");
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        // clear the notification area when the app is opened
    }

////////////Multiple Premession and alerts//////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ////// callNextActivity///////////
                    checkAppFlow();
                } else {
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checker() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("ddfsd", "da");
            //  StartAnimations();
            checkAppFlow();
        } else {
            Log.e("ddfsd", "da1");
          //  enableLoc();
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("allenable", "startLogin1");
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));

                }
            }, 1000);
        }
    }

  /*  //// Below 6.0.1 Laction can be on /////
    private void enableLoc() {
        if (googleApiClient == null) {
            Log.e("flow", "flow :");
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            Log.e("checkk", "flow1");
                            // StartAnimations();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.e("checkk", "flow2");
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.e("Location error", "Location error " + connectionResult.getErrorCode());

                            Log.e("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            builder.setAlwaysShow(true); //this is the key ingredient
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                Log.e("eee", "status");

                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SplashScreen.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e("eee", "aa" + e.getMessage());
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        finish();
                        break;
                    }
                    case Activity.RESULT_OK: {
                        // The user was asked to change settings, but chose not to
                        checkAppFlow();

                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }

    @Override
    public void onBackPressed() {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
