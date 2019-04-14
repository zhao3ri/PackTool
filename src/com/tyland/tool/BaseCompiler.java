package com.tyland.tool;

import com.tyland.tool.entity.Channel;
import com.tyland.tool.entity.GameChannelConfig;
import com.tyland.tool.util.ShellUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.tyland.tool.Main.ROOT_PATH;

public abstract class BaseCompiler {
    //    public static final String ROOT_PATH = "H:\\PackTools";
    public static final String BIN_PATH = ROOT_PATH + File.separator + "bin";
    public static final String APKTOOL_PATH = BIN_PATH + File.separator + "apktool.bat";
    //    public static final String OUT_PATH = BIN_PATH + File.separator + "out";
//    public static final String MANIFEST_PATH = OUT_PATH + File.separator + "AndroidManifest.xml";
    //    public static final String SMALI_PATH = OUT_PATH + File.separator + "smali";
//    public static final String ASSETS_PATH = OUT_PATH + File.separator + "assets";
//    public static final String LIBS_PATH = OUT_PATH + File.separator + "lib";
//    public static final String RES_PATH = OUT_PATH + File.separator + "res";
//    public static final String ANIM_PATH = RES_PATH + File.separator + "anim";
//    public static final String DRAWABLE_PATH = RES_PATH + File.separator + "drawable*";
//    public static final String LAYOUT_PATH = RES_PATH + File.separator + "layout";
//    public static final String VALUES_PATH = RES_PATH + File.separator + "values*";
    public static final String OUT_DIR_PREFIX = "out-";
    public static final String APK_SUFFIX = ".apk";

    public static final String DRAWABLE_ICON_LAUNCHER = "ic_launcher";

    protected String apkFileName;

    protected YJConfig yjConfig;

    protected ShellUtils.ProgressListener progressListener;

    private BaseCompiler() {
    }

    public BaseCompiler(String apkName) {
        apkFileName = apkName;
    }

    protected String getOutDirPath() {
        return ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkFileName + File.separator;
    }

    protected String getDecodeApkPath() {
        return getOutDirPath() + "game" + File.separator;
    }

    protected String getManifestPath() {
        return getDecodeApkPath() + File.separator + "AndroidManifest.xml";
    }

    protected String getResDirPath() {
        return getDecodeApkPath() + File.separator + "res";
    }

    public void setApkName(String apk) {
        apkFileName = apk;
    }

    public void setConfig(YJConfig config) {
        this.yjConfig = config;
    }

    public void setProgressListener(ShellUtils.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
