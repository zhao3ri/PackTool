package com.qinglan.tool;

import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.entity.GameChannelConfig;
import com.qinglan.tool.util.ShellUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.qinglan.tool.Main.ROOT_PATH;

public abstract class BaseCompiler {
    //    public static final String ROOT_PATH = "H:\\PackTools";
    public static final String BIN_PATH = ROOT_PATH + File.separator + "bin";
    public static final String APKTOOL_PATH = BIN_PATH + File.separator + "apktool.bat";
    public static final String OUT_PATH = BIN_PATH + File.separator + "out";
    public static final String MANIFEST_PATH = OUT_PATH + File.separator + "AndroidManifest.xml";
    public static final String SMALI_PATH = OUT_PATH + File.separator + "smali";
    public static final String ASSETS_PATH = OUT_PATH + File.separator + "assets";
    public static final String LIBS_PATH = OUT_PATH + File.separator + "lib";
    public static final String RES_PATH = OUT_PATH + File.separator + "res";
    public static final String ANIM_PATH = RES_PATH + File.separator + "anim";
    public static final String DRAWABLE_PATH = RES_PATH + File.separator + "drawable*";
    public static final String LAYOUT_PATH = RES_PATH + File.separator + "layout";
    public static final String VALUES_PATH = RES_PATH + File.separator + "values*";
    public static final String OUT_DIR_PREFIX = "out-";

    public static final String DRAWABLE_ICON_LAUNCHER = "ic_launcher";
    /**
     * 替换androidmanifest的标记
     */
    public static final String PACKAGE_NAME_TAG = "qinglanGameApplicationId";
    public static final String APP_ID_TAG = "qinglanChannelAppId";
    public static final String APP_KEY_TAG = "qinglanChannelAppKey";
    public static final String CP_ID_TAG = "qinglanChannelCpId";
    public static final String CP_KEY_TAG = "qinglanChannelCpKey";
    public static final String LAUNCHER_TAG = "qinglanChannelLauncher";

    public static final String CHANNEL_PACKAGE_NAME = "com.qinglan.sdk.android.channel";
    public static final String CHANNEL_SUB_NAME_ENTITY = "entity";

    protected Channel currChannel;
    protected List<Channel> exceptChannels;
    protected String apkFileName;

    protected GameChannelConfig config;

    protected ShellUtils.ProgressListener progressListener;

    private BaseCompiler() {
    }

    public BaseCompiler(Channel c, List<Channel> channels, String apkName) {
        currChannel = c;
        exceptChannels = new ArrayList<>(channels);
        exceptChannels.remove(currChannel);
        apkFileName = apkName;
    }

    protected String getOutDirPath(String apkName) {
        return ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkName + File.separator;
    }

    public void setApkName(String apk) {
        apkFileName = apk;
    }

    public void setConfig(GameChannelConfig config) {
        this.config = config;
    }

    public void setProgressListener(ShellUtils.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
