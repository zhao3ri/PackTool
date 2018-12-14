package com.qinglan.tool;

import com.qinglan.tool.ui.HomeUI;
import com.qinglan.tool.util.FileUtil;
import com.qinglan.tool.xml.ChannelList;
import com.qinglan.tool.xml.Channel;
import com.qinglan.tool.xml.XmlTool;

import java.util.List;

/**
 * Created by zhaoj on 2018/10/29.
 */
public class ChannelHandler {
    List<Channel> channels;
    private int channelId;
    private String appId;
    private String appKey;
    private String pubKey;
    private String secretKey;
    private String cpId;
    private HomeUI ui;

    public static void main(String[] args) {
    }

    public ChannelHandler() {
        initChannel();
    }

    private void initChannel() {
        String xml = FileUtil.readFile("channel_list.xml");
        ChannelList channelList = XmlTool.xml2Object(xml, ChannelList.class);
        if (channelList != null) {
            channels = channelList.getChannelList();
        }
    }

    public void execute() {
        Channel channel = getChannel(channelId);

        Decoder decoder = new Decoder(channel, channels);
        int result = decoder.decode();
        setMessage("decoding.....");
        if (result == 0) {
            setMessage("decode success!");
            String apk = decoder.updateConfig(appId, cpId, appKey);
            Builder builder = new Builder(this, channel, channels);
            builder.setApkName(apk);

            builder.build(appId, appKey, pubKey, secretKey, cpId);
        }
    }

    public void setChannelId(int id) {
        channelId = id;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    private Channel getChannel(int channelId) {
        if (null != channels && !channels.isEmpty()) {
            for (Channel channel : channels) {
                if (channelId == channel.getId()) {
                    return channel;
                }
            }
        }
        return null;
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

    public void setUi(HomeUI ui) {
        this.ui = ui;
    }

    public void setMessage(String msg) {
        if (ui != null)
            ui.setMessage(msg);
    }

    public boolean showDialog(String msg, HomeUI.OnDialogButtonClickListener listener) {
        if (ui != null) {
            ui.showDialog(msg, true, listener);
            return true;
        }
        return false;
    }

}
