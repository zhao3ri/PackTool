package com.qinglan.tool.ui;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;

public class MoreUI extends BaseUI implements ActionListener {
    private Container container;
    private JFormattedTextField textMinSDK;
    private JFormattedTextField textTargetSDK;
    private JFormattedTextField textVersionCode;
    private JTextField textVersionName;
    private JButton btnConfirm;

    private OnConfirmClickListener onConfirmClickListener;

    public MoreUI() {
        super();
        container = frame.getContentPane();
        container.setLayout(new GridLayout(5, 1, 5, 5));
        addSDKInfo();
        addVersionInfo();
        addButton();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    @Override
    protected JFrame createFrame() {
        frame = new JFrame();
        frame.setTitle("更多配置");
        FlowLayout layout = new FlowLayout();
        frame.setLayout(layout);
        frame.setMinimumSize(new Dimension(FRAME_WIDTH - 50, FRAME_HEIGHT - 50)); //设置窗口的大小
        setLocation(frame);
        frame.setResizable(false);
        frame.addComponentListener(this);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//关闭窗口
        return frame;
    }

    private void addSDKInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));
//        labelPublicKey.setHorizontalAlignment(SwingConstants.CENTER);
        textMinSDK = getTextField("minSdkVersion:", panel, 0);
        textTargetSDK = getTextField("targetSdkVersion:", panel, 0);
        container.add(panel);
    }

    private void addVersionInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));
        panel.setPreferredSize(new Dimension(FRAME_WIDTH - 60, 0));
        textVersionCode = getTextField("versionCode:", panel, 0);
        textVersionName = setConfigPanel("versionName:", panel, 0);
        container.add(panel);
    }

    private JFormattedTextField getTextField(String text, JPanel panel, int columns) {
        JLabel label = new JLabel(text);
        final JFormattedTextField textField = new JFormattedTextField();
        if (columns > 0)
            textField.setColumns(columns);
        textField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter()));
        panel.add(label);
        panel.add(textField);
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String old = textField.getText();
                JFormattedTextField.AbstractFormatter formatter = textField.getFormatter();
                if (!old.equals("")) {
                    if (formatter != null) {
                        String str = textField.getText();
                        try {
                            long page = (Long) formatter.stringToValue(str);
                            textField.setText(page + "");
                        } catch (ParseException pe) {
                            textField.setText("");
                        }
                    }
                }
            }
        });
        return textField;
    }

    private void addButton() {
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(50, 20));
        btnConfirm = new JButton("确认");
        btnConfirm.addActionListener(this);
        panel.add(btnConfirm);
        container.add(panel);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        onConfirmClickListener = listener;
    }

    public void setMinSDK(String min) {
        if (min != null)
            textMinSDK.setText(min);
    }

    public void setTargetSDK(String target) {
        if (target != null)
            textTargetSDK.setText(target);
    }

    public void setVersionCode(String code) {
        if (code != null)
            textVersionCode.setText(code);
    }

    public void setVersionName(String name) {
        if (name != null)
            textVersionName.setText(name);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            if (onConfirmClickListener != null) {
                onConfirmClickListener.onClick(this, textMinSDK.getText(), textTargetSDK.getText()
                        , textVersionCode.getText(), textVersionName.getText());
            }
        }
    }

    public interface OnConfirmClickListener {
        void onClick(BaseUI ui, String min, String target, String vcode, String vname);
    }
}
