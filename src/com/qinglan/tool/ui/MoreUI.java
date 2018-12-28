package com.qinglan.tool.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MoreUI extends BaseUI {
    private JFrame frame;

    public MoreUI() {
        frame = new JFrame();
        frame.setTitle("更多配置");
        frame.setLayout(new FlowLayout());
        frame.setMinimumSize(new Dimension(FRAME_WIDTH - 50, FRAME_HEIGHT - 50)); //设置窗口的大小
        setLocation(frame);
        frame.setResizable(false);
        frame.addComponentListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//关闭窗口
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
            }
        });
    }

    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }
}
