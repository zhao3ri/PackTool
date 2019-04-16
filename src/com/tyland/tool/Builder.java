package com.tyland.tool;

import brut.common.BrutException;
import com.tyland.common.Log;
import com.tyland.tool.entity.AppVersionInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.Utils;

import java.util.*;

import static com.tyland.tool.ChannelManager.*;

public class Builder extends BaseCompiler {
    private static final String DEFAULT_APK_NAME = "build.apk";
    private String mApkPackageName;
    private Map<String, String> applicationIcons;

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
        return execBuild(config.appInfo);
    }

    private int execBuild(AppVersionInfo app) throws BrutException {
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
