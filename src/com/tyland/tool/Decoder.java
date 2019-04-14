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

import static com.tyland.tool.ChannelManager.*;
import static com.tyland.tool.YJConfig.META_DATA_CHANNEL_KEY;
import static com.tyland.tool.YJConfig.META_DATA_GAME_ID;
import static com.tyland.tool.YJConfig.META_DATA_GAME_KEY;
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

    public YJConfig updateConfig(YJConfig c) {
        if (c == null) {
            c = new YJConfig();
            c.appName = mApkInfo.getApplicationLable();
            c.apkInfo = new AppConfig(mApkInfo.getSdkVersion(), mApkInfo.getTargetSdkVersion(), mApkInfo.getVersionCode(), mApkInfo.getVersionName());
        }
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, getManifestPath());
        c.channelKey = manifestHelper.getChannelKey();
        c.gameId = manifestHelper.getGameId();
        c.gameKey = manifestHelper.getGameKey();
        c.packageName = manifestHelper.getConfigPackageName();
        return c;
    }

    public void updateManifest(YJConfig c) {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, getManifestPath());
        AppConfig app = c.apkInfo;
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
        File resDirFile = new File(getResDirPath());
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
}
