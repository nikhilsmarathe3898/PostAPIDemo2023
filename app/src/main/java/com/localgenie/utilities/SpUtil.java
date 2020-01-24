package com.localgenie.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by dj
 * Function: sp storage tools
 */
public class SpUtil {

    private static final String APP_SP = "app_sp";
    private static final String TAG = SpUtil.class.getSimpleName();

    private SpUtil() {
    }

    private static SpUtil instance = new SpUtil();
    private static SharedPreferences mSp = null;

    public static SpUtil getInstance() {
        if (mSp == null) {
            mSp = LSPApplication.getContext().getSharedPreferences(APP_SP, Context.MODE_PRIVATE);
        }
        return instance;
    }

    /**
     * save data
     *
     * @param key   key
     * @param value value
     */
    public void save(String key, Object value) {
        if (value == null) {
            Log.e(TAG, "value==null保存失败");
            return;
        }
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    /**
     * Read String data
     *
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    /**
     * Read boolean data
     *
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    /**
     * Read boolean data
     *
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    /**
     *
     * Clear all saved data (.xml still exists, but no data inside)
     */
    public void clearAll() {
        mSp.edit().clear().commit();
    }

}
