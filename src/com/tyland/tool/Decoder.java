package com.tyland.tool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.entity.AppConfig;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
import com.tyland.tool.entity.Channel;
import com.tyland.tool.xml.XmlTool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.tyland.tool.ChannelManager.STATUS_DECODE_SUCCESS;
import static com.tyland.tool.ChannelManager.STATUS_NO_FIND;
import static com.tyland.tool.YJConfig.META_DATA_CHANNEL_KEY;
import static com.tyland.tool.YJConfig.META_DATA_GAME_ID;
import static com.tyland.tool.YJConfig.META_DATA_GAME_KEY;
import static com.tyland.tool.util.FileUtils.createFileDir;
import static com.tyland.tool.util.FileUtils.getPath;

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
            String cmd = "%s d %s -o %s -f";
            String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/cmd, APKTOOL_PATH, path, OUT_PATH);
            result = Utils.execShell(progressListener, scriptPath);
            return STATUS_DECODE_SUCCESS;
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

    public YJConfig createConfig() {
        YJConfig c = new YJConfig();
        c.apkInfo = new AppConfig(mApkInfo.getSdkVersion(), mApkInfo.getTargetSdkVersion(), mApkInfo.getVersionCode(), mApkInfo.getVersionName());
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, MANIFEST_PATH);
        c.channelKey = manifestHelper.getChannelKey();
        c.gameId = manifestHelper.getGameId();
        c.gameKey = manifestHelper.getGameKey();
        c.packageName = manifestHelper.getConfigPackageName();
        return c;
    }

    public void updateManifest() {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, MANIFEST_PATH);
        AppConfig app = config.getAppInfo();
        if (app != null) {
            manifestHelper.addVersionInfo(app.getVersionCode(), app.getVersionName());
            manifestHelper.addSdkInfo(app.getMinSdk(), app.getTargetSdk());
        }
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

    public void updateManifest(YJConfig c) {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, MANIFEST_PATH);
        AppConfig app = config.getAppInfo();
        if (app != null) {
            manifestHelper.addVersionInfo(app.getVersionCode(), app.getVersionName());
            manifestHelper.addSdkInfo(app.getMinSdk(), app.getTargetSdk());
        }
        String[] names = new String[]{META_DATA_CHANNEL_KEY, META_DATA_GAME_ID, META_DATA_GAME_KEY};
        String[] values = new String[]{c.channelKey, c.gameId, c.gameKey};
        //更新meta-data
        manifestHelper.updateMetaData(names, values);
        //更新包名
        manifestHelper.updatePackageName();
        if (!mApkInfo.getApplicationLable().equals(c.appName)) {
            manifestHelper.updateAppName(c.appName);
            updateResourceAppName(c.appName);
        }
    }

    private static final String RES_VALUE = "value";

    public boolean updateResourceAppName(String appName) {
        File resDirFile = new File(RES_PATH);
        if (resDirFile.exists() && resDirFile.isDirectory()) {
            String[] resStringNames = resDirFile.list();
            for (String name : resStringNames) {
                String resPath = resDirFile.getAbsolutePath() + File.separator + name;
                if (name.startsWith(RES_VALUE)) {
                    readValues(resPath, appName);
                }
            }
        }//if
        return true;
    }

    private void readValues(String resName, String appName) {
        File valueFile = new File(resName);
        if (valueFile.exists() && valueFile.isDirectory()) {
            String[] files = valueFile.list();
            for (String file : files) {
                updateValueXmlAppName(getPath(valueFile.getAbsolutePath()) + file, appName);
            }
        }
    }

    private static final String ATTRIBUTE_NAME = "name";
    private static final String ELEMENT_VALUES_STRING = "string";
    private static final String ATTRIBUTE_VALUES_APP_NAME = "app_name";

    private void updateValueXmlAppName(String path, String appName) {
        Document document = XmlTool.createDocument(path);
        NodeList nodeList = XmlTool.getDocumentRootNodeList(document);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getAttributes() != null && item.getAttributes().getNamedItem(ATTRIBUTE_NAME) != null) {
                //获取android:name的值
                Node attributeNode = item.getAttributes().getNamedItem(ATTRIBUTE_NAME);
                String name = attributeNode.getTextContent();
                //若当前是string资源，则查找替换渠道配置
                if (item.getNodeName().equals(ELEMENT_VALUES_STRING)) {
                    if (name.equals(ATTRIBUTE_VALUES_APP_NAME) && !Utils.isEmpty(appName)) {
                        item.setTextContent(appName);
                    }
                }
            }
        }
        XmlTool.saveXml(document, path);
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
