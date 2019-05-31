package com.tyland.tool;

import brut.androlib.AndrolibException;
import com.tyland.tool.entity.ApkInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import static com.tyland.tool.BaseCompiler.OUT_DIR_PREFIX;
import static com.tyland.tool.Main.ROOT_PATH;
import static com.tyland.tool.util.FileUtils.createFileDir;
import static com.tyland.tool.util.FileUtils.delFolder;
import static com.tyland.tool.util.FileUtils.searchApk;

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
    private Decoder mDecoder;

    private static final String CHANNEL_CONFIG_PATH = ROOT_PATH + File.separator + "conf" + File.separator + "channel_list.xml";
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_NO_FIND = 1;
    public static final int STATUS_FAIL = 2;
    public static final int STATUS_SIGN_SUCCESS = 4;
    public static final int STATUS_DECODE_SUCCESS = 5;
    public static final int STATUS_BUILD_SUCCESS = 6;

    private YJConfig yjConfig;

    public ChannelManager() {
        File apk = searchApk(ROOT_PATH);
        initApk(apk);
    }

    public ChannelManager(String apkPath) {
        File apk = new File(apkPath);
        initApk(apk);
    }

    private void initApk(File apkFile) {
        //查找当前目录下的apk，并创建输出目录
        try {
            apkPath = createOutDir(apkFile);
            mApkInfo = new ApkUtils().getApkInfo(apkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createOutDir(File apk) {
        if (apk != null) {
            apkFileName = apk.getName().substring(0, apk.getName().indexOf("."));
            delFolder(ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkFileName);
            createFileDir(ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkFileName + File.separator);
//                FileUtils.createOutDir(apk, new File(outPath + apk.getName()));
            return apk.getAbsolutePath();
        }
        return null;
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }


    public void decodeApk() {
        new SubThread(cyclicBarrier, "BuildApk") {
            @Override
            public void execute() {
                int result = STATUS_FAIL;
                if (mApkInfo == null) {
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                    return;
                }
                try {
                    //初始化Decoder
                    if (mDecoder == null)
                        mDecoder = new Decoder(mApkInfo, apkFileName);
                    mDecoder.setProgressListener(progressListener);
                    result = mDecoder.decode(apkPath);
                    if (result == STATUS_SUCCESS) {
                        result = STATUS_DECODE_SUCCESS;
                        yjConfig = mDecoder.getDecodeResult(yjConfig);
                        mDecoder.updateYml(yjConfig.appInfo);
                    }
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

    public void buildApk() {
        new SubThread(cyclicBarrier, "BuildApk") {

            @Override
            public void execute() {
                Builder builder = new Builder(apkFileName);
                builder.setProgressListener(progressListener);
                builder.setConfig(getYjConfig());
                int result = builder.build();
                if (result == STATUS_SUCCESS) {
                    result = STATUS_BUILD_SUCCESS;
                    buildApkPath = builder.getApkBuildPath();
                }
                if (listener != null) {
                    listener.onFinish(result);
                }
            }
        }.start();
    }

    public void updateConfig(YJConfig c) {
        if (mDecoder == null) {
            return;
        }
        yjConfig = c;
        mDecoder.updateManifest(c);
        try {
            mDecoder.updateYml(c.appInfo);
        } catch (AndrolibException e) {
            e.printStackTrace();
        }
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

    public String getDefaultMinSdk() {
        if (mApkInfo == null)
            return null;
        if (!Utils.isEmpty(mApkInfo.getMinSdkVersion())) {
            return mApkInfo.getMinSdkVersion();
        }
        return mApkInfo.getSdkVersion();
    }

    public String getDefaultTargetSdk() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getTargetSdkVersion();
    }

    public String getDefaultVersionCode() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getVersionCode();
    }

    public String getDefaultVersionName() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getVersionName();
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

    public void signApk() {
        new SubThread(cyclicBarrier, "SignApk") {
            @Override
            public void execute() {
                int result = STATUS_FAIL;
                if (!Utils.isEmpty(buildApkPath)) {
                    Signer signer = new Signer(apkFileName);
                    signer.setProgressListener(progressListener);
                    updateProgress("Sign Apk...");
                    try {
                        result = signer.sign(buildApkPath);
                        if (result == STATUS_SUCCESS) {
                            result = STATUS_SIGN_SUCCESS;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (listener != null) {
                    listener.onFinish(result);
                }
            }
        }.start();
    }

    public void setExecuteFinishListener(OnExecuteFinishListener listener) {
        this.listener = listener;
    }

    public interface OnExecuteFinishListener {
        void onFinish(int code);
    }

}
