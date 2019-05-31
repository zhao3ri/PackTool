package com.tyland.tool;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.meta.MetaInfo;
import brut.androlib.res.util.ExtFile;
import brut.directory.DirectoryException;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.entity.AppVersionInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
import com.tyland.tool.xml.XmlTool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;

import static com.tyland.tool.ChannelManager.*;
import static com.tyland.tool.entity.YJConfig.*;
import static com.tyland.tool.util.FileUtils.createFileDir;
import static com.tyland.tool.util.FileUtils.getPath;

public class Decoder extends BaseCompiler {
    private ApkInfo mApkInfo;

    public Decoder(ApkInfo apkInfo, String apkName) {
        super(apkName);
        mApkInfo = apkInfo;
    }

    public int decode(String path) {
        int result = STATUS_FAIL;
        if (Utils.isEmpty(path)) {
            return STATUS_NO_FIND;
        }
        try {
            FileUtils.delFolder(getDecodeApkPath());
            createFileDir(getDecodeApkPath());
//            apkDecode(path);
            String cmd = "%s d %s -o %s -f";
            String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/cmd, APKTOOL_PATH, path, getDecodeApkPath());
            result = Utils.execShell(progressListener, scriptPath);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void apkDecode(String apkPath) throws AndrolibException, IOException, DirectoryException {
        ApkDecoder decoder = new ApkDecoder();
        decoder.setForceDelete(true);
        decoder.setDecodeSources((short) 0);
        decoder.setOutDir(new File(getDecodeApkPath()));
        decoder.setApkFile(new File(apkPath));
        decoder.decode();
    }

    public YJConfig getDecodeResult(YJConfig c) {
        if (c == null) {
            c = new YJConfig();
            c.appName = mApkInfo.getApplicationLable();
            c.appInfo = new AppVersionInfo(mApkInfo.getSdkVersion(), mApkInfo.getTargetSdkVersion(), mApkInfo.getVersionCode(), mApkInfo.getVersionName());
        }
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, getManifestPath());
        c.channelKey = manifestHelper.getChannelKey();
        c.gameId = manifestHelper.getGameId();
        c.gameKey = manifestHelper.getGameKey();
        c.gameVersion = manifestHelper.getGameVersion();
        c.packageName = manifestHelper.getConfigPackageName();
        return c;
    }

    public void updateManifest(YJConfig c) {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, getManifestPath());
//        AppVersionInfo app = c.appInfo;
//        if (app != null) {
//            manifestHelper.addVersionInfo(app.getVersionCode(), app.getVersionName());
//            manifestHelper.addSdkInfo(app.getMinSdk(), app.getTargetSdk());
//        }
        String[] names = new String[]{META_DATA_CHANNEL_KEY, META_DATA_GAME_ID, META_DATA_GAME_KEY, META_DATA_GAME_VERSION};
        String[] values = new String[]{c.channelKey, c.gameId, c.gameKey, c.gameVersion};
        //更新meta-data
        manifestHelper.updateMetaData(names, values);
        //更新包名
        manifestHelper.updatePackageName();
        if (!mApkInfo.getApplicationLable().equals(c.appName)) {
            manifestHelper.updateAppName(c.appName);
            ResourceHelper resHelper = new ResourceHelper(getResDirPath());
            resHelper.updateResourceAppName(c.appName);
        }
    }

    private static final String APK_FILE_NAME = "game.apk";
    private static final String MIN_SDK = "minSdkVersion";
    private static final String TARGET_SDK = "targetSdkVersion";

    public void updateYml(AppVersionInfo app) throws AndrolibException {
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
    }


}
