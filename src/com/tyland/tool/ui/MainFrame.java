package com.tyland.tool.ui;

import com.tyland.common.Log;
import com.tyland.tool.entity.AppVersionInfo;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.tyland.tool.ui.BasePane.*;
import static javax.swing.JOptionPane.*;

public class MainFrame extends JFrame implements ComponentListener, PropertyChangeListener, ActionListener {
    protected static final int FRAME_WIDTH = 550;
    protected static final int FRAME_HEIGHT = 575;

    private OnCloseListener closeListener;
    private LoadApkListener loadApkListener;
    private OnUpdateClickListener updateClickListener;
    private OnPackageClickListener packageClickListener;

    private HomePane homePane;
    private String currentPath;

    public MainFrame(String path) {
        create();
        currentPath = path;
    }

    private void create() {
        setTitle("打包工具");
        setLayout(new FlowLayout());
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT)); //设置窗口的大小
        setLocation(300, 200);//设置窗口的初始位置
        setResizable(false);
        addComponentListener(this);
//        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (closeListener != null)
                    closeListener.onClose(CLOSED_OPTION);

            }
        });
        homePane = createHomePane();
    }

    public void open() {
//        this.setVisible(true);
//        homePane.load();
        DialogOptionPane optionPane = new DialogOptionPane(this);
        int result = optionPane.showDialog(currentPath, "apk", "apk");
        if (result == CODE_ACTION_FILE_CONFIRM) {
            if (!Utils.isEmpty(optionPane.getView().getChooseFilePath())) {
                this.setVisible(true);
                homePane.load();
            }
            if (loadApkListener != null)
                loadApkListener.onLoad(optionPane.getView().getChooseFilePath());
        } else if (result == CODE_ACTION_CLOSE) {
            close();
            System.exit(0);
        }
    }

    private HomePane createHomePane() {
        HomePane home = new HomePane(this);
        home.setPropertyChangeListener(this);
        home.setActionListener(this);
        return home;
    }

    public void refreshView(YJConfig config) {
        if (homePane == null) {
            return;
        }
        homePane.setAppPackageText(config.packageName);
        homePane.setAppNameText(config.appName);
        homePane.setChannelKeyText(config.channelKey);
        homePane.setGameIdText(config.gameId);
        homePane.setGameKeyText(config.gameKey);
        homePane.setGameVersionText(config.gameVersion);
        homePane.setVersionName(config.appInfo.getVersionName());
        homePane.setVersionCode(config.appInfo.getVersionCode());
        homePane.setMinSdk(config.appInfo.getMinSdk());
        homePane.setTargetSdk(config.appInfo.getTargetSdk());
        homePane.setAgentIdText(config.agentId);
        homePane.setSiteIdText(config.siteId);
    }

    public void close() {
//        setVisible(false);
        homePane.recycle();
        getContentPane().removeAll();
        dispose();
    }

    public void showDialog(String msg) {
        showDialog(msg, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorDialog(String msg) {
        showDialog(msg, JOptionPane.ERROR_MESSAGE);
    }

    public void showErrorDialog(String msg, OnCloseListener closeListener) {
        showDialog(msg, JOptionPane.ERROR_MESSAGE, closeListener);
    }

    public void showWarningDialog(String msg) {
        showDialog(msg, JOptionPane.WARNING_MESSAGE);
    }

    private void showDialog(String msg, int messageType) {
        this.showDialog(msg, messageType, null);
    }

    private void showDialog(String msg, int messageType, OnCloseListener closeListener) {
//        JOptionPane.showMessageDialog(this, msg, "提示", messageType);
        int result = JOptionPane.showOptionDialog(this, msg, "提示", DEFAULT_OPTION,
                messageType, null, null, null);
        if (result == CLOSED_OPTION || result == YES_OPTION) {
            if (closeListener != null)
                closeListener.onClose(result);
        }
    }

    public void showDialog(String msg, final OnDialogButtonClickListener listener, final OnCloseListener closeListener) {
        showDialog(msg, listener, closeListener, QUESTION_MESSAGE);
    }

    public void showDialog(String msg, final OnDialogButtonClickListener listener, final OnCloseListener closeListener, int msgType) {
        int result = JOptionPane.showConfirmDialog(this, msg, "提示", JOptionPane.YES_NO_OPTION, msgType);
        switch (result) {
            case YES_OPTION:
                if (listener != null) {
                    listener.onPositive();
                }
                break;
            case NO_OPTION:
                if (listener != null) {
                    listener.onNegative();
                }
                break;
            case CLOSED_OPTION:
                if (closeListener != null) {
                    closeListener.onClose(result);
                }
                break;
        }
        Log.iln("result==" + result);
    }

    public void setCloseListener(OnCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setPackageClickListener(OnPackageClickListener packageClickListener) {
        this.packageClickListener = packageClickListener;
    }

    public void setUpdateClickListener(OnUpdateClickListener updateClickListener) {
        this.updateClickListener = updateClickListener;
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        String newValue = String.valueOf(evt.getNewValue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Integer) {
            int action = (int) e.getSource();
            Log.iln("action==" + action);
            if (action == CODE_ACTION_CLICK_UPDATE) {
                //点击更新
                if (updateClickListener != null) {
                    YJConfig config = new YJConfig();
                    config.appName = homePane.getAppNameText();
                    config.channelKey = homePane.getChannelKeyText();
                    config.gameId = homePane.getGameIdText();
                    config.gameKey = homePane.getGameKeyText();
                    config.gameVersion = homePane.getGameVersionText();
                    config.agentId = homePane.getAgentIdText();
                    config.siteId = homePane.getSiteIdText();
                    config.appInfo = new AppVersionInfo(homePane.getMinSDK(), homePane.getTargetSdk(), homePane.getVersionCode(), homePane.getVersionName());
                    updateClickListener.onClickUpdate(config);
                }
                return;
            }
            if (action == CODE_ACTION_CLICK_PACKAGE) {
                //点击打包
                if (packageClickListener != null) {
                    packageClickListener.onClickPackage();
                }
                return;
            }
        }
    }

    public void setMessage(String msg) {
        homePane.setMessage(msg);
    }

    public void changeEnable(boolean enable) {
        homePane.setViewEnable(enable);
    }

    public void setLoadApkListener(LoadApkListener listener) {
        loadApkListener = listener;
    }

    public interface OnCloseListener {
        void onClose(int result);
    }

    public interface OnDialogButtonClickListener {
        void onPositive();

        void onNegative();
    }

    public interface OnUpdateClickListener {
        void onClickUpdate(YJConfig config);
    }

    public interface OnPackageClickListener {
        void onClickPackage();
    }

    public interface LoadApkListener {
        void onLoad(String apk);
    }

}
