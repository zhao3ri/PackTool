package com.qinglan.tool;

import com.qinglan.tool.xml.Channel;

import java.io.File;
import java.util.List;

import static com.qinglan.tool.ChannelManager.ROOT_PATH;

public abstract class BaseCompiler {
    //    public static final String ROOT_PATH = "H:\\PackTools";
    public static final String BIN_PATH = ROOT_PATH + File.separator + "bin";
    public static final String APKTOOL_PATH = BIN_PATH + File.separator + "apktool.bat";
    public static final String OUT_PATH = BIN_PATH + File.separator + "out";
    public static final String MANIFEST_PATH = OUT_PATH + File.separator + "AndroidManifest.xml";
    public static final String OUT_DIR_PREFIX = "out-";
    /**
     * 替换packageName的标记
     */
    public static final String PACKAGE_NAME_TAG = "qinglanGameApplicationId";
    public static final String APP_ID_TAG = "qinglanChannelAppId";
    public static final String APP_KEY_TAG = "qinglanChannelAppKey";
    public static final String CP_ID_TAG = "qinglanChannelCpId";
    /**
     * 第三方渠道res配置文件名
     */
    public static final String RES_NAME_APP_ID = "qlsdk_third_party_appid";
    public static final String RES_NAME_APP_KEY = "qlsdk_third_party_appkey";
    public static final String RES_NAME_PUBLIC_KEY = "qlsdk_third_party_pubkey";
    public static final String RES_NAME_CP_ID = "qlsdk_third_party_cpid";
    public static final String RES_NAME_SECRET_KEY = "qlsdk_third_party_seckey";

    protected Channel currChannel;
    protected List<Channel> exceptChannels;
    protected String decodeApkName;

    private BaseCompiler() {
    }

    public BaseCompiler(Channel c, List<Channel> channels) {
        currChannel = c;
        channels.remove(currChannel);
        exceptChannels = channels;
    }

    protected String getOutDirPath(String apkName) {
        return ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkName + File.separator;
    }

    public void setApkName(String apk) {
        decodeApkName = apk;
    }

}
