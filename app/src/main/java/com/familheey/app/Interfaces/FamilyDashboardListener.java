package com.familheey.app.Interfaces;

import com.familheey.app.Models.Response.Family;

public interface FamilyDashboardListener {

    void hideProgressDialog();

    void showProgressDialog();

    void showErrorDialog(String errorMessage);

    void loadFamilySubscriptions(Family family);

    void loadFamilyAdvancedSettings(Family family);

    void loadFamilyBasicSettings(Family family);

    void loadFamilySettings(Family family);

    void loadFileUploading(Family family);

    void onFamilyUpdated(boolean isBackPressedNeeded);


    //void onFamilyUpdatednew(boolean isBackPressedNeeded);
}
