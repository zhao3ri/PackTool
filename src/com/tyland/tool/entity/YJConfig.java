package com.tyland.tool.entity;

import static com.tyland.tool.util.Utils.equalsString;

public class YJConfig {
    public static final String META_DATA_CHANNEL_KEY = "YJSDK_CHANNEL_KEY";
    public static final String META_DATA_GAME_ID = "YJSDK_GAME_ID";
    public static final String META_DATA_GAME_KEY = "YJSDK_APP_KEY";
    public static final String META_DATA_GAME_VERSION = "YJSDK_GAME_VERSION";
    public static final String CHANNEL_CONFIG_FILE_NAME = "yjConfig.ini";
    public static final String DEFAULT_AGENT_ID = "1001";
    public static final String DEFAULT_SITE_ID = "1001";

    public String appName;
    public String packageName;
    public String channelKey;
    public String gameId;
    public String gameKey;
    public String gameVersion;
    public String agentId = DEFAULT_AGENT_ID;
    public String siteId = DEFAULT_SITE_ID;
    public AppVersionInfo appInfo;

    @Override
    public boolean equals(Object obj) {
        return equalsString(this.appName, ((YJConfig) obj).appName) &&
                equalsString(this.packageName, ((YJConfig) obj).packageName) &&
                equalsString(this.channelKey, ((YJConfig) obj).channelKey) &&
                equalsString(this.gameId, ((YJConfig) obj).gameId) &&
                equalsString(this.gameVersion, ((YJConfig) obj).gameVersion) &&
                equalsString(this.gameKey, ((YJConfig) obj).gameKey) &&
                equalsString(this.agentId, ((YJConfig) obj).agentId) &&
                equalsString(this.siteId, ((YJConfig) obj).siteId) &&
                this.appInfo.equals(((YJConfig) obj).appInfo);
    }

    public void updateAppInfo(String min, String target, String vcode, String vname) {
        if (this.appInfo == null) {
            this.appInfo = new AppVersionInfo();
        }
        this.appInfo.update(min, target, vcode, vname);
    }
}
