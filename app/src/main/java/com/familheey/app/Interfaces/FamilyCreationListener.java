package com.familheey.app.Interfaces;

import com.familheey.app.Models.FamilyCreation;
import com.familheey.app.Models.Request.SimilarFamilyRequest;
import com.familheey.app.Models.Response.Family;

import java.util.ArrayList;

public interface FamilyCreationListener {

    void loadFamilyCreationLevelOne(Family family);

    void loadFamilyCreationLevelTwo(Family family);

    void loadFamilyCreationLevelThree(Family family);

    void loadFamilyCreationLevelFour(Family family);

    void loadFamilyCreationLevelAdvanced(Family family);

    void loadSimilarFamilies(SimilarFamilyRequest similarFamilyRequest, ArrayList<Family> similarFamilies);

    void showProgressDialog();

    void hideProgressDialog();

    void showErrorDialog(String errorMessage);

    FamilyCreation getFamilyCreation();

    String getFamilyIdToLink();

    boolean hasLinkedFamily();

    void setLinked(boolean isLinked);

    boolean familyNeedsLinking();
}
