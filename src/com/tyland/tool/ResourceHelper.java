package com.tyland.tool;

import com.tyland.tool.util.Utils;
import com.tyland.tool.xml.XmlTool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import static com.tyland.tool.util.FileUtils.getPath;

public class ResourceHelper {
    private static final String ELEMENT_VALUES_STRING = "string";
    private static final String ELEMENT_VALUES_STYLE = "style";
    private static final String RES_VALUE = "value";
    private static final String RES_DRAWABLE = "drawable";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_VALUES_APP_NAME = "app_name";


    private String resPath;
    private File resDirFile;

    public ResourceHelper(String path) {
        resPath = path;
        resDirFile = new File(resPath);
    }

    public boolean updateResourceAppName(String appName) {
        File resDirFile = new File(resPath);
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
