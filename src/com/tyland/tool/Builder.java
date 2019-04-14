package com.tyland.tool;

import brut.androlib.Androlib;
import brut.androlib.meta.MetaInfo;
import brut.androlib.res.util.ExtFile;
import brut.common.BrutException;
import com.tyland.common.Log;
import com.tyland.tool.entity.AppConfig;
import com.tyland.tool.util.Utils;

import java.io.File;
import java.util.*;

import static com.tyland.tool.ChannelManager.*;

public class Builder extends BaseCompiler {
//    private static final String BUILD_PATH = OUT_PATH + File.separator + "build";
//    private static final String BUILD_APK_PATH = BUILD_PATH + File.separator + "apk";
    private static final String DEFAULT_APK_NAME = "build.apk";
    private static final String MIN_SDK = "minSdkVersion";
    private static final String TARGET_SDK = "targetSdkVersion";
    private String mApkPackageName;
    private Map<String, String> applicationIcons;

    private static final String APK_FILE_NAME = "game.apk";

    private String apkBuildPath;

    public Builder(String apkName) {
        super(apkName);
    }

    public int build() {
        int result = STATUS_FAIL;
        try {
            result = buildApk(yjConfig);
            return result;
        } catch (BrutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getApkBuildPath() {
        return apkBuildPath;
    }

    private int buildApk(YJConfig config) throws BrutException {
        String apkName = DEFAULT_APK_NAME;
        if (!Utils.isEmpty(apkFileName)) {
            apkName = String.format("%s-%s.apk", apkFileName, "unsigned");
        }

        apkBuildPath = getOutDirPath() + apkName;
        return execBuild(config.apkInfo);
    }

    private int execBuild(AppConfig app) throws BrutException {
        Androlib androlib = new Androlib();
        File appDir = new File(getDecodeApkPath());
        MetaInfo metaInfo = androlib.readMetaFile(new ExtFile(appDir));
        if (app != null) {
            metaInfo.versionInfo.versionCode = app.getVersionCode();
            metaInfo.versionInfo.versionName = app.getVersionName();
            metaInfo.apkFileName = APK_FILE_NAME;
            metaInfo.sdkInfo.put(MIN_SDK, app.getMinSdk());
            if (!Utils.isEmpty(app.getTargetSdk()))
                metaInfo.sdkInfo.put(TARGET_SDK, app.getTargetSdk());
        }
        androlib.writeMetaFile(appDir, metaInfo);
//        androlib.buildResourcesFull(appDir, metaInfo.usesFramework);
//        FileUtils.delFolder(RES_PATH);
//        FileUtils.deleteFile(MANIFEST_PATH);
//        FileUtils.copyFolder(new File(BUILD_APK_PATH), new File(OUT_PATH));
//        FileUtils.delFolder(BUILD_PATH);
        Log.iln("build path=" + apkBuildPath);
//        androlib.build(appDir, new File(apkPath));
        return executeBuild(apkBuildPath);
    }

    private int executeBuild(String apkPath) {
//        String cmd="%s b %s -o %s -a %s";
        String cmd = "%s b %s -o %s";
        String scriptPath = String.format(cmd, APKTOOL_PATH, getDecodeApkPath(), apkPath/*, BIN_PATH + File.separator + "aapt.exe"*/);
        return Utils.execShell(progressListener, scriptPath);
    }
}
