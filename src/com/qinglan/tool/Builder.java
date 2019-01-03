package com.qinglan.tool;

import brut.androlib.Androlib;
import brut.androlib.res.util.ExtFile;
import brut.common.BrutException;
import com.qinglan.common.Log;
import com.qinglan.tool.util.FileUtils;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.entity.Filter;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.qinglan.tool.util.FileUtils.getPath;

public class Builder extends BaseCompiler {
    private static final String CHANNEL_PREFIX = "qlsdk_";
    private static final String CHANNEL_REGEX = CHANNEL_PREFIX + "\\d+";
    private static final String BUILD_PATH = OUT_PATH + File.separator + "build";
    private static final String BUILD_APK_PATH = BUILD_PATH + File.separator + "apk";
    private static final String DEFAULT_APK_NAME = "build.apk";
    private List<String> packageNameFilter;
    private String mPackageName;
    private Map<String, String> applicationIcons;

    public Builder(Channel c, List<Channel> channels) {
        super(c, channels);
        packageNameFilter = new ArrayList<>();
        addPackageFilter("android.support");
        addPackageFilter("com.google");
        addPackageFilter("com.alipay");
        addPackageFilter("com.ta.utdid2");
        addPackageFilter("com.ut.device");
        addPackageFilter("org.json.alipay");
        addPackageFilter("cn.uc.gamesdk");
        addPackageFilter("cn.gundam.sdk");
        addPackageFilter("com.huawei");
        addPackageFilter("com.unionpay");
        addPackageFilter("com.bignox.sdk");
        addPackageFilter("com.game.sdk");
    }

    private void addPackageFilter(String pkg) {
        packageNameFilter.add(replacePackageSeparator(pkg, File.separator));
    }

    public String build(String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey, String suffix, String replacePath) {
        try {
            if (!Utils.isEmpty(replacePath)) {
                DrawableReplaceHelper helper = new DrawableReplaceHelper(applicationIcons, replacePath);
                helper.replace();
            }
            delUnrelatedRes(appId, appKey, pubKey, secretKey, cpId, cpKey);
            delUnrelatedAssets();
            delUnrelatedLibs();
            delChannelClass();
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
                                FileUtils.delFolder(assetFile.getAbsolutePath());
                            else
                                FileUtils.deleteFile(assetFile.getAbsolutePath());
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
            FileUtils.delMatchFile(path, file.getName(), filter);
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
    private void delUnrelatedRes(String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey) throws IOException {
        ResourceHelper resourceHelper = new ResourceHelper(RES_PATH, appId, appKey, pubKey, secretKey, cpId, cpKey);

        for (Channel channel : exceptChannels) {
            resourceHelper.deleteUnrelatedChannelResource(channel);
        }//for
    }

    private void delChannelClass() throws IOException {
        File channelDir = new File(SMALI_PATH + File.separator + replacePackageSeparator(CHANNEL_PACKAGE_NAME, File.separator));
        Iterator<File> iterator = Arrays.asList(channelDir.listFiles()).iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            if (file.isDirectory() && !file.getName().equals(CHANNEL_SUB_NAME_ENTITY)) {
                FileUtils.delFolder(file.getCanonicalPath());
            }
        }
    }

    private void updatePackage(String suffix) throws IOException {
        for (Channel channel : exceptChannels) {
            if (null == channel.getFilter().getPackageNameList() || channel.getFilter().getPackageNameList().isEmpty()) {
                continue;
            }
            for (Filter.Package pkg : channel.getFilter().getPackageNameList()) {
                String pkgFileName = replacePackageSeparator(pkg.getName(), "/");
                FileUtils.delFolder(SMALI_PATH + File.separator + pkgFileName);
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
        FileUtils.renameFile(sourceFile, tmpFile);
        FileUtils.copyFolder(tmpFile, destFile);
        FileUtils.delFolder(tmpFile.getCanonicalPath());
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
                String content = FileUtils.readAndReplaceFile(file.getCanonicalPath(), targets, replaces);
                FileUtils.writer2File(file.getCanonicalPath(), content);
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

    /**
     * 添加渠道文件qlsdk_[channelId]
     */
    private void addChannelFile() throws IOException {
        String channelFile = FileUtils.findFile(ASSETS_PATH, CHANNEL_REGEX);
        if (channelFile != null) {
            int id = Integer.valueOf(channelFile.substring(channelFile.indexOf(CHANNEL_PREFIX) + CHANNEL_PREFIX.length()));
            if (id != currChannel.getId()) {
                FileUtils.renameFile(ASSETS_PATH + File.separator + CHANNEL_PREFIX + id,
                        ASSETS_PATH + File.separator + CHANNEL_PREFIX + currChannel.getId());
            }
        } else {
            FileUtils.createFile(ASSETS_PATH, CHANNEL_PREFIX + currChannel.getId());
        }
    }

    private String buildApk() throws BrutException {
        String apkName = DEFAULT_APK_NAME;
        if (!Utils.isEmpty(decodeApkName)) {
            apkName = String.format("%s-%s.apk", decodeApkName, currChannel.getName());
        }

        Androlib androlib = new Androlib();
        File appDir = new File(OUT_PATH);
        androlib.buildResourcesFull(appDir, androlib.readMetaFile(new ExtFile(appDir)).usesFramework);
        FileUtils.delFolder(RES_PATH);
        FileUtils.deleteFile(MANIFEST_PATH);
        FileUtils.copyFolder(new File(BUILD_APK_PATH), new File(OUT_PATH));
        FileUtils.delFolder(BUILD_PATH);
        String apkPath = BIN_PATH + File.separator + apkName;
        androlib.build(appDir, new File(apkPath));
//        String scriptPath = String.format("%s b %s -o %s -a %s", APKTOOL_PATH, OUT_PATH, apkPath, BIN_PATH + File.separator + "aapt.exe");
//        Utils.execShell(scriptPath);
        return apkPath;
    }
}
