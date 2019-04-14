package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.AppConfig;
import com.tyland.tool.entity.GameChannelConfig;
import com.tyland.tool.ui.MainFrame;
import com.tyland.tool.entity.Channel;
import com.tyland.tool.util.ShellUtils;
import com.tyland.tool.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.tyland.tool.ChannelManager.*;

public class Main implements MainFrame.OnUpdateClickListener, MainFrame.OnCloseListener, ChannelManager.OnBuildFinishListener, MainFrame.OnPackageClickListener, MainFrame.LoadApkListener {
    public static final String ROOT_PATH = ".";
    private static final String VERSION_REGEX = "\\d+(\\.\\d+){0,2}";

    private ChannelManager channelManager;
    //    private HomePane homePane;
    private MainFrame mainFrame;
    private CyclicBarrier cyclicBarrier;

    private String apkPackageName;
    private YJConfig mConfig;

    public static void main(String[] args) {
        Main main = new Main();
        Log.setLevel(Log.INFO);
        main.start();
    }

    private void start() {
        cyclicBarrier = new CyclicBarrier(1, new Runnable() {
            @Override
            public void run() {

            }
        });
        mainFrame = new MainFrame(ROOT_PATH);
        mainFrame.setCloseListener(this);
        mainFrame.setUpdateClickListener(this);
        mainFrame.setPackageClickListener(this);
        mainFrame.setLoadApkListener(this);

        mainFrame.open();
        refresh();
    }

    private void finish(String msg) {
        mainFrame.changeEnable(true);
        mainFrame.setMessage(msg);
        cyclicBarrier.reset();
        if (channelManager != null)
            channelManager.setCyclicBarrier(cyclicBarrier);
    }

    private void refresh() {
        mConfig = createConfig();
        mainFrame.refreshView(mConfig);
    }

    private YJConfig createConfig() {
        YJConfig config = new YJConfig();
        config.appName = channelManager.getDefaultAppName();
        config.packageName = channelManager.getDefaultPackageName();
        config.apkInfo = createDefaultAppInfo();
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

    @Override
    public void onFinish(int status) {
        String tip = "Error!!";
        switch (status) {
            case STATUS_DECODE_SUCCESS:
                Log.iln("反编译完成");
                finish("Decode finish!!");
                mConfig = channelManager.getYjConfig();
                mainFrame.refreshView(mConfig);
                break;
            case STATUS_BUILD_SUCCESS:
                Log.iln("打包完成");
                finish("Build finish!!");
                sign();
                break;
            case STATUS_SIGN_SUCCESS:
                Log.iln("签名完成");
                finish("Signed finish!!");
                break;
            case STATUS_NO_FIND:
                tip = "No find apk!";
            default:
                mainFrame.showDialog(tip);
                finish("Finish!!");
                break;
        }
    }

    private void sign() {
        new Thread() {
            @Override
            public void run() {
                channelManager.sign();
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

//    @Override
//    public boolean onConfirm(String min, String target, String vcode, String vname) {
//        if (!checkSdkVersion(min, target)) {
//            return false;
//        }
//        if (!Utils.matches(VERSION_REGEX, vname)) {
//            mainFrame.showErrorDialog("versionName格式有误");
//            return false;
//        }
//        mConfig.updateAppInfo(min, target, vcode, vname);
//        return true;
//    }

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
    public void onClickPackage() {
        mainFrame.changeEnable(false);
        mainFrame.setMessage("Build apk....");
        new Thread() {
            @Override
            public void run() {
                channelManager.buildApk();
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void onClickUpdate(String appName, String channelKey, String gameId, String gameKey, String min, String target, String vcode, String vname) {
        mConfig.appName = appName;
        mConfig.channelKey = channelKey;
        mConfig.gameId = gameId;
        mConfig.gameKey = gameKey;
        mConfig.apkInfo.setMinSdk(min);
        mConfig.apkInfo.setTargetSdk(target);
        mConfig.apkInfo.setVersionCode(vcode);
        mConfig.apkInfo.setVersionName(vname);
        channelManager.updateConfig(mConfig);
    }

    @Override
    public void onLoad(String apk) {
        channelManager = new ChannelManager(apk);
        apkPackageName = channelManager.getDefaultPackageName();
        channelManager.setBuildFinishListener(this);

        channelManager.setProgressListener(new ShellUtils.ProgressListener() {
            @Override
            public void publishProgress(String values) {
                mainFrame.setMessage(values);
            }
        });
        if (!channelManager.isExistApk()) {
            mainFrame.showErrorDialog("当前无apk文件！");
            return;
        }
        finish("Decode apk....");
        new Thread() {
            @Override
            public void run() {
                channelManager.decodeApk();
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
