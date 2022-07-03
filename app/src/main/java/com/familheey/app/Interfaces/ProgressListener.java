package com.familheey.app.Interfaces;

public interface ProgressListener {

    void hideProgressDialog();

    void showProgressDialog();

    void showErrorDialog(String errorMessage);
}
