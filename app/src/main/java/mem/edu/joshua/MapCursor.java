package mem.edu.joshua;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;

/**
 * Created by erikllerena on 8/14/16.
 */
public class MapCursor extends CursorAdapter {

    private LatLng home;
    private GoogleMap mMap;
    Cursor initQueryCursor;


    public static class ViewHolder {
       // public final GoogleMap map;


        public ViewHolder(View view) {
           // map = ((MapView) view.findViewById(R.id.map)).getMap();
        }
    }

    public MapCursor(Context context, Cursor c, int flags) {
        super(context, c, flags);

        initQueryCursor = c;

        //setContentView(R.layout.activity_maps);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


//        mMap = googleMap;
//
//        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
//                .getMap();



        //initQueryCursor=cursorAdapter.getCursor();

//        initQueryCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
//                new String[]{QuoteColumns.LATITUDE, QuoteColumns.LOGITUDE, QuoteColumns.ID_BUSINESS_NAME}, null,
//                null, null);


        int layoutId = -1;
        //layoutId = R.layout.yelp_map;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        initQueryCursor=cursor;

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            }
        }


    }
}
