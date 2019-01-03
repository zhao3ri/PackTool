package com.qinglan.tool.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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
    String suffix;

    @JacksonXmlProperty
    String minSdk;

    @JacksonXmlProperty
    String targetSdk;

    @JacksonXmlProperty
    String versionCode;

    @JacksonXmlProperty
    String versionName;

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
        this.suffix = config.suffix;
        this.minSdk = config.minSdk;
        this.targetSdk = config.targetSdk;
        this.versionCode = config.versionCode;
        this.versionName = config.versionName;
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

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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
                equalsString(this.suffix, ((GameChannelConfig) obj).suffix) &&
                equalsString(this.minSdk, ((GameChannelConfig) obj).minSdk) &&
                equalsString(this.targetSdk, ((GameChannelConfig) obj).targetSdk) &&
                equalsString(this.versionCode, ((GameChannelConfig) obj).versionCode) &&
                equalsString(this.versionName, ((GameChannelConfig) obj).versionName);
    }

    private boolean equalsString(String currStr, String compareStr) {
        if (null == currStr && null == compareStr) {
            return true;
        }
        if (currStr != null && currStr.equals(compareStr)) {
            return true;
        }
        return false;
    }
}
