package com.familheey.app.Interfaces;

import com.familheey.app.Models.Response.Family;

public interface HomeInteractor {

    void loadFamilyGroupDashboard(Family family);

    void showProgressDialog();

    void hideProgressDialog();

    void showErrorDialog(String errorMessage);

    void loadGlobalSearch();

    void loadNotifications();

    int getNotificationsCount();

    void loadFeedback();

    void loadAnnouncement();

    void loadCalender();

  //  void loadSpotlight();

    void loadNewUserHelper();

    void navigateMessageScreen();

    void setNotificationCount(int count);
}
