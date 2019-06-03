package com.tyland.tool.ui.widget.impl;

import com.tyland.tool.entity.YJConfig;
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
        TitledBorder border = createBorder(apkInfoItem, "请输入");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 5, 5));
        panel.setSize(new Dimension(getWidth() - contentPadding - defaultMargin * 2, itemHeight - defaultMargin));

        Insets insets = border.getBorderInsets(parent);
//        int margin = contentWidth / 2 - labelWidth - textWidth;
        int startX = insets.left + contentPadding / 2;
        int startY = insets.top;
        panel.setLocation(startX, startY);

        textAppPackage = new JTextField();
        getLabelWithTextView(textAppPackage, "包 名:", null, panel, 20);
        textAppPackage.setEnabled(false);
        panel.add(textAppPackage);
        textAppName = new JHintTextField();
        getLabelWithTextView(textAppName, "应用名:", null, panel, 20);
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

        btnPackage = new JButton("确认打包");
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
//        } else if (prop.equals(APP_PKG_CHANGED_PROPERTY)) {
//            String pkg = (String) evt.getNewValue();
//            setText(pkg, textAppPackage);
//        } else if (prop.equals(APP_NAME_CHANGED_PROPERTY)) {
//            String name = (String) evt.getNewValue();
//            setText(name, textAppName);
        }
    }

    private void setUIEnable(boolean enable) {
        if (textAppPackage != null) {
            textAppPackage.setEnabled(enable);
        }

        if (textAppName != null) {
            textAppName.setEnabled(enable);
        }

        if (btnPackage != null) {
            btnPackage.setEnabled(enable);
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
    public String getAppPackageText() {
        return textAppPackage.getText();
    }

    @Override
    public void setAppPackageText(String pkg) {
        setText(pkg, textAppPackage);
    }

    @Override
    public String getAppNameText() {
        return getText(textAppName);
    }

    @Override
    public void setAppNameText(String name) {
        setText(name, textAppName);
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
