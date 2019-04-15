package com.tyland.tool.ui.widget.impl;

import com.tyland.common.Log;
import com.tyland.tool.YJConfig;
import com.tyland.tool.entity.Channel;
import com.tyland.tool.ui.BasePane;
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
import java.io.File;
import java.text.ParseException;
import java.util.List;

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
    private JButton btnUpdate;
    private JButton btnPackage;


    private ActionListener updateClickActionListener;
    private ActionListener packageClickActionListener;
    private List<Channel> channelList;

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
        setText(getParent().getAppPackageText(), textAppPackage);

        setText(getParent().getAppNameText(), textAppName);
        textAppName.setHintText(getParent().getAppNameText());

        setText(getParent().getChannelKeyText(), textChannelKey);
        textChannelKey.setHintText(getParent().getChannelKeyText());

        setText(getParent().getGameIdText(), textGameId);
        textGameId.setHintText(getParent().getGameIdText());

        setText(getParent().getGameKeyText(), textGameKey);
        textGameKey.setHintText(getParent().getGameKeyText());

        setText(getParent().getGameVersionText(), textGameVersion);
        textGameVersion.setHintText(getParent().getGameVersionText());

        setText(getParent().getMinSDK(), textMinSDK);
        textMinSDK.setHintText(getParent().getMinSDK());
        setText(getParent().getTargetSdk(), textTargetSDK);
        setText(getParent().getVersionCode(), textVersionCode);
        textVersionCode.setHintText(getParent().getVersionCode());
        setText(getParent().getVersionName(), textVersionName);
        textVersionName.setHintText(getParent().getVersionName());
        setMessage(getParent().getMessage());
    }

    @Override
    protected Component createView() {
        contentView = new JPanel();
        contentView.setLayout(new FlowLayout());
        contentView.setPreferredSize(new Dimension(getWidth(), getHeight()));
        addAppInfoView();
        addMetaDataView();
        addSDKInfoView();
        addVersionInfoView();
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
        TitledBorder border = createBorder(apkInfoItem, "App Info");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));
        panel.setSize(new Dimension(getWidth() - contentPadding - defaultMargin * 2, itemHeight - defaultMargin));

        Insets insets = border.getBorderInsets(parent);
//        int margin = contentWidth / 2 - labelWidth - textWidth;
        int startX = insets.left + contentPadding / 2;
        int startY = insets.top;
        panel.setLocation(startX, startY);

        textAppPackage = new JTextField();
        getLabelWithTextView(textAppPackage, "app package:", null, panel, 20);
        textAppPackage.setEnabled(false);
        panel.add(textAppPackage);
        textAppName = new JHintTextField();
        getLabelWithTextView(textAppName, "app name:", null, panel, 20);
        panel.add(textAppName);
        apkInfoItem.add(panel);
        addContentView(apkInfoItem);
    }

    private void addMetaDataView() {
        JPanel metaDataItem = new JPanel();
        metaDataItem.setLayout(null);
        int height = 135;
        metaDataItem.setPreferredSize(new Dimension(getWidth() - contentPadding, height));
        TitledBorder border = createBorder(metaDataItem, "meta-data");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 5, 5));
        panel.setSize(new Dimension(getWidth() - contentPadding - defaultMargin * 2, height - defaultMargin * 3));

        Insets insets = border.getBorderInsets(parent);
//        int margin = contentWidth / 2 - labelWidth - textWidth;
        int startX = insets.left + contentPadding / 2;
        int startY = insets.top;
        panel.setLocation(startX, startY);

        textChannelKey = new JHintTextField();
        getLabelWithTextView(textChannelKey, YJConfig.META_DATA_CHANNEL_KEY, null, panel, 20);
        panel.add(textChannelKey);
        textGameId = new JHintTextField();
        getLabelWithTextView(textGameId, YJConfig.META_DATA_GAME_ID, null, panel, 20);
        panel.add(textGameId);
        textGameKey = new JHintTextField();
        getLabelWithTextView(textGameKey, YJConfig.META_DATA_GAME_KEY, null, panel, 20);
        textGameVersion = new JHintTextField();
        getLabelWithTextView(textGameVersion, YJConfig.META_DATA_GAME_VERSION, null, panel, 20);
        panel.add(textGameVersion);
        metaDataItem.add(panel);
        addContentView(metaDataItem);
    }

    private TitledBorder createBorder(JPanel p, String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(Color.GRAY);
        p.setBorder(border);
        return border;
    }

    private void addSDKInfoView() {
        JPanel panel = new JPanel();
        textMinSDK = createFormatTextField(true);
        textTargetSDK = createFormatTextField(true);
        panel = initRow(panel, "Android SDK", textMinSDK, "minSdkVersion:", textTargetSDK, "targetSdkVersion:");
        addContentView(panel);
    }

    private void addVersionInfoView() {
        JPanel panel = new JPanel();
        textVersionName = createFormatTextField(false);
        textVersionCode = createFormatTextField(true);
        panel = initRow(panel, "App Version", textVersionName, "versionName:", textVersionCode, "versionCode:");
        addContentView(panel);
    }

    private JPanel initRow(JPanel item, String title, JTextField textFirstColumn, String firstLab, JTextField textSecondColumn, String secondLab) {
        if (item == null) {
            item = new JPanel();
        }
        item.setLayout(null);
        item.setPreferredSize(new Dimension(getWidth() - contentPadding, itemHeight));
        TitledBorder border = createBorder(item, title);

        Dimension borderSize = border.getMinimumSize(parent);
        Insets insets = border.getBorderInsets(parent);
        int contentWidth = item.getPreferredSize().width - insets.left - insets.right;
        int margin = contentWidth / 2 - labelWidth - textWidth;
        int startX = insets.left + margin / 2;
        int startY = (item.getPreferredSize().height - borderSize.height + insets.top) / 2;

        setItemLocation(firstLab, item, textFirstColumn, startX, startY, startX + labelWidth, startY);

        int labelX = startX + labelWidth + textWidth + margin;
        int textX = startX + labelWidth * 2 + textWidth + margin;
        setItemLocation(secondLab, item, textSecondColumn, labelX, startY, textX, startY);
        return item;
    }

    private void setItemLocation(String text, JPanel panel, JTextField textField, int... locations) {
        JLabel label = new JLabel(text);
        label.setSize(new Dimension(labelWidth, defaultHeight));
        if (locations != null && locations.length > 1) {
            label.setLocation(locations[0], locations[1]);
            if (locations.length > 3) {
                textField.setLocation(locations[2], locations[3]);
            }
        }
        panel.add(label);
        panel.add(textField);
    }

    private <T extends JTextField> T createFormatTextField(boolean format) {
        JTextField textField;
        textField = new JHintTextField();
        if (format) {
            ((JHintTextField) textField).setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter()));
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getSource() instanceof JFormattedTextField) {
                        JFormattedTextField textField = (JFormattedTextField) e.getSource();
                        String old = textField.getText();
                        JFormattedTextField.AbstractFormatter formatter = textField.getFormatter();
                        if (!Utils.isEmpty(old)) {
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
                }
            });
//        } else {
//            textField = new JTextField();
        }
        textField.setSize(new Dimension(textWidth, defaultHeight));
        return (T) textField;
    }

    private void addButton() {
        JPanel parent = new JPanel();
        parent.setPreferredSize(new Dimension(bodyWidth, buttonHeight + defaultMargin));
        parent.setLayout(null);
        btnUpdate = new JButton("更新配置");
        btnUpdate.setSize(buttonWidth, buttonHeight);
        int x = (bodyWidth - buttonWidth * 2 - defaultMargin) / 2;
        btnUpdate.setLocation(x, defaultMargin);
        btnUpdate.addActionListener(this);
        parent.add(btnUpdate);

        btnPackage = new JButton("打包签名");
        btnPackage.setSize(buttonWidth, buttonHeight);
        btnPackage.addActionListener(packageClickActionListener);
        btnPackage.setLocation(btnUpdate.getX() + buttonWidth + defaultMargin, defaultMargin);
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
        labMsg.setPreferredSize(new Dimension(bodyWidth, labMsg.getFont().getSize() * 2));
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
        if (e.getSource() == btnUpdate) {
            if (updateClickActionListener != null) {
                updateClickActionListener.actionPerformed(e);
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
        } else if (prop.equals(CHANNEL_KEY_CHANGED_PROPERTY)) {
            String key = (String) evt.getNewValue();
            setText(key, textChannelKey);
        } else if (prop.equals(GAME_ID_CHANGED_PROPERTY)) {
            String id = (String) evt.getNewValue();
            setText(id, textGameId);
        } else if (prop.equals(GAME_KEY_CHANGED_PROPERTY)) {
            String key = (String) evt.getNewValue();
            setText(key, textGameKey);
        } else if (prop.equals(GAME_VERSION_CHANGED_PROPERTY)) {
            String key = (String) evt.getNewValue();
            setText(key, textGameVersion);
        } else if (prop.equals(MESSAGE_TEXT_CHANGED_PROPERTY)) {
            String msg = String.valueOf(evt.getNewValue());
            setMessage(msg);
        } else if (prop.equals(MIN_SDK_TEXT_CHANGED_PROPERTY)) {
            String min = (String) evt.getNewValue();
            setText(min, textMinSDK);
        } else if (prop.equals(TARGET_SDK_TEXT_CHANGED_PROPERTY)) {
            String target = (String) evt.getNewValue();
            setText(target, textTargetSDK);
        } else if (prop.equals(VERSION_CODE_TEXT_CHANGED_PROPERTY)) {
            String code = (String) evt.getNewValue();
            setText(code, textVersionCode);
        } else if (prop.equals(VERSION_NAME_TEXT_CHANGED_PROPERTY)) {
            String name = (String) evt.getNewValue();
            setText(name, textVersionName);
        }
    }

    private void setUIEnable(boolean enable) {
        if (btnUpdate != null)
            btnUpdate.setEnabled(enable);

        if (btnPackage != null)
            btnPackage.setEnabled(enable);

        if (textMinSDK != null)
            textMinSDK.setEnabled(enable);

        if (textTargetSDK != null)
            textTargetSDK.setEnabled(enable);

        if (textVersionCode != null)
            textVersionCode.setEnabled(enable);

        if (textVersionName != null)
            textVersionName.setEnabled(enable);

        if (textAppName != null)
            textAppName.setEnabled(enable);

        if (textChannelKey != null)
            textChannelKey.setEnabled(enable);

        if (textGameId != null)
            textGameId.setEnabled(enable);

        if (textGameKey != null)
            textGameKey.setEnabled(enable);

        if (textGameVersion != null)
            textGameVersion.setEnabled(enable);
    }

    private void setMessage(String msg) {
        labMsg.setText(msg);
    }

    @Override
    public void remove() {
        parent.removePropertyChangeListener(this);
        if (updateClickActionListener != null) {
            btnUpdate.removeActionListener(updateClickActionListener);
        }
        if (packageClickActionListener != null) {
            btnPackage.removeActionListener(packageClickActionListener);
        }
    }

    @Override
    public void setUpdateClickAction(ActionListener listener) {
        this.updateClickActionListener = listener;
    }

    @Override
    public void setPackageClickAction(ActionListener listener) {
        this.packageClickActionListener = listener;
    }

    @Override
    public void setChannelList(List<Channel> channels) {
        this.channelList = channels;
    }

    @Override
    public JButton getMoreButton() {
        return btnPackage;
    }

    @Override
    public JButton getSubmitButton() {
        return btnUpdate;
    }

    @Override
    public String getAppNameText() {
        return getText(textAppName);
    }

    @Override
    public String getChannelKeyText() {
        return getText(textChannelKey);
    }

    @Override
    public String getGameIdText() {
        return getText(textGameId);
    }

    @Override
    public String getGameKeyText() {
        return getText(textGameKey);
    }

    @Override
    public String getGameVersionText() {
        return getText(textGameVersion);
    }

    @Override
    public String getMinSdkText() {
        return getText(textMinSDK);
    }

    @Override
    public String getTargetSdkText() {
        return getText(textTargetSDK);
    }

    @Override
    public String getVersionCodeText() {
        return getText(textVersionCode);
    }

    @Override
    public String getVersionNameText() {
        return getText(textVersionName);
    }

    private String getText(JHintTextField textField) {
        if (Utils.isEmpty(textField.getText())) {
            return textField.getHintText();
        }
        return textField.getText();
    }
}
