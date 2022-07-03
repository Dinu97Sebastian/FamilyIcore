package com.familheey.app.Networking.utils;

import android.content.Context;

import com.familheey.app.Models.ErrorData;
import com.familheey.app.R;
import com.familheey.app.Utilities.Constants;
import com.google.gson.Gson;

import org.json.JSONObject;

public class HttpUtil {
    private static Logger logger = new Logger(HttpUtil.class.getSimpleName());

    public static JSONObject getServerErrorJsonObject(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.ErrorClass.STATUS, 505);
            jsonObject.put(Constants.ErrorClass.CODE, 3000);
            jsonObject.put(Constants.ErrorClass.MESSAGE, context.getString(R.string.serverNotAvailable));
            jsonObject.put(Constants.ErrorClass.DEVELOPER_MESSAGE, context.getString(R.string.serverNotAvailable));
        } catch (Exception e) {
            logger.error(e);
        }
        return jsonObject;
    }

    public static JSONObject getInternetUnavailableJsonObject(Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.ErrorClass.STATUS, 0);
            jsonObject.put(Constants.ErrorClass.CODE, 1000);
            jsonObject.put(Constants.ErrorClass.MESSAGE, context.getString(R.string.internetNotAvailable));
            jsonObject.put(Constants.ErrorClass.DEVELOPER_MESSAGE, context.getString(R.string.internetNotAvailable));
        } catch (Exception e) {
            logger.error(e);
        }
        return jsonObject;
    }

    public static ErrorData getServerErrorPojo(Context context) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(getServerErrorJsonObject(context).toString(), ErrorData.class);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

}
