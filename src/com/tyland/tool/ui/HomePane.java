package com.tyland.tool.ui;

import com.tyland.tool.ui.widget.IHomeView;
import com.tyland.tool.ui.widget.IView;
import com.tyland.tool.ui.widget.impl.HomeView;
import com.tyland.tool.util.Utils;

import javax.swing.*;
import java.awt.event.*;

import static com.tyland.tool.entity.YJConfig.DEFAULT_AGENT_ID;
import static com.tyland.tool.entity.YJConfig.DEFAULT_SITE_ID;

public class HomePane extends BasePane {

    public static final String UI_ENABLE_CHANGED_PROPERTY = "HomePane.UiEnableChangedProperty";
    public static final String APP_PKG_CHANGED_PROPERTY = "HomePane.AppPackageChangedProperty";
    public static final String APP_NAME_CHANGED_PROPERTY = "HomePane.AppNameChangedProperty";
    public static final String CHANNEL_KEY_CHANGED_PROPERTY = "HomePane.ChannelKeyChangedProperty";
    public static final String GAME_ID_CHANGED_PROPERTY = "HomePane.GameIdChangedProperty";
    public static final String GAME_KEY_CHANGED_PROPERTY = "HomePane.GameKeyChangedProperty";
    public static final String GAME_VERSION_CHANGED_PROPERTY = "HomePane.GameVersionChangedProperty";
    public static final String MESSAGE_TEXT_CHANGED_PROPERTY = "HomePane.MessageTextChangedProperty";
    public static final String AGENT_ID_CHANGED_PROPERTY = "HomePane.AgentIdChangedProperty";
    public static final String SITE_ID_CHANGED_PROPERTY = "HomePane.SiteIdChangedProperty";

    public static final String MIN_SDK_TEXT_CHANGED_PROPERTY = "MorePane.MinSdkTextChangedProperty";
    public static final String TARGET_SDK_TEXT_CHANGED_PROPERTY = "MorePane.TargetSdkTextChangedProperty";
    public static final String VERSION_NAME_TEXT_CHANGED_PROPERTY = "MorePane.VersionNameTextChangedProperty";
    public static final String VERSION_CODE_TEXT_CHANGED_PROPERTY = "MorePane.VersionCodeTextChangedProperty";

    private String appPackage;
    private String appName;
    private String channelKey;
    private String gameId;
    private String gameKey;
    private String gameVersion;
    private String minSdk;
    private String targetSdk;
    private String versionCode;
    private String versionName;
    private String agentId;
    private String siteId;
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
        if (e.getSource() == getView().getSubmitButton()) {
            returnCode = CODE_ACTION_CLICK_UPDATE;
            fireActionPerformed(ACTION_UPDATE_BUTTON_CLICK);
        } else if (e.getSource() == getView().getMoreButton()) {
            returnCode = CODE_ACTION_CLICK_PACKAGE;
            fireActionPerformed(ACTION_PACKAGE_BUTTON_CLICK);
        }
    }

    public void setAppPackageText(String pkg) {
        String old = appPackage;
        appPackage = pkg;
        firePropertyChange(APP_PKG_CHANGED_PROPERTY, old, appPackage);
    }

    public String getAppPackageText() {
        return appPackage;
    }

    public void setAppNameText(String name) {
        String old = appName;
        appName = name;
        firePropertyChange(APP_NAME_CHANGED_PROPERTY, old, appName);
    }

    public String getAppNameText() {
        appName = getView().getAppNameText();
        return appName;
    }

    public void setChannelKeyText(String key) {
        String old = channelKey;
        channelKey = key;
        firePropertyChange(CHANNEL_KEY_CHANGED_PROPERTY, old, channelKey);
    }

    public String getChannelKeyText() {
        channelKey = getView().getChannelKeyText();
        return channelKey;
    }

    public void setGameIdText(String id) {
        String old = gameId;
        gameId = id;
        firePropertyChange(GAME_ID_CHANGED_PROPERTY, old, gameId);
    }

    public String getGameIdText() {
        gameId = getView().getGameIdText();
        return gameId;
    }

    public void setAgentIdText(String id) {
        String old = agentId;
        agentId = id;
        firePropertyChange(AGENT_ID_CHANGED_PROPERTY, old, agentId);
    }

    public String getAgentIdText() {
        agentId = getView().getAgentIdText();
        return agentId;
    }

    public void setSiteIdText(String id) {
        String old = siteId;
        siteId = id;
        firePropertyChange(SITE_ID_CHANGED_PROPERTY, old, siteId);
    }

    public String getSiteIdText() {
        siteId = getView().getSiteIdText();
        return siteId;
    }

    public void setGameKeyText(String key) {
        String old = gameKey;
        gameKey = key;
        firePropertyChange(GAME_KEY_CHANGED_PROPERTY, old, gameKey);
    }

    public String getGameKeyText() {
        gameKey = getView().getGameKeyText();
        return gameKey;
    }

    public void setGameVersionText(String version) {
        String old = gameVersion;
        gameVersion = version;
        firePropertyChange(GAME_VERSION_CHANGED_PROPERTY, old, gameVersion);
    }

    public String getGameVersionText() {
        gameVersion = getView().getGameVersionText();
        return gameVersion;
    }

    public void setMessage(String msg) {
        String old = message;
        message = msg;
        firePropertyChange(MESSAGE_TEXT_CHANGED_PROPERTY, old, message);
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

    public void setMinSdk(String min) {
        String old = minSdk;
        minSdk = min;
        firePropertyChange(MIN_SDK_TEXT_CHANGED_PROPERTY, old, minSdk);
    }

    public String getMinSDK() {
        minSdk = getView().getMinSdkText();
        return minSdk;
    }

    public void setTargetSdk(String target) {
        String old = targetSdk;
        targetSdk = target;
        firePropertyChange(TARGET_SDK_TEXT_CHANGED_PROPERTY, old, targetSdk);
    }

    public String getTargetSdk() {
        targetSdk = getView().getTargetSdkText();
        return targetSdk;
    }

    public void setVersionCode(String code) {
        String old = versionCode;
        versionCode = code;
        firePropertyChange(VERSION_CODE_TEXT_CHANGED_PROPERTY, old, versionCode);
    }

    public String getVersionCode() {
        versionCode = getView().getVersionCodeText();
        return versionCode;
    }

    public void setVersionName(String name) {
        String old = versionName;
        versionName = name;
        firePropertyChange(VERSION_NAME_TEXT_CHANGED_PROPERTY, old, versionName);
    }

    public String getVersionName() {
        versionName = getView().getVersionNameText();
        return versionName;
    }
}
