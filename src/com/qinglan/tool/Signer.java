package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.util.FileUtils;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.entity.Channel;

import java.io.File;
import java.util.List;

import static com.qinglan.tool.Main.ROOT_PATH;

public class Signer extends BaseCompiler {
    private static final String DEFAULT_KEYSTORE_PATH = ROOT_PATH + File.separator + "default.jks";
    private static final String DEFAULT_KEYSTORE_PASSWORDS = "123456";
    private static final String DEFAULT_KEYSTORE_ALIAS = "qinglan";

    public Signer(List<Channel> channels) {
        super(null, channels);
    }

    public Signer(Channel c, List<Channel> channels) {
        super(c, channels);
    }

    public void sign(String apkPath, String... args) {
        if (args != null && args.length == 3) {
            String keystorePath = args[0];
            String keystorePass = args[1];
            String keystoreAlias = args[2];
            signApk(apkPath, keystorePath, keystorePass, keystoreAlias);
        } else {
            signApk(apkPath, null, null, null);
        }
    }

    private int signApk(String apkPath, String keystorePath, String keystorePass, String keystoreAlias) {
        String scriptPath = BIN_PATH + File.separator + "apk-sign.bat";
        if (Utils.isEmpty(keystorePath)) {
            keystorePath = DEFAULT_KEYSTORE_PATH;
        }
        if (Utils.isEmpty(keystorePass)) {
            keystorePass = DEFAULT_KEYSTORE_PASSWORDS;
        }
        if (Utils.isEmpty(keystoreAlias)) {
            keystoreAlias = DEFAULT_KEYSTORE_ALIAS;
        }
        String apkName = apkPath.substring(apkPath.lastIndexOf(File.separator) + 1);
        String signApkPath = getOutDirPath(decodeApkName) + apkName;
        FileUtils.deleteFile(signApkPath);
        Log.dln("sign apk path: " + signApkPath);
        int result = Utils.execShell(scriptPath, keystorePath, keystorePass, signApkPath, apkPath, keystoreAlias);
        FileUtils.deleteFile(apkPath);
        return result;
    }
}
