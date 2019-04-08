package com.tyland.tool.ui.widget.impl;

import com.tyland.tool.ui.JHintTextField;
import com.tyland.tool.ui.MorePane;
import com.tyland.tool.ui.widget.IMoreView;
import com.tyland.tool.util.Utils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.text.ParseException;

public class MoreView extends BaseView implements IMoreView {
    private JHintTextField textMinSDK;
    private JHintTextField textTargetSDK;
    private JHintTextField textVersionCode;
    private JHintTextField textVersionName;
    private JButton btnConfirm;

    private int contentPadding = 20;
    private int dialogMargin = 50;
    private int labelWidth = 110;
    private int textWidth = 85;
    private int itemHeight = 80;

    protected static final String MIN_SDK_TEXT_CHANGED_PROPERTY = "MorePane.MinSdkTextChangedProperty";
    protected static final String TARGET_SDK_TEXT_CHANGED_PROPERTY = "MorePane.TargetSdkTextChangedProperty";
    protected static final String VERSION_NAME_TEXT_CHANGED_PROPERTY = "MorePane.VersionNameTextChangedProperty";
    protected static final String VERSION_CODE_TEXT_CHANGED_PROPERTY = "MorePane.VersionCodeTextChangedProperty";

    private ActionListener confirmClickActionListener;

    public MoreView(Window win, MorePane parent) {
        super(win, parent);
    }

    @Override
    protected MorePane getParent() {
        return (MorePane) super.getParent();
    }

    @Override
    public void load() {
        textMinSDK.setText(getParent().getMinSDK());
        textMinSDK.setHintText(getParent().getMinSDK());
        textTargetSDK.setText(getParent().getTargetSdk());
        textTargetSDK.setHintText(getParent().getTargetSdk());
        textVersionCode.setText(getParent().getVersionCode());
        textVersionCode.setHintText(getParent().getVersionCode());
        textVersionName.setText(getParent().getVersionName());
        textVersionName.setHintText(getParent().getVersionName());
    }

    @Override
    protected Component createView() {
        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(getWidth(), getHeight()));
        addSDKInfoPane(content);
        addVersionInfoPane(content);
        addButton(content);
        return content;
    }

    private void addSDKInfoPane(Container content) {
        JPanel panel = new JPanel();
        textMinSDK = createFormatTextField(true);
        textTargetSDK = createFormatTextField(true);
        panel = initRow(panel, "Android SDK", textMinSDK, "minSdkVersion:", textTargetSDK, "targetSdkVersion:");
        content.add(panel);
    }

    private void addVersionInfoPane(Container content) {
        JPanel panel = new JPanel();
        textVersionName = createFormatTextField(false);
        textVersionCode = createFormatTextField(true);
        panel = initRow(panel, "App Version", textVersionName, "versionName:", textVersionCode, "versionCode:");
        content.add(panel);
    }

    private JPanel initRow(JPanel item, String title, JTextField textFirstColumn, String firstLab, JTextField textSecondColumn, String secondLab) {
        if (item == null) {
            item = new JPanel();
        }
        item.setLayout(null);
        item.setPreferredSize(new Dimension(getWidth() - contentPadding, itemHeight));
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleColor(Color.GRAY);
        item.setBorder(border);

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

    private void addButton(Container content) {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
        btnConfirm = new JButton("确认");
        btnConfirm.addActionListener(confirmClickActionListener);
        panel.add(btnConfirm);
        content.add(panel);
    }

    @Override
    public int getWidth() {
        return getWindowWidth() - dialogMargin;
    }

    @Override
    public int getHeight() {
        return getWindowHeight() - dialogMargin;
    }

    @Override
    public void remove() {
        parent.removePropertyChangeListener(this);
        if (confirmClickActionListener != null) {
            btnConfirm.removeActionListener(confirmClickActionListener);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(MIN_SDK_TEXT_CHANGED_PROPERTY)) {
            String min = (String) evt.getNewValue();
            setText(min, textMinSDK);
        } else if (propertyName.equals(TARGET_SDK_TEXT_CHANGED_PROPERTY)) {
            String target = (String) evt.getNewValue();
            setText(target, textTargetSDK);
        } else if (propertyName.equals(VERSION_CODE_TEXT_CHANGED_PROPERTY)) {
            String code = (String) evt.getNewValue();
            setText(code, textVersionCode);
        } else if (propertyName.equals(VERSION_NAME_TEXT_CHANGED_PROPERTY)) {
            String name = (String) evt.getNewValue();
            setText(name, textVersionName);
        }

    }

    @Override
    public void setConfirmClickAction(ActionListener listener) {
        confirmClickActionListener = listener;
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
