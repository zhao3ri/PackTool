package com.tyland.tool.ui.widget.impl;

import com.tyland.tool.ui.DialogOptionPane;
import com.tyland.tool.ui.widget.IDialogView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class DialogView extends BaseView implements IDialogView, ActionListener {
    private ActionListener completedClickListener;
    private String currentPath;
    private String filterDesc;
    private String[] filters;

    private int dialogWidth = 300;
    private int dialogHeight = 200;
    private int inputItemHeight = 25;
    private int padding = 15;
    private int margin = 5;

    private JButton btnChoose;
    private JButton btnCompleted;
    private JTextField textPath;

    public DialogView(Window win, DialogOptionPane parent) {
        super(win, parent);
    }

    @Override
    protected DialogOptionPane getParent() {
        return (DialogOptionPane) super.getParent();
    }

    @Override
    protected Component createView() {
        return getApkChooseDialogContentView();
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
    public String getChooseFilePath() {
        return textPath.getText();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnChoose) {
            JFileChooser chooser = createFileChooser(JFileChooser.FILES_ONLY, currentPath, filterDesc, filters);
            int result = chooser.showDialog(window, null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file != null) {
                    textPath.setText(file.getAbsolutePath());
                }
            }
        } else if (e.getSource() == btnCompleted) {
            if (completedClickListener != null) {
                completedClickListener.actionPerformed(e);
            }
        }
    }
}
