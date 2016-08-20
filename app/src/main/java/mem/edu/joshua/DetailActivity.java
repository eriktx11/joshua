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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;

/**
 * Created by erikllerena on 8/18/16.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private TextView textTitle;
    private TextView textWebsite;
    private ImageView rateImg;
    private TextView textAddress;
    private TextView textCity;
    private TextView textPhone;
    private static String visiting;
    private static final int DETAIL_LOADER = 0;


             @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_view);

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString("title", intent.getStringExtra("title"));


//        TextView textTitle = (TextView) findViewById(R.id.name_txt_id);
//        TextView textWebsite = (TextView) findViewById(R.id.website_txt_id);
//        ImageView rateImg = (ImageView) findViewById(R.id.rate_img_id);
//        TextView textAddress = (TextView) findViewById(R.id.address_txt_id);
//        TextView textCity = (TextView) findViewById(R.id.city_txt_id);
//        TextView textPhone = (TextView) findViewById(R.id.phone_txt_id);
////
//        textTitle.setText(intent.getExtras().getString("title"));
//        textWebsite.setText(intent.getExtras().getString("website"));
//        //rateImg.setText(intent.getExtras().getString("rate"));
//        textAddress.setText(intent.getExtras().getString("address"));
//        textCity.setText(intent.getExtras().getString("city"));
//        textPhone.setText(intent.getExtras().getString("phone"));


        getSupportLoaderManager().initLoader(DETAIL_LOADER, bundle, this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //int id = item.getItemId();

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
            TextView textAddress = (TextView)findViewById(R.id.address_txt_id);
            TextView textCity = (TextView)findViewById(R.id.city_txt_id);
            TextView textPhone = (TextView)findViewById(R.id.phone_txt_id);


            Intent intent = getIntent();

            visiting = data.getString(data.getColumnIndex("url"));
            textTitle.setText(data.getString(data.getColumnIndex("id_bussines_name")));
            textWebsite.setText(R.string.website);
            textWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(visiting);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
            });
            textAddress.setText(data.getString(data.getColumnIndex("display_address")));
            textCity.setText(data.getString(data.getColumnIndex("postal_code")));
            textPhone.setText(data.getString(data.getColumnIndex("display_phone")));

            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
