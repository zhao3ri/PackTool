package com.qinglan.tool;

import brut.androlib.AndrolibException;
import brut.androlib.ApkDecoder;
import brut.directory.DirectoryException;
import com.qinglan.tool.entity.ApkInfo;
import com.qinglan.tool.util.ApkUtil;
import com.qinglan.tool.util.FileUtil;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.xml.Channel;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.qinglan.tool.ChannelManager.CODE_NO_FIND;
import static com.qinglan.tool.util.FileUtil.createFileDir;

public class Decoder extends BaseCompiler {

    private ApkInfo mApkInfo;

    public Decoder(Channel c, List<Channel> channels) {
        super(c, channels);
    }

    public int decode(String path) {
        int result = -1;
        if (Utils.isEmpty(path)) {
            return CODE_NO_FIND;
        }
        try {
            mApkInfo = new ApkUtil().getApkInfo(path);
            FileUtil.delFolder(OUT_PATH);
            createFileDir(OUT_PATH);
//            apkDecode(path);
            String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/"%s d %s -o %s -f", APKTOOL_PATH, path, OUT_PATH);
            result = Utils.execShell(scriptPath);
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
        return result;
    }

    public String getPackageName() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getPackageName();
    }

    public Map<String, String> getIcons() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getApplicationIcons();
    }

    private void apkDecode(String apkPath) throws AndrolibException, IOException, DirectoryException {
        ApkDecoder decoder = new ApkDecoder();
        decoder.setForceDelete(true);
        decoder.setDecodeSources((short) 0);
        decoder.setOutDir(new File(OUT_PATH));
        decoder.setApkFile(new File(apkPath));
        decoder.decode();
    }

    public void updateConfig(String appId, String appKey, String cpId, String cpKey, String suffix) {
        ManifestHelper manifestHelper = new ManifestHelper(mApkInfo, MANIFEST_PATH);
        manifestHelper.addSdkInfo();
        manifestHelper.replaceLauncher(currChannel);
        for (Channel channel : exceptChannels) {
            manifestHelper.deleteUnrelatedChannelInfo(channel);
        }
        String packageName = mApkInfo.getPackageName();
        String[] targets;
        String[] replaces;
        if (Utils.isEmpty(suffix)) {
            targets = new String[]{PACKAGE_NAME_TAG, APP_ID_TAG, CP_ID_TAG, APP_KEY_TAG, CP_KEY_TAG, LAUNCHER_TAG};
            replaces = new String[]{packageName, appId, cpId, appKey, cpKey, mApkInfo.getLaunchableActivity()};
        } else {
            String completePkg = String.format("%s.%s", packageName, suffix);
            targets = new String[]{PACKAGE_NAME_TAG, APP_ID_TAG, CP_ID_TAG, APP_KEY_TAG, CP_KEY_TAG, LAUNCHER_TAG, packageName};
            replaces = new String[]{packageName, appId, cpId, appKey, cpKey, mApkInfo.getLaunchableActivity(), completePkg};
        }
        //必须在最后一步才进行替换，否则可能会导致错误
        manifestHelper.updateManifestConfig(targets, replaces);
    }
}
