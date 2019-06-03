package com.tyland.tool;

import brut.androlib.Androlib;
import brut.common.BrutException;
import com.tyland.common.Log;
import com.tyland.tool.entity.AppVersionInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;

import java.io.File;
import java.util.*;

import static com.tyland.tool.ChannelManager.*;

public class Builder extends BaseCompiler {
    private static final String DEFAULT_APK_NAME = "build-unsigned.apk";
    private String mApkPackageName;
    private Map<String, String> applicationIcons;

    private String apkBuildPath;

    public Builder(String apkName) {
        super(apkName);
    }

    public int build() {
        int result = STATUS_FAIL;
        try {
            result = buildApk();
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

    private int buildApk() throws BrutException {
        String apkName = DEFAULT_APK_NAME;
        if (!Utils.isEmpty(apkFileName)) {
            apkName = String.format("%s-%s.apk", apkFileName, "unsigned");
        }

        apkBuildPath = getOutDirPath() + apkName;
        FileUtils.deleteFile(apkBuildPath);
        return execBuild();
//        return execBuild(apkBuildPath);
    }

    private int execBuild() throws BrutException {
//        androlib.buildResourcesFull(appDir, metaInfo.usesFramework);
//        FileUtils.delFolder(RES_PATH);
//        FileUtils.deleteFile(MANIFEST_PATH);
//        FileUtils.copyFolder(new File(BUILD_APK_PATH), new File(OUT_PATH));
        Log.dln("build path=" + apkBuildPath);
        //        String cmd="%s b %s -o %s -a %s";
        String cmd = "%s b %s -o %s";
        String scriptPath = String.format(cmd, APKTOOL_PATH, getDecodeApkPath(), apkBuildPath/*, BIN_PATH + File.separator + "aapt.exe"*/);
        return Utils.execShell(progressListener, scriptPath);
    }

    private int execBuild(String buildPath) {
        int result = STATUS_FAIL;
        Androlib androlib = new Androlib();
        File appDir = new File(getDecodeApkPath());
        try {
            androlib.build(appDir, new File(buildPath));
            result = STATUS_SUCCESS;
        } catch (BrutException e) {
            e.printStackTrace();
        }
        return result;
    }
}
