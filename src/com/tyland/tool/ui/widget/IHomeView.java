package com.tyland.tool.ui.widget;


import javax.swing.*;
import java.awt.event.ActionListener;

public interface IHomeView extends IView {

    void setUpdateClickAction(ActionListener listener);

    void setPackageClickAction(ActionListener listener);

    JButton getConfirmButton();

    JButton getSubmitButton();

    String getAppPackageText();

    void setAppPackageText(String pkg);

    String getAppNameText();

    void setAppNameText(String name);

    void setMessage(String msg);

    String getChannelKeyText();

    String getGameIdText();

    String getGameKeyText();

    String getGameVersionText();

    String getMinSdkText();

    String getTargetSdkText();

    String getVersionCodeText();

    String getVersionNameText();
}
