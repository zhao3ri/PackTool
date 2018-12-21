package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.util.FileUtil;
import com.qinglan.tool.util.SubThread;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.xml.ChannelList;
import com.qinglan.tool.xml.Channel;
import com.qinglan.tool.xml.XmlTool;

import java.io.File;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

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
    private String suffix;
    private String apkPath;
    private String apkName;
    private CyclicBarrier cyclicBarrier;
    private OnBuildFinishListener listener;

    public static final String ROOT_PATH = ".";
    private static final String SIGN_PATH = ROOT_PATH + File.separator + "conf" + File.separator + "channel_list.xml";
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_NO_FIND = 1;
    public static final int CODE_FAIL = 2;

    public ChannelManager() {
        initChannel();
    }

    private void initChannel() {
        String xml = FileUtil.readFile(SIGN_PATH);
        ChannelList channelList = XmlTool.xml2Object(xml, ChannelList.class);
        if (channelList != null) {
            channels = channelList.getChannelList();
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
                Decoder decoder = new Decoder(channel, channels);
                int result = decoder.decode();
                if (result == CODE_SUCCESS) {
                    apkName = decoder.updateConfig(appId, cpId, appKey, suffix);
                    Builder builder = new Builder(channel, channels);
                    builder.setApkName(apkName);
                    builder.setPackageName(decoder.getPackageName());
                    builder.setApplicationIcons(decoder.getIcons());
                    apkPath = builder.build(appId, appKey, pubKey, secretKey, cpId, suffix);
                    if (Utils.isEmpty(apkPath)) {
                        result = CODE_FAIL;
                    }
                }
                if (listener != null) {
                    listener.onFinish(result);
                }
            }
        }.start();
    }

    public void setChannelId(int id) {
        channelId = id;
    }

    public List<Channel> getChannels() {
        return channels;
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
                if (!Utils.isEmpty(apkPath)) {
                    Signer signer = new Signer(channels);
                    signer.setApkName(apkName);
                    signer.sign(apkPath, args);
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
