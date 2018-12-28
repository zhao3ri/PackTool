package com.qinglan.tool.ui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class BaseUI extends ComponentAdapter {
    protected static final int FRAME_WIDTH = 500;
    protected static final int FRAME_HEIGHT = 400;
    private static int frame_locx;
    private static int frame_locy;

    @Override
    public void componentResized(ComponentEvent e) {
        super.componentResized(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        Component comp = e.getComponent();

        //更新当前窗口所在的坐标
        frame_locx = comp.getX();
        frame_locy = comp.getY();
    }

    public void setLocation(Window win) {
        int location[] = getLocation(win);
        int x = location[0];
        int y = location[1];
        win.setLocation(x, y);
    }

    protected int[] getLocation(Window win) {
        int x = frame_locx - win.getWidth() / 2 + FRAME_WIDTH / 2;
        int y = frame_locy - win.getHeight() / 2 + FRAME_HEIGHT / 2;
        return new int[]{x, y};
    }
}
