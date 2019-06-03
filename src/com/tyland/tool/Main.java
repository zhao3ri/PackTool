package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.ui.MainFrame;
import com.tyland.tool.util.ShellUtils;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static com.tyland.tool.ChannelManager.*;

public class Main implements MainFrame.OnCloseListener, OnExecuteFinishListener, MainFrame.OnPackageClickListener {
    public static final String ROOT_PATH = ".";
    private static final String VERSION_REGEX = "\\d+(\\.\\d+){0,2}";

    private ChannelManager channelManager;
    private MainFrame mainFrame;
    private CyclicBarrier cyclicBarrier;

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
        mainFrame.setPackageClickListener(this);
        mainFrame.changeEnable(true);
        initChannelManager();
        finish("等待输入确认");
        mainFrame.open();
    }

    private void finish(String msg) {
        mainFrame.changeEnable(true);
        mainFrame.setMessage(msg);
        cyclicBarrier.reset();
        if (channelManager != null)
            channelManager.setCyclicBarrier(cyclicBarrier);
    }

    private void initChannelManager() {
        channelManager = new ChannelManager();
        channelManager.setExecuteFinishListener(this);

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
    }

    @Override
    public void onClose(int result) {
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
            case STATUS_SUCCESS:
                Log.iln("打包完成");
                finish("Success!! path = " + channelManager.getOutApkPath());
                break;
            case STATUS_NO_FIND:
                tip = "No find apk!";
            default:
                mainFrame.showDialog(tip);
                finish("Finish!!");
                break;
        }
    }

    @Override
    public void onClickPackage(YJConfig config) {
        mainFrame.changeEnable(false);
        mainFrame.setMessage("Build apk....");
        new Thread() {
            @Override
            public void run() {
                channelManager.execute(config);
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
