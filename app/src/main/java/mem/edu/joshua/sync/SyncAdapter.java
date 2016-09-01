package mem.edu.joshua.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import mem.edu.joshua.BuildConfig;
import mem.edu.joshua.MainActivity;
import mem.edu.joshua.MapsActivity;
import mem.edu.joshua.R;
import mem.edu.joshua.Yelp;
import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;

/**
 * Created by erikllerena on 8/4/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();

    //All the synchronization intervals
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    public static final String ACTION_DATA_UPDATED =
            "com.sam_chordas.android.stockhawk.ACTION_DATA_UPDATED";

    //find these Buildconfig in app Gradel and gradel.properties
    String consumerKey = BuildConfig.YELP_CONSUMER_KEY;
    String consumerSecret = BuildConfig.YELP_CONSUMER_SECRET;
    String token = BuildConfig.YELP_TOKEN;
    String tokenSecret = BuildConfig.YELP_TOKEN_SECRET;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }


    //for the home screen widget
    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


        Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
        String response = yelp.search(getContext().getString(R.string.search), extras.getDouble("lat"), extras.getDouble("lon"));

        try {
            extractData(response);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return;
    }

    //response above = to resultJsonStr here. : )
    private void extractData(String resultJsonStr)
            throws JSONException {

        final String ROOT ="businesses";
        final String RATE = "rating";
        final String ID_BUSINESS = "name";
        final String URL = "url";

        //Yelp gets the GPS locaion but extracs new locations for map marker for 13 new businesses
        final String LOCATION = "location";
        final String COORDINATES = "coordinate";
        final String LATITUDE = "latitude";
        final String LONGITUDE = "longitude";

        final String RATE_IMG = "rating_img_url_large";

        final String DIS_ADDRESS = "address";
        final String DIS_PHONE = "display_phone";
        final String ZIP_CODE = "postal_code";


        try {
            JSONObject resultJson = new JSONObject(resultJsonStr);
            JSONArray churchArray = resultJson.getJSONArray(ROOT);

                for (int i = 1; i < churchArray.length(); i++) {

                    JSONObject churchObject = churchArray.getJSONObject(i);
                    JSONObject LocObj = churchObject.getJSONObject(LOCATION);//so I can get the address

                    JSONObject coorObject = LocObj.getJSONObject(COORDINATES);//so I can get coordinates
                    JSONArray AddressArray = LocObj.getJSONArray(DIS_ADDRESS);//so I can get phone number


                    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                            QuoteProvider.Quotes.CONTENT_URI);

                    //this list of if is because sometimes data is null
                    if (!churchObject.has(RATE)) {
                        builder.withValue(QuoteColumns.RATING, "No rated yet");
                    } else {
                        builder.withValue(QuoteColumns.RATING, churchObject.getString(RATE));
                    }

                    if (!churchObject.has(ID_BUSINESS)) {
                        builder.withValue(QuoteColumns.ID_BUSINESS_NAME, "No registered");
                    } else {
                        builder.withValue(QuoteColumns.ID_BUSINESS_NAME, churchObject.getString(ID_BUSINESS));
                    }

                    if (!churchObject.has(URL)) {
                        builder.withValue(QuoteColumns.URL, "yelp.com");
                    } else {
                        builder.withValue(QuoteColumns.URL, churchObject.getString(URL));
                    }

                    if (!coorObject.has(LATITUDE)) {
                        builder.withValue(QuoteColumns.LATITUDE, "0");
                    } else {
                        builder.withValue(QuoteColumns.LATITUDE, coorObject.getString(LATITUDE));
                    }

                    if (!coorObject.has(LONGITUDE)) {
                        builder.withValue(QuoteColumns.LOGITUDE, "0");
                    } else {
                        builder.withValue(QuoteColumns.LOGITUDE, coorObject.getString(LONGITUDE));
                    }

                    if (AddressArray.toString().isEmpty()) {
                        builder.withValue(QuoteColumns.DISPLAY_ADDRESS, "No address");
                    } else {
                        builder.withValue(QuoteColumns.DISPLAY_ADDRESS, AddressArray.toString());
                    }

                    if ((!churchObject.has(DIS_PHONE))) {
                        builder.withValue(QuoteColumns.DISPLAY_PHONE, "No phone");
                    } else {
                        builder.withValue(QuoteColumns.DISPLAY_PHONE, churchObject.getString(DIS_PHONE));
                    }

                    if (!LocObj.has(ZIP_CODE)) {
                        builder.withValue(QuoteColumns.POSTAL_CODE, "00000");
                    } else {
                        builder.withValue(QuoteColumns.POSTAL_CODE, LocObj.getString(ZIP_CODE));
                    }

                    if (!churchObject.has(RATE_IMG)) {
                        builder.withValue(QuoteColumns.RATING_IMG, "No img");
                    } else {
                        builder.withValue(QuoteColumns.RATING_IMG, churchObject.getString(RATE_IMG));
                    }

                    if (!churchObject.has(RATE)) {//column in table not used
                        builder.withValue(QuoteColumns.FLAG_A, "x");
                    } else {
                        builder.withValue(QuoteColumns.FLAG_A, churchObject.getString(RATE));
                    }

                    if (!churchObject.has(RATE)) {//column in table not used
                        builder.withValue(QuoteColumns.FLAG_B, "x");
                    } else {
                        builder.withValue(QuoteColumns.FLAG_B, churchObject.getString(RATE));
                    }

                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
                    batchOperations.add(builder.build());

                    try {//procede to insert all in a batch
                        Context mContext = getContext();
                        mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                                batchOperations);
                        Log.d(LOG_TAG, getContext().getString(R.string.insert_data));

                        getContext().sendBroadcast(new Intent(MainActivity.ACTION_FINISHED_SYNC));

                    } catch (RemoteException | OperationApplicationException e) {
                        Log.d(LOG_TAG, getContext().getString(R.string.error_insert), e);
                    }
                }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context, double lat, double lon) {//
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        //these are the coord from the GPS so Yelp knows where user is. :)
        bundle.putDouble("lat", lat);
        bundle.putDouble("lon", lon);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context, 0, 0);
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
