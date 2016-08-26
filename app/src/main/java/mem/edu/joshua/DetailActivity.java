package mem.edu.joshua;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;

/**
 * Created by erikllerena on 8/18/16.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static String visiting;
    private static String callingout;
    private static final int DETAIL_LOADER = 0;
    Cursor c;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setContentView(R.layout.detail_view);
            Intent intent = getIntent();
            Bundle bundle = new Bundle();
            bundle.putString("title", intent.getStringExtra("title"));

            getSupportLoaderManager().initLoader(DETAIL_LOADER, bundle, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        Intent intent = getIntent();
        savedInstanceState.putString("title", intent.getStringExtra("title"));
        super.onSaveInstanceState(savedInstanceState);
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        setContentView(R.layout.detail_view);
        getSupportLoaderManager().restartLoader(DETAIL_LOADER, savedInstanceState, this);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] criteria = {args.getString("title")};
        String SELECTION = "id_bussines_name=?";

        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns._ID, QuoteColumns.DISPLAY_ADDRESS, QuoteColumns.DISPLAY_PHONE,
                        QuoteColumns.RATING, QuoteColumns.URL, QuoteColumns.POSTAL_CODE, QuoteColumns.ID_BUSINESS_NAME,
                        QuoteColumns.LATITUDE,QuoteColumns.RATING_IMG,
                        QuoteColumns.LOGITUDE},
                SELECTION ,
                criteria,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {


            data.moveToFirst();

            TextView textTitle = (TextView)findViewById(R.id.name_txt_id);
            TextView textWebsite = (TextView)findViewById(R.id.website_txt_id);
            ImageView rateImg = (ImageView)findViewById(R.id.rate_img_id);
            rateImg.setContentDescription(getBaseContext().getString(R.string.img_desc));
            TextView textAddress = (TextView)findViewById(R.id.address_txt_id);
            TextView textCity = (TextView)findViewById(R.id.city_txt_id);
            TextView textPhone = (TextView)findViewById(R.id.phone_txt_id);

            visiting = data.getString(data.getColumnIndex("url"));
            textTitle.setText(data.getString(data.getColumnIndex("id_bussines_name")));
            textTitle.setContentDescription(data.getString(data.getColumnIndex("id_bussines_name")));
            textWebsite.setText(R.string.website);
            textWebsite.setContentDescription(getBaseContext().getString(R.string.website));
            textWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(visiting);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
            });
            textAddress.setText(data.getString(data.getColumnIndex("display_address")));
            textAddress.setContentDescription(data.getString(data.getColumnIndex("display_address")));
            textCity.setText(data.getString(data.getColumnIndex("postal_code")));
            textCity.setContentDescription(data.getString(data.getColumnIndex("postal_code")));
            textPhone.setText(data.getString(data.getColumnIndex("display_phone")));
            textPhone.setContentDescription(data.getString(data.getColumnIndex("display_phone")));
//            callingout=data.getString(data.getColumnIndex("display_phone"));
//            textPhone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Uri uri = Uri.parse(callingout);
//                    Intent callIntent = new Intent(Intent.ACTION_CALL, uri);
//                    try {
//                        startActivity(callIntent);
//                    }catch (Exception exception){
//                        Log.e(LOG_TAG, "Not working phone call");
//                    }
//
//                }
//            });
            Picasso.with(getBaseContext()).load(data.getString(data.getColumnIndex("rating_img"))).resize(205, 45).into(rateImg);

            c=data;
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
