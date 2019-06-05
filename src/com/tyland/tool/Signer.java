package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.tyland.tool.Main.ROOT_PATH;

public class Signer extends BaseCompiler {
    private static final String SUFFIX_NAME = "-out.apk";
    private String signApkPath;

    public Signer(String apkName) {
        super(apkName);
    }

    public int sign(String unsignedApkPath) throws IOException {
        return signApk(unsignedApkPath);
    }

    private int signApk(String unsignedApkPath) throws IOException {
        String scriptPath = BIN_PATH + File.separator + "Sign.bat";

        String apkName = apkFileName + SUFFIX_NAME;
        signApkPath = getOutDirPath() + apkName;
        FileUtils.deleteFile(signApkPath);
        Log.dln("signApk apk path: " + signApkPath);
        int result = Utils.execShell(progressListener, scriptPath, unsignedApkPath, signApkPath);
//        FileUtils.deleteFile(apkPath);
        return result;
    }

    public String getSignApkPath() {
        return signApkPath;
    }
}
