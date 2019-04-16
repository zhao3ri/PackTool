package com.tyland.tool.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import static com.tyland.tool.util.Utils.equalsString;

public class GameChannelConfig {
    @JacksonXmlProperty
    int channelId;

    @JacksonXmlProperty
    String drawablePath;

    @JacksonXmlProperty
    String appId;

    @JacksonXmlProperty
    String appKey;

    @JacksonXmlProperty
    String publicKey;

    @JacksonXmlProperty
    String secretKey;

    @JacksonXmlProperty
    String cpId;

    @JacksonXmlProperty
    String cpKey;

    @JacksonXmlProperty
    boolean useDefaultPackage = true;

    @JacksonXmlProperty
    String packageName;

    @JacksonXmlProperty
    boolean suffix;

    @JacksonXmlProperty
    AppVersionInfo appInfo;


    public GameChannelConfig() {
    }

    public GameChannelConfig(GameChannelConfig config) {
        this.channelId = config.channelId;
        this.drawablePath = config.drawablePath;
        this.appId = config.appId;
        this.appKey = config.appKey;
        this.publicKey = config.publicKey;
        this.secretKey = config.secretKey;
        this.cpId = config.cpId;
        this.cpKey = config.cpKey;
        this.packageName = config.packageName;
        this.useDefaultPackage = config.useDefaultPackage;
        this.suffix = config.suffix;
        this.appInfo = config.appInfo;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getDrawablePath() {
        return drawablePath == null ? "" : drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public String getCpKey() {
        return cpKey;
    }

    public void setCpKey(String cpKey) {
        this.cpKey = cpKey;
    }

    public boolean isUseDefaultPackage() {
        return useDefaultPackage;
    }

    public void setUseDefaultPackage(boolean useDefaultPackage) {
        this.useDefaultPackage = useDefaultPackage;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSuffix() {
        return suffix;
    }

    public void setSuffix(boolean suffix) {
        this.suffix = suffix;
    }

    public AppVersionInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppVersionInfo appInfo) {
        this.appInfo = appInfo;
    }

    public void updateAppInfo(String min, String target, String vcode, String vname) {
        if (this.appInfo == null) {
            this.appInfo = new AppVersionInfo();
        }
        this.appInfo.update(min, target, vcode, vname);
    }

    @Override
    public boolean equals(Object obj) {
        return this.channelId == ((GameChannelConfig) obj).channelId &&
                equalsString(this.drawablePath, ((GameChannelConfig) obj).drawablePath) &&
                equalsString(this.appId, ((GameChannelConfig) obj).appId) &&
                equalsString(this.appKey, ((GameChannelConfig) obj).appKey) &&
                equalsString(this.publicKey, ((GameChannelConfig) obj).publicKey) &&
                equalsString(this.secretKey, ((GameChannelConfig) obj).secretKey) &&
                equalsString(this.cpId, ((GameChannelConfig) obj).cpId) &&
                equalsString(this.cpKey, ((GameChannelConfig) obj).cpKey) &&
                equalsString(this.packageName, ((GameChannelConfig) obj).packageName) &&
                this.useDefaultPackage == ((GameChannelConfig) obj).useDefaultPackage &&
                this.suffix == ((GameChannelConfig) obj).suffix &&
                this.appInfo.equals(((GameChannelConfig) obj).appInfo);
    }

    public static GameChannelConfig createDefaultConfig() {
        GameChannelConfig config = new GameChannelConfig();
        config.setChannelId(0);
        config.setDrawablePath("");
        config.setAppId("");
        config.setAppKey("");
        config.setPublicKey("");
        config.setSecretKey("");
        config.setCpId("");
        config.setCpKey("");
        config.setUseDefaultPackage(true);
        config.setSuffix(false);
        config.setPackageName("");
        return config;
    }
}
