package com.qinglan.tool;

import com.qinglan.tool.ui.HomeUI;
import com.qinglan.tool.xml.Channel;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main implements HomeUI.OnChangedChannelListener, HomeUI.OnSubmitClickListener, HomeUI.OnCloseListener {
    final CyclicBarrier barrier = new CyclicBarrier(2);
    ChannelHandler handler;
    int channelId;
    HomeUI homeUI;

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }

    private void init() {
        handler = new ChannelHandler();
        List<Channel> channels = handler.getChannels();

        homeUI = new HomeUI(channels);
        homeUI.setCloseListener(this);
        homeUI.setSubmitClickListener(this);
        homeUI.setChangedChannelListener(this);

        handler.setUi(homeUI);
        barrier.reset();
    }

    @Override
    public void onClose() {
        if (barrier.isBroken()) {
            homeUI.showDialog("The thread is running. Do you want to exit?", true);
            return;
        }
        System.exit(1);
    }

    @Override
    public void onClick(String appId, String appKey, String pubKey, String secretKey, String cpId) {
        if (channelId == 0) {
            homeUI.showDialog("Please choose the channel!");
            return;
        }
        homeUI.setRadioButtonsEnable(false);
        handler.setAppId(appId);
        handler.setAppKey(appKey);
        handler.setPubKey(pubKey);
        handler.setSecretKey(secretKey);
        handler.setCpId(cpId);
        Thread thread = new Thread() {
            @Override
            public void run() {
                handler.execute();
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChange(int id) {
        channelId = id;
        handler.setChannelId(channelId);
    }
}
