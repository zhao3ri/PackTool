package com.qinglan.tool.ui.widget;

import java.awt.*;

public interface IView {

    void load();

    Component getContentView();

    int getWidth();

    int getHeight();

    void remove();
}
