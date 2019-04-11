package com.tyland.tool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.entity.AppConfig;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
import com.tyland.tool.entity.Channel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.tyland.tool.ChannelManager.STATUS_NO_FIND;
import static com.tyland.tool.util.FileUtils.createFileDir;

public class Decoder extends BaseCompiler {
    private ApkInfo mApkInfo;

    public Decoder(ApkInfo apkInfo, Channel c, List<Channel> channels, String apkName) {
        super(c, channels, apkName);
        mApkInfo = apkInfo;
    }

    public int decode(String path) {
        int result = -1;
        if (Utils.isEmpty(path)) {
            return STATUS_NO_FIND;
        }
        try {
            FileUtils.delFolder(OUT_PATH);
            createFileDir(OUT_PATH);
//            apkDecode(path);
            String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/"%s d %s -o %s -f", APKTOOL_PATH, path, OUT_PATH);
            result = Utils.execShell(progressListener, scriptPath);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void apkDecode(String apkPath) throws AndrolibException, IOException, DirectoryException {
        ApkDecoder decoder = new ApkDecoder();
        decoder.setForceDelete(true);
        decoder.setDecodeSources((short) 0);
        decoder.setOutDir(new File(OUT_PATH));
        decoder.setApkFile(new File(apkPath));
        decoder.decode();
    }

    public void updateManifest() {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, MANIFEST_PATH);
        AppConfig app = config.getAppInfo();
        if (app != null) {
            manifestHelper.addVersionInfo(app.getVersionCode(), app.getVersionName());
            manifestHelper.addSdkInfo(app.getMinSdk(), app.getTargetSdk());
        }
        manifestHelper.updateMetaData("", "");
        if (currChannel != null)
            manifestHelper.replaceLauncher(currChannel);
        if (exceptChannels != null)
            for (Channel channel : exceptChannels) {
                manifestHelper.deleteUnrelatedChannelInfo(channel);
            }

        //必须在最后一步才进行替换，否则可能会导致错误
//        replacePackage(manifestHelper);
        manifestHelper.updatePackageName();
    }

    private void replacePackage(ManifestHelper manifestHelper) {
        String packageName = mApkInfo.getPackageName();
        String[] targets;
        String[] replaces;
        if (Utils.isEmpty(config.getPackageName())) {
            targets = new String[]{PACKAGE_NAME_TAG, APP_ID_TAG, CP_ID_TAG, APP_KEY_TAG, CP_KEY_TAG, LAUNCHER_TAG};
            replaces = new String[]{packageName, config.getAppId(), config.getCpId(), config.getAppKey(), config.getCpKey(), mApkInfo.getLaunchableActivity()};
        } else {
            String replacePkg = config.getPackageName();
            if (config.isSuffix()) {
                replacePkg = String.format("%s.%s", packageName, config.getPackageName());
            }
            targets = new String[]{PACKAGE_NAME_TAG, APP_ID_TAG, CP_ID_TAG, APP_KEY_TAG, CP_KEY_TAG, LAUNCHER_TAG, packageName};
            replaces = new String[]{packageName, config.getAppId(), config.getCpId(), config.getAppKey(), config.getCpKey(), mApkInfo.getLaunchableActivity(), replacePkg};
        }
        manifestHelper.updateManifestConfig(targets, replaces);
    }
}
