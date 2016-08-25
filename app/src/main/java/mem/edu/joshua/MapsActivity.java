package mem.edu.joshua;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Build;
import android.os.TransactionTooLargeException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.zip.Inflater;

import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;
import mem.edu.joshua.sync.SyncAdapter;

public class MapsActivity extends Fragment {//implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MapsActivity.class.getSimpleName();

    private static final int CURSOR_LOADER_ID = 0;

    private Cursor initQueryCursor;
    private GetSet coord;
    private AppPreferences sPref;
    private LatLng home;
    private SupportMapFragment sMap;
    private FragmentManager fm;
    private GoogleMap mMap;
    private MapView mapView;
    private View v;

    private static final String FRAGMENT_LISTS =
                "net.simonvt.schematic.samples.ui.SampleActivity.LISTS";


    public MapsActivity() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = new AppPreferences(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initQueryCursor = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.URL, QuoteColumns.DISPLAY_PHONE, QuoteColumns.RATING_IMG,
                        QuoteColumns.ID_BUSINESS_NAME, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.POSTAL_CODE,
                        QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE}, null,
                null, null);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        mapView.onResume();
        mMap = mapView.getMap();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (initQueryCursor != null) {

            DatabaseUtils.dumpCursor(initQueryCursor);
            initQueryCursor.moveToFirst();


            for (int i = 0; i < initQueryCursor.getCount(); i++) {


                home = new LatLng(Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("latitude"))),
                        Double.valueOf(initQueryCursor.getString(initQueryCursor.getColumnIndex("longitude"))));
                Marker m = mMap.addMarker(new MarkerOptions().position(home).
                        title(initQueryCursor.getString(initQueryCursor.getColumnIndex("id_bussines_name"))));

                initQueryCursor.moveToNext();
                builder.include(home);
            }
            if (home != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 215, 210, 0));
            }
            initQueryCursor.close();

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                public void onInfoWindowClick(Marker marker) {


                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("title", marker.getTitle());
                    startActivity(intent);
                }

            });

        }

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.yelp_google_map, container, false);
        return v;
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return new CursorLoader(getActivity(), QuoteProvider.Quotes.CONTENT_URI,
//                new String[]{ QuoteColumns._ID, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.DISPLAY_PHONE,
//                        QuoteColumns.RATING, QuoteColumns.URL, QuoteColumns.POSTAL_CODE, QuoteColumns.ID_BUSINESS_NAME,
//                        QuoteColumns.LATITUDE,
//                        QuoteColumns.LOGITUDE},
//                null,
//                null,
//                null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//        if (loader != null) {
//            //     mMapCursor.swapCursor(data);
//            initQueryCursor = data;
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }

}
