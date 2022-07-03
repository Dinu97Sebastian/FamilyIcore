package com.familheey.app.Models;

import com.familheey.app.Models.Response.Family;
import com.familheey.app.Models.Response.FamilyMember;
import com.familheey.app.Models.Response.FamilySearchModal;
import com.familheey.app.Models.Response.ListAgendaResponse;
import com.familheey.app.Models.Response.MemberRequests;
import com.familheey.app.Models.Response.User;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ApiCallbackParams {

    private Family family;

    private FamilyMember familyMember;

    private MemberRequests memberRequests;

    private FamilyJoiningRequest familyJoiningRequest;

    private FamilySearchModal globalSearchFamily;

    private String developerString = "";

    private int developerCode = 0;

    private boolean status = true;

    private User user;

    private int position = 0;

    private ListAgendaResponse.Datum agenda;

    private ContactModel contact;

    public ApiCallbackParams() {
        //Default Constructor
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public FamilyMember getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(FamilyMember familyMember) {
        this.familyMember = familyMember;
    }

    public MemberRequests getMemberRequests() {
        return memberRequests;
    }

    public void setRequest(MemberRequests memberRequests) {
        this.memberRequests=memberRequests;
    }

    public String getDeveloperString() {
        return developerString;
    }

    public void setDeveloperString(String developerString) {
        this.developerString = developerString;
    }

    public int getDeveloperCode() {
        return developerCode;
    }

    public void setDeveloperCode(int developerCode) {
        this.developerCode = developerCode;
    }

    public FamilyJoiningRequest getFamilyJoiningRequest() {
        return familyJoiningRequest;
    }

    public void setFamilyJoiningRequest(FamilyJoiningRequest familyJoiningRequest) {
        this.familyJoiningRequest = familyJoiningRequest;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public FamilySearchModal getGlobalSearchFamily() {
        return globalSearchFamily;
    }

    public void setGlobalSearchFamily(FamilySearchModal globalSearchFamily) {
        this.globalSearchFamily = globalSearchFamily;
    }

    private SweetAlertDialog sDialog;

    public SweetAlertDialog getDialog() {
        return sDialog;
    }

    public void setDialog(SweetAlertDialog sDialog) {
        this.sDialog = sDialog;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ListAgendaResponse.Datum getAgenda() {
        return agenda;
    }

    public void setAgenda(ListAgendaResponse.Datum agenda) {
        this.agenda = agenda;
    }

    public ContactModel getContact() {
        return contact;
    }

    public void setContact(ContactModel contact) {
        this.contact = contact;
    }
}
