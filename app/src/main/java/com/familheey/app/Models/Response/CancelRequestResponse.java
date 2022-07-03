package com.familheey.app.Models.Response;

import java.util.ArrayList;

public class CancelRequestResponse {
    private String code;
    private ArrayList<Family> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Family> getData() {
        return data;
    }

    public void setData(ArrayList<Family> data) {
        this.data = data;
    }
}
