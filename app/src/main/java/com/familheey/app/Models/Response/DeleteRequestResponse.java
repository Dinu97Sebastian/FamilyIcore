package com.familheey.app.Models.Response;

import java.util.ArrayList;

public class DeleteRequestResponse {
    private String message;

    public String getCode() {
        return message;
    }

    public void setCode(String code) {
        this.message = code;
    }

}
