package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.entity.ApkInfo;
import com.qinglan.tool.entity.GameChannelConfig;
import com.qinglan.tool.util.*;
import com.qinglan.tool.entity.ChannelList;
import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.xml.XmlTool;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import static com.qinglan.tool.BaseCompiler.OUT_DIR_PREFIX;
import static com.qinglan.tool.Main.ROOT_PATH;
import static com.qinglan.tool.util.FileUtils.createFileDir;
import static com.qinglan.tool.util.FileUtils.searchApk;

/**
 * Created by zhaoj on 2018/10/29.
 */
public class ChannelManager {
    List<Channel> channels;
    private int channelId;
    private CyclicBarrier cyclicBarrier;
    private OnBuildFinishListener listener;
    private ShellUtils.OnProgressListener onProgressListener;

    private GameChannelConfig config;
    private String buildApkPath;
    private String apkFileName;
    private ApkInfo mApkInfo;
    private String apkPath;

    private static final String CHANNEL_CONFIG_PATH = ROOT_PATH + File.separator + "conf" + File.separator + "channel_list.xml";
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_NO_FIND = 1;
    public static final int STATUS_FAIL = 2;
    public static final int STATUS_SIGN_SUCCESS = 4;

    public ChannelManager() {
        initChannel();
    }

    private void initChannel() {
        String xml = FileUtils.readFile(CHANNEL_CONFIG_PATH);
        ChannelList channelList = XmlTool.xml2Object(xml, ChannelList.class);
        if (channelList != null) {
            channels = channelList.getChannelList();
        }
        //查找当前目录下的apk，并创建输出目录
        apkPath = createOutDir();
        try {
            mApkInfo = new ApkUtils().getApkInfo(apkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    public void execute() {
        start();
    }

    private void start() {
        new SubThread(cyclicBarrier, "BuildApk") {
            @Override
            public void execute() {
                Channel channel = getChannel(channelId);
                Log.dln("channel===" + channel);
                if (mApkInfo == null) {
                    if (listener != null) {
                        listener.onFinish(STATUS_FAIL);
                    }
                    return;
                }
                int result = STATUS_FAIL;
                try {
                    //初始化Decoder
                    Decoder decoder = new Decoder(mApkInfo, channel, channels, apkFileName);
                    decoder.setConfig(config);
                    decoder.setOnProgressListener(onProgressListener);
                    updateProgress("Decode Apk...");
                    result = decoder.decode(apkPath);
                    if (result == STATUS_SUCCESS) {
                        decoder.updateManifest();
                        Builder builder = new Builder(channel, channels, apkFileName);
                        builder.setConfig(config);
                        builder.setApkPackageName(getDefaultPackageName());
                        builder.setApplicationIcons(getIcons());
                        updateProgress("Build Apk...");
                        buildApkPath = builder.build();
                        if (Utils.isEmpty(buildApkPath)) {
                            result = STATUS_FAIL;
                        }
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

    private String createOutDir() {
        File apk = searchApk(ROOT_PATH);
        if (apk != null) {
            apkFileName = apk.getName().substring(0, apk.getName().indexOf("."));
            createFileDir(ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkFileName + File.separator);
//                FileUtils.createOutDir(apk, new File(outPath + apk.getName()));
            return apk.getAbsolutePath();
        }
        return null;
    }

    private void updateProgress(String msg) {
        if (onProgressListener != null) {
            onProgressListener.publishProgress(msg);
        }
    }

    public void setChannelId(int id) {
        channelId = id;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setConfig(GameChannelConfig config) {
        this.config = config;
    }

    public void setOnProgressListener(ShellUtils.OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public String getDefaultMinSdk() {
        if (!Utils.isEmpty(mApkInfo.getMinSdkVersion())) {
            return mApkInfo.getMinSdkVersion();
        }
        return mApkInfo.getSdkVersion();
    }

    public String getDefaultTargetSdk() {
        return mApkInfo.getTargetSdkVersion();
    }

    public String getDefaultVersionCode() {
        return mApkInfo.getVersionCode();
    }

    public String getDefaultVersionName() {
        return mApkInfo.getVersionName();
    }

    public String getDefaultPackageName() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getPackageName();
    }

    public String getAppName() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getApplicationLable();
    }

    public Map<String, String> getIcons() {
        if (mApkInfo == null)
            return null;
        return mApkInfo.getApplicationIcons();
    }

    private Channel getChannel(int channelId) {
        Log.dln("channels" + channels + " channelId==" + channelId);
        if (null != channels && !channels.isEmpty()) {
            for (Channel channel : channels) {
                if (channelId == channel.getId()) {
                    return channel;
                }
            }
        }
        return null;
    }

    public void sign() {
        sign(null, null, null);
    }

    public void sign(final String keystorePath, final String keystorePass, final String keystoreAlias) {
        new SubThread(cyclicBarrier, "SignApk") {
            @Override
            public void execute() {
                if (!Utils.isEmpty(buildApkPath)) {
                    Signer signer = new Signer(getChannel(channelId), channels, apkFileName);
                    updateProgress("Sign Apk...");
                    int result = signer.sign(buildApkPath, keystorePath, keystorePass, keystoreAlias);
                    if (result == STATUS_SUCCESS) {
                        result = STATUS_SIGN_SUCCESS;
                    }
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                }
            }
        }.start();
    }

    public void setBuildFinishListener(OnBuildFinishListener listener) {
        this.listener = listener;
    }

    public interface OnBuildFinishListener {
        void onFinish(int code);
    }

}
