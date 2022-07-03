package com.familheey.app.Models.Response;

import java.util.ArrayList;

public class Banner {

    private String banner_type;
    private boolean visibility;
    private String link_type;
    private String link_subtype;
    private String link_id;
    private ArrayList<String> banner_url;

    public String getBanner_type() {
        return banner_type;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public String getLink_type() {
        return link_type;
    }

    public String getLink_subtype() {
        return link_subtype;
    }

    public String getLink_id() {
        return link_id;
    }

    public ArrayList<String> getBanner_url() {
        return banner_url;
    }
}
