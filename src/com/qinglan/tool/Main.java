package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.ui.HomeUI;
import com.qinglan.tool.xml.Channel;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.qinglan.tool.ChannelManager.CODE_NO_FIND;
import static com.qinglan.tool.ChannelManager.CODE_SUCCESS;

public class Main implements HomeUI.OnChangedChannelListener, HomeUI.OnSubmitClickListener, HomeUI.OnCloseListener, ChannelManager.OnBuildFinishListener {
    ChannelManager manager;
    int channelId;
    HomeUI homeUI;
    CyclicBarrier cyclicBarrier;

    public static void main(String[] args) {
        Main main = new Main();
        Log.setLevel(Log.INFO);
        main.start();
    }

    private void start() {
        cyclicBarrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {

            }
        });
        manager = new ChannelManager();
        List<Channel> channels = manager.getChannels();
        manager.setBuildFinishListener(this);

        homeUI = new HomeUI(channels);
        init("Welcome!");
        homeUI.setCloseListener(this);
        homeUI.setSubmitClickListener(this);
        homeUI.setChangedChannelListener(this);
    }

    private void init(String msg) {
        homeUI.setUIEnable(true);
        homeUI.setMessage(msg);
        manager.setCyclicBarrier(cyclicBarrier);
        cyclicBarrier.reset();
    }

    @Override
    public void onClose() {
        Log.eln("" + cyclicBarrier.isBroken() + " num==" + cyclicBarrier.getNumberWaiting());
        if (cyclicBarrier.getNumberWaiting() > 0) {
            homeUI.showDialog("<html>The thread is running.<br/> Do you want to exit?</html>", new HomeUI.OnDialogButtonClickListener() {
                @Override
                public void onPositive() {
                    System.exit(1);
                }

                @Override
                public void onNegative() {

                }
            }, null);
            return;
        }
        System.exit(0);
    }

    @Override
    public void onClick(String appId, String appKey, String pubKey, String secretKey, String cpId, String suffix) {
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
        manager.setSuffix(suffix);
        manager.setChannelId(channelId);
        new Thread() {
            @Override
            public void run() {
                manager.execute();
                try {
                    cyclicBarrier.await();
                    init("Finish!!");
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
    }

    @Override
    public void onFinish(int code) {
        String tip = "Build Error!!";
        switch (code) {
            case CODE_SUCCESS:
                showKeystoreChooser(code);
                break;
            case CODE_NO_FIND:
                tip = "No find apk!";
            default:
                homeUI.showDialog(tip);
                init("Finish!!");
                break;
        }
    }

    private void showKeystoreChooser(final int code) {
        homeUI.showDialog("Use default keystore?", new HomeUI.OnDialogButtonClickListener() {//弹窗按钮点击事件
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
                        manager.sign(path, passwords, alias);
                    }
                }, new HomeUI.OnCloseListener() {
                    @Override
                    public void onClose() {
                        onFinish(code);
                    }
                }, "Keystore", "jks", "keystore");
            }
        }, new HomeUI.OnCloseListener() {//弹窗关闭事件
            @Override
            public void onClose() {
                if (code == CODE_SUCCESS) {
                    homeUI.setMessage("Sign apk.....");
                    manager.sign();
                }
            }
        });
    }

}
