package com.tyland.tool;

import brut.androlib.Androlib;
import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.androlib.meta.MetaInfo;
import brut.androlib.res.util.ExtFile;
import brut.directory.DirectoryException;
import com.tyland.common.Log;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

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

    public void updateManifest(YJConfig c) {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, getManifestPath());
        //更新包名
        manifestHelper.updatePackageName(c.packageName);
        if (!mApkInfo.getApplicationLable().equals(c.appName)) {
            manifestHelper.updateAppName(c.appName);
            ResourceHelper resHelper = new ResourceHelper(getResDirPath());
            resHelper.updateResourceAppName(c.appName);
        }
    }

    public void updatePackage(String defPkg, YJConfig config) throws IOException {
        if (Utils.isEmpty(config.packageName)) {
            return;
        }
        String[] smailNames = (new File(getSmaliPath())).list();
        String packageName = config.packageName;
        Iterator<String> iterator = Arrays.asList(smailNames).iterator();
        while (iterator.hasNext()) {
            String fileName = iterator.next();
            readSmail(fileName, defPkg, packageName);
        }
        File sourceFile = new File(getSmaliPath() + File.separator + replacePackageSeparator(defPkg, File.separator));
        File destFile = new File(getSmaliPath() + File.separator + replacePackageSeparator(packageName, File.separator));
        File tmpFile = new File(getSmaliPath() + File.separator + replacePackageSeparator(defPkg + "0", File.separator));
        FileUtils.renameFile(sourceFile, tmpFile);
        FileUtils.copyFolder(tmpFile, destFile);
        FileUtils.delFolder(tmpFile.getCanonicalPath());
    }

    private void readSmail(String fileName, String defPkg, String replacePackage) throws IOException {
        File file = new File(getSmaliPath() + File.separator + fileName);
        String pkgName = file.getPath().substring(getSmaliPath().length());
        if (file.isFile()) {
            if (!Utils.isEmpty(defPkg) && !Utils.isEmpty(replacePackage)) {
                String[] targets = new String[]{defPkg, replacePackageSeparator(defPkg, "/")};
                String[] replaces = new String[]{replacePackage, replacePackageSeparator(replacePackage, "/")};
                String content = FileUtils.readAndReplaceFile(file.getCanonicalPath(), targets, replaces);
                FileUtils.writer2File(file.getCanonicalPath(), content);
            }
        } else {
            Iterator<String> iterator = Arrays.asList(file.list()).iterator();
            while (iterator.hasNext()) {
                if (pkgName.startsWith("android")) {
                    break;
                }
                readSmail(pkgName + File.separator + iterator.next(), defPkg, replacePackage);
            }
        }
    }

    private String replacePackageSeparator(String pkg, String separator) {
        String rp = pkg.replace(".", separator);
        return rp;
    }

    public void replaceIcon(Map<String, String> icons) throws IOException {
        DrawableReplaceHelper helper = new DrawableReplaceHelper(icons, getResDirPath(), REPLACE_RES_PATH);
        helper.replace();
    }
}
