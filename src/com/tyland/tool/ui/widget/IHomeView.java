package com.tyland.tool.ui.widget;

import com.tyland.tool.entity.Channel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public interface IHomeView extends IView {

    void setSubmitClickAction(ActionListener listener);

    void setMoreClickAction(ActionListener listener);

    void setChannelList(List<Channel> channels);

    JButton getMoreButton();

    JButton getSubmitButton();
}
