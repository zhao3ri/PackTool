package com.tyland.tool.ui;

import com.tyland.common.Log;
import com.tyland.tool.YJConfig;
import com.tyland.tool.entity.Channel;
import com.tyland.tool.entity.GameChannelConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import static com.tyland.tool.ui.BasePane.*;
import static com.tyland.tool.ui.DialogOptionPane.TYPE_APK;
import static javax.swing.JOptionPane.*;

public class MainFrame extends JFrame implements ComponentListener, PropertyChangeListener, ActionListener {
    protected static final int FRAME_WIDTH = 550;
    protected static final int FRAME_HEIGHT = 555;

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
                    closeListener.onClose();

            }
        });
        homePane = createHomePane();
    }

    public void open() {
//        this.setVisible(true);
//        homePane.load();
        DialogOptionPane optionPane = new DialogOptionPane(this, TYPE_APK);
        int result = optionPane.showDialog(currentPath, "apk", "apk");
        if (result == CODE_ACTION_FILE_CONFIRM) {
            this.setVisible(true);
            homePane.load();
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
        homePane.setVersionName(config.apkInfo.getVersionName());
        homePane.setVersionCode(config.apkInfo.getVersionCode());
        homePane.setMinSdk(config.apkInfo.getMinSdk());
        homePane.setTargetSdk(config.apkInfo.getTargetSdk());
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

    public void showWarningDialog(String msg) {
        showDialog(msg, JOptionPane.WARNING_MESSAGE);
    }

    private void showDialog(String msg, int messageType) {
        JOptionPane.showMessageDialog(this, msg, "提示", messageType);
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
                    closeListener.onClose();
                }
                break;
        }
        Log.iln("result==" + result);
    }

    public void showSignChooseDialog(final OnSignSelectedClickListener completeClickListener
            , final OnCloseListener closeListener, final String filterDesc, final String... filters) {
        DialogOptionPane optionPane = new DialogOptionPane(this);
        int result = optionPane.showDialog(currentPath, filterDesc, filters);
        if (result == CODE_ACTION_FILE_CONFIRM) {
            if (completeClickListener != null) {
                completeClickListener.onSelected(optionPane.getSignPath(), optionPane.getPassword(), optionPane.getAlias());
            }
        } else if (result == CODE_ACTION_CLOSE) {
            if (closeListener != null) {
                closeListener.onClose();
            }
        }
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
                if (updateClickListener != null)
                    updateClickListener.onClickUpdate(homePane.getAppNameText(), homePane.getChannelKeyText(), homePane.getGameIdText(),
                            homePane.getGameKeyText(), homePane.getGameVersionText(), homePane.getMinSDK(), homePane.getTargetSdk(), homePane.getVersionCode(), homePane.getVersionName());
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
        void onClose();
    }

    public interface OnDialogButtonClickListener {
        void onPositive();

        void onNegative();
    }

    public interface OnUpdateClickListener {
        void onClickUpdate(String appName, String channelKey, String gameId, String gameKey, String gameVersion, String min, String target, String vcode, String vname);
    }

    public interface OnPackageClickListener {
        void onClickPackage();
    }

    public interface OnSignSelectedClickListener {
        void onSelected(String path, String passwords, String alias);
    }

    public interface LoadApkListener {
        void onLoad(String apk);
    }

}
