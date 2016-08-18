package mem.edu.joshua;



import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
//import android.app.FragmentTransaction;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;

//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;
import mem.edu.joshua.sync.SyncAdapter;

public class MapsActivity extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
//OnMapReadyCallback,
    //GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
    public static final String LOG_TAG = MapsActivity.class.getSimpleName();

    private static final int CURSOR_LOADER_ID = 0;

    private Context mContext;


    private MapCursor mMapCursor;
    protected Location mLastLocation;
    private Cursor initQueryCursor;
    private GetSet coord;
    private AppPreferences sPref;
    private LatLng home;
    private SupportMapFragment sMap;
    private FragmentManager fm;
    private GoogleMap mMap;
    MapView mMapView;
    MapView m;
    private GoogleMap googleMap;

        private static final String FRAGMENT_LISTS =
                "net.simonvt.schematic.samples.ui.SampleActivity.LISTS";

//    public static interface Callbacks {
//        void onMyContainerAttached();
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Log.d(LOG_TAG, "--- onAttach");
//        ((Callbacks) activity).onMyContainerAttached();
//    }


    public MapsActivity() {
    }

//    public interface Callback {
//        public void onItemSelected(Uri dateUri);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//        mGoogleApiClient.connect();

        //mContext = this;


        //Activity activity=new Activity();

        sPref = new AppPreferences(getActivity());

        SyncAdapter.syncImmediately(getActivity(),
                sPref.getCoordBody("lat"),
                sPref.getCoordBody("lon"));


        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
      //  getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

       // mMapCursor = new MapCursor(getActivity(), null, 0);


//        ConnectivityManager cm =
//                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


//        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);


    }

//    @Override
//    public void onDestroyView()
//    {
//        super.onDestroyView();
//        Fragment fragment = (getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ft.remove(fragment);
//        ft.commitAllowingStateLoss();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        View v = inflater.inflate(R.layout.yelp_google_map, container, false);

        fm=getChildFragmentManager();

        sMap = ((SupportMapFragment) fm.findFragmentById(R.id.map));

        mMap = sMap.getMap();


        initQueryCursor = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE, QuoteColumns.ID_BUSINESS_NAME}, null,
                null, null);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (initQueryCursor != null) {

            DatabaseUtils.dumpCursor(initQueryCursor);
            initQueryCursor.moveToFirst();
            for (int i = 0; i < initQueryCursor.getCount(); i++) {

                home = new LatLng(Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("latitude"))),
                        Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("longitude"))));
                mMap.addMarker(new MarkerOptions().position(home).
                        title(initQueryCursor.getString(initQueryCursor.getColumnIndex("id_bussines_name"))));

                initQueryCursor.moveToNext();
                builder.include(home);
            }
            if(home!=null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 215, 210, 0));
            }
        }

        return v;

    }


        @Override
        public void onResume() {
            super.onResume();
            sMap.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            sMap.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            sMap.onDestroy();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
            sMap.onLowMemory();
        }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {

//        View rootView = inflater.inflate(R.layout.yelp_map, container, false);
//        mapView = (MapView) rootView.findViewById(R.id.map);
//        mapView.getMap();
//
//
//        mMap = ((MapView) rootView.findViewById(R.id.fragment_yelp)).getMap();
//
//
//        View rootView = inflater.inflate(R.layout.yelp_google_map, container, false);
//        mMap = ((MapView)rootView.findViewById(R.id.map)).getMap();

        //mMapView = (MapView) rootView.findViewById(R.id.map);

//        mMapView.onCreate(savedInstanceState);
//        mMapView.onResume();
//
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMap();



//        try {
//            MapsInitializer.initialize(getActivity());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mapView.getMapAsync(new OnMapReadyCallback() {
//
////            setContentView(R.layout.yelp_google_map);
////            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
////                    .findFragmentById(R.id.map);
////            mapFragment.getMapAsync(this);
////
//            @Override
//            public void onMapReady(GoogleMap mMap) {
//
//                googleMap = mMap;
//
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//
//                if (initQueryCursor != null) {
//
//                    DatabaseUtils.dumpCursor(initQueryCursor);
//                    initQueryCursor.moveToFirst();
//                    for (int i = 0; i < initQueryCursor.getCount(); i++) {
//
//                        home = new LatLng(Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("latitude"))),
//                                Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("longitude"))));
//                        mMap.addMarker(new MarkerOptions().position(home).
//                                title(initQueryCursor.getString(initQueryCursor.getColumnIndex("id_bussines_name"))));
//
//                        initQueryCursor.moveToNext();
//
//                        builder.include(home);
//                    }
//                    if(home!=null) {
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
//                    }
//                }
//
//
//            }
//        });


//
//        return rootView;
//    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//
//        super.onActivityCreated(savedInstanceState);
//    }



//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
////        try {
////
////            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
////
////            if (mLastLocation != null) {
////                latitude=mLastLocation.getLatitude();
////                longitude=mLastLocation.getLongitude();
////            }
////
////        }catch  (SecurityException e) {
////            Log.e(LOG_TAG, e.getMessage(), e);
////            e.printStackTrace();
////        }
//
//        //SyncAdapter.syncImmediately(mContext, latitude, longitude);
//
//        //return null;
//
////        return initQueryCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
////                new String[]{QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE, QuoteColumns.ID_BUSINESS_NAME}, null,
////                null, null);
//
//
//
//
////        return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
////                new String[]{ QuoteColumns._ID, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.DISPLAY_PHONE,
////                        QuoteColumns.RATING, QuoteColumns.URL, QuoteColumns.POSTAL_CODE, QuoteColumns.ID_BUSINESS_NAME,
////                        QuoteColumns.LATITUDE,
////                        QuoteColumns.LOGITUDE},
////                null,
////                null,
////                null);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.DISPLAY_PHONE,
                        QuoteColumns.RATING, QuoteColumns.URL, QuoteColumns.POSTAL_CODE, QuoteColumns.ID_BUSINESS_NAME,
                        QuoteColumns.LATITUDE,
                        QuoteColumns.LOGITUDE},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (loader != null) {
            //     mMapCursor.swapCursor(data);
            initQueryCursor = data;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//        mMap = googleMap;
//
//        initQueryCursor = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//                new String[]{QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE, QuoteColumns.ID_BUSINESS_NAME}, null,
//                null, null);
//
//
//        if (initQueryCursor != null) {
//
//            DatabaseUtils.dumpCursor(initQueryCursor);
//            initQueryCursor.moveToFirst();
//            for (int i = 0; i < initQueryCursor.getCount(); i++) {
//
//                home = new LatLng(Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("latitude"))),
//                        Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("longitude"))));
//                mMap.addMarker(new MarkerOptions().position(home).
//                        title(initQueryCursor.getString(initQueryCursor.getColumnIndex("id_bussines_name"))));
//
//                initQueryCursor.moveToNext();
//
//                builder.include(home);
//            }
//            if(home!=null) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 91, 91, 42));
//            }
//        }
//    }
}
