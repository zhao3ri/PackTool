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
        view.load();
        dialog.setLocationRelativeTo(win);
        return dialog;
    }

    @Override
    protected IMoreView createView() {
        IMoreView view = new MoreView(win, this);
        view.setConfirmClickAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnCode = CODE_ACTION_MORE_CONFIRM;
                if (dialog != null) {
                    dialog.setVisible(false);
                }
            }
        });
        return view;
    }

    public void setMinSdkText(String min) {
        String old = minSdk;
        minSdk = min;
        firePropertyChange(MIN_SDK_TEXT_CHANGED_PROPERTY, old, minSdk);
    }

    public void setTargetSdkText(String target) {
        String old = targetSdk;
        targetSdk = target;
        firePropertyChange(TARGET_SDK_TEXT_CHANGED_PROPERTY, old, targetSdk);
    }

    public void setVersionCodeText(String code) {
        String old = versionCode;
        versionCode = code;
        firePropertyChange(VERSION_CODE_TEXT_CHANGED_PROPERTY, old, versionCode);
    }

    public void setVersionNameText(String name) {
        String old = versionName;
        versionName = name;
        firePropertyChange(VERSION_NAME_TEXT_CHANGED_PROPERTY, old, versionName);
    }

    public String getMinSDKText() {
        return minSdk;
    }

    public String getTargetSdkText() {
        return targetSdk;
    }

    public String getVersionNameText() {
        return versionName;
    }

    public String getVersionCodeText() {
        return versionCode;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        returnCode = CODE_ACTION_CLOSE;
    }

}
