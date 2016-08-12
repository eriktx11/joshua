package mem.edu.joshua;

/**
 * Created by erikllerena on 8/10/16.
 */

import org.scribe.model.Token;
import org.scribe.builder.api.DefaultApi10a;

public class YelpApi2 extends DefaultApi10a{

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(Token arg0) {
        return null;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return null;
    }
}
