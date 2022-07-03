package com.familheey.app.Interfaces;

import com.familheey.app.Models.Response.Family;

public interface FamilyDashboardInteractor {

    void updateFamily(int TYPE, Family family);

    void onFamilyAddComponentVisible(int type);

    void onFamilyAddComponentHidden(int type);

}
