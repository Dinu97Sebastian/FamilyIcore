package com.familheey.app.Parsers;

import com.familheey.app.Models.FamilyJoiningRequest;
import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.FamilySearchModal;
import com.familheey.app.Models.Response.LinkfamilyList;
import com.familheey.app.Models.Response.MemberRequests;
import com.familheey.app.Models.Response.PeopleSearchModal;
import com.familheey.app.Models.Response.RelationShip;
import com.familheey.app.Networking.utils.GsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FamilyParser {

    public static List<Family> parseLinkedFamilies(String responseString) throws JsonParseException, NullPointerException {
        List<Family> linkedFamilies = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        if (responseArray.size() == 0)
            return linkedFamilies;
        linkedFamilies = GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<Family>>() {
        }.getType());
        return linkedFamilies;
    }

    public static Family parseFamily(String responseString) throws JsonParseException, NullPointerException {
        return parseLinkedFamilies(responseString).get(0);
    }

    public static String getFamilyMembers(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getJSONObject("count").getString("familyMembers");
        } catch (Exception e) {
            return "-";
        }
    }

    public static String getKnownConnections(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getJSONObject("count").getString("knownConnections");
        } catch (Exception e) {
            return "-";
        }
    }

    public static String getEventsCount(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getJSONObject("count").getString("event_count");
        } catch (Exception e) {
            return "-";
        }
    }

    public static String getPostCount(String responseString) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            return jsonObject.getJSONObject("count").getString("post_count");
        } catch (Exception e) {
            return "-";
        }
    }

    public static int getAdminsCount(List<FamilyMember> familyMembers) {
        int adminsCount = 0;
        if (familyMembers != null && familyMembers.size() > 0) {
            for (FamilyMember familyMember : familyMembers) {
                if (familyMember.getUserType() != null && familyMember.getUserType().equalsIgnoreCase("admin")) {
                    adminsCount++;
                }
            }
            return adminsCount;
        }
        return 0;
    }

    public static ArrayList<PeopleSearchModal> parseSearchedPeoples(JSONArray familyArray) throws JsonParseException, NullPointerException {
        ArrayList<PeopleSearchModal> peopleSearchModalArrayList;

        peopleSearchModalArrayList = GsonUtils.getInstance().getGson().fromJson(familyArray.toString(), new TypeToken<ArrayList<PeopleSearchModal>>() {
        }.getType());
        return peopleSearchModalArrayList;
    }

    public static ArrayList<FamilySearchModal> parseSearchedFamily(JSONArray familyArray) throws JsonParseException, NullPointerException {
        ArrayList<FamilySearchModal> familySearchModalArrayList;

        familySearchModalArrayList = GsonUtils.getInstance().getGson().fromJson(familyArray.toString(), new TypeToken<ArrayList<FamilySearchModal>>() {
        }.getType());
        return familySearchModalArrayList;
    }

    public static List<FamilyMember> parseFamilyMembers(String responseString) throws JsonParseException, NullPointerException {
        List<FamilyMember> familyMembers = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        familyMembers = GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<FamilyMember>>() {
        }.getType());
        return familyMembers;
    }

    public static ArrayList<MemberRequests> parseMemberRequests(String responseString) throws JsonParseException, NullPointerException {
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("request");
        ArrayList<MemberRequests> familyMembers = GsonUtils.getInstance().getGson().fromJson(responseArray, new TypeToken<ArrayList<MemberRequests>>() {
        }.getType());

        return familyMembers;
    }

    public static ArrayList<MemberRequests> parseFamilyLinkingRequests(String responseString) throws JsonParseException, NullPointerException {
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("linkRequest");
        ArrayList<MemberRequests> familyMembers = GsonUtils.getInstance().getGson().fromJson(responseArray, new TypeToken<ArrayList<MemberRequests>>() {
        }.getType());

        return familyMembers;
    }

    public static ArrayList<LinkfamilyList> parseLinkFamilyList(String responseString) throws JsonParseException, NullPointerException {
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        ArrayList<LinkfamilyList> linkfamilyLists = GsonUtils.getInstance().getGson().fromJson(responseArray, new TypeToken<ArrayList<LinkfamilyList>>() {
        }.getType());

        return linkfamilyLists;
    }

    public static List<RelationShip> parseRelationShip(String responseString) throws JsonParseException, NullPointerException {
        List<RelationShip> relationShips = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        relationShips = GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<RelationShip>>() {
        }.getType());
        return relationShips;
    }

    public static List<FamilyJoiningRequest> parseinvitations(String responseString) throws JsonParseException, NullPointerException {
        List<FamilyJoiningRequest> familyJoiningRequests = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        familyJoiningRequests = GsonUtils.getInstance().getGson().fromJson(responseArray.toString(), new TypeToken<ArrayList<FamilyJoiningRequest>>() {
        }.getType());
        return familyJoiningRequests;
    }

    public static ArrayList<MemberRequests> parseFamilyUserPendingRequests(String responseString) throws JsonParseException, NullPointerException {
        JsonParser jsonParser = new JsonParser();
        JsonObject responseJson = (JsonObject) jsonParser.parse(responseString);
        JsonArray responseArray = responseJson.getAsJsonArray("data");
        ArrayList<MemberRequests> familyMembers = GsonUtils.getInstance().getGson().fromJson(responseArray, new TypeToken<ArrayList<MemberRequests>>() {
        }.getType());

        return familyMembers;
    }

    public static Integer parsePendingCount(String responseString) {
        try {
            JSONObject responseJson = new JSONObject(responseString);
            return responseJson.getInt("pending_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
