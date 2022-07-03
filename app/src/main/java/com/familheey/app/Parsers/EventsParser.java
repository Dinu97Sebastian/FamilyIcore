package com.familheey.app.Parsers;

import com.familheey.app.Models.Response.Event;
import com.familheey.app.Models.Response.FetchCalendarResponse;
import com.familheey.app.Models.Response.SignUpContributor;
import com.familheey.app.Networking.utils.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class EventsParser {

    public static List<Event> parseEvents(String responseString) throws JsonParseException, NullPointerException {
        List<Event> events = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        if (responseArray.size() == 0)
            return events;
        events = GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<Event>>() {
        }.getType());
        return events;
    }

    public static List<SignUpContributor> parseSignUpContributors(String responseString) throws JsonParseException, NullPointerException {
        List<SignUpContributor> signUpContributors = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        if (responseArray.size() == 0)
            return signUpContributors;
        signUpContributors = GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<SignUpContributor>>() {
        }.getType());
        return signUpContributors;
    }

    public static List<Event> parseFamilyEvents(String responseString) throws JsonParseException, NullPointerException {
        List<Event> events = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonObject eventsJson = responseJson.getAsJsonObject("data");
        JsonArray eventShareArray = eventsJson.getAsJsonArray("event_share");
        JsonArray eventInvitationArray = eventsJson.getAsJsonArray("event_invitation");
        if (eventShareArray.size() == 0 && eventInvitationArray.size() == 0)
            return events;
        events.addAll(GsonUtils.getInstance().getGson().fromJson(eventShareArray.toString(), new TypeToken<ArrayList<Event>>() {
        }.getType()));
        events.addAll(GsonUtils.getInstance().getGson().fromJson(eventInvitationArray.toString(), new TypeToken<ArrayList<Event>>() {
        }.getType()));
        return events;
    }

    public static List<FetchCalendarResponse.Datum> parseFamilyCalendarEvents(String responseString) throws JsonParseException, NullPointerException {
        List<FetchCalendarResponse.Datum> events = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonObject eventsJson = responseJson.getAsJsonObject("data");
        JsonArray eventShareArray = eventsJson.getAsJsonArray("event_share");
        JsonArray eventInvitationArray = eventsJson.getAsJsonArray("event_invitation");
        if (eventShareArray.size() == 0 && eventInvitationArray.size() == 0)
            return events;
        events.addAll(GsonUtils.getInstance().getGson().fromJson(eventShareArray.toString(), new TypeToken<ArrayList<FetchCalendarResponse.Datum>>() {
        }.getType()));
        events.addAll(GsonUtils.getInstance().getGson().fromJson(eventInvitationArray.toString(), new TypeToken<ArrayList<FetchCalendarResponse.Datum>>() {
        }.getType()));
        return events;
    }

    public static List<Event> parseInvitationEvents(String responseString) throws JsonParseException, NullPointerException {
        List<Event> events = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray eventsJson = responseJson.getAsJsonArray("data");
        if (eventsJson.size() == 0)
            return events;
        events.addAll(GsonUtils.getInstance().getGson().fromJson(eventsJson.toString(), new TypeToken<ArrayList<Event>>() {
        }.getType()));
        return events;
    }

    public static ArrayList<Event> parseEventArray(JSONArray responseArray) {
        ArrayList<Event> events = new ArrayList<>();
        if (responseArray.length() == 0)
            return events;
        events.addAll(GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<Event>>() {
        }.getType()));
        return events;
    }

}
