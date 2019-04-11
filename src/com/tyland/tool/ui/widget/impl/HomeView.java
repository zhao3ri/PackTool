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


public class HomeView extends BaseView implements IHomeView, ItemListener, ActionListener {
    private int bodyWidth;
    private int padding = 45;
    private int packageTextWidth = 225;//package输入框
    private int packageHeight = 50;
    private int radioHeight = 30;
    private int radioWidth = 80;
    private int radioGroupWidth = 270;
    private int packageNameWidth = 325;
    private int suffixWidth = 100;
    private int itemLabelWidth = 60;

    private int defaultMargin = 10;
    private int messageHeight = 55;
    private int labelWidth = 110;
    private int textWidth = 85;
    private int contentPadding = 20;
    private int itemHeight = 80;

    private JButton btnUpdate;
    private JButton btnPackage;
    private JLabel labPackage;
    private JTextField textSuffix;
    private JTextField textPackage;
    private JRadioButton radioUseDefPackage;
    private JRadioButton radioSuffix;
    private JRadioButton radioPackage;
    private JTextField textDrawable;
    private JButton btnDrawableChoose;

    private JHintTextField textMinSDK;
    private JHintTextField textTargetSDK;
    private JHintTextField textVersionCode;
    private JHintTextField textVersionName;

    private JLabel labMsg;

    private Container contentView;

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
        updateText(getParent().getDrawablePath(), textDrawable);
//        setDefaultPackageLabelText(getParent().getDefaultPackageName());
        setPackageText(getParent().getDefaultPackageName());
        updateText(getParent().getNewPackageName(), textPackage);
//        selectPackage(getParent().isUseDefaultPackage(), getParent().isUseSuffix());
        setApkInfoText(getParent().getApkInfo());
        setMessage(getParent().getMessage());

        updateText(getParent().getMinSDK(), textMinSDK);
        textMinSDK.setHintText(getParent().getMinSDK());
        updateText(getParent().getTargetSdk(), textTargetSDK);
        updateText(getParent().getVersionCode(), textVersionCode);
        textVersionCode.setHintText(getParent().getVersionCode());
        updateText(getParent().getVersionName(), textVersionName);
        textVersionName.setHintText(getParent().getVersionName());
    }

    @Override
    protected Component createView() {
        contentView = new JPanel();
        contentView.setLayout(new FlowLayout());
        contentView.setPreferredSize(new Dimension(getWidth(), getHeight()));
//        addChannelListCheckBox();
//        addChooseDrawableView();
//        addChannelConfigView();
//        addPackageView();
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

    private void addChooseDrawableView() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
//        textDrawable = new JTextField(35);
        textDrawable = getLabelWithTextView("选择替换图片目录:", null, panel, 25);
        textDrawable.setEnabled(false);
        panel.add(textDrawable);
        btnDrawableChoose = new JButton("选择");
        btnDrawableChoose.addActionListener(this);
        panel.add(btnDrawableChoose);
        addContentView(panel);
    }

    private JTextField textAppPackage;
    private JTextField textAppName;

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

        textAppPackage = getLabelWithTextView("app package:", null, panel, 20);
        textAppPackage.setEnabled(false);
        panel.add(textAppPackage);
        textAppName = getLabelWithTextView("app name:", null, panel, 20);
        panel.add(textAppName);
        apkInfoItem.add(panel);
        addContentView(apkInfoItem);
    }

    private JTextField textChannelKey;
    private JTextField textGameId;
    private JTextField textGameKey;

    private void addMetaDataView() {
        JPanel metaDataItem = new JPanel();
        metaDataItem.setLayout(null);
        int height = 130;
        metaDataItem.setPreferredSize(new Dimension(getWidth() - contentPadding, height));
        TitledBorder border = createBorder(metaDataItem, "meta-data");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 5, 5));
        panel.setSize(new Dimension(getWidth() - contentPadding - defaultMargin * 2, height - defaultMargin * 3));

        Insets insets = border.getBorderInsets(parent);
//        int margin = contentWidth / 2 - labelWidth - textWidth;
        int startX = insets.left + contentPadding / 2;
        int startY = insets.top;
        panel.setLocation(startX, startY);

        textChannelKey = getLabelWithTextView(YJConfig.META_DATA_CHANNEL_KEY, null, panel, 20);
        panel.add(textChannelKey);
        textGameId = getLabelWithTextView(YJConfig.META_DATA_GAME_ID, null, panel, 20);
        panel.add(textGameId);
        textGameKey = getLabelWithTextView(YJConfig.META_DATA_GAME_KEY, null, panel, 20);
        panel.add(textGameKey);
        metaDataItem.add(panel);
        addContentView(metaDataItem);
    }

    private TitledBorder createBorder(JPanel p, String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(Color.GRAY);
        p.setBorder(border);
        return border;
    }

    private void addPackageView() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(bodyWidth, packageHeight));
        JLabel label = new JLabel("包名配置:");
        label.setLocation(0, 0);
        label.setSize(new Dimension(itemLabelWidth, defaultHeight));

        labPackage = new JLabel();
        labPackage.setFont(new Font("Default", Font.BOLD, labPackage.getFont().getSize()));
        labPackage.setSize(packageNameWidth, defaultHeight);
        labPackage.setForeground(Color.GRAY);
        labPackage.setHorizontalAlignment(SwingConstants.RIGHT);

        textSuffix = new JTextField();
        textSuffix.setSize(suffixWidth, defaultHeight);
        textSuffix.setLocation(bodyWidth - suffixWidth, 0);

        textPackage = new JTextField();
        textPackage.setSize(packageTextWidth, defaultHeight);
        textPackage.setLocation(bodyWidth - packageTextWidth, 0);
        panel.add(label);
        panel.add(labPackage);
        panel.add(textSuffix);
        panel.add(textPackage);
        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setSize(radioGroupWidth, radioHeight);
        buttonPanel.setLocation(bodyWidth - radioGroupWidth, defaultHeight);

        radioUseDefPackage = getRadio(radioWidth, radioHeight, "使用默认", "0");
        radioUseDefPackage.addItemListener(this);
        buttonGroup.add(radioUseDefPackage);

        radioSuffix = getRadio(radioWidth, radioHeight, "添加后缀", "1");
        radioSuffix.addItemListener(this);
        buttonGroup.add(radioSuffix);

        radioPackage = getRadio(radioWidth, radioHeight, "修改包名", "2");
        radioPackage.addItemListener(this);
        buttonGroup.add(radioPackage);

        buttonPanel.add(radioUseDefPackage);
        buttonPanel.add(radioSuffix);
        buttonPanel.add(radioPackage);
        panel.add(buttonPanel);
        addContentView(panel);
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

    private JRadioButton getRadio(int width, int height, String text, String name) {
        JRadioButton radioSuffix = new JRadioButton(text);
        radioSuffix.setSize(width, height);
        radioSuffix.setName(name);
        radioSuffix.setSelected(true);
        radioSuffix.addItemListener(this);
        return radioSuffix;
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
    public void itemStateChanged(ItemEvent e) {
        JRadioButton radioButton = (JRadioButton) e.getItem();
        if (radioButton.isSelected()) {
            if (radioButton == radioPackage || radioButton == radioSuffix || radioButton == radioUseDefPackage) {
                getParent().selectedPackage(radioUseDefPackage.isSelected(), radioSuffix.isSelected());
            } else {
                Log.d(radioButton.isSelected() ? "select " : "cancel ");
                Log.dln(radioButton.getText() + " id=" + radioButton.getName());

                getParent().selectedChannelId(Integer.valueOf(radioButton.getName()).intValue());
//                firePropertyChange(CHANNEL_RADIO_CHANGED_PROPERTY, 0, Integer.valueOf(radioButton.getName()).intValue());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdate) {
//            getParent().setDrawablePath(getDrawablePath());
            getParent().setNewPackageName(getPackageNameText());
            getParent().setPackageSuffix(getPackageSuffixText());
            getParent().selectedPackage(isSelectedDefault(), isSelectedSuffix());
            getParent().setTargetSdk(getTargetSdkText());
            getParent().setMinSdk(getMinSdkText());
            getParent().setVersionName(getVersionNameText());
            getParent().setVersionCode(getVersionCodeText());
            if (updateClickActionListener != null) {
                updateClickActionListener.actionPerformed(e);
            }
        } else if (e.getSource() == btnDrawableChoose) {
            JFileChooser chooser = createFileChooser(JFileChooser.DIRECTORIES_ONLY, getParent().getDrawablePath(), null);
            chooser.showDialog(window, null);
            File file = chooser.getSelectedFile();
            if (file != null) {
                updateText(file.getAbsoluteFile().toString(), textDrawable);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();
        if (prop.equals(UI_ENABLE_CHANGED_PROPERTY)) {
            Boolean enable = (Boolean) evt.getNewValue();
            setUIEnable(enable);
        } else if (prop.equals(DRAWABLE_PATH_CHANGED_PROPERTY)) {
            String path = getParent().getDrawablePath();
            updateText(path, textDrawable);
//        } else if (prop.equals(APP_ID_CHANGED_PROPERTY)) {
//            String id = getParent().getAppIdText();
////            updateText(id, textAppId);
//        } else if (prop.equals(APP_KEY_CHANGED_PROPERTY)) {
//            String key = getParent().getAppKeyText();
////            updateText(key, textAppKey);
//        } else if (prop.equals(PUBLIC_KEY_CHANGED_PROPERTY)) {
//            String key = getParent().getPublicKeyText();
////            updateText(key, textPubKey);
//        } else if (prop.equals(SECRET_KEY_CHANGED_PROPERTY)) {
//            String key = getParent().getSecretKeyText();
////            updateText(key, textSecKey);
//        } else if (prop.equals(CP_ID_CHANGED_PROPERTY)) {
//            String id = getParent().getCpIdText();
////            updateText(id, textCpId);
//        } else if (prop.equals(CP_KEY_CHANGED_PROPERTY)) {
//            String key = getParent().getCpKeyText();
////            updateText(key, textCpKey);
        } else if (prop.equals(PACKAGE_DEFAULT_NAME_CHANGED_PROPERTY)) {
            String pkg = String.valueOf(evt.getNewValue());
//            setDefaultPackageLabelText(pkg);
            setPackageText(pkg);
        } else if (prop.equals(PACKAGE_SUFFIX_CHANGED_PROPERTY)) {
            updateText(getParent().getPackageSuffix(), textSuffix);
        } else if (prop.equals(PACKAGE_NAME_CHANGED_PROPERTY)) {
            updateText(getParent().getNewPackageName(), textPackage);
        } else if (prop.equals(PACKAGE_SELECT_CHANGED_PROPERTY)) {
//            selectPackage(getParent().isUseDefaultPackage(), getParent().isUseSuffix());
        } else if (prop.equals(APK_INFO_CHANGED_PROPERTY)) {
            String info = String.valueOf(evt.getNewValue());
            setApkInfoText(info);
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
        if (textSuffix != null)
            textSuffix.setEnabled(enable);
        if (textPackage != null)
            textPackage.setEnabled(enable);
        if (radioUseDefPackage != null)
            radioUseDefPackage.setEnabled(enable);
        if (radioPackage != null)
            radioPackage.setEnabled(enable);
        if (radioSuffix != null)
            radioSuffix.setEnabled(enable);
        if (btnUpdate != null)
            btnUpdate.setEnabled(enable);
        if (btnPackage != null)
            btnPackage.setEnabled(enable);
        if (btnDrawableChoose != null)
            btnDrawableChoose.setEnabled(enable);
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
    }

    private void updateText(String text, JTextField tf) {
        setText(text, tf);
    }

    private void setMessage(String msg) {
        labMsg.setText(msg);
    }

    private void setApkInfoText(String info) {
        Log.iln(info);
//        labApkInfo.setText(info);
    }

    private void setDefaultPackageLabelText(String pkg) {
        labPackage.setText(pkg);
    }

    private void setPackageText(String pkg) {
        textAppPackage.setText(pkg);
    }

    private void selectPackage(boolean useDefault, boolean useSuffix) {
        if (useDefault) {
            radioUseDefPackage.setSelected(true);
        } else {
            if (useSuffix) {
                radioSuffix.setSelected(true);
            } else {
                radioPackage.setSelected(true);
            }
        }
        changePackage(radioUseDefPackage.isSelected(), radioSuffix.isSelected());
    }

    private void changePackage(boolean useDefault, boolean useSuffix) {
        if (useDefault) {
            textSuffix.setVisible(false);
            labPackage.setVisible(true);
            labPackage.setLocation(bodyWidth - packageNameWidth, 0);
            textPackage.setVisible(false);
        } else {
            if (useSuffix) {
                textSuffix.setVisible(true);
                labPackage.setVisible(true);
                labPackage.setLocation(bodyWidth - suffixWidth - packageNameWidth, 0);
                textPackage.setVisible(false);
            } else {
                textSuffix.setVisible(false);
                labPackage.setVisible(false);
                textPackage.setVisible(true);
            }
        }
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

    private String getDrawablePath() {
        return textDrawable.getText();
    }

    private String getPackageSuffixText() {
        return textSuffix.getText();
    }

    private String getPackageNameText() {
        return textPackage.getText();
    }

    private boolean isSelectedDefault() {
        return radioUseDefPackage.isSelected();
    }

    private boolean isSelectedSuffix() {
        return radioSuffix.isSelected();
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
