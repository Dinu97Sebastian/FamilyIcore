package com.familheey.app.Models;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;

public class Appinfo {

    private final Context context;

    public Appinfo(Context context) {
        this.context = context;
    }
    public String getDate() {
        appdata appinfo=new appdata();
        appinfo.setOs("Android");
        appinfo.setApp_version(appVersion());
        appinfo.setApp_name("familheey");
        return new Gson().toJson(appinfo);
    }

    public String appVersion() {
        PackageInfo packageInfo = getPackageInfo();
        return packageInfo != null ? packageInfo.versionName : "";
    }
    private PackageInfo getPackageInfo() {
        return getPackageInfo(this.context.getPackageName());
    }

    private PackageInfo getPackageInfo(String str) {
        try {
            return this.context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    class appdata{

        private String app_name;
        private String app_version;
        private String os;

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getApp_version() {
            return app_version;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }
    }
}
