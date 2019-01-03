package com.qinglan.tool.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class BaseUI extends ComponentAdapter {
    protected JFrame frame;
    protected static final int FRAME_WIDTH = 500;
    protected static final int FRAME_HEIGHT = 430;
    private static int frame_locx;
    private static int frame_locy;

    public BaseUI() {
        frame = createFrame();
    }

    protected abstract JFrame createFrame();

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

    protected JTextField setConfigPanel(String text, JPanel panel, int columns) {
        JLabel label = new JLabel(text);
//        labelPublicKey.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField textField = new JTextField();
        if (columns > 0)
            textField.setColumns(columns);
        panel.add(label);
        panel.add(textField);
        return textField;
    }

    public void show() {
        frame.setVisible(true);
    }

    public void close() {
        frame.setVisible(false);
    }

    public void showDialog(String msg) {
        JOptionPane.showMessageDialog(frame, msg);
//        showDialog(msg, false);
    }

    public void showDialog(String msg, boolean addBtn) {
        showDialog(msg, addBtn, null, null);
    }

    public void showDialog(String msg, final HomeUI.OnDialogButtonClickListener listener, final HomeUI.OnCloseListener closeListener) {
        showDialog(msg, true, listener, closeListener);
    }

    private void showDialog(String msg, boolean addBtn, final HomeUI.OnDialogButtonClickListener listener, final HomeUI.OnCloseListener closeListener) {
        final JDialog dialog = new JDialog(frame, "Tips", false);
        dialog.setMinimumSize(new Dimension(200, 125));
        setLocation(dialog);
        dialog.setLayout(new GridLayout(2, 1));
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(msg);
        dialog.add(label);
        if (addBtn) {
            JPanel panel = new JPanel();
            JButton btnYes = new JButton("是");
            JButton btnNo = new JButton("否");
            btnYes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    if (listener != null)
                        listener.onPositive();
                }
            });
            btnNo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    if (listener != null)
                        listener.onNegative();
                }
            });
            FlowLayout fl = (FlowLayout) panel.getLayout();
            fl.setHgap(10);//水平间距
            fl.setVgap(10);//组件垂直间距
            panel.setLayout(fl);
            panel.add(btnYes);
            panel.add(btnNo);
            dialog.add(panel);
        }
        dialog.setResizable(false);
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dialog.setVisible(false);
                if (closeListener != null)
                    closeListener.onClose();
            }
        });
    }
}
