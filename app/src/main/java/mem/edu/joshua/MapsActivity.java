package mem.edu.joshua;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;

public class MapsActivity extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = MapsActivity.class.getSimpleName();

    private static final int CURSOR_LOADER_ID = 0;

    private Cursor mCursor;
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

//        initQueryCursor = getActivity().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//                new String[]{QuoteColumns.URL, QuoteColumns.DISPLAY_PHONE, QuoteColumns.RATING_IMG,
//                        QuoteColumns.ID_BUSINESS_NAME, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.POSTAL_CODE,
//                        QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE}, null,
//                null, null);
        //getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        mapView.onResume();
        mMap = mapView.getMap();
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }


//    mapView = (MapView) v.findViewById(R.id.map);
//    mapView.onCreate(savedInstanceState);
//    MapsInitializer.initialize(getActivity());
//    mapView.onResume();
//    mMap = mapView.getMap();
//
//    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//
//    if (mCursor != null) {
//
//        DatabaseUtils.dumpCursor(mCursor);
//        mCursor.moveToFirst();
//
//        for (int i = 0; i < mCursor.getCount(); i++) {
//            home = new LatLng(Double.valueOf(mCursor.getString(mCursor.getColumnIndex("latitude"))),
//                    Double.valueOf(mCursor.getString(mCursor.getColumnIndex("longitude"))));
//            Marker m = mMap.addMarker(new MarkerOptions().position(home).
//                    title(mCursor.getString(mCursor.getColumnIndex("id_bussines_name"))));
//
//            mCursor.moveToNext();
//            builder.include(home);
//        }
//        if (home != null) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 215, 210, 0));
//        }
//        mCursor.close();
//
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//
//            public void onInfoWindowClick(Marker marker) {
//
//                View v = getActivity().getLayoutInflater()
//                        .inflate(R.layout.marker_layout, null);
//                TextView textView = (TextView) v.findViewById(R.id.finfo_w_id);
//                textView.setText(marker.getTitle());
//                textView.setContentDescription(marker.getTitle());
//
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra("title", marker.getTitle());
//                startActivity(intent);
//            }
//        });
//    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.yelp_google_map, container, false);
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return  new CursorLoader(getActivity(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.RATING, QuoteColumns.URL, QuoteColumns.DISPLAY_PHONE, QuoteColumns.RATING_IMG,
                        QuoteColumns.ID_BUSINESS_NAME, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.POSTAL_CODE,
                        QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE, QuoteColumns.FLAG_A,QuoteColumns.FLAG_B},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor!=null){

            cursor.moveToFirst();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < cursor.getCount(); i++) {
                drawMarker(
                        cursor.getString(cursor.getColumnIndex("latitude")),
                        cursor.getString(cursor.getColumnIndex("longitude")),
                        cursor.getString(cursor.getColumnIndex("id_bussines_name"))
                );
                builder.include(home);
                cursor.moveToNext();
            }
            if (home != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 60));
            }
            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    private void drawMarker(String lat, String lon, String title) {

        home = new LatLng(Double.valueOf(lat),
                Double.valueOf(lon));
        Marker m = mMap.addMarker(new MarkerOptions().position(home).
                title(title));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            public void onInfoWindowClick(Marker marker) {

                View v = getActivity().getLayoutInflater()
                        .inflate(R.layout.marker_layout, null);
                TextView textView = (TextView) v.findViewById(R.id.finfo_w_id);
                textView.setText(marker.getTitle());
                textView.setContentDescription(marker.getTitle());

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("title", marker.getTitle());
                startActivity(intent);
            }
        });
    }


}
