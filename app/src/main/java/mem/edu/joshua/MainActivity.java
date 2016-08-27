package mem.edu.joshua;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.Fragment;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


import mem.edu.joshua.data.QuoteProvider;
import mem.edu.joshua.sync.SyncAdapter;

/**
 * Created by erikllerena on 8/14/16.
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private GetSet coord = new GetSet();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private AppPreferences sPref;
    private Fragment fragment;
    private ContentObserver mObserver;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int MY_PERMISSION_REQUEST_COARSE_LOCATION = 102;
    private boolean permissionIsGranted=false;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
        }

    boolean isConnected;


    public static final String ACTION_FINISHED_SYNC = "mem.edu.joshua.ACTION_FINISHED_SYNC";
    private static IntentFilter syncIntentFilter = new IntentFilter(ACTION_FINISHED_SYNC);

    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            setContentView(R.layout.yelp_google_map);

            fragment = Fragment.instantiate(getBaseContext(), MapsActivity.class.getName());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.map, fragment);
            ft.commit();
        }
    };

    @Override protected void onResume() {
        super.onResume();
        registerReceiver(syncBroadcastReceiver, syncIntentFilter);
    }

    @Override protected void onPause() {
        unregisterReceiver(syncBroadcastReceiver);
        super.onPause();
    };

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("go", permissionIsGranted);
////        fragment.onSaveInstanceState(outState);
//    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Toast.makeText(this, getString(R.string.low_mem), Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        permissionIsGranted=savedInstanceState.getBoolean("go");
//        //savedInstanceState.putBoolean("go", false);
//
//
//    }




    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected){

            sPref = new AppPreferences(getBaseContext());

            buildGoogleApiClient();


        }else {
            Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_LONG).show();
        }
        sPref = new AppPreferences(getBaseContext());
    }

    @Override
    protected void onStart() {

            if (isConnected) {
                mGoogleApiClient.connect();
            }

        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(isConnected) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, location.toString());

        if(permissionIsGranted){
            coord.setLon(location.getLongitude());
            coord.setLat(location.getLatitude());

            sPref.saveCoordBody("lat", coord.getLat());
            sPref.saveCoordBody("lon", coord.getLon());
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        int interval = 1000*60*60*2;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(interval);

        try {
            requestLocationUpdates();

            if(permissionIsGranted) {
                SyncAdapter.initializeSyncAdapter(getBaseContext());
                if (mLocationRequest != null) {
                    Log.e(LOG_TAG, "google working");

                    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    String provider = service.getBestProvider(criteria, false);
                    Location location = service.getLastKnownLocation(provider);

                    coord.setLon(location.getLongitude());
                    coord.setLat(location.getLatitude());

                    sPref.saveCoordBody("lat", coord.getLat());
                    sPref.saveCoordBody("lon", coord.getLon());

                    SyncAdapter.syncImmediately(getBaseContext(),
                            sPref.getCoordBody("lat"),
                            sPref.getCoordBody("lon"));
                }
            }

        }catch  (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
               // requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_COARSE_LOCATION);
            }
            else {
                permissionIsGranted=true;
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            return;
        }
        permissionIsGranted=true;
        Toast.makeText(this, getString(R.string.loading), Toast.LENGTH_LONG).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                    if(grantResults[0]== getPackageManager().PERMISSION_GRANTED){
                        permissionIsGranted=true;
                        Toast.makeText(this, getString(R.string.loading), Toast.LENGTH_SHORT).show();

                    }else {
                        permissionIsGranted=false;
                        Toast.makeText(getBaseContext(), R.string.no_permission, Toast.LENGTH_SHORT).show();
                    }
            case MY_PERMISSION_REQUEST_COARSE_LOCATION:
                if(grantResults[0]== getPackageManager().PERMISSION_GRANTED){
                    permissionIsGranted=true;
                }else {
                    permissionIsGranted=false;
                    Toast.makeText(getBaseContext(), R.string.no_permission, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}