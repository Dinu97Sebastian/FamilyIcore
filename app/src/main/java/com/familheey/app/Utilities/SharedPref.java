package com.familheey.app.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.familheey.app.Models.Response.UserRegistrationResponse;
import com.familheey.app.Networking.utils.GsonUtils;

public class SharedPref {

    public static final String USER = "USER";
    public static final String IS_REGISTERED = "IS_REGISTERED";
    public static final String HAS_FAMILY = "HAS_FAMILY";
    public static final String ON_BOARD = "ON_BOARD";
    public static final String EVENT_SUGGESTION = "EVENT_SUGGESTION";
    public static final String SHOW_WALKTHROUGH = "SHOW_WALKTHROUGH";
    public static final String GOOGLE_API = "GOOGLE_API";
    public static final String STRIPE_KEY = "STRIPE_KEY";
    public static final String DYNAMIC_TYPE = "DYNAMIC_TYPE";
    public static final String DYNAMIC_TYPE_ID = "DYNAMIC_TYPE_ID";

    public static final String POST_DATA = "POST_DATA";
    public static final String FAMILY_LINK = "FAMILY_LINK";
    public static final String UPLOADING_FAMILIES = "UPLOADING_FAMILIES";
    public static final String STICKY_POST_POSITION = "STICKY_POST_POSITION";

    private static SharedPreferences mSharedPref;
    private static UserRegistrationResponse userRegistration;

    private SharedPref() {

    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        if (userRegistration == null && read(IS_REGISTERED, false))
            userRegistration = GsonUtils.getInstance().getGson().fromJson(read(USER, ""), UserRegistrationResponse.class);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).apply();
    }

    public static UserRegistrationResponse getUserRegistration() {
        return userRegistration;
    }

    public static void setUserRegistration(UserRegistrationResponse userRegistration) {
        SharedPref.userRegistration = userRegistration;
    }

    public static void setUserHasFamily(boolean hasFamily) {
        write(HAS_FAMILY, hasFamily);
    }

    public static boolean userHasFamily() {
        return read(HAS_FAMILY, false);
    }


    public static void setWalkThroughStatus(boolean hasFamily) {
        write(SHOW_WALKTHROUGH, hasFamily);
    }

    public static boolean getWalkThrough() {
        return read(SHOW_WALKTHROUGH, true);
    }

    public static void clear() {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
