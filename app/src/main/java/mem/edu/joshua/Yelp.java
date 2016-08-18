package mem.edu.joshua;

/**
 * Created by erikllerena on 8/10/16.
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;


public class Yelp {

    OAuthService service;
    Token accessToken;

    private static final int SEARCH_LIMIT = 13;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public String search(String term, double latitude, double longitude) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    // CLI
//    public static void main(String[] args) {
//        // Update tokens here from Yelp developers site, Manage API access.
//
//        String consumerKey = BuildConfig.YELP_CONSUMER_KEY;
//        String consumerSecret = BuildConfig.YELP_CONSUMER_SECRET;
//        String token = BuildConfig.YELP_TOKEN;
//        String tokenSecret = BuildConfig.YELP_TOKEN_SECRET;
//
//        Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
//        String response = yelp.search("burritos", 30.361471, -87.164326);
//
//        System.out.println(response);
//    }
}

