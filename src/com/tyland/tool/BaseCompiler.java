package com.tyland.tool;

import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.ShellUtils;

import java.io.File;

import static com.tyland.tool.Main.ROOT_PATH;

public abstract class BaseCompiler {
    public static final String BIN_PATH = ROOT_PATH + File.separator + "bin";
    public static final String APKTOOL_PATH = BIN_PATH + File.separator + "apktool.bat";
    public static final String DRAWABLE_ICON_NAME = "icon.png";
    public static final String REPLACE_RES_PATH = ROOT_PATH + File.separator + "res" + File.separator;
    public static final String REPLACE_ICON_PATH = REPLACE_RES_PATH + DRAWABLE_ICON_NAME;
    public static final String OUT_DIR_NAME = "output";
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
        return ROOT_PATH + File.separator + OUT_DIR_NAME + File.separator;
    }

    protected String getDecodeApkPath() {
        return getOutDirPath() + "build";
    }

    protected String getManifestPath() {
        return getDecodeApkPath() + File.separator + "AndroidManifest.xml";
    }

    protected String getResDirPath() {
        return getDecodeApkPath() + File.separator + "res";
    }

    protected String getSmaliPath() {
        return getDecodeApkPath() + File.separator + "smali";
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
