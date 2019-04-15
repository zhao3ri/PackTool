package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.SignInfo;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
import com.tyland.tool.entity.Channel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.tyland.tool.Main.ROOT_PATH;

public class Signer extends BaseCompiler {
    private static final String SUFFIX_NAME = "-out.apk";

    public Signer(String apkName) {
        super(apkName);
    }

    public int sign(String unsignedApkPath) throws IOException {
        return signApk(unsignedApkPath);
    }

    private int signApk(String unsignedApkPath) throws IOException {
        String scriptPath = BIN_PATH + File.separator + "Sign.bat";

        String apkName = apkFileName + SUFFIX_NAME;
        File signApkFile = new File(getOutDirPath() + apkName);
        String signApkPath = signApkFile.getCanonicalPath();
//        FileUtils.deleteFile(signApkPath);
        Log.iln("sign apk path: " + signApkPath);
        int result = Utils.execShell(progressListener, scriptPath, unsignedApkPath, apkName);
//        FileUtils.deleteFile(apkPath);
        return result;
    }
}
