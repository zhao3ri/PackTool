package com.tyland.tool.ui.widget.impl;

import com.tyland.tool.Main;
import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.ui.BasePane;
import com.tyland.tool.ui.DialogOptionPane;
import com.tyland.tool.ui.HomePane;
import com.tyland.tool.ui.JHintTextField;
import com.tyland.tool.ui.widget.IHomeView;
import com.tyland.tool.util.Utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;

import static com.tyland.tool.ui.HomePane.*;


public class HomeView extends BaseView implements IHomeView, ActionListener {
    private int bodyWidth;
    private int padding = 45;

    private int defaultMargin = 10;
    private int messageHeight = 55;
    private int labelWidth = 110;
    private int textWidth = 85;
    private int contentPadding = 20;
    private int itemHeight = 80;

    private Container contentView;

    private JTextField textAppPackage;
    private JHintTextField textAppName;

    private JHintTextField textChannelKey;
    private JHintTextField textGameId;
    private JHintTextField textGameKey;
    private JHintTextField textGameVersion;

    private JHintTextField textMinSDK;
    private JHintTextField textTargetSDK;
    private JHintTextField textVersionCode;
    private JHintTextField textVersionName;

    private JLabel labMsg;
    private JButton btnChoose;
    private JButton btnPackage;


    private ActionListener updateClickActionListener;
    private ActionListener packageClickActionListener;

    public HomeView(Window win, BasePane parent) {
        super(win, parent);
        bodyWidth = getWindowWidth() - padding;
    }

    @Override
    public void load() {
        if (contentView == null) {
            return;
        }
        setUIEnable(getParent().isViewEnable());
        setText(getParent().getJarText(), textAppPackage);

        setMessage(getParent().getMessage());
    }

    @Override
    protected Component createView() {
        contentView = new JPanel();
        contentView.setLayout(new FlowLayout());
        contentView.setPreferredSize(new Dimension(getWidth(), getHeight()));
        addAppInfoView();
        addButton();
        addLabel();
        return contentView;
    }

    @Override
    protected HomePane getParent() {
        return (HomePane) super.getParent();
    }

    @Override
    public int getWidth() {
        return getWindowWidth();
    }

    @Override
    public int getHeight() {
        return getWindowHeight();
    }

    private void addContentView(Component comp) {
        contentView.add(comp);
    }

    private void addAppInfoView() {
        JPanel apkInfoItem = new JPanel();
        apkInfoItem.setLayout(null);
        apkInfoItem.setPreferredSize(new Dimension(getWidth() - contentPadding, itemHeight + defaultMargin * 2));
        TitledBorder border = createBorder(apkInfoItem, "请选择");

        JPanel panel = new JPanel();
        panel.setLayout(null);
        int width = getWidth() - contentPadding - defaultMargin * 2;
        int height = itemHeight / 2 - defaultMargin;
        panel.setSize(new Dimension(width, height));

        Insets insets = border.getBorderInsets(parent);
//        int margin = contentWidth / 2 - labelWidth - textWidth;
        int startX = insets.left + contentPadding / 2;
        int startY = insets.top;
        panel.setLocation(startX, startY);

        textAppPackage = new JTextField();
        textAppPackage.setSize(new Dimension(width - labelWidth - defaultMargin, height));
        getLabelWithTextView(textAppPackage, "选择文件:", new Dimension(labelWidth, height), panel, 20);
        textAppPackage.setLocation(labelWidth + defaultMargin/2, 0);
        textAppPackage.setEnabled(false);
        panel.add(textAppPackage);
        apkInfoItem.add(panel);
        btnChoose = new JButton();
        btnChoose.setText("选择");
        btnChoose.setSize(new Dimension(buttonWidth * 2 / 3, buttonHeight));
        btnChoose.setLocation(getWidth() - contentPadding - defaultMargin - buttonWidth * 2 / 3, itemHeight / 2 + defaultMargin * 2);
        btnChoose.addActionListener(this);
        apkInfoItem.add(btnChoose);
        addContentView(apkInfoItem);
    }

    private TitledBorder createBorder(JPanel p, String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(Color.GRAY);
        p.setBorder(border);
        return border;
    }

    private void addButton() {
        JPanel parent = new JPanel();
        parent.setPreferredSize(new Dimension(bodyWidth, buttonHeight + defaultMargin));
        parent.setLayout(null);

        btnPackage = new JButton("确认");
        btnPackage.setSize(buttonWidth, buttonHeight);
        btnPackage.addActionListener(packageClickActionListener);
//        btnPackage.setLocation(btnUpdate.getX() + buttonWidth + defaultMargin, defaultMargin);
        btnPackage.setLocation((bodyWidth - buttonWidth) / 2, defaultMargin);
        parent.add(btnPackage);
        addContentView(parent);
    }

    private void addLabel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(getWindowWidth(), messageHeight));

        JLabel line = new JLabel();
        line.setPreferredSize(new Dimension(getWindowWidth(), 1));
        line.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        line.setLayout(null);
        line.setForeground(Color.LIGHT_GRAY);
        panel.add(line);

        labMsg = new JLabel("", JLabel.CENTER);
        labMsg.setPreferredSize(new Dimension(bodyWidth, (labMsg.getFont().getSize() + labMsg.getFont().getSize() / 2) * 2));
        labMsg.setHorizontalAlignment(SwingConstants.LEFT);
        labMsg.setHorizontalTextPosition(SwingConstants.LEFT);
//        Font fnt = new Font("Default", Font.BOLD, 30);
//        lab.setFont(fnt);
        labMsg.setForeground(Color.BLUE);
        panel.add(labMsg);
        addContentView(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnChoose) {
            DialogOptionPane dialogOptionPane = new DialogOptionPane(window);
            int result = dialogOptionPane.showDialog(Main.ROOT_PATH, ".jar/.class/.dex", "jar", "class","dex");
            if (result == CODE_ACTION_FILE_CONFIRM) {
                setJarText(dialogOptionPane.getChoosePath());
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals(UI_ENABLE_CHANGED_PROPERTY)) {
            Boolean enable = (Boolean) evt.getNewValue();
            setUIEnable(enable);
        } else if (prop.equals(APP_PKG_CHANGED_PROPERTY)) {
            String pkg = (String) evt.getNewValue();
            setText(pkg, textAppPackage);
        } else if (prop.equals(APP_NAME_CHANGED_PROPERTY)) {
            String name = (String) evt.getNewValue();
            setText(name, textAppName);
        }
    }

    private void setUIEnable(boolean enable) {

        if (btnPackage != null) {
            btnPackage.setEnabled(enable);
        }

        if (btnChoose != null) {
            btnChoose.setEnabled(enable);
        }
    }

    public void setMessage(String msg) {
        labMsg.setText("<html>" + msg + "</html>");
    }

    @Override
    public void remove() {
        parent.removePropertyChangeListener(this);
        if (packageClickActionListener != null) {
            btnPackage.removeActionListener(packageClickActionListener);
        }
    }

    @Override
    public void setPackageClickAction(ActionListener listener) {
        this.packageClickActionListener = listener;
    }

    @Override
    public JButton getConfirmButton() {
        return btnPackage;
    }

    @Override
    public JButton getChooseButton() {
        return btnChoose;
    }

    @Override
    public void setJarText(String pkg) {
        textAppPackage.setText(pkg);
    }

    @Override
    public String getJarText() {
        return textAppPackage.getText();
    }

    private String getText(JHintTextField textField) {
        if (Utils.isEmpty(textField.getText())) {
            return textField.getHintText();
        }
        return textField.getText();
    }
}
