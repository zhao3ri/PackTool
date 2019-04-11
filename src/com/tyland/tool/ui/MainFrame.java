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
    private OnSubmitClickListener submitClickListener;
    private OnChannelChangedListener changedChannelListener;
    private OnConfirmClickListener confirmClickListener;
    private LoadApkListener loadApkListener;

    private HomePane homePane;
    private String currentPath;

    public MainFrame(String path, List<Channel> channelList) {
        create(channelList);
        currentPath = path;
    }

    private void create(List<Channel> channelList) {
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
        homePane = createHomePane(channelList);
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
                loadApkListener.onLoad();
        } else if (result == CODE_ACTION_CLOSE) {
            close();
            System.exit(0);
        }
    }

    private HomePane createHomePane(List<Channel> channelList) {
        HomePane home = new HomePane(this, channelList);
        home.setPropertyChangeListener(this);
        home.setActionListener(this);
        return home;
    }

    public void refreshUI(GameChannelConfig config, String packageName) {
        if (homePane == null) {
            return;
        }
        homePane.setDrawablePath(config.getDrawablePath());
//        homePane.setAppIdText(config.getAppId());
//        homePane.setAppKeyText(config.getAppKey());
//        homePane.setPublicKeyText(config.getPublicKey());
//        homePane.setSecretKeyText(config.getSecretKey());
//        homePane.setCpIdText(config.getCpId());
//        homePane.setCpKeyText(config.getCpKey());
        homePane.setVersionName(config.getAppInfo().getVersionName());
        homePane.setVersionCode(config.getAppInfo().getVersionCode());
        homePane.setMinSdk(config.getAppInfo().getMinSdk());
        homePane.setTargetSdk(config.getAppInfo().getTargetSdk());
        homePane.selectedPackage(config.isUseDefaultPackage(), config.isSuffix());
        homePane.setDefaultPackageName(packageName);
        if (!config.isUseDefaultPackage()) {
            if (config.isSuffix()) {
                homePane.setPackageSuffix(config.getPackageName());
            } else {
                homePane.setNewPackageName(config.getPackageName());
            }
        }
    }

    public void refreshView(YJConfig config) {
        if (homePane == null) {
            return;
        }
        homePane.setVersionName(config.apkInfo.getVersionName());
        homePane.setVersionCode(config.apkInfo.getVersionCode());
        homePane.setMinSdk(config.apkInfo.getMinSdk());
        homePane.setTargetSdk(config.apkInfo.getTargetSdk());
        homePane.setDefaultPackageName(config.packageName);
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

    public void setSubmitClickListener(OnSubmitClickListener submitClickListener) {
        this.submitClickListener = submitClickListener;
    }

    public void setChangedChannelListener(OnChannelChangedListener changedChannelListener) {
        this.changedChannelListener = changedChannelListener;
    }

    public void setConfirmClickListener(OnConfirmClickListener listener) {
        this.confirmClickListener = listener;
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
        if (prop.equals(CHANNEL_RADIO_CHANGED_PROPERTY)) {
            if (changedChannelListener != null) {
                changedChannelListener.onChange(Integer.valueOf(newValue));
            }
            return;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Integer) {
            int action = (int) e.getSource();
            Log.iln("action==" + action);
            if (action == CODE_ACTION_CLICK_UPDATE) {
                return;
            }
            if (action == CODE_ACTION_CLICK_PACKAGE) {
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

    public interface OnConfirmClickListener {
        boolean onConfirm(String min, String target, String vcode, String vname);
    }

    public interface OnSubmitClickListener {
        void onClick(String drawable, String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey,
                     String pkg, boolean suffix, boolean useDefault);
    }

    public interface OnChannelChangedListener {
        void onChange(int id);
    }

    public interface OnSignSelectedClickListener {
        void onSelected(String path, String passwords, String alias);
    }

    public interface LoadApkListener {
        void onLoad();
    }

//    public interface OnSignChooseClickListener {
//        void onSelected(JTextField text);
//    }

}
