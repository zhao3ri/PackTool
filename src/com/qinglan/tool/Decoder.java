package com.qinglan.tool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import com.qinglan.tool.entity.ApkInfo;
import com.qinglan.tool.util.FileUtils;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.entity.Channel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.qinglan.tool.ChannelManager.CODE_NO_FIND;
import static com.qinglan.tool.util.FileUtils.createFileDir;

public class Decoder extends BaseCompiler {
    private ApkInfo mApkInfo;
    private String minSdk;
    private String targetSdk;
    private String versionCode;
    private String versionName;

    public Decoder(ApkInfo apkInfo, Channel c, List<Channel> channels) {
        super(c, channels);
        mApkInfo = apkInfo;
    }

    public int decode(String path) {
        int result = -1;
        if (Utils.isEmpty(path)) {
            return CODE_NO_FIND;
        }
        try {
            FileUtils.delFolder(OUT_PATH);
            createFileDir(OUT_PATH);
//            apkDecode(path);
            String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/"%s d %s -o %s -f", APKTOOL_PATH, path, OUT_PATH);
            result = Utils.execShell(scriptPath);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setMinSdk(String min) {
        this.minSdk = min;
    }

    public void setTargetSdk(String target) {
        this.targetSdk = target;
    }

    public void setVersionCode(String code) {
        this.versionCode = code;
    }

    public void setVersionName(String name) {
        this.versionName = name;
    }

    private void apkDecode(String apkPath) throws AndrolibException, IOException, DirectoryException {
        ApkDecoder decoder = new ApkDecoder();
        decoder.setForceDelete(true);
        decoder.setDecodeSources((short) 0);
        decoder.setOutDir(new File(OUT_PATH));
        decoder.setApkFile(new File(apkPath));
        decoder.decode();
    }

    public void updateConfig(String appId, String appKey, String cpId, String cpKey, String suffix) {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, MANIFEST_PATH);
        manifestHelper.addVersionInfo(versionCode, versionName);
        manifestHelper.addSdkInfo(minSdk, targetSdk);
        manifestHelper.replaceLauncher(currChannel);
        for (Channel channel : exceptChannels) {
            manifestHelper.deleteUnrelatedChannelInfo(channel);
        }
        String packageName = mApkInfo.getPackageName();
        String[] targets;
        String[] replaces;
        if (Utils.isEmpty(suffix)) {
            targets = new String[]{PACKAGE_NAME_TAG, APP_ID_TAG, CP_ID_TAG, APP_KEY_TAG, CP_KEY_TAG, LAUNCHER_TAG};
            replaces = new String[]{packageName, appId, cpId, appKey, cpKey, mApkInfo.getLaunchableActivity()};
        } else {
            String completePkg = String.format("%s.%s", packageName, suffix);
            targets = new String[]{PACKAGE_NAME_TAG, APP_ID_TAG, CP_ID_TAG, APP_KEY_TAG, CP_KEY_TAG, LAUNCHER_TAG, packageName};
            replaces = new String[]{packageName, appId, cpId, appKey, cpKey, mApkInfo.getLaunchableActivity(), completePkg};
        }
        //必须在最后一步才进行替换，否则可能会导致错误
        manifestHelper.updateManifestConfig(targets, replaces);
    }
}
