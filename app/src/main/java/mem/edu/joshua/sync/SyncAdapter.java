package mem.edu.joshua.sync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

//import com.beust.jcommander.Parameter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationServices;
import com.google.ads.mediation.MediationServerParameters;
import com.google.android.gms.common.api.GoogleApiClient;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import mem.edu.joshua.BuildConfig;
import mem.edu.joshua.R;
import mem.edu.joshua.TwoStepOAuth;
import mem.edu.joshua.Yelp;
import mem.edu.joshua.data.QuoteColumns;
import mem.edu.joshua.data.QuoteProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by erikllerena on 8/4/16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = SyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 4;//60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000;//1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;


//
//    private static final String API_HOST = "api.yelp.com";
//    private static final String DEFAULT_TERM = "dinner";
//    private static final String DEFAULT_LOCATION = "San Francisco, CA";
//    private static final int SEARCH_LIMIT = 3;
//    private static final String SEARCH_PATH = "/v2/search";
//    private static final String BUSINESS_PATH = "/v2/business";


//
//    private GoogleApiClient mGoogleApiClient;
//    private YelpAPI yelpAPI;
//    private Location mLastLocation;
////    private Yelp yelp;
//YelpAPI yelpApi;
//   // YelpAPICLI yelpApiCli;
//
//    OAuthService service;
//    Token accessToken;

    String consumerKey = BuildConfig.YELP_CONSUMER_KEY;
    String consumerSecret = BuildConfig.YELP_CONSUMER_SECRET;
    String token = BuildConfig.YELP_TOKEN;
    String tokenSecret = BuildConfig.YELP_TOKEN_SECRET;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

//        YelpAPIFactory apiFactory = new YelpAPIFactory(BuildConfig.YELP_CONSUMER_KEY, BuildConfig.YELP_CONSUMER_SECRET, BuildConfig.YELP_TOKEN, BuildConfig.YELP_TOKEN_SECRET);
//        YelpAPI yelpAPI = apiFactory.createAPI();
//
//        this.service =
//                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(BuildConfig.YELP_CONSUMER_KEY)
//                        .apiSecret(BuildConfig.YELP_CONSUMER_SECRET).build();
//        this.accessToken = new Token(BuildConfig.YELP_TOKEN, BuildConfig.YELP_TOKEN_SECRET);

    }



//    public class TwoStepOAuth extends DefaultApi10a {
//
//        @Override
//        public String getAccessTokenEndpoint() {
//            return null;
//        }
//
//        @Override
//        public String getAuthorizationUrl(Token arg0) {
//            return null;
//        }
//
//        @Override
//        public String getRequestTokenEndpoint() {
//            return null;
//        }
//    }
//
//
//    public String searchForBusinessesByLocation(String term, String location) {
//        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
//        request.addQuerystringParameter("term", term);
//        request.addQuerystringParameter("location", location);
//        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
//        return sendRequestAndGetResponse(request);
//    }
//
//    public String searchByBusinessId(String businessID) {
//        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
//        return sendRequestAndGetResponse(request);
//    }
//
//
//    private OAuthRequest createOAuthRequest(String path) {
//        OAuthRequest request = new OAuthRequest(Verb.GET, "https://" + API_HOST + path);
//        return request;
//    }
//
//
//    private String sendRequestAndGetResponse(OAuthRequest request) {
//        System.out.println("Querying " + request.getCompleteUrl() + " ...");
//        this.service.signRequest(this.accessToken, request);
//        Response response = request.send();
//        return response.getBody();
//    }
//
//
//    private static class YelpAPICLI {
//        @Parameter(names = {"-q", "--term"}, description = "Search Query Term")
//        public String term = DEFAULT_TERM;
//
//        @Parameter(names = {"-l", "--location"}, description = "Location to be Queried")
//        public String location = DEFAULT_LOCATION;
//    }



    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {


        Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
        String response = yelp.search("church", 30.361471, -87.164326);

        try {
            extractData(response);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return;
    }

    private void extractData(String resultJsonStr)
            throws JSONException {

        final String ROOT ="businesses";
        final String RATE = "rating";
        final String ID_BUSINESS = "name";
        final String URL = "url";

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

            if(resultJson.has(ROOT)) {

                for (int i = 0; i < churchArray.length(); i++) {

                    JSONObject churchObject = churchArray.getJSONObject(i);
                    JSONObject LocObj = churchObject.getJSONObject(LOCATION);

                    JSONObject coorObject = LocObj.getJSONObject(COORDINATES);
                    JSONArray AddressArray = LocObj.getJSONArray(DIS_ADDRESS);


                    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                            QuoteProvider.Quotes.CONTENT_URI);

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

                    if (!churchObject.has(RATE)) {
                        builder.withValue(QuoteColumns.FLAG_A, "x");
                    } else {
                        builder.withValue(QuoteColumns.FLAG_A, churchObject.getString(RATE));
                    }

                    if (!churchObject.has(RATE)) {
                        builder.withValue(QuoteColumns.FLAG_B, "x");
                    } else {
                        builder.withValue(QuoteColumns.FLAG_B, churchObject.getString(RATE));
                    }

                    //builder.build();


                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
                    batchOperations.add(builder.build());

                    try {
                        Context mContext = getContext();
                        mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY,
                                batchOperations);
                    } catch (RemoteException | OperationApplicationException e) {
                        Log.d(LOG_TAG, "Error inserting", e);
                    }


                }
                Log.d(LOG_TAG, "Sync Complete. data Inserted");
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
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
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
        syncImmediately(context);
    }


    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
