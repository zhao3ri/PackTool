package com.tyland.tool.ui.widget;


import javax.swing.*;
import java.awt.event.ActionListener;

public interface IHomeView extends IView {

    void setPackageClickAction(ActionListener listener);

    JButton getConfirmButton();

    JButton getChooseButton();

    String getJarText();

    void setJarText(String pkg);

    void setMessage(String msg);
}
