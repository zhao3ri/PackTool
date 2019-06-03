package com.tyland.tool.ui;

import com.tyland.tool.ui.widget.IHomeView;
import com.tyland.tool.ui.widget.IView;
import com.tyland.tool.ui.widget.impl.HomeView;

import javax.swing.*;
import java.awt.event.*;

public class HomePane extends BasePane {

    public static final String UI_ENABLE_CHANGED_PROPERTY = "HomePane.UiEnableChangedProperty";
    public static final String APP_PKG_CHANGED_PROPERTY = "HomePane.AppPackageChangedProperty";
    public static final String APP_NAME_CHANGED_PROPERTY = "HomePane.AppNameChangedProperty";
    public static final String MESSAGE_TEXT_CHANGED_PROPERTY = "HomePane.MessageTextChangedProperty";

    private String appPackage;
    private String appName;
    private String message;
    private boolean isEnable = true;


    public HomePane(JFrame frame) {
        super(frame);
        frame.getContentPane().add(view.getContentView());
    }

    @Override
    protected IView createView() {
        IHomeView view = new HomeView(win, this);
        view.setPackageClickAction(this);
        view.setUpdateClickAction(this);
        return view;
    }

    @Override
    protected IHomeView getView() {
        return (IHomeView) view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getView().getMoreButton()) {
            returnCode = CODE_ACTION_CLICK_PACKAGE;
            fireActionPerformed(ACTION_PACKAGE_BUTTON_CLICK);
        }
    }

    public void setAppPackageText(String pkg) {
//        String old = appPackage;
        appPackage = pkg;
//        firePropertyChange(APP_PKG_CHANGED_PROPERTY, old, appPackage);
        getView().setAppPackageText(pkg);
    }

    public String getAppPackageText() {
        appPackage = getView().getAppPackageText();
        return appPackage;
    }

    public void setAppNameText(String name) {
//        String old = appName;
        appName = name;
//        firePropertyChange(APP_NAME_CHANGED_PROPERTY, old, appName);
        getView().setAppNameText(name);
    }

    public String getAppNameText() {
        appName = getView().getAppNameText();
        return appName;
    }

    public void setMessage(String msg) {
//        String old = message;
//        message = msg;
//        firePropertyChange(MESSAGE_TEXT_CHANGED_PROPERTY, old, message);
        message = msg;
        getView().setMessage(message);
    }

    public String getMessage() {
        return message;
    }


    public void setViewEnable(boolean enable) {
        boolean old = isEnable;
        isEnable = enable;
        firePropertyChange(UI_ENABLE_CHANGED_PROPERTY, old, isEnable);
    }

    public boolean isViewEnable() {
        return isEnable;
    }

}
