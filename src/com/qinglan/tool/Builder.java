package com.qinglan.tool;

import brut.androlib.Androlib;
import brut.androlib.res.util.ExtFile;
import brut.common.BrutException;
import com.qinglan.common.Log;
import com.qinglan.tool.util.FileUtil;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.xml.Channel;
import com.qinglan.tool.xml.Filter;
import com.qinglan.tool.xml.XmlTool;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.qinglan.tool.util.FileUtil.getPath;

public class Builder extends BaseCompiler {
    private static final String TAG_VALUES_STRING = "string";
    private static final String TAG_VALUES_STYLE = "style";
    private static final String RES_VALUE = "value";
    private static final String RES_DRAWABLE = "drawable";
    private static final String ATTRIBUTE_NAME = "name";

    private static final String CHANNEL_PREFIX = "qlsdk_";
    private List<String> packageNameFilter;
    private String mPackageName;
    private Map<String, String> applicationIcons;

    public Builder(Channel c, List<Channel> channels) {
        super(c, channels);
        packageNameFilter = new ArrayList<>();
        addPackage("android.support");
        addPackage("com.google");
        addPackage("com.alipay");
        addPackage("com.ta.utdid2");
        addPackage("com.ut.device");
        addPackage("org.json.alipay");
        addPackage("cn.uc.gamesdk");
        addPackage("cn.gundam.sdk");
        addPackage("com.huawei");
        addPackage("com.unionpay");
        addPackage("com.bignox.sdk");
    }

    private void addPackage(String pkg) {
        packageNameFilter.add(replacePackageSeparator(pkg, File.separator));
    }

    public String build(String appId, String appKey, String pubKey, String secretKey, String cpId, String suffix, String replacePath) {
        try {
            if (!Utils.isEmpty(replacePath)) {
                DrawableReplaceHelper helper = new DrawableReplaceHelper(applicationIcons, replacePath);
                helper.replace();
            }
            delUnrelatedRes(appId, appKey, pubKey, secretKey, cpId);
            delUnrelatedAssets();
            delUnrelatedLibs();
//            delClasses();
            updatePackage(suffix);
            addChannelFile();
            String apkPath = buildApk();
            return apkPath;
        } catch (IOException | BrutException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setApplicationIcons(Map<String, String> applicationIcons) {
        this.applicationIcons = applicationIcons;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    private void delUnrelatedAssets() {
        File assetsDir = new File(ASSETS_PATH);
        for (Channel channel : exceptChannels) {
            if (channel.getFilter().getAssets() == null
                    || channel.getFilter().getAssets().isEmpty()) {
                Log.dln(channel.getName() + " no existed assets filter.");
                continue;
            }
            //删除assets下无关渠道文件或文件夹
            if (assetsDir.exists() && assetsDir.isDirectory()) {
                Iterator<String> iterator = Arrays.asList(assetsDir.list()).iterator();
                while (iterator.hasNext()) {
                    String assetsFileName = iterator.next();
                    for (String asset : channel.getFilter().getAssets()) {
                        if (assetsFileName.equals(asset)) {
                            File assetFile = new File(getPath(assetsDir.getAbsolutePath()) + assetsFileName);
                            if (assetFile.isDirectory())
                                FileUtil.delFolder(assetFile.getAbsolutePath());
                            else
                                FileUtil.deleteFile(assetFile.getAbsolutePath());
                        }
                    }
                }
            }//if
        }//for
    }

    /**
     * 删除无关渠道的libs
     */
    private void delUnrelatedLibs() throws IOException {
        List<String> exceptLibs = getExceptChannelLibs();
        if (exceptLibs.isEmpty())
            return;

        File libsDir = new File(LIBS_PATH);
        Iterator<String> it = Arrays.asList(libsDir.list()).iterator();
        while (it.hasNext()) {
            File libFile = new File(libsDir.getCanonicalPath() + File.separator + it.next());
            for (String filter : exceptLibs) {
                delLibDirFile(libFile, filter);
            }
        }
    }

    private List<String> getExceptChannelLibs() {
        List<String> exceptLibs = new ArrayList<>();
        for (Channel channel : exceptChannels) {
            if (channel.getFilter().getLibsName() == null
                    || channel.getFilter().getLibsName().isEmpty()) {
                Log.dln(channel.getName() + " no existed lib filter.");
                continue;
            }
            exceptLibs.addAll(channel.getFilter().getLibsName());
        }
        return exceptLibs;
    }

    private void delLibDirFile(File file, String filter) {
        if (file.isFile()) {
            String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator));
            FileUtil.delMatchFile(path, file.getName(), filter);
        } else {
            Iterator<String> iterator = Arrays.asList(file.list()).iterator();
            while (iterator.hasNext()) {
                String libName = iterator.next();
                File libSub = new File(file.getAbsolutePath() + File.separator + libName);
                delLibDirFile(libSub, filter);
            }
        }
    }

    /**
     * 删除无关渠道的res文件
     */
    private void delUnrelatedRes(String appId, String appKey, String pubKey, String secretKey, String cpId) throws IOException {
        File resDir = new File(RES_PATH);
        for (Channel channel : exceptChannels) {
            if (channel.getFilter().getResNames() == null
                    || channel.getFilter().getResNames().isEmpty()) {
                Log.dln(channel.getName() + " no existed res filter.");
                continue;
            }

            //删除res下无关渠道文件
            if (resDir.exists() && resDir.isDirectory()) {
                String[] resStringNames = resDir.list();
                for (String name : resStringNames) {
                    String resPath = resDir.getAbsolutePath() + File.separator + name;
                    if (name.startsWith(RES_VALUE)) {
                        delValues(channel, resPath, appId, appKey, pubKey, secretKey, cpId);
                    } else {
                        delRes(channel, resPath);
                    }
                }
            }//if
        }//for
    }

    private void delRes(Channel channel, String resPath) throws IOException {
        File resFile = new File(resPath);
        if (resFile.exists() && resFile.isDirectory()) {
            Iterator<String> iterator = Arrays.asList(resFile.list()).iterator();
            while (iterator.hasNext()) {
                String fileName = iterator.next();
                for (String matchName : channel.getFilter().getResNames()) {
                    String path = resFile.getCanonicalPath();
                    FileUtil.delMatchFile(path, fileName, matchName);
                }
            }
        }
    }

    private void delValues(Channel channel, String resName, String appId, String appKey, String pubKey, String secretKey, String cpId) {
        File valueFile = new File(resName);
        if (valueFile.exists() && valueFile.isDirectory()) {
            String[] files = valueFile.list();
            for (String file : files) {
                readValueXml(channel, getPath(valueFile.getAbsolutePath()) + file, appId, appKey, pubKey, secretKey, cpId);
            }
        }
    }

    private void readValueXml(Channel channel, String path, String appId, String appKey, String pubKey, String secretKey, String cpId) {
        Document document = XmlTool.createDocument(path);
        NodeList nodeList = XmlTool.getDocumentRootNodeList(document);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getAttributes() != null && item.getAttributes().getNamedItem(ATTRIBUTE_NAME) != null) {
                //获取android:name的值
                Node attributeNode = item.getAttributes().getNamedItem(ATTRIBUTE_NAME);
                String attributeName = attributeNode.toString();
                String name = attributeName.substring(attributeName.indexOf('"') + 1, attributeName.lastIndexOf('"'));
                //若当前是string资源，则查找替换渠道配置
                if (item.getNodeName().equals(TAG_VALUES_STRING))
                    updateResConfig(item, name, appId, appKey, pubKey, secretKey, cpId);
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
        if (!item.getNodeName().equals(TAG_VALUES_STYLE))
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

    private void updateResConfig(Node item, String attribute, String appId, String appKey, String pubKey, String secretKey, String cpId) {
        if (!item.getNodeName().equals(TAG_VALUES_STRING)) {
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
        }
    }

    /**
     * 添加渠道文件qlsdk_[channelId]
     */
    private void addChannelFile() throws IOException {
        String channelFile = FileUtil.findFile(ASSETS_PATH, CHANNEL_PREFIX + "\\d+");
        if (channelFile != null) {
            int id = Integer.valueOf(channelFile.substring(channelFile.indexOf(CHANNEL_PREFIX) + CHANNEL_PREFIX.length()));
            if (id != currChannel.getId()) {
                FileUtil.renameFile(ASSETS_PATH + File.separator + CHANNEL_PREFIX + id,
                        ASSETS_PATH + File.separator + CHANNEL_PREFIX + currChannel.getId());
            }
        } else {
            FileUtil.createFile(ASSETS_PATH, CHANNEL_PREFIX + currChannel.getId());
        }
    }

    private void updatePackage(String suffix) throws IOException {
        for (Channel channel : exceptChannels) {
            if (null == channel.getFilter().getPackageNameList() || channel.getFilter().getPackageNameList().isEmpty()) {
                continue;
            }
            for (Filter.Package pkg : channel.getFilter().getPackageNameList()) {
                String pkgFileName = replacePackageSeparator(pkg.getName(), "/");
                FileUtil.delFolder(SMALI_PATH + File.separator + pkgFileName);
            }
        }
        if (Utils.isEmpty(suffix)) {
            return;
        }
        String[] smailNames = (new File(SMALI_PATH)).list();
        Iterator<String> iterator = Arrays.asList(smailNames).iterator();
        while (iterator.hasNext()) {
            String fileName = iterator.next();
            readSmail(fileName, suffix);
        }
        File sourceFile = new File(SMALI_PATH + File.separator + replacePackageSeparator(mPackageName, File.separator));
        File destFile = new File(SMALI_PATH + File.separator + replacePackageSeparator(String.format("%s.%s", mPackageName, suffix), File.separator));
        File tmpFile = new File(SMALI_PATH + File.separator + replacePackageSeparator(mPackageName + "0", File.separator));
        FileUtil.renameFile(sourceFile, tmpFile);
        FileUtil.copyFolder(tmpFile, destFile);
        FileUtil.delFolder(tmpFile.getCanonicalPath());
    }

    private void readSmail(String fileName, String suffix) throws IOException {
        File file = new File(SMALI_PATH + File.separator + fileName);
        String pkgName = file.getPath().substring(SMALI_PATH.length() + 1);
        Log.iln("package==" + pkgName);
        if (file.isFile()) {
            Log.ln();
            if (!Utils.isEmpty(mPackageName) && !Utils.isEmpty(suffix)) {
                String[] targets = new String[]{mPackageName, replacePackageSeparator(mPackageName, "/")};
                String[] replaces = new String[]{String.format("%s.%s", mPackageName, suffix), replacePackageSeparator(String.format("%s.%s", mPackageName, suffix), "/")};
                String content = FileUtil.readAndReplaceFile(file.getCanonicalPath(), targets, replaces);
                FileUtil.writer2File(file.getCanonicalPath(), content);
            }
        } else {
            Iterator<String> iterator = Arrays.asList(file.list()).iterator();
            while (iterator.hasNext()) {
                if (packageNameFilter.contains(pkgName)) {
                    break;
                }
                readSmail(pkgName + File.separator + iterator.next(), suffix);
            }
        }
    }

    private String replacePackageSeparator(String pkg, String separator) {
        String rp = pkg.replace(".", separator);
        return rp;
    }

    private void delClasses() throws Exception {
//        String scriptPath = BIN_PATH + File.separator + "dex2jar-2.0" + File.separator + "d2j-dex2jar.bat";
        String scriptPath = BIN_PATH + File.separator + "dex2jar.bat";
        int result = Utils.execShell(scriptPath/*, "--force", OUT_PATH + File.separator + "classes.dex"*/, OUT_PATH + File.separator + "classes.dex");
        if (result == 0) {
            String dexJar = BIN_PATH + File.separator + "classes-dex2jar.jar";
            FileUtil.deleteFile(dexJar);
            List<String> deletes = new ArrayList<>();
            for (Channel channel : exceptChannels) {
                if (null == channel.getFilter().getPackageNameList() || channel.getFilter().getPackageNameList().isEmpty())
                    continue;
                for (Filter.Package pkg : channel.getFilter().getPackageNameList()) {
                    deletes.add(pkg.getName().replace(".", "/"));
                }
            }
            Utils.delete(dexJar, deletes);
        }
    }

    private String buildApk() throws BrutException {
        String apkName = "build.apk";
        if (!Utils.isEmpty(decodeApkName)) {
            apkName = String.format("%s-%s.apk", decodeApkName, currChannel.getName());
        }

        Androlib androlib = new Androlib();
        File appDir = new File(OUT_PATH);
        androlib.buildResourcesFull(appDir, androlib.readMetaFile(new ExtFile(appDir)).usesFramework);
        String buildPath = OUT_PATH + File.separator + "build" + File.separator + "apk";
        FileUtil.delFolder(RES_PATH);
        FileUtil.deleteFile(MANIFEST_PATH);
        FileUtil.copyFolder(new File(buildPath), new File(OUT_PATH));
        FileUtil.delFolder(OUT_PATH + File.separator + "build");
        String apkPath = BIN_PATH + File.separator + apkName;
        androlib.build(appDir, new File(apkPath));
//        String scriptPath = String.format("%s b %s -o %s -a %s", APKTOOL_PATH, OUT_PATH, apkPath, BIN_PATH + File.separator + "aapt.exe");
//        Utils.execShell(scriptPath);
        return apkPath;
    }
}
