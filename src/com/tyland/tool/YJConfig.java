package com.tyland.tool;

import com.tyland.tool.entity.AppConfig;

import static com.tyland.tool.util.Utils.equalsString;

public class YJConfig {
    public static final String META_DATA_CHANNEL_KEY = "GAORE_CHANNEL_KEY";
    public static final String META_DATA_GAME_ID = "GAORE_GAME_ID";
    public static final String META_DATA_GAME_KEY = "GAORE_APP_KEY";
    public static final String META_DATA_GAME_VERSION = "GAORE_GAME_VERSION";

    public String appName;
    public String packageName;
    public String channelKey;
    public String gameId;
    public String gameKey;
    public String gameVersion;
    public AppConfig apkInfo;

    @Override
    public boolean equals(Object obj) {
        return equalsString(this.appName, ((YJConfig) obj).appName) &&
                equalsString(this.packageName, ((YJConfig) obj).packageName) &&
                equalsString(this.channelKey, ((YJConfig) obj).channelKey) &&
                equalsString(this.gameId, ((YJConfig) obj).gameId) &&
                equalsString(this.gameVersion, ((YJConfig) obj).gameVersion) &&
                equalsString(this.gameKey, ((YJConfig) obj).gameKey) &&
                this.apkInfo.equals(((YJConfig) obj).apkInfo);
    }

    public void updateAppInfo(String min, String target, String vcode, String vname) {
        if (this.apkInfo == null) {
            this.apkInfo = new AppConfig();
        }
        this.apkInfo.update(min, target, vcode, vname);
    }
}
