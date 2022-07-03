package com.familheey.app.Networking.utils;

import com.google.gson.Gson;

public class GsonUtils {
    private static GsonUtils sInstance;
    private Gson mGson;

    private GsonUtils() {
        /*mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .enableComplexMapKeySerialization().create();*/
        mGson = new Gson();
    }

    public static GsonUtils getInstance() {
        if (sInstance == null) {
            sInstance = new GsonUtils();
        }
        return sInstance;
    }

    public String toJson(Object object) {
        return mGson.toJson(object);
    }

    public Gson getGson() {
        return mGson;
    }
}