package com.qinglan.tool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import com.qinglan.tool.entity.ApkInfo;
import com.qinglan.tool.util.ApkUtil;
import com.qinglan.tool.util.FileUtil;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.xml.Channel;
import com.qinglan.tool.xml.Filter;
import com.qinglan.tool.xml.XmlTool;
import org.w3c.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.qinglan.tool.ChannelManager.ROOT_PATH;
import static com.qinglan.tool.util.FileUtil.createFileDir;
import static com.qinglan.tool.util.FileUtil.searchApk;

public class Decoder extends BaseCompiler {
    private static final String TAG_APPLICATION = "application";
    private static final String TAG_ACTIVITY = "activity";
    private static final String TAG_SERVICE = "service";
    private static final String TAG_PROVIDER = "provider";
    private static final String TAG_RECEIVER = "receiver";
    private static final String TAG_METADATA = "meta-data";
    private static final String ATTRIBUTE_ANDROID_NAME = "android:name";
    private static final String ATTRIBUTE_ANDROID_RESOURCE = "android:resource";

    //    private String mApkPath;
    private ApkInfo mApkInfo;

    public Decoder(Channel c, List<Channel> channels) {
        super(c, channels);
    }

    public int decode() {
        int result = -1;
        String path = createOutDir();
        if (!Utils.isEmpty(path)) {
            try {
//                mApkPath = path;
                mApkInfo = new ApkUtil().getApkInfo(path);
                FileUtil.delFolder(OUT_PATH);
                FileUtil.createFileDir(OUT_PATH);
                apkDecode(path);
//            String scriptPath = String.format("%s d %s -o %s -s -f", APKTOOL_PATH, path, OUT_PATH);
//            result = Utils.execShell(scriptPath);
                return 0;
            } catch (AndrolibException e) {
                e.printStackTrace();
            } catch (DirectoryException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String createOutDir() {
        File apk = searchApk(ROOT_PATH);
        if (apk != null) {
            decodeApkName = apk.getName().substring(0, apk.getName().indexOf("."));
            createFileDir(getOutDirPath(decodeApkName));
//                FileUtil.createOutDir(apk, new File(outPath + apk.getName()));
            return apk.getAbsolutePath();
        }
        return null;
    }

    private void apkDecode(String apkPath) throws AndrolibException, IOException, DirectoryException {
        ApkDecoder decoder = new ApkDecoder();
        decoder.setForceDelete(true);
        decoder.setDecodeSources((short) 0);
        decoder.setOutDir(new File(OUT_PATH));
        decoder.setApkFile(new File(apkPath));
        decoder.decode();
    }

    public String updateConfig(String appId, String cpId, String appKey) {
        updateManifest(appId, cpId, appKey);
        return decodeApkName;
    }

    private void updateManifest(String appId, String cpId, String appKey) {
        Document document = XmlTool.createDocument(MANIFEST_PATH);
        delUnrelatedManifest(addSDKInfo(document));
        updateManifestTag(mApkInfo.getPackageName(), appId, cpId, appKey);
    }

    private Document addSDKInfo(Document document) {
        if (document.getElementsByTagName("uses-sdk") != null) {
            return document;
        }

        Element sdkElm = document.createElement("uses-sdk");
        Attr minSdkAttr = document.createAttribute("android:minSdkVersion");
        minSdkAttr.setValue(mApkInfo.getSdkVersion());
        Attr targetSdkAttr = document.createAttribute("android:targetSdkVersion");
        targetSdkAttr.setValue(mApkInfo.getTargetSdkVersion());
        sdkElm.setAttributeNode(minSdkAttr);
        sdkElm.setAttributeNode(targetSdkAttr);
        XmlTool.addElement(document, sdkElm);
        return document;
    }

    private void delUnrelatedManifest(Document document) {
        for (Channel channel : exceptChannels) {
//            if (null == channel.getFilter().getPackageNameList() || channel.getFilter().getPackageNameList().isEmpty()) {
//                continue;
//            }
            delUnrelatedTag(document, channel);
        }
    }

    private void delUnrelatedTag(Document document, Channel channel) {
        NodeList nodeList = XmlTool.getDocumentRootNodeList(document);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (!node.getNodeName().equals(TAG_APPLICATION)) {
                continue;
            }
            NodeList applicationList = node.getChildNodes();

            for (int j = 0; j < applicationList.getLength(); j++) {
                Node component = applicationList.item(j);
                if (component.getNodeName().equals(TAG_ACTIVITY)
                        || component.getNodeName().equals(TAG_SERVICE)
                        || component.getNodeName().equals(TAG_PROVIDER)
                        || component.getNodeName().equals(TAG_METADATA)
                        || component.getNodeName().equals(TAG_RECEIVER)) {
                    delTag(component, channel);
                }
            }
        }
        XmlTool.saveXml(document, MANIFEST_PATH);
    }

    private void delTag(Node item, Channel channel) {
        Node attributeNameNode = item.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_NAME);
        String attributeName = attributeNameNode.toString();
//        Log.dln("=======\n" + attributeName.substring(attributeName.indexOf('"') + 1, attributeName.lastIndexOf('"')));
        if (null != channel.getFilter().getPackageNameList() && !channel.getFilter().getPackageNameList().isEmpty()) {
            for (Filter.Package pk : channel.getFilter().getPackageNameList()) {
                if (attributeName.contains(pk.getName())) {
                    item.getParentNode().removeChild(item);
                }
            }
        }
        if (!item.hasChildNodes() || null == channel.getFilter().getResNames() || channel.getFilter().getResNames().isEmpty()) {
            return;
        }
        //查找标签下包含android:resource属性的值
        NodeList subItems = item.getChildNodes();
        for (int i = 0; i < subItems.getLength(); i++) {
            Node subItem = subItems.item(i);
            if (subItem.hasAttributes() && subItem.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_RESOURCE) != null) {
                Node attributeResNode = subItem.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_RESOURCE);
                String attributeRes = attributeResNode.toString();
                String attributeResName = attributeRes.substring(attributeRes.indexOf('"') + 1, attributeRes.lastIndexOf('"'));
                for (String name : channel.getFilter().getResNames()) {
                    String input = attributeResName.substring(attributeResName.indexOf("/") + 1);
                    if (Utils.matches(name, input)) {
                        item.getParentNode().removeChild(item);
                    }
                }
            }
        }

    }

    private void updateManifestTag(String packageName, String appId, String cpId, String appKey) {
        if (null == packageName || packageName.isEmpty())
            return;
        String manifest = FileUtil.readAndReplaceFile(MANIFEST_PATH, PACKAGE_NAME_TAG, packageName);
        String[] lines = manifest.split("\n");
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            if (line.contains(APP_ID_TAG)) {
                line = line.replace(APP_ID_TAG, appId);
            } else if (line.contains(CP_ID_TAG)) {
                line = line.replace(CP_ID_TAG, cpId);
            } else if (line.contains(APP_KEY_TAG)) {
                line = line.replace(APP_KEY_TAG, appKey);
            }
            sb.append(line + "\n");
        }
        FileUtil.writer2File(MANIFEST_PATH, sb.toString());
    }
}