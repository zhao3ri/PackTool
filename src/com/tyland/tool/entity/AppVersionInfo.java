package com.tyland.tool.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import static com.tyland.tool.util.Utils.equalsString;

public class AppVersionInfo {
    @JacksonXmlProperty
    String minSdk;

    @JacksonXmlProperty
    String targetSdk;

    @JacksonXmlProperty
    String versionCode;

    @JacksonXmlProperty
    String versionName;

    public AppVersionInfo() {
    }

    public AppVersionInfo(AppVersionInfo app) {
        this(app.targetSdk, app.minSdk, app.versionCode, app.versionName);
    }

    public AppVersionInfo(String minSdk, String targetSdk, String versionCode, String versionName) {
        this.minSdk = minSdk;
        this.targetSdk = targetSdk;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public String getMinSdk() {
        return minSdk;
    }

    public void setMinSdk(String minSdk) {
        this.minSdk = minSdk;
    }

    public String getTargetSdk() {
        return targetSdk;
    }

    public void setTargetSdk(String targetSdk) {
        this.targetSdk = targetSdk;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public AppVersionInfo update(String min, String target, String vcode, String vname) {
        this.setMinSdk(min);
        this.setTargetSdk(target);
        this.setVersionCode(vcode);
        this.setVersionName(vname);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return equalsString(this.minSdk, ((AppVersionInfo) obj).minSdk) &&
                equalsString(this.targetSdk, ((AppVersionInfo) obj).targetSdk) &&
                equalsString(this.versionCode, ((AppVersionInfo) obj).versionCode) &&
                equalsString(this.versionName, ((AppVersionInfo) obj).versionName);
    }
}
