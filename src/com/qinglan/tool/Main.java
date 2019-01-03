package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.entity.GameChannelConfig;
import com.qinglan.tool.ui.BaseUI;
import com.qinglan.tool.ui.HomeUI;
import com.qinglan.tool.ui.MoreUI;
import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.util.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.qinglan.tool.ChannelManager.CODE_NO_FIND;
import static com.qinglan.tool.ChannelManager.CODE_SUCCESS;

public class Main implements HomeUI.OnChangedChannelListener, HomeUI.OnSubmitClickListener, HomeUI.OnCloseListener, ChannelManager.OnBuildFinishListener, MoreUI.OnConfirmClickListener {
    public static final String ROOT_PATH = ".";
    private static final String VERSION_REGEX = "\\d+(\\.\\d+){0,2}";

    private ChannelManager channelManager;
    private ConfigManager confManager;
    private int channelId;
    private HomeUI homeUI;
    private CyclicBarrier cyclicBarrier;

    private String packageName;
    private String drawablePath;
    private String appId;
    private String appKey;
    private String pubKey;
    private String secretKey;
    private String cpId;
    private String cpKey;
    private String suffix;
    private String minSdk;
    private String targetSdk;
    private String versionCode;
    private String versionName;

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
        confManager = new ConfigManager();
        channelManager = new ChannelManager();
        List<Channel> channels = channelManager.getChannels();
        packageName = channelManager.getPackageName();
        channelManager.setBuildFinishListener(this);

        homeUI = new HomeUI(channels, ROOT_PATH);
        init("Welcome!");
        initUI();
        homeUI.setApkInfoText("find apk: " + channelManager.getAppName() + ", versionName: " + versionName);
        homeUI.setCloseListener(this);
        homeUI.setSubmitClickListener(this);
        homeUI.setChangedChannelListener(this);
        homeUI.setOnConfirmClickListener(this);
        homeUI.show();
    }

    private void init(String msg) {
        homeUI.setUIEnable(true);
        homeUI.setMessage(msg);
        channelManager.setCyclicBarrier(cyclicBarrier);
        cyclicBarrier.reset();
    }

    private void initUI() {
        refreshHomeUI("", "", "", "", "", "", "", "",
                channelManager.getDefaultMinSdk(), channelManager.getDefaultTargetSdk(), channelManager.getDefaultVersionCode(), channelManager.getDefaultVersionName());
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
    public void onClick(String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey, String suffix) {
        if (channelId == 0) {
            homeUI.showDialog("Please choose the channel!");
            return;
        }
        homeUI.setUIEnable(false);
        homeUI.setMessage("Build apk.....");
        updateConfig(appId, appKey, pubKey, secretKey, cpId, cpKey, suffix);
        new Thread() {
            @Override
            public void run() {
                channelManager.execute();
                try {
                    cyclicBarrier.await();
                    init("Finish!!");
                    GameChannelConfig config = createGameConfig();
                    if (!confManager.exists(packageName, channelId)) {
                        showSaveDialog("是否保存当前配置？", config);
                    } else {
                        if (!confManager.readConfig(packageName, channelId).equals(config)) {
                            showSaveDialog("配置修改，是否保存？", config);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateConfig(String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey, String suffix) {
        this.appId = appId;
        this.appKey = appKey;
        this.pubKey = pubKey;
        this.secretKey = secretKey;
        this.cpId = cpId;
        this.cpKey = cpKey;
        this.suffix = suffix;
        drawablePath = homeUI.getDrawablePath();

        channelManager.setAppId(appId);
        channelManager.setAppKey(appKey);
        channelManager.setPubKey(pubKey);
        channelManager.setSecretKey(secretKey);
        channelManager.setCpId(cpId);
        channelManager.setCpKey(cpKey);
        channelManager.setSuffix(suffix);
        channelManager.setChannelId(channelId);
        channelManager.setDrawableDir(drawablePath);
        channelManager.setMinSdk(minSdk);
        channelManager.setTargetSdk(targetSdk);
        channelManager.setVersionCode(versionCode);
        channelManager.setVersionName(versionName);
    }

    private void showSaveDialog(String text, final GameChannelConfig config) {
        homeUI.showDialog(text, new HomeUI.OnDialogButtonClickListener() {

            @Override
            public void onPositive() {
                confManager.saveConfig(packageName, channelId, config);
            }

            @Override
            public void onNegative() {

            }
        }, null);
    }

    private GameChannelConfig createGameConfig() {
        GameChannelConfig config = new GameChannelConfig();
        config.setChannelId(channelId);
        config.setDrawablePath(drawablePath);
        config.setAppId(appId);
        config.setAppKey(appKey);
        config.setPublicKey(pubKey);
        config.setSecretKey(secretKey);
        config.setCpId(cpId);
        config.setCpKey(cpKey);
        config.setSuffix(suffix);
        config.setMinSdk(minSdk);
        config.setTargetSdk(targetSdk);
        config.setVersionCode(versionCode);
        config.setVersionName(versionName);
        return config;
    }

    @Override
    public void onChange(int id) {
        channelId = id;
        if (confManager.exists(packageName, channelId)) {
            //存在配置文件，则询问是否导入
            showImportDialog();
        } else {
            initUI();
        }
    }

    private void showImportDialog() {
        homeUI.showDialog("已存在该渠道配置，是否导入？", new HomeUI.OnDialogButtonClickListener() {
            @Override
            public void onPositive() {
                GameChannelConfig config = confManager.readConfig(packageName, channelId);
                if (config != null) {
                    refreshHomeUI(config.getDrawablePath(), config.getAppId(), config.getAppKey(), config.getPublicKey(),
                            config.getSecretKey(), config.getCpId(), config.getCpKey(), config.getSuffix(),
                            config.getMinSdk(), config.getTargetSdk(), config.getVersionCode(), config.getVersionName());
                }
            }

            @Override
            public void onNegative() {

            }
        }, null);
    }

    private void refreshHomeUI(String drawablePath, String appId, String appKey, String publicKey, String secretKey
            , String cpId, String cpKey, String suffix, String minSdk, String targetSdk, String versionCode, String versionName) {
        homeUI.setDrawablePath(drawablePath);
        homeUI.setAppIdText(appId);
        homeUI.setAppKeyText(appKey);
        homeUI.setPublicKeyText(publicKey);
        homeUI.setSecretKeyText(secretKey);
        homeUI.setCpIdText(cpId);
        homeUI.setCpKeyText(cpKey);
        homeUI.setSuffixText(suffix);
        setApkInfo(minSdk, targetSdk, versionCode, versionName);
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
                channelManager.sign();
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
                        channelManager.sign(path, passwords, alias);
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
                    channelManager.sign();
                }
            }
        });
    }

    @Override
    public void onClick(BaseUI ui, String min, String target, String vcode, String vname) {
        if (!checkSdk(ui, min, target)) {
            return;
        }
        if (!Utils.matches(VERSION_REGEX, vname)) {
            ui.showDialog("versionName格式有误");
            return;
        }
        ui.close();
        setApkInfo(min, target, vcode, vname);
    }

    private boolean checkSdk(BaseUI ui, String min, String target) {
        if (!StringUtils.isEmpty(min)) {
            int minNum = Integer.valueOf(min);
            if (minNum <= 0) {
                ui.showDialog("minSdk的值有误");
                return false;
            }
            if (minNum < 9) {
                ui.showDialog("minSdk不能小于9");
                return false;
            }
        }
        if (!StringUtils.isEmpty(target)) {
            int targetNum = Integer.valueOf(target);
            if (targetNum <= 0) {
                ui.showDialog("targetSdk的值有误");
                return false;
            }
            if (targetNum > 29) {
                ui.showDialog("targetSdk不能大于29");
                return false;
            }
        }
        if (!StringUtils.isEmpty(min) && !StringUtils.isEmpty(target)) {
            int minNum = Integer.valueOf(min);
            int targetNum = Integer.valueOf(target);
            if (minNum > targetNum) {
                ui.showDialog("minSdk的值不可大于targetSdk");
                return false;
            }
        }
        return true;
    }

    private void setApkInfo(String min, String target, String vcode, String vname) {
        if (!StringUtils.isEmpty(min)) {
            minSdk = min;
        }
        if (!StringUtils.isEmpty(target)) {
            targetSdk = target;
        }
        if (!StringUtils.isEmpty(vcode)) {
            versionCode = vcode;
        }
        if (!StringUtils.isEmpty(vname)) {
            versionName = vname;
        }
        homeUI.setMinSDK(minSdk);
        homeUI.setTargetSDK(targetSdk);
        homeUI.setVersionCode(versionCode);
        homeUI.setVersionName(versionName);
    }

}
