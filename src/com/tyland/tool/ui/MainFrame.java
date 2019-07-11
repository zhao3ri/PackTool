package com.tyland.tool.ui;

import com.tyland.common.Log;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.tyland.tool.ui.BasePane.*;
import static javax.swing.JOptionPane.*;

public class MainFrame extends JFrame implements ComponentListener, PropertyChangeListener, ActionListener {
    protected static final int FRAME_WIDTH = 480;
    protected static final int FRAME_HEIGHT = 260;

    private OnCloseListener closeListener;
    private OnPackageClickListener packageClickListener;

    private HomePane homePane;
    private String currentPath;

    private static final String PKG_REGEX = "([a-zA-Z_][a-zA-Z0-9_]*[.])*([a-zA-Z_][a-zA-Z0-9_]*)$";

    public MainFrame(String path) {
        create();
        currentPath = path;
    }

    private void create() {
        setTitle("Packager");
        setLayout(new FlowLayout());
        setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT)); //设置窗口的大小
        setLocation(300, 200);//设置窗口的初始位置
        setResizable(false);
        addComponentListener(this);
//        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (closeListener != null)
                    closeListener.onClose(CLOSED_OPTION);

            }
        });
        homePane = createHomePane();
    }

    public void open() {
        this.setVisible(true);
        homePane.load();
    }

    private HomePane createHomePane() {
        HomePane home = new HomePane(this);
        home.setPropertyChangeListener(this);
        home.setActionListener(this);
        return home;
    }

    public void refreshView(YJConfig config) {
        if (homePane == null) {
            return;
        }
    }

    public void close() {
//        setVisible(false);
        homePane.recycle();
        getContentPane().removeAll();
        dispose();
    }

    public void showDialog(String msg) {
        showDialog(msg, JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorDialog(String msg) {
        showDialog(msg, JOptionPane.ERROR_MESSAGE);
    }

    public void showErrorDialog(String msg, OnCloseListener closeListener) {
        showDialog(msg, JOptionPane.ERROR_MESSAGE, closeListener);
    }

    public void showWarningDialog(String msg) {
        showDialog(msg, JOptionPane.WARNING_MESSAGE);
    }

    private void showDialog(String msg, int messageType) {
        this.showDialog(msg, messageType, null);
    }

    private void showDialog(String msg, int messageType, OnCloseListener closeListener) {
//        JOptionPane.showMessageDialog(this, msg, "提示", messageType);
        int result = JOptionPane.showOptionDialog(this, msg, "提示", DEFAULT_OPTION,
                messageType, null, null, null);
        if (result == CLOSED_OPTION || result == YES_OPTION) {
            if (closeListener != null)
                closeListener.onClose(result);
        }
    }

    public void showDialog(String msg, final OnDialogButtonClickListener listener, final OnCloseListener closeListener) {
        showDialog(msg, listener, closeListener, QUESTION_MESSAGE);
    }

    public void showDialog(String msg, final OnDialogButtonClickListener listener, final OnCloseListener closeListener, int msgType) {
        int result = JOptionPane.showConfirmDialog(this, msg, "提示", JOptionPane.YES_NO_OPTION, msgType);
        switch (result) {
            case YES_OPTION:
                if (listener != null) {
                    listener.onPositive();
                }
                break;
            case NO_OPTION:
                if (listener != null) {
                    listener.onNegative();
                }
                break;
            case CLOSED_OPTION:
                if (closeListener != null) {
                    closeListener.onClose(result);
                }
                break;
        }
        Log.dln("result==" + result);
    }

    public void setCloseListener(OnCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setPackageClickListener(OnPackageClickListener packageClickListener) {
        this.packageClickListener = packageClickListener;
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        String newValue = String.valueOf(evt.getNewValue());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof Integer) {
            int action = (int) e.getSource();
            Log.dln("action==" + action);
            if (action == CODE_ACTION_CLICK_PACKAGE) {
                //点击打包
                if (packageClickListener != null) {
                    if (Utils.isEmpty(homePane.getJarText().trim())) {
                        showWarningDialog("请选择文件");
                        return;
                    }
                    packageClickListener.onClickPackage(homePane.getJarText());
                }
                return;
            }
        }
    }

    public void setMessage(String msg) {
        homePane.setMessage(msg);
    }

    public void changeEnable(boolean enable) {
        homePane.setViewEnable(enable);
    }

    public interface OnCloseListener {
        void onClose(int result);
    }

    public interface OnDialogButtonClickListener {
        void onPositive();

        void onNegative();
    }

    public interface OnPackageClickListener {
        void onClickPackage(String path);
    }

}
