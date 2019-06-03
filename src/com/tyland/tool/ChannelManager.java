package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import static com.tyland.tool.BaseCompiler.*;
import static com.tyland.tool.Main.ROOT_PATH;
import static com.tyland.tool.util.FileUtils.*;

/**
 * Created by zhaoj on 2018/10/29.
 */
public class ChannelManager {
    private CyclicBarrier cyclicBarrier;
    private OnExecuteFinishListener listener;
    private ShellUtils.ProgressListener progressListener;

    private String buildApkPath;
    private String apkFileName;
    private ApkInfo mApkInfo;
    private String apkPath;
    private String outApkPath;

    private String APK_PATH = BIN_PATH + File.separator + "build.apk";

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_NO_FIND = 1;
    public static final int STATUS_FAIL = 2;

    private YJConfig yjConfig;

    public ChannelManager() {
        File apk = new File(APK_PATH);
        initApk(apk);
    }

    private void initApk(File apkFile) {
        //查找当前目录下的apk，并创建输出目录
        try {
            apkPath = createOutDir(apkFile);
            mApkInfo = new ApkUtils().getApkInfo(apkPath);
            Log.iln("run");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createOutDir(File apk) {
        if (apk != null) {
            delFolder(ROOT_PATH + File.separator + OUT_DIR_NAME);
            createFileDir(ROOT_PATH + File.separator + OUT_DIR_NAME + File.separator);
//                FileUtils.createOutDir(apk, new File(outPath + apk.getName()));
            return apk.getAbsolutePath();
        }
        return null;
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    public void execute(YJConfig config) {
        new SubThread(cyclicBarrier, "BuildApk") {
            @Override
            public void execute() {
                yjConfig = config;
                int result = STATUS_FAIL;
                if (mApkInfo == null) {
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                    return;
                }
                try {
                    //初始化Decoder
                    Decoder decoder = new Decoder(mApkInfo, apkFileName);
                    decoder.setProgressListener(progressListener);
                    result = decoder.decode(APK_PATH);
                    if (result == STATUS_SUCCESS) {
                        decoder.updateManifest(yjConfig);
                        decoder.updatePackage(getDefaultPackageName(), yjConfig);
                        if (isExists(REPLACE_ICON_PATH)) {
                            decoder.replaceIcon(getIcons());
                        }
                        result = buildApk();
                        if (result == STATUS_SUCCESS) {
                            apkFileName = yjConfig.appName;
                            result = signApk();
                        }
                    }
                    delFolder(decoder.getDecodeApkPath());
                    deleteFile(buildApkPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = STATUS_FAIL;
                } finally {
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                }
            }
        }.start();
    }

    private int buildApk() {
        Builder builder = new Builder(apkFileName);
        builder.setProgressListener(progressListener);
        builder.setConfig(getYjConfig());
        int result = builder.build();
        if (result == STATUS_SUCCESS) {
            buildApkPath = builder.getApkBuildPath();
        }
        return result;
    }

    private int signApk() throws IOException {
        int result = STATUS_FAIL;
        if (!Utils.isEmpty(buildApkPath)) {
            Signer signer = new Signer(apkFileName);
            signer.setProgressListener(progressListener);
            updateProgress("Sign Apk...");
            result = signer.sign(buildApkPath);
            outApkPath = signer.getSignApkPath();
        }
        return result;
    }

    public YJConfig getYjConfig() {
        return yjConfig;
    }

    private void updateProgress(String msg) {
        if (progressListener != null) {
            progressListener.publishProgress(msg);
        }
    }

    public boolean isExistApk() {
        return mApkInfo != null;
    }

    public void setProgressListener(ShellUtils.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public String getDefaultPackageName() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getPackageName();
    }

    public String getDefaultAppName() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getApplicationLable();
    }

    public Map<String, String> getIcons() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getApplicationIcons();
    }

    public String getOutApkPath() {
        File file = new File(outApkPath);
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outApkPath;
    }

    public void setExecuteFinishListener(OnExecuteFinishListener listener) {
        this.listener = listener;
    }

    public interface OnExecuteFinishListener {
        void onFinish(int code);
    }

}
