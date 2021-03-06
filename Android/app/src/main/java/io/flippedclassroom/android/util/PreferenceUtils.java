package io.flippedclassroom.android.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

//Preference存储工具类，方便利用Preference类存储数据到本地
public class PreferenceUtils {
    private static Context sContext;
    private static final String ID = "Id";
    private static final String TOKEN = "Token";
    private static final String ROLE = "Role";
    private static final String ALLOW_NO_WIFI_DOWNLOAD = "AllowNoWifiDownload";

    public static void init(Context context) {
        sContext = context;
    }

    private static SharedPreferences getInstance() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    public static String getToken() {
        return getInstance().getString(TOKEN, null);
    }

    public static void saveToken(String token) {
        getInstance().edit().putString(TOKEN, token).apply();
    }

    public static void saveRole(String role) {
        getInstance().edit().putString(ROLE, role).apply();
    }

    public static String getRole() {
        return getInstance().getString(ROLE, null);
    }

    public static void saveId(String id) {
        getInstance().edit().putString(ID, id).apply();
    }

    public static String getId() {
        return getInstance().getString(ID, null);
    }

    //读取本地保存的是不是允许没有wifi下载文件
    public boolean canDownloadNoWifi() {
        return getInstance().getBoolean(ALLOW_NO_WIFI_DOWNLOAD, false);
    }
}
