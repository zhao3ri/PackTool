package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.entity.ApkInfo;
import com.qinglan.tool.util.ApkUtils;
import com.qinglan.tool.util.FileUtils;
import com.qinglan.tool.util.SubThread;
import com.qinglan.tool.util.Utils;
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
    private String appId;
    private String appKey;
    private String pubKey;
    private String secretKey;
    private String cpId;
    private String cpKey;
    private String suffix;
    private String buildApkPath;
    private String apkName;
    private String drawableDir;
    private CyclicBarrier cyclicBarrier;
    private OnBuildFinishListener listener;

    private String minSdk;
    private String targetSdk;
    private String versionCode;
    private String versionName;
    private ApkInfo mApkInfo;
    private String apkPath;

    private static final String CHANNEL_CONFIG_PATH = ROOT_PATH + File.separator + "conf" + File.separator + "channel_list.xml";
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_NO_FIND = 1;
    public static final int CODE_FAIL = 2;

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
                        listener.onFinish(CODE_FAIL);
                    }
                    return;
                }
                try {
                    //初始化Decoder
                    Decoder decoder = new Decoder(mApkInfo, channel, channels);
                    decoder.setApkName(apkName);
                    decoder.setMinSdk(minSdk);
                    decoder.setTargetSdk(targetSdk);
                    decoder.setVersionCode(versionCode);
                    decoder.setVersionName(versionName);
                    int result = decoder.decode(apkPath);
                    if (result == CODE_SUCCESS) {
                        decoder.updateConfig(appId, appKey, cpId, cpKey, suffix);
                        Builder builder = new Builder(channel, channels);
                        builder.setApkName(apkName);
                        builder.setPackageName(getPackageName());
                        builder.setApplicationIcons(getIcons());
                        buildApkPath = builder.build(appId, appKey, pubKey, secretKey, cpId, cpKey, suffix, drawableDir);
                        if (Utils.isEmpty(buildApkPath)) {
                            result = CODE_FAIL;
                        }
                    }
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String createOutDir() {
        File apk = searchApk(ROOT_PATH);
        if (apk != null) {
            apkName = apk.getName().substring(0, apk.getName().indexOf("."));
            createFileDir(ROOT_PATH + File.separator + OUT_DIR_PREFIX + apkName + File.separator);
//                FileUtils.createOutDir(apk, new File(outPath + apk.getName()));
            return apk.getAbsolutePath();
        }
        return null;
    }

    public void setChannelId(int id) {
        channelId = id;
    }

    public List<Channel> getChannels() {
        return channels;
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

    public void setMinSdk(String min) {
        minSdk = min;
    }

    public void setTargetSdk(String target) {
        targetSdk = target;
    }

    public void setVersionCode(String code) {
        versionCode = code;
    }

    public void setVersionName(String name) {
        versionName = name;
    }

    public String getPackageName() {
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

    public void sign(final String... args) {
        new SubThread(cyclicBarrier, "SignApk") {
            @Override
            public void execute() {
                if (!Utils.isEmpty(buildApkPath)) {
                    Signer signer = new Signer(channels);
                    signer.setApkName(apkName);
                    signer.sign(buildApkPath, args);
                }
            }
        }.start();
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public void setCpKey(String cpKey) {
        this.cpKey = cpKey;
    }

    public void setDrawableDir(String drawableDir) {
        this.drawableDir = drawableDir;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setBuildFinishListener(OnBuildFinishListener listener) {
        this.listener = listener;
    }

    public interface OnBuildFinishListener {
        void onFinish(int code);
    }
}
