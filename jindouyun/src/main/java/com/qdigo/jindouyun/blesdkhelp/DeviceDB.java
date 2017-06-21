package com.qdigo.jindouyun.blesdkhelp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class DeviceDB {
    public static final String TAG = "DeviceDB";
    public static class Record {
        public String name;
        public String identifier;
        public String key;

        public Record(String name, String identifier, String key) {
            this.name = name;
            this.identifier = identifier;
            this.key = key;
        }
    }

    // Private constants.
    private final static String BLUE_GUARD_SETTINGS = "blue_guard_settings";
    private final static String THE_BLUE_GUARD_ID = "blue_guard_id";
    private final static String THE_BLUE_GUARD_KEY = "blue_guard_key";
    private final static String THE_BLUE_GUARD_NAME = "blue_guard_name";

    // Persist last connected BlueGuard.
    public static void saveScan(Context context, Record rec) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS, 0);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(THE_BLUE_GUARD_ID, rec.identifier);
        editor.putString(THE_BLUE_GUARD_KEY, rec.key);
        editor.putString(THE_BLUE_GUARD_NAME, rec.name);
        editor.apply();
    }

    public static void deleteScan(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS, 0);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(THE_BLUE_GUARD_ID, null);
        editor.putString(THE_BLUE_GUARD_KEY, null);
        editor.putString(THE_BLUE_GUARD_NAME, null);
        editor.apply();
    }

    public static Record loadScan(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS, 0);
        String identifier = sPreferences.getString(THE_BLUE_GUARD_ID, "");
        String name = sPreferences.getString(THE_BLUE_GUARD_NAME, "");
        String key = sPreferences.getString(THE_BLUE_GUARD_KEY, "");

        if (identifier.length() == 0)
            return null;

        return new Record(name.length() > 0 ? name : null,
                identifier.length() > 0 ? identifier : null,
                key.length() > 0 ? key : null);
    }

    // ******************************** 保存连接上 且返回key的设备
    private final static String BLUE_GUARD_SETTINGS_KEY = "blue_guard_settings_key";
    private final static String THE_BLUE_GUARD_ID_KEY = "blue_guard_id_key";
    private final static String THE_BLUE_GUARD_KEY_KEY = "blue_guard_key_key";
    private final static String THE_BLUE_GUARD_NAME_KEY = "blue_guard_name_key";

    // Persist last connected BlueGuard.
    public static void saveKey(Context context, Record rec) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS_KEY, 0);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(THE_BLUE_GUARD_ID_KEY, rec.identifier);
        editor.putString(THE_BLUE_GUARD_KEY_KEY, rec.key);
        editor.putString(THE_BLUE_GUARD_NAME_KEY, rec.name);
        editor.apply();
    }

    public static void deleteKey(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS_KEY, 0);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(THE_BLUE_GUARD_ID_KEY, null);
        editor.putString(THE_BLUE_GUARD_KEY_KEY, null);
        editor.putString(THE_BLUE_GUARD_NAME_KEY, null);
        editor.apply();
    }

    public static Record loadKey(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS_KEY, 0);
        String identifier = sPreferences.getString(THE_BLUE_GUARD_ID_KEY, "");
        String name = sPreferences.getString(THE_BLUE_GUARD_NAME_KEY, "");
        String key = sPreferences.getString(THE_BLUE_GUARD_KEY_KEY, "");
        Log.w(TAG,"identifier = "+identifier+
                "\n name = "+name+
                "\n key = "+key);
        return new Record(name.length() > 0 ? name : null,
                identifier.length() > 0 ? identifier : null,
                key.length() > 0 ? key : null);
    }

}
