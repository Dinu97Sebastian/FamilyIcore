package com.familheey.app.Utilities;

import android.content.Context;
import android.content.res.AssetManager;

import com.familheey.app.Models.Country;
import com.familheey.app.Networking.utils.GsonUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CountryCodeUtilities {


    public static ArrayList<Country> getCountryCodes(Context context) {
        try {
            String countryCodeDatas = AssetJSONFile(Constants.Json.COUNTRY_CODE, context);
            return parseCountryCodes(countryCodeDatas);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static ArrayList<Country> parseCountryCodes(String countryCodeData) {
        ArrayList<Country> countries = new ArrayList<>();
        try {
            JSONArray countryJsonArray = new JSONArray(countryCodeData);
            for (int i = 0; i < countryJsonArray.length(); i++) {
                countries.add(getCountries(countryJsonArray.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return countries;
    }

    private static Country getCountries(String countryJson) {
        return GsonUtils.getInstance().getGson().fromJson(countryJson, Country.class);
    }

    private static String AssetJSONFile(String filename, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();
        return new String(formArray);
    }
}
