package mem.edu.joshua.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by sam_chordas on 10/5/15.
 */

public class QuoteColumns {

  @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
  public static final String _ID = "_id";
  @DataType(DataType.Type.TEXT)
  public static final String RATING = "rating";
  @DataType(DataType.Type.TEXT)
  public static final String URL = "url";
  @DataType(DataType.Type.TEXT)
  public static final String DISPLAY_PHONE = "display_phone";
  @DataType(DataType.Type.TEXT)
  public static final String RATING_IMG = "rating_img";
  @DataType(DataType.Type.TEXT)
  @Unique(onConflict = ConflictResolutionType.REPLACE)
  public static final String ID_BUSINESS_NAME = "id_bussines_name";
  @DataType(DataType.Type.TEXT)
  public static final String DISPLAY_ADDRESS = "display_address";
  @DataType(DataType.Type.TEXT)
  public static final String POSTAL_CODE = "postal_code";
  @DataType(DataType.Type.TEXT)
  public static final String LATITUDE = "latitude";
  @DataType(DataType.Type.TEXT)
  public static final String LOGITUDE = "longitude";
  @DataType(DataType.Type.TEXT)
  public static final String FLAG_A = "flag_a";
  @DataType(DataType.Type.TEXT)
  public static final String FLAG_B = "flag_b";

}
