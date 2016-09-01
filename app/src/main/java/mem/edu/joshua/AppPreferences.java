package mem.edu.joshua;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by erikllerena on 6/20/16.
 */
public class AppPreferences {

    public static final String KEY_PREFS_SMS_BODY = "sms_body";
    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;
private Context context;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }


    public Double getCoordBody(String coord) {
        return Double.longBitsToDouble(_sharedPrefs.getLong(coord, 0));
    }

    //not used in this app. Oops typo here
    public int getIntdBody() {
        return _sharedPrefs.getInt("selction", -1);
    }

    //not used in this app. Oops typo here.
    public void saveIntVal(int val) {
        _prefsEditor.putInt("selction", val);
        _prefsEditor.apply();
    }

    //here I get Lat and Lon
    public void saveCoordBody(String coord, Double val) {
        _prefsEditor.putLong(coord, Double.doubleToRawLongBits(val));
        _prefsEditor.apply();
    }

    //not used in this app
    public void removePref(String coord) {
        _prefsEditor.remove(coord);
        _prefsEditor.apply();
    }

    //not used in this app
    public Map<String, ?> getAll() {
        return _sharedPrefs.getAll();
    }

}
