package com.qinglan.tool.ui.widget.impl;

import com.qinglan.tool.ui.DialogOptionPane;
import com.qinglan.tool.ui.widget.IDialogView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

import static com.qinglan.tool.ui.DialogOptionPane.TYPE_APK;
import static com.qinglan.tool.ui.DialogOptionPane.TYPE_SIGN;

public class DialogView extends BaseView implements IDialogView, ActionListener {
    private ActionListener completedClickListener;
    private String currentPath;
    private String filterDesc;
    private String[] filters;

    private int dialogWidth = 300;
    private int dialogHeight = 200;
    private int chooseItemHeight = 35;
    private int labWidth = 70;
    private int inputItemHeight = 25;
    private int padding = 15;
    private int margin = 5;

    private JButton btnChoose;
    private JButton btnCompleted;
    private JTextField textPath;
    private int dialogType;

    public DialogView(Window win, DialogOptionPane parent) {
        this(win, parent, TYPE_SIGN);
    }

    public DialogView(Window win, DialogOptionPane parent, int type) {
        super(win, parent);
        this.dialogType = type;
    }

    @Override
    protected DialogOptionPane getParent() {
        return (DialogOptionPane) super.getParent();
    }

    @Override
    protected Component createView() {
        switch (dialogType) {
            case TYPE_SIGN:
                return getSignDialogContentView();
            case TYPE_APK:
                return getApkChooseDialogContentView();
        }
        return null;
    }

    private int getBodyWidth() {
        return dialogWidth - padding;
    }

    private Component getApkChooseDialogContentView() {
        JPanel group = new JPanel();
        group.setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder("选择apk:");
        group.setBorder(border);
        group.setPreferredSize(new Dimension(getBodyWidth(), dialogHeight - 2 * padding));
        Dimension borderSize = border.getMinimumSize(parent);

        textPath = new JTextField();
        textPath.setSize(new Dimension(getBodyWidth() - margin, inputItemHeight));
        textPath.setLocation(padding / 2, borderSize.height + padding);
        textPath.setEnabled(false);

        btnChoose = new JButton("选择");
        btnChoose.setSize(new Dimension(buttonWidth, buttonHeight));
        btnChoose.setLocation(getBodyWidth() - 2 * buttonWidth - margin, inputItemHeight + margin + borderSize.height + padding);
        btnCompleted = new JButton("确定");
        btnCompleted.setSize(buttonWidth, buttonHeight);
        btnCompleted.setLocation(getBodyWidth() - buttonWidth, inputItemHeight + margin + borderSize.height + padding);
        group.add(textPath);
        group.add(btnChoose);
        group.add(btnCompleted);
        btnCompleted.addActionListener(this);
        btnChoose.addActionListener(this);
        return group;
    }

    private Component getSignDialogContentView() {
        JPanel group = new JPanel();
        group.setLayout(null);
        group.setPreferredSize(new Dimension(dialogWidth, dialogHeight));

        JPanel choosePanel = new JPanel();
        choosePanel.setSize(new Dimension(getBodyWidth(), chooseItemHeight));
        choosePanel.setLocation(padding / 2, padding / 2);
        textPath = new JTextField();
        textPath.setPreferredSize(new Dimension(getBodyWidth() - buttonWidth - padding / 2, inputItemHeight));
        textPath.setEnabled(false);
        choosePanel.add(textPath);

        btnChoose = new JButton("选择");
        btnChoose.setSize(new Dimension(buttonWidth, buttonHeight));
        btnChoose.setLocation(getBodyWidth() - buttonWidth - padding / 2, 0);
        choosePanel.add(btnChoose);
        group.add(choosePanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setSize(new Dimension(getBodyWidth(), getHeight() - padding / 2 - chooseItemHeight - margin));
        inputPanel.setLayout(null);
        inputPanel.setLocation(padding / 2, chooseItemHeight + margin + padding / 2);

        Dimension labSize = new Dimension(labWidth, inputItemHeight);
        Dimension textFieldSize = new Dimension(getBodyWidth() - labWidth - margin, inputItemHeight);
        final JTextField textPass = getLabelWithTextView("请输入密码", labSize, inputPanel, 0, 0, 0, labWidth, 0);
        textPass.setSize(new Dimension(textFieldSize));
        final JTextField textAlias = getLabelWithTextView("请输入别名", labSize, inputPanel, 0, 0, inputItemHeight + margin, labWidth, inputItemHeight + margin);
        textAlias.setSize(textFieldSize);
        btnCompleted = new JButton("完成");
        btnCompleted.setSize(buttonWidth, buttonHeight);
        btnCompleted.setLocation((getBodyWidth() - buttonWidth) / 2, (inputItemHeight + margin) * 2);
        inputPanel.add(btnCompleted);
        group.add(inputPanel);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == btnCompleted) {
                    getParent().setSignPath(textPath.getText());
                    getParent().setPassword(textPass.getText());
                    getParent().setAlias(textAlias.getText());
                    if (completedClickListener != null) {
                        completedClickListener.actionPerformed(e);
                    }
                }
            }
        };
        btnCompleted.addActionListener(actionListener);
        btnChoose.addActionListener(this);
        return group;
    }

    @Override
    public void load() {

    }

    @Override
    public int getWidth() {
        return dialogWidth;
    }

    @Override
    public int getHeight() {
        return dialogHeight;
    }

    @Override
    public void remove() {

    }

    @Override
    public void setCompletedClickAction(ActionListener listener) {
        this.completedClickListener = listener;
    }

    @Override
    public void setCurrentPath(String path) {
        this.currentPath = path;
    }

    @Override
    public void setFilter(String filterDesc, String... filters) {
        this.filterDesc = filterDesc;
        this.filters = filters;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnChoose) {
            JFileChooser chooser = showFileChooser(JFileChooser.FILES_ONLY, currentPath, filterDesc, filters);
            File file = chooser.getSelectedFile();
            if (file != null) {
                textPath.setText(file.getAbsolutePath());
            }
        } else if (e.getSource() == btnCompleted) {
            if (completedClickListener != null) {
                completedClickListener.actionPerformed(e);
            }
        }
    }
}
