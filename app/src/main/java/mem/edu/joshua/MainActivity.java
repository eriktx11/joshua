package mem.edu.joshua;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseOptions;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


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
    private Location location;
    private MapCursor mMapCursor;
    protected Location mLastLocation;
    private AppPreferences sPref;
    private Fragment fragment;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
        }

    Boolean go=false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("go", true);
        fragment.onSaveInstanceState(outState);
    }


    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.yelp_google_map);
        fragment = Fragment.instantiate(this, MapsActivity.class.getName());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map, fragment);
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        fragment.onPause();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        fragment.onLowMemory();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        go=savedInstanceState.getBoolean("go");
        savedInstanceState.putBoolean("go", false);


    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        sPref = new AppPreferences(getBaseContext());

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
        mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, location.toString());

    coord.setLon(location.getLongitude());
    coord.setLat(location.getLatitude());

    sPref.saveCoordBody("lat", coord.getLat());
    sPref.saveCoordBody("lon", coord.getLon());

    SyncAdapter.initializeSyncAdapter(this);

        }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000);


        try {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, (LocationListener) this);

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
            }

        }catch  (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}