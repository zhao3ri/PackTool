package com.qinglan.tool;

import com.qinglan.tool.util.FileUtils;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.xml.XmlTool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static com.qinglan.tool.util.FileUtils.getPath;

public class ResourceHelper {
    private static final String ELEMENT_VALUES_STRING = "string";
    private static final String ELEMENT_VALUES_STYLE = "style";
    private static final String RES_VALUE = "value";
    private static final String RES_DRAWABLE = "drawable";
    private static final String ATTRIBUTE_NAME = "name";

    /**
     * 第三方渠道res配置文件名
     */
    public static final String RES_NAME_APP_ID = "qlsdk_third_party_appid";
    public static final String RES_NAME_APP_KEY = "qlsdk_third_party_appkey";
    public static final String RES_NAME_PUBLIC_KEY = "qlsdk_third_party_pubkey";
    public static final String RES_NAME_CP_ID = "qlsdk_third_party_cpid";
    public static final String RES_NAME_CP_KEY = "qlsdk_third_party_cpkey";
    public static final String RES_NAME_SECRET_KEY = "qlsdk_third_party_seckey";

    private String resPath;
    private File resDirFile;
    private String appId;
    private String appKey;
    private String pubKey;
    private String secretKey;
    private String cpId;
    private String cpKey;

    public ResourceHelper(String path, String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey) {
        resPath = path;
        resDirFile = new File(resPath);
        this.appId = appId;
        this.appKey = appKey;
        this.pubKey = pubKey;
        this.secretKey = secretKey;
        this.cpId = cpId;
        this.cpKey = cpKey;
    }

    public boolean deleteUnrelatedChannelResource(Channel channel) throws IOException {
        if (channel.getFilter().getResNames() == null
                || channel.getFilter().getResNames().isEmpty()) {
            return false;
        }
        //删除res下无关渠道文件
        if (resDirFile.exists() && resDirFile.isDirectory()) {
            String[] resStringNames = resDirFile.list();
            for (String name : resStringNames) {
                String resPath = resDirFile.getAbsolutePath() + File.separator + name;
                if (name.startsWith(RES_VALUE)) {
                    deleteValues(channel, resPath);
                } else {
                    deleteRes(channel, resPath);
                }
            }
        }//if
        return true;
    }

    private void deleteRes(Channel channel, String resPath) throws IOException {
        File resFile = new File(resPath);
        if (resFile.exists() && resFile.isDirectory()) {
            Iterator<String> iterator = Arrays.asList(resFile.list()).iterator();
            while (iterator.hasNext()) {
                String fileName = iterator.next();
                for (String matchName : channel.getFilter().getResNames()) {
                    String path = resFile.getCanonicalPath();
                    FileUtils.delMatchFile(path, fileName, matchName);
                }
            }
        }
    }

    private void deleteValues(Channel channel, String resName) {
        File valueFile = new File(resName);
        if (valueFile.exists() && valueFile.isDirectory()) {
            String[] files = valueFile.list();
            for (String file : files) {
                readValueXml(channel, getPath(valueFile.getAbsolutePath()) + file);
            }
        }
    }

    private void readValueXml(Channel channel, String path) {
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
                    updateResConfig(item, name, appId, appKey, pubKey, secretKey, cpId, cpKey);
                }
                for (String regex : channel.getFilter().getResNames()) {
                    matchesStyle(item, regex);
                    if (Utils.matches(regex, name)) {
                        item.getParentNode().removeChild(item);
                    }
                }
            }
        }
        XmlTool.saveXml(document, path);
    }

    private void matchesStyle(Node item, String regex) {
        if (!item.getNodeName().equals(ELEMENT_VALUES_STYLE))
            return;
        if (!item.hasChildNodes())
            return;
        NodeList styleItems = item.getChildNodes();
        for (int i = 0; i < styleItems.getLength(); i++) {
            Node styleItem = styleItems.item(i);
            String text = styleItem.getTextContent();
            if (!text.contains("@") || text.indexOf("/") < 0) {
                continue;
            }
            String name = text.substring(text.indexOf("/") + 1);
            if (Utils.matches(regex, name)) {
//                item.getParentNode().removeChild(item);
                //不能直接删除父标签，因为若未在res过滤中配置style名称，可能导致编译出错
                item.removeChild(styleItem);
            }
        }
    }

    /**
     * 设置第三方渠道参数
     */
    private void updateResConfig(Node item, String attribute, String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey) {
        if (!item.getNodeName().equals(ELEMENT_VALUES_STRING)) {
            return;
        }
        if (attribute.equals(RES_NAME_APP_ID) && !Utils.isEmpty(appId)) {
            item.setTextContent(appId.trim());
        } else if (attribute.equals(RES_NAME_APP_KEY) && !Utils.isEmpty(appKey)) {
            item.setTextContent(appKey);
        } else if (attribute.equals(RES_NAME_PUBLIC_KEY) && !Utils.isEmpty(pubKey)) {
            item.setTextContent(pubKey);
        } else if (attribute.equals(RES_NAME_SECRET_KEY) && !Utils.isEmpty(secretKey)) {
            item.setTextContent(secretKey);
        } else if (attribute.equals(RES_NAME_CP_ID) && !Utils.isEmpty(cpId)) {
            item.setTextContent(cpId);
        } else if (attribute.equals(RES_NAME_CP_KEY) && !Utils.isEmpty(cpKey)) {
            item.setTextContent(cpKey);
        }
    }

}
