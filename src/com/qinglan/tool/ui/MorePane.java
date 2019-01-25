package com.qinglan.tool.ui;

import com.qinglan.tool.ui.widget.IMoreView;
import com.qinglan.tool.ui.widget.impl.MoreView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MorePane extends BasePane {
    private JDialog dialog;

    private String minSdk;
    private String targetSdk;
    private String versionCode;
    private String versionName;

    protected static final String MIN_SDK_TEXT_CHANGED_PROPERTY = "MorePane.MinSdkTextChangedProperty";
    protected static final String TARGET_SDK_TEXT_CHANGED_PROPERTY = "MorePane.TargetSdkTextChangedProperty";
    protected static final String VERSION_NAME_TEXT_CHANGED_PROPERTY = "MorePane.VersionNameTextChangedProperty";
    protected static final String VERSION_CODE_TEXT_CHANGED_PROPERTY = "MorePane.VersionCodeTextChangedProperty";

    public MorePane(JFrame frame) {
        super(frame);
    }

    public int showDialog() {
        dialog = createDialog("更多设置", win);
        dialog.setVisible(true);
        dialog.getContentPane().removeAll();
        dialog.dispose();
        dialog = null;
        return returnCode;
    }

    private JDialog createDialog(String title, Component parent) {
        JDialog dialog;
        Window window = getWindowForComponent(parent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, title, true);
        } else {
            dialog = new JDialog((Dialog) window, title, true);
        }
        dialog.addWindowListener(this);
        dialog.setResizable(false);
        dialog.setSize(new Dimension(view.getWidth(), view.getHeight()));
        dialog.getContentPane().add(view.getContentView());
        dialog.setLocationRelativeTo(win);
        load();
        return dialog;
    }

    @Override
    protected IMoreView getView() {
        return (IMoreView) view;
    }

    @Override
    protected IMoreView createView() {
        IMoreView view = new MoreView(win, this);
        view.setConfirmClickAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnCode = CODE_ACTION_MORE_CONFIRM;
                setMinSdk(getView().getMinSdkText());
                setTargetSdk(getView().getTargetSdkText());
                setVersionCode(getView().getVersionCodeText());
                setVersionName(getView().getVersionNameText());
                if (dialog != null) {
                    dialog.setVisible(false);
                }
            }
        });
        return view;
    }

    public void setMinSdk(String min) {
        String old = minSdk;
        minSdk = min;
        firePropertyChange(MIN_SDK_TEXT_CHANGED_PROPERTY, old, minSdk);
    }

    public void setTargetSdk(String target) {
        String old = targetSdk;
        targetSdk = target;
        firePropertyChange(TARGET_SDK_TEXT_CHANGED_PROPERTY, old, targetSdk);
    }

    public void setVersionCode(String code) {
        String old = versionCode;
        versionCode = code;
        firePropertyChange(VERSION_CODE_TEXT_CHANGED_PROPERTY, old, versionCode);
    }

    public void setVersionName(String name) {
        String old = versionName;
        versionName = name;
        firePropertyChange(VERSION_NAME_TEXT_CHANGED_PROPERTY, old, versionName);
    }

    public String getMinSDK() {
        return minSdk;
    }

    public String getTargetSdk() {
        return targetSdk;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        returnCode = CODE_ACTION_CLOSE;
    }

}
