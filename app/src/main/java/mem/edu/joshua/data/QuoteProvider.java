package mem.edu.joshua.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by sam_chordas on 10/5/15.
 */
@ContentProvider(authority = QuoteProvider.AUTHORITY, database = QuoteDatabase.class)
public class QuoteProvider {
  public static final String AUTHORITY = "mem.edu.joshua.data.QuoteProvider";

  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

  interface Path{
    String ID_BUSINESS_NAME = "biz";
  }

  private static Uri buildUri(String... paths){
    Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
    for (String path:paths){
      builder.appendPath(path);
    }
    return builder.build();
  }

  @TableEndpoint(table = QuoteDatabase.ID_BUSINESS_NAME)
  public static class Quotes{
    @ContentUri(
        path = Path.ID_BUSINESS_NAME,
        type = "vnd.android.cursor.dir/biz"
    )
    public static final Uri CONTENT_URI = buildUri(Path.ID_BUSINESS_NAME);

    @InexactContentUri(
        name = "ID_BIZ",
        path = Path.ID_BUSINESS_NAME + "/*",
        type = "vnd.android.cursor.item/biz",
        whereColumn = QuoteColumns.ID_BUSINESS_NAME,
        pathSegment = 1
    )
    public static Uri withSymbol(String symbol){
      return buildUri(Path.ID_BUSINESS_NAME, symbol);
    }
  }
}
