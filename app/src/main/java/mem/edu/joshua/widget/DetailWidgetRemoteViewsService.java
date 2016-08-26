package mem.edu.joshua.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import mem.edu.joshua.R;
import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;

/**
 * Created by erikllerena on 6/17/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.ID_BUSINESS_NAME, QuoteColumns.RATING_IMG},
                        null,
                        null,
                        null);


                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }


            RecyclerView.ViewHolder viewHolder;
            TextView textView;

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.layout_widget_list_item);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, data.getString(data.getColumnIndex("id_bussines_name")));
                }

                views.setTextViewText(R.id.textView, data.getString(data.getColumnIndex("id_bussines_name")));
                setRemoteContentDescription(views, data.getString(data.getColumnIndex("id_bussines_name")));

                try {
                    Bitmap b = Picasso.with(getBaseContext()).load(data.getString(data.getColumnIndex("rating_img"))).get();
                    views.setImageViewBitmap(R.id.imageView, b);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                    final Intent fillInIntent = new Intent();

                    fillInIntent.setData(QuoteProvider.Quotes.CONTENT_URI);
                    fillInIntent.putExtra("title", data.getString(data.getColumnIndex("id_bussines_name")));
                    views.setOnClickFillInIntent(R.id.tapWidId, fillInIntent);

                    return views;
                }


                @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                private void setRemoteContentDescription (RemoteViews views, String description){
                    views.setContentDescription(R.id.textView, description);
                }


                @Override
                public int getViewTypeCount () {
                    return 1;
                }

                @Override
                public long getItemId ( int position){
                    if (data.moveToPosition(position))
                        return data.getColumnIndex("_id");
                    return position;
                }

                @Override
                public boolean hasStableIds () {
                    return true;
                }


                @Override
                public RemoteViews getLoadingView () {
                    return new RemoteViews(getPackageName(), R.layout.layout_widget_list_item);
                }
            };
        }
    }
