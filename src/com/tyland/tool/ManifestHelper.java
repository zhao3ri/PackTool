package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
import com.tyland.tool.entity.Channel;
import com.tyland.tool.entity.Filter;
import com.tyland.tool.xml.XmlTool;
import org.w3c.dom.*;

public class ManifestHelper {
    private static final String ELEMENT_APPLICATION = "application";
    private static final String ELEMENT_ACTIVITY = "activity";
    private static final String ELEMENT_SERVICE = "service";
    private static final String ELEMENT_PROVIDER = "provider";
    private static final String ELEMENT_RECEIVER = "receiver";
    private static final String ELEMENT_METADATA = "meta-data";
    private static final String ATTRIBUTE_ANDROID_NAME = "android:name";
    private static final String ATTRIBUTE_ANDROID_VALUE = "android:value";
    private static final String ATTRIBUTE_ANDROID_RESOURCE = "android:resource";
    private static final String ATTRIBUTE_ANDROID_LABEL = "android:label";
    private static final String ATTRIBUTE_ANDROID_VERSION_CODE = "android:versionCode";
    private static final String ATTRIBUTE_ANDROID_VERSION_NAME = "android:versionName";

    private static final String ELEMENT_USE_SDK = "uses-sdk";
    private static final String ELEMENT_PACKAGE = "package";
    private static final String ATTRIBUTE_ANDROID_MIN_SDK = "android:minSdkVersion";
    private static final String ATTRIBUTE_ANDROID_TARGET_SDK = "android:targetSdkVersion";

    private Document mDocument;
    private ApkInfo mApkInfo;
    private String manifestPath;
    private String confPackage;
    private String channelKey;
    private String gameId;
    private String gameKey;
    private String gameVersion;

    public ManifestHelper(ApkInfo apk, String path) {
        mApkInfo = apk;
        mDocument = XmlTool.createDocument(path);
        manifestPath = path;
        init();
    }

    private void init() {
        Element root = mDocument.getDocumentElement();
        //获得配置包的名称
        confPackage = root.getAttribute(ELEMENT_PACKAGE);
        readMetaData(new OnReadContentListener() {
            @Override
            public void onRead(Node attributeNameNode, String attributeName, Node attributeValueNode, String attributeValue) {
                if (attributeName.equals(YJConfig.META_DATA_CHANNEL_KEY)) {
                    channelKey = attributeValue;
                    Log.iln("channelKey=" + channelKey);
                } else if (attributeName.equals(YJConfig.META_DATA_GAME_ID)) {
                    gameId = attributeValue;
                    Log.iln("gameId=" + gameId);
                } else if (attributeName.equals(YJConfig.META_DATA_GAME_KEY)) {
                    gameKey = attributeValue;
                    Log.iln("gameKey=" + gameKey);
                } else if (attributeName.equals(YJConfig.META_DATA_GAME_VERSION)) {
                    gameVersion = attributeValue;
                    Log.iln("gameVersion=" + gameVersion);
                }
            }
        });
    }

    public void addVersionInfo(String code, String name) {
        if (Utils.isEmpty(code)) {
            code = mApkInfo.getVersionCode();
        }
        if (Utils.isEmpty(name)) {
            name = mApkInfo.getVersionName();
        }
        createVersion(ATTRIBUTE_ANDROID_VERSION_CODE, code);
        createVersion(ATTRIBUTE_ANDROID_VERSION_NAME, name);
    }

    private void createVersion(String name, String value) {
        Log.eln(name + "=" + value);
        Node attributeVersion = mDocument.getDocumentElement().getAttributes().getNamedItem(name);
        if (attributeVersion != null) {
            attributeVersion.setTextContent(value);
        } else {
            Attr codeAttr = mDocument.createAttribute(name);
            codeAttr.setValue(value);
            mDocument.getDocumentElement().setAttributeNode(codeAttr);
        }
    }

    /**
     * 添加sdk配置
     */
    public void addSdkInfo(String minSdk, String targetSdk) {
        String[] sdkInfo = getSdkInfo();
        if (Utils.isEmpty(minSdk)) {
            minSdk = sdkInfo[0];
        }
//        if (Utils.isEmpty(targetSdk)) {
//            targetSdk = sdkInfo[1];
//        }
        if (mDocument.getElementsByTagName(ELEMENT_USE_SDK) != null) {
            NodeList nodeList = mDocument.getElementsByTagName(ELEMENT_USE_SDK);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }
        }

        Element sdkElm = mDocument.createElement(ELEMENT_USE_SDK);
        Attr minSdkAttr = mDocument.createAttribute(ATTRIBUTE_ANDROID_MIN_SDK);
        minSdkAttr.setValue(minSdk);
        sdkElm.setAttributeNode(minSdkAttr);

        if (!Utils.isEmpty(targetSdk)) {
            Attr targetSdkAttr = mDocument.createAttribute(ATTRIBUTE_ANDROID_TARGET_SDK);
            targetSdkAttr.setValue(targetSdk);
            sdkElm.setAttributeNode(targetSdkAttr);
        }
        XmlTool.addElement(mDocument, sdkElm);
    }

    private String[] getSdkInfo() {
        String minSdk = mApkInfo.getMinSdkVersion();
        String targetSdk = mApkInfo.getTargetSdkVersion();
        if (Utils.isEmpty(minSdk)) {
            minSdk = mApkInfo.getSdkVersion();
        }
        if (Utils.isEmpty(targetSdk)) {
            targetSdk = minSdk;
        }
        if (!Utils.isEmpty(minSdk) && !Utils.isEmpty(targetSdk)) {
            if (Integer.valueOf(minSdk) > Integer.valueOf(targetSdk)) {
                minSdk = targetSdk;
            }
        }
        return new String[]{minSdk, targetSdk};
    }

    private NodeList getRootList() {
        return XmlTool.getDocumentRootNodeList(mDocument);
    }

    /**
     * 替换入口activity
     */
    public void replaceLauncher(Channel channel) {
        if (channel == null || Utils.isEmpty(channel.getLauncher())) {
            return;
        }
        NodeList nodeList = getRootList();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (!node.getNodeName().equals(ELEMENT_APPLICATION)) {
                continue;
            }
            NodeList applicationList = node.getChildNodes();
            for (int j = 0; j < applicationList.getLength(); j++) {
                Node component = applicationList.item(j);
                if (component.getNodeName().equals(ELEMENT_ACTIVITY)) {
                    Node attributeNameNode = component.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_NAME);
                    String activityName = attributeNameNode.getTextContent();
                    if (activityName.equals(mApkInfo.getLaunchableActivity())) {
                        copyLauncher(component);
                        attributeNameNode.setTextContent(channel.getLauncher());
                        break;
                    }
                }
            }
        }//for
    }

    private void copyLauncher(Node item) {
        Document appDoc = item.getParentNode().getOwnerDocument();
        Element activityElement = appDoc.createElement(ELEMENT_ACTIVITY);
        for (int i = 0; i < item.getAttributes().getLength(); i++) {
            Attr attr = (Attr) item.getAttributes().item(i);
            Attr copyAttr = appDoc.createAttribute(attr.getName());
            copyAttr.setValue(attr.getValue());
            activityElement.setAttributeNode(copyAttr);
        }
        item.getParentNode().appendChild(activityElement);
    }

    /**
     * 删除无关的渠道信息
     */
    public void deleteUnrelatedChannelInfo(Channel channel) {
        NodeList nodeList = getRootList();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (!node.getNodeName().equals(ELEMENT_APPLICATION)) {
                continue;
            }
            NodeList applicationList = node.getChildNodes();

            for (int j = 0; j < applicationList.getLength(); j++) {
                Node component = applicationList.item(j);
                if (component.getNodeName().equals(ELEMENT_ACTIVITY)
                        || component.getNodeName().equals(ELEMENT_SERVICE)
                        || component.getNodeName().equals(ELEMENT_PROVIDER)
                        || component.getNodeName().equals(ELEMENT_RECEIVER)) {
                    deleteElement(component, channel);
                } else if (component.getNodeName().equals(ELEMENT_METADATA)) {
                    deleteMetaData(component, channel);
                }
            }
        }
        XmlTool.saveXml(mDocument, manifestPath);
    }

    private void deleteElement(Node item, Channel channel) {
        Node attributeNameNode = item.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_NAME);
        String attributeName = attributeNameNode.toString();
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
                String attributeResName = attributeResNode.getTextContent();
                for (String name : channel.getFilter().getResNames()) {
                    String input = attributeResName.substring(attributeResName.indexOf("/") + 1);
                    if (Utils.matches(name, input)) {
                        item.getParentNode().removeChild(item);
                    }
                }
            }
        }
    }

    private void deleteMetaData(Node item, Channel channel) {
        if (null == channel.getFilter().getMetaData() || channel.getFilter().getMetaData().isEmpty()) {
            return;
        }
        Node attributeNameNode = item.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_NAME);
        String attributeName = attributeNameNode.getTextContent();
        for (String name : channel.getFilter().getMetaData()) {
            if (Utils.matches(name, attributeName)) {
                item.getParentNode().removeChild(item);
                Log.eln("delete:" + item.getNodeName() + " name=" + attributeName);
            }
        }
    }

    public void updateMetaData(String[] names, String[] values) {
        if (null == names || null == values || names.length == 0 || values.length == 0) {
            return;
        }
        readMetaData(new OnReadContentListener() {
            @Override
            public void onRead(Node attributeNameNode, String attributeName, Node attributeValueNode, String attributeValue) {
                for (int index = 0; index < names.length; index++) {
                    if (names[index].equals(attributeName) && !attributeValue.equals(values[index])) {
                        attributeValueNode.setTextContent(values[index]);
                        Log.eln("name=" + attributeName + ",new value=" + values[index]);
                    }
                }
            }
        });
        XmlTool.saveXml(mDocument, manifestPath);
    }

    private void readMetaData(OnReadContentListener listener) {
        NodeList nodeList = getRootList();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (!node.getNodeName().equals(ELEMENT_APPLICATION)) {
                continue;
            }
            NodeList applicationList = node.getChildNodes();

            for (int j = 0; j < applicationList.getLength(); j++) {
                Node component = applicationList.item(j);
                if (component.getNodeName().equals(ELEMENT_METADATA)) {
                    Node attributeNameNode = component.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_NAME);
                    String attributeName = attributeNameNode.getTextContent();
                    Node attributeValueNode = component.getAttributes().getNamedItem(ATTRIBUTE_ANDROID_VALUE);
                    String attributeValue = attributeValueNode.getTextContent();
                    if (listener != null) {
                        listener.onRead(attributeNameNode, attributeName, attributeValueNode, attributeValue);
                    }
                }
            }
        }
    }

    public interface OnReadContentListener {
        void onRead(Node attributeNameNode, String attributeName, Node attributeValueNode, String attributeValue);
    }

    public void updateManifestConfig(String targets, String replaces) {
        String manifest = FileUtils.replaceFile(manifestPath, targets, replaces);
        FileUtils.writer2File(manifestPath, manifest);
    }

    public void updateManifestConfig(String[] targets, String[] replaces) {
        String manifest = FileUtils.readAndReplaceFile(manifestPath, targets, replaces);
        FileUtils.writer2File(manifestPath, manifest);
    }

    public String getConfigPackageName() {
        return confPackage;
    }

    public void updatePackageName() {
        if (Utils.isEmpty(confPackage)) {
            return;
        }
//        updateManifestConfig(mApkInfo.getPackageName(), confPackage);
    }

    public void updateAppName(String appName) {
        if (Utils.isEmpty(appName)) {
            return;
        }
        updateManifestConfig(mApkInfo.getApplicationLable(), appName);
    }

    public String getChannelKey() {
        return channelKey;
    }

    public String getGameId() {
        return gameId;
    }

    public String getGameKey() {
        return gameKey;
    }

    public String getGameVersion() {
        return gameVersion;
    }
}
