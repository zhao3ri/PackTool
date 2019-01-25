package com.qinglan.tool.ui.widget.impl;

import com.qinglan.common.Log;
import com.qinglan.tool.entity.Channel;
import com.qinglan.tool.ui.BasePane;
import com.qinglan.tool.ui.HomePane;
import com.qinglan.tool.ui.widget.IHomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.List;

import static com.qinglan.tool.ui.HomePane.*;


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

    private JButton btnSubmit;
    private JButton btnMore;
    private JPanel panelCheckBox;
    private JTextField textAppId;
    private JTextField textAppKey;
    private JTextField textPubKey;
    private JTextField textSecKey;
    private JTextField textCpId;
    private JTextField textCpKey;
    private JLabel labPackage;
    private JTextField textSuffix;
    private JTextField textPackage;
    private JRadioButton radioUseDefPackage;
    private JRadioButton radioSuffix;
    private JRadioButton radioPackage;
    private JTextField textDrawable;
    private JButton btnDrawableChoose;
    private JLabel labMsg;
    private JLabel labApkInfo;

    private Container contentView;

    private ActionListener submitClickActionListener;
    private ActionListener moreClickActionListener;
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
        updateText(getParent().getAppIdText(), textAppId);
        updateText(getParent().getAppKeyText(), textAppKey);
        updateText(getParent().getPublicKeyText(), textPubKey);
        updateText(getParent().getSecretKeyText(), textSecKey);
        updateText(getParent().getCpIdText(), textCpId);
        updateText(getParent().getCpKeyText(), textCpKey);
        setDefaultPackageLabelText(getParent().getDefaultPackageName());
        updateText(getParent().getNewPackageName(), textPackage);
        selectPackage(getParent().isUseDefaultPackage(), getParent().isUseSuffix());
        setApkInfoText(getParent().getApkInfo());
        setMessage(getParent().getMessage());
    }

    @Override
    protected Component createView() {
        contentView = new JPanel();
        contentView.setLayout(new FlowLayout());
        contentView.setPreferredSize(new Dimension(getWidth(), getHeight()));
        addChannelListCheckBox();
        addChooseDrawableView();
        addChannelConfigView();
        addPackageView();
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

    private void addChannelListCheckBox() {
        panelCheckBox = new JPanel();
        panelCheckBox.setBorder(BorderFactory.createTitledBorder("选择需要打包的渠道："));
//        panelCheckBox.setLayout(new GridLayout()); // 设置组件的排版
        ButtonGroup buttonGroup = new ButtonGroup();

        if (channelList != null) {
            for (Channel channel : channelList) {
//            JCheckBox checkBox = new JCheckBox(channel.getName());
                JRadioButton radioButton = new JRadioButton(channel.getName());
                radioButton.setFont(window.getFont());
                radioButton.setName(String.valueOf(channel.getId()));
                radioButton.addItemListener(this);
                buttonGroup.add(radioButton);
                panelCheckBox.add(radioButton);
            }
        }

        JScrollPane pane = new JScrollPane(panelCheckBox);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        addContentView(panelCheckBox);
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

    private void addChannelConfigView() {
        int columns = 20;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 5, 5));
        textAppId = getLabelWithTextView("AppId:", null, panel, columns);
        textAppKey = getLabelWithTextView("AppKey:", null, panel, columns);
        textPubKey = getLabelWithTextView("PublicKey:", null, panel, columns);
        textSecKey = getLabelWithTextView("SecretKey:", null, panel, columns);
        textCpId = getLabelWithTextView("CpId:", null, panel, columns);
        textCpKey = getLabelWithTextView("CpKey:", null, panel, columns);
        addContentView(panel);
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
        btnSubmit = new JButton("提交");
        btnSubmit.setSize(buttonWidth, buttonHeight);
        int x = (bodyWidth - buttonWidth * 2 - defaultMargin) / 2;
        btnSubmit.setLocation(x, defaultMargin);
        btnSubmit.addActionListener(this);
        parent.add(btnSubmit);

        btnMore = new JButton("更多");
        btnMore.setSize(buttonWidth, buttonHeight);
        btnMore.addActionListener(moreClickActionListener);
        btnMore.setLocation(btnSubmit.getX() + buttonWidth + defaultMargin, defaultMargin);
        parent.add(btnMore);
        addContentView(parent);
    }

    private void addLabel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(getWindowWidth(), messageHeight));
        labApkInfo = new JLabel("", JLabel.CENTER);
        labApkInfo.setForeground(Color.RED);
        panel.add(labApkInfo);

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
        if (e.getSource() == btnSubmit) {
            getParent().setDrawablePath(getDrawablePath());
            getParent().setAppIdText(getAppIdText());
            getParent().setAppKeyText(getAppKeyText());
            getParent().setPublicKeyText(getPublicKeyText());
            getParent().setSecretKeyText(getSecretKeyText());
            getParent().setCpIdText(getCpIdText());
            getParent().setCpKeyText(getCpKeyText());
            getParent().setNewPackageName(getPackageNameText());
            getParent().setPackageSuffix(getPackageSuffixText());
            getParent().selectedPackage(isSelectedDefault(), isSelectedSuffix());
            if (submitClickActionListener != null) {
                submitClickActionListener.actionPerformed(e);
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
        } else if (prop.equals(APP_ID_CHANGED_PROPERTY)) {
            String id = getParent().getAppIdText();
            updateText(id, textAppId);
        } else if (prop.equals(APP_KEY_CHANGED_PROPERTY)) {
            String key = getParent().getAppKeyText();
            updateText(key, textAppKey);
        } else if (prop.equals(PUBLIC_KEY_CHANGED_PROPERTY)) {
            String key = getParent().getPublicKeyText();
            updateText(key, textPubKey);
        } else if (prop.equals(SECRET_KEY_CHANGED_PROPERTY)) {
            String key = getParent().getSecretKeyText();
            updateText(key, textSecKey);
        } else if (prop.equals(CP_ID_CHANGED_PROPERTY)) {
            String id = getParent().getCpIdText();
            updateText(id, textCpId);
        } else if (prop.equals(CP_KEY_CHANGED_PROPERTY)) {
            String key = getParent().getCpKeyText();
            updateText(key, textCpKey);
        } else if (prop.equals(PACKAGE_DEFAULT_NAME_CHANGED_PROPERTY)) {
            String pkg = String.valueOf(evt.getNewValue());
            setDefaultPackageLabelText(pkg);
        } else if (prop.equals(PACKAGE_SUFFIX_CHANGED_PROPERTY)) {
            updateText(getParent().getPackageSuffix(), textSuffix);
        } else if (prop.equals(PACKAGE_NAME_CHANGED_PROPERTY)) {
            updateText(getParent().getNewPackageName(), textPackage);
        } else if (prop.equals(PACKAGE_SELECT_CHANGED_PROPERTY)) {
            selectPackage(getParent().isUseDefaultPackage(), getParent().isUseSuffix());
        } else if (prop.equals(APK_INFO_CHANGED_PROPERTY)) {
            String info = String.valueOf(evt.getNewValue());
            setApkInfoText(info);
        } else if (prop.equals(MESSAGE_TEXT_CHANGED_PROPERTY)) {
            String msg = String.valueOf(evt.getNewValue());
            setMessage(msg);
        }
    }

    private void setUIEnable(boolean enable) {
        int count = panelCheckBox.getComponentCount();
        for (int i = 0; i < count; i++) {
            Object obj = panelCheckBox.getComponent(i);
            if (obj instanceof JRadioButton) {
                ((JRadioButton) obj).setEnabled(enable);
            }
        }
        textAppId.setEnabled(enable);
        textAppKey.setEnabled(enable);
        textPubKey.setEnabled(enable);
        textSecKey.setEnabled(enable);
        textCpId.setEnabled(enable);
        textCpKey.setEnabled(enable);
        textSuffix.setEnabled(enable);
        textPackage.setEnabled(enable);
        radioUseDefPackage.setEnabled(enable);
        radioPackage.setEnabled(enable);
        radioSuffix.setEnabled(enable);
        btnSubmit.setEnabled(enable);
        btnMore.setEnabled(enable);
        btnDrawableChoose.setEnabled(enable);
    }

    private void updateText(String text, JTextField tf) {
        setText(text, tf);
    }

    private void setMessage(String msg) {
        labMsg.setText(msg);
    }

    private void setApkInfoText(String info) {
        Log.iln(info);
        labApkInfo.setText(info);
    }

    private void setDefaultPackageLabelText(String pkg) {
        labPackage.setText(pkg);
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
        if (submitClickActionListener != null) {
            btnSubmit.removeActionListener(submitClickActionListener);
        }
        if (moreClickActionListener != null) {
            btnMore.removeActionListener(moreClickActionListener);
        }
    }

    @Override
    public void setSubmitClickAction(ActionListener listener) {
        this.submitClickActionListener = listener;
    }

    @Override
    public void setMoreClickAction(ActionListener listener) {
        this.moreClickActionListener = listener;
    }

    @Override
    public void setChannelList(List<Channel> channels) {
        this.channelList = channels;
    }

    private String getDrawablePath() {
        return textDrawable.getText();
    }

    private String getAppIdText() {
        return textAppId.getText();
    }

    private String getAppKeyText() {
        return textAppKey.getText();
    }

    private String getPublicKeyText() {
        return textPubKey.getText();
    }

    private String getSecretKeyText() {
        return textSecKey.getText();
    }

    private String getCpIdText() {
        return textCpId.getText();
    }

    private String getCpKeyText() {
        return textCpKey.getText();
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
        return btnMore;
    }

    @Override
    public JButton getSubmitButton() {
        return btnSubmit;
    }
}
