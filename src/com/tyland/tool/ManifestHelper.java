package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;
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

    public ManifestHelper(ApkInfo apk, String path) {
        mApkInfo = apk;
        mDocument = XmlTool.createDocument(path);
        manifestPath = path;
        init();
    }

    private void init() {
        Element root = mDocument.getDocumentElement();
        //获得配置包的名称
//        packageName = root.getAttribute(ELEMENT_PACKAGE);
        readMetaData(new OnReadContentListener() {
            @Override
            public void onRead(Node attributeNameNode, String attributeName, Node attributeValueNode, String attributeValue) {
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

    private NodeList getRootList() {
        return XmlTool.getDocumentRootNodeList(mDocument);
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

    private void updateManifestConfig(String targets, String replaces) {
        String manifest = FileUtils.replaceFile(manifestPath, targets, replaces);
        FileUtils.writer2File(manifestPath, manifest);
    }

    public void updateManifestConfig(String[] targets, String[] replaces) {
        String manifest = FileUtils.readAndReplaceFile(manifestPath, targets, replaces);
        FileUtils.writer2File(manifestPath, manifest);
    }

    public void updatePackageName(String confPackage) {
        if (Utils.isEmpty(confPackage)) {
            return;
        }
        updateManifestConfig(mApkInfo.getPackageName(), confPackage);
    }

    public void updateAppName(String appName) {
        if (Utils.isEmpty(appName)) {
            return;
        }
        updateManifestConfig(mApkInfo.getApplicationLable(), appName);
    }
}
