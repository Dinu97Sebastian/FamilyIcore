package com.familheey.app.Parsers;

import android.content.Context;

import com.familheey.app.Models.ErrorData;
import com.familheey.app.Networking.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorParser {
    public static ErrorData parseError(Context context, String responseString) {
        ErrorData errorData = new ErrorData();
        JSONObject responseJson = null;
        try {
            responseJson = new JSONObject(responseString);
        } catch (JSONException | NullPointerException e) {
            errorData = HttpUtil.getServerErrorPojo(context);
            return errorData;
        }
        try {
            if (responseJson.get("message") instanceof JSONObject) {
                JSONObject messageJson = responseJson.getJSONObject("message");
                errorData.setName(messageJson.getString("name"));
                errorData.setMessage(messageJson.getString("message"));
            } else {
                errorData.setName(responseJson.getString("name"));
                errorData.setMessage(responseJson.getString("message"));
            }
            errorData.setCode(responseJson.getInt("code"));
        } catch (JSONException e) {
            e.printStackTrace();
            errorData = HttpUtil.getServerErrorPojo(context);
        }
        return errorData;
    }
}
