package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.SignInfo;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
import com.tyland.tool.entity.Channel;

import java.io.File;
import java.util.List;

import static com.tyland.tool.Main.ROOT_PATH;

public class Signer extends BaseCompiler {
    private static final String DEFAULT_KEYSTORE_PATH = ROOT_PATH + File.separator + "default.jks";
    private static final String DEFAULT_KEYSTORE_PASSWORDS = "123456";
    private static final String DEFAULT_KEYSTORE_ALIAS = "tyland";

    public Signer(Channel c, List<Channel> channels, String apkName) {
        super(c, channels, apkName);
    }

    public int sign(String apkPath, String keystorePath, String keystorePass, String keystoreAlias) {
        SignInfo signInfo = null;
        if (!Utils.isEmpty(keystorePath) && !Utils.isEmpty(keystorePass) && !Utils.isEmpty(keystoreAlias)) {
            keystorePath = DEFAULT_KEYSTORE_PATH;
            keystorePass = DEFAULT_KEYSTORE_PASSWORDS;
            keystoreAlias = DEFAULT_KEYSTORE_ALIAS;
            signInfo = new SignInfo(keystorePath, keystorePass, keystoreAlias);
        }
        return sign(apkPath, signInfo);
    }

    public int sign(String apkPath, SignInfo signInfo) {
        if (signInfo == null) {
            signInfo = new SignInfo(DEFAULT_KEYSTORE_PATH, DEFAULT_KEYSTORE_PASSWORDS, DEFAULT_KEYSTORE_ALIAS);
        }
        return signApk(apkPath, signInfo);
    }

    private int signApk(String apkPath, SignInfo signInfo) {
        String scriptPath = BIN_PATH + File.separator + "apk-sign.bat";

        String apkName = apkPath.substring(apkPath.lastIndexOf(File.separator) + 1);
        String signApkPath = getOutDirPath(apkFileName) + apkName;
        FileUtils.deleteFile(signApkPath);
        Log.dln("sign apk path: " + signApkPath);
        int result = Utils.execShell(scriptPath, signInfo.getPath(), signInfo.getPasswords(), signApkPath, apkPath, signInfo.getAlias());
        FileUtils.deleteFile(apkPath);
        return result;
    }
}
