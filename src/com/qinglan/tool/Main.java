package com.qinglan.tool;

import com.qinglan.tool.ui.HomeUI;
import com.qinglan.tool.xml.Channel;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Main implements HomeUI.OnChangedChannelListener, HomeUI.OnSubmitClickListener, HomeUI.OnCloseListener, ChannelManager.OnBuildFinishListener {
    ChannelManager manager;
    int channelId;
    HomeUI homeUI;
    CyclicBarrier cyclicBarrier;

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }

    private void init() {
        manager = new ChannelManager();
        List<Channel> channels = manager.getChannels();
        manager.setBuildFinishListener(this);

        homeUI = new HomeUI(channels);
        homeUI.setCloseListener(this);
        homeUI.setSubmitClickListener(this);
        homeUI.setChangedChannelListener(this);
        cyclicBarrier = new CyclicBarrier(3);
        manager.setCyclicBarrier(cyclicBarrier);
    }

    @Override
    public void onClose() {
//        Log.eln("" + cyclicBarrier.isBroken() + " num==" + cyclicBarrier.getNumberWaiting());
//        homeUI.showDialog("The thread is running. Do you want to exit?", true);
//        System.exit(1);
    }

    @Override
    public void onClick(String appId, String appKey, String pubKey, String secretKey, String cpId) {
        if (channelId == 0) {
            homeUI.showDialog("Please choose the channel!");
            return;
        }
        homeUI.setUIEnable(false);
        homeUI.setMessage("Build apk.....");
        manager.setAppId(appId);
        manager.setAppKey(appKey);
        manager.setPubKey(pubKey);
        manager.setSecretKey(secretKey);
        manager.setCpId(cpId);
        new Thread() {
            @Override
            public void run() {
                manager.execute();
                try {
                    cyclicBarrier.await();
                    homeUI.setUIEnable(true);
                    homeUI.setMessage("Finish!!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onChange(int id) {
        channelId = id;
        manager.setChannelId(channelId);
    }

    @Override
    public void onFinish(String... args) {
        homeUI.showDialog("Use default keystore?", true, new HomeUI.OnDialogButtonClickListener() {
            @Override
            public void onPositive() {
                homeUI.setMessage("Sign apk.....");
                manager.sign();
            }

            @Override
            public void onNegative() {
                homeUI.setMessage("Sign apk.....");
                homeUI.showSignChooseDialog(new HomeUI.OnSignChooseClickListener() {
                    @Override
                    public void onClick(JTextField text) {
                        if (!text.getText().endsWith(".jks") && !text.getText().endsWith(".keystore")) {
                            homeUI.showDialog("Illegal keystore path!");
                            text.setText("");
                            return;
                        }
                    }
                }, new HomeUI.OnSignCompleteClickListener() {
                    @Override
                    public void onClick(String path, String passwords, String alias) {
                        manager.sign(path,passwords,alias);
                    }
                }, "Keystore", "jks", "keystore");
            }
        });
    }
}
