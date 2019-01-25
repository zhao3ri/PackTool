package com.qinglan.tool;

import com.qinglan.common.Log;
import com.qinglan.tool.entity.AppConfig;
import com.qinglan.tool.entity.GameChannelConfig;
import com.qinglan.tool.ui.MainFrame;
import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.util.ShellUtils;
import com.qinglan.tool.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.qinglan.tool.ChannelManager.STATUS_NO_FIND;
import static com.qinglan.tool.ChannelManager.STATUS_SIGN_SUCCESS;
import static com.qinglan.tool.ChannelManager.STATUS_SUCCESS;

public class Main implements MainFrame.OnChannelChangedListener, MainFrame.OnSubmitClickListener, MainFrame.OnCloseListener, ChannelManager.OnBuildFinishListener, MainFrame.OnConfirmClickListener {
    public static final String ROOT_PATH = ".";
    private static final String VERSION_REGEX = "\\d+(\\.\\d+){0,2}";

    private ChannelManager channelManager;
    private ConfigManager configManager;
    private int channelId;
    //    private HomePane homePane;
    private MainFrame mainFrame;
    private CyclicBarrier cyclicBarrier;

    private String apkPackageName;
    private GameChannelConfig mConfig;

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
        configManager = new ConfigManager();
        channelManager = new ChannelManager();
        List<Channel> channels = channelManager.getChannels();
        apkPackageName = channelManager.getDefaultPackageName();
        channelManager.setBuildFinishListener(this);

        mainFrame = new MainFrame(ROOT_PATH, channels);
        finish("Welcome!");
        mainFrame.setCloseListener(this);
        mainFrame.setSubmitClickListener(this);
        mainFrame.setChangedChannelListener(this);
        mainFrame.setConfirmClickListener(this);
        channelManager.setProgressListener(new ShellUtils.ProgressListener() {
            @Override
            public void publishProgress(String values) {
                mainFrame.setMessage(values);
            }
        });
        mainFrame.setApkInfoText("apk: " + channelManager.getAppName() + ", versionName: " + channelManager.getDefaultVersionName());
        mainFrame.open();
        refresh();
    }

    private void finish(String msg) {
        mainFrame.changeEnable(true);
        mainFrame.setMessage(msg);
        cyclicBarrier.reset();
        channelManager.setCyclicBarrier(cyclicBarrier);
    }

    private void refresh() {
        mConfig = createConfig();
        mainFrame.refreshUI(mConfig, apkPackageName);
    }

    private GameChannelConfig createConfig() {
        GameChannelConfig config = GameChannelConfig.createDefaultConfig();
        config.setAppInfo(createDefaultAppInfo());
        return config;
    }

    private AppConfig createDefaultAppInfo() {
        AppConfig appConfig = new AppConfig();
        appConfig.setMinSdk(channelManager.getDefaultMinSdk());
        appConfig.setTargetSdk(channelManager.getDefaultTargetSdk());
        appConfig.setVersionCode(channelManager.getDefaultVersionCode());
        appConfig.setVersionName(channelManager.getDefaultVersionName());
        return appConfig;
    }

    @Override
    public void onClose() {
        Log.eln("" + cyclicBarrier.isBroken() + " num==" + cyclicBarrier.getNumberWaiting());
        if (cyclicBarrier.getNumberWaiting() > 0) {
            mainFrame.showDialog("<html>The thread is running.<br/> Do you want to exit?</html>", new MainFrame.OnDialogButtonClickListener() {
                @Override
                public void onPositive() {
                    mainFrame.close();
                    System.exit(1);
                }

                @Override
                public void onNegative() {

                }
            }, null);
            return;
        }
        Log.eln("exit!");
        mainFrame.close();
        System.exit(0);
    }

    private void showSaveDialog(String text, final GameChannelConfig config) {
        mainFrame.showDialog(text, new MainFrame.OnDialogButtonClickListener() {

            @Override
            public void onPositive() {
                configManager.saveConfig(apkPackageName, channelId, config);
            }

            @Override
            public void onNegative() {

            }
        }, null);
    }

    @Override
    public void onChange(int id) {
        channelId = id;
        if (configManager.exists(apkPackageName, channelId)) {
            //存在配置文件，则询问是否导入
            showImportDialog();
        } else {
            refresh();
        }
    }

    private void showImportDialog() {
        mainFrame.showDialog("已存在该渠道配置，是否导入？", new MainFrame.OnDialogButtonClickListener() {
            @Override
            public void onPositive() {
                GameChannelConfig config = configManager.readConfig(apkPackageName, channelId);
                if (config != null) {
                    mConfig = config;
                    mainFrame.refreshUI(config, apkPackageName);
                } else {
                    mainFrame.showErrorDialog("导入配置失败！");
                }
            }

            @Override
            public void onNegative() {

            }
        }, null);
    }

    @Override
    public void onFinish(int status) {
        String tip = "Build Error!!";
        switch (status) {
            case STATUS_SUCCESS:
                showKeystoreChooser(status);
                break;
            case STATUS_SIGN_SUCCESS:

                break;
            case STATUS_NO_FIND:
                tip = "No find apk!";
            default:
                mainFrame.showDialog(tip);
                finish("Finish!!");
                break;
        }
    }

    private void showKeystoreChooser(final int code) {
        mainFrame.showDialog("Use default keystore?", new MainFrame.OnDialogButtonClickListener() {//弹窗按钮点击事件
            @Override
            public void onPositive() {
                channelManager.sign();
            }

            @Override
            public void onNegative() {
                mainFrame.showSignChooseDialog(new MainFrame.OnSignSelectedClickListener() {
                    @Override
                    public void onSelected(String path, String passwords, String alias) {
                        channelManager.sign(path, passwords, alias);
                    }
                }, new MainFrame.OnCloseListener() {
                    @Override
                    public void onClose() {
                        onFinish(code);
                    }
                }, "Keystore", "jks", "keystore");
            }
        }, new MainFrame.OnCloseListener() {//弹窗关闭事件
            @Override
            public void onClose() {
                if (code == STATUS_SUCCESS) {
                    channelManager.sign();
                }
            }
        });
    }

    @Override
    public boolean onConfirm(String min, String target, String vcode, String vname) {
        if (!checkSdkVersion(min, target)) {
            return false;
        }
        if (!Utils.matches(VERSION_REGEX, vname)) {
            mainFrame.showErrorDialog("versionName格式有误");
            return false;
        }
        mConfig.updateAppInfo(min, target, vcode, vname);
        return true;
    }

    private boolean checkSdkVersion(String min, String target) {
        if (!StringUtils.isEmpty(min)) {
            int minNum = Integer.valueOf(min);
            if (minNum <= 0) {
                mainFrame.showErrorDialog("minSdk的值有误");
                return false;
            }
            if (minNum < 9) {
                mainFrame.showErrorDialog("minSdk不能小于9");
                return false;
            }
        }
        if (!StringUtils.isEmpty(target)) {
            int targetNum = Integer.valueOf(target);
            if (targetNum <= 0) {
                mainFrame.showErrorDialog("targetSdk的值有误");
                return false;
            }
            if (targetNum > 29) {
                mainFrame.showErrorDialog("targetSdk不能大于29");
                return false;
            }
        }
        if (!StringUtils.isEmpty(min) && !StringUtils.isEmpty(target)) {
            int minNum = Integer.valueOf(min);
            int targetNum = Integer.valueOf(target);
            if (minNum > targetNum) {
                mainFrame.showErrorDialog("minSdk的值不可大于targetSdk");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(String drawable, String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey, String pkg, boolean suffix, boolean useDefault) {
        if (channelId == 0) {
            mainFrame.showErrorDialog("Please choose the channel!");
            return;
        }
        mainFrame.changeEnable(false);
        updateConfig(drawable, appId, appKey, pubKey, secretKey, cpId, cpKey, pkg, suffix, useDefault);
        channelManager.setChannelId(channelId);
        channelManager.setConfig(mConfig);
        new Thread() {
            @Override
            public void run() {
                channelManager.execute();
                try {
                    cyclicBarrier.await();
                    finish("Finish!!");
                    if (!configManager.exists(apkPackageName, channelId)) {
                        showSaveDialog("是否保存当前配置？", mConfig);
                    } else {
                        if (!configManager.readConfig(apkPackageName, channelId).equals(mConfig)) {
                            showSaveDialog("配置修改，是否保存？", mConfig);
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

    private void updateConfig(String drawable, String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey, String pkg, boolean suffix, boolean useDefault) {
        mConfig.setChannelId(channelId);
        mConfig.setDrawablePath(drawable);
        mConfig.setAppId(appId);
        mConfig.setAppKey(appKey);
        mConfig.setPublicKey(pubKey);
        mConfig.setSecretKey(secretKey);
        mConfig.setCpId(cpId);
        mConfig.setCpKey(cpKey);
        mConfig.setSuffix(suffix);
        mConfig.setUseDefaultPackage(useDefault);
        mConfig.setPackageName(pkg);
    }
}
