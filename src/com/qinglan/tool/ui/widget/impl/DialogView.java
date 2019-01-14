package com.qinglan.tool.ui.widget.impl;

import com.qinglan.tool.ui.BasePane;
import com.qinglan.tool.ui.DialogOptionPane;
import com.qinglan.tool.ui.widget.IDialogView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class DialogView extends BaseView implements IDialogView {
    private ActionListener completedClickListener;
    private String currentPath;
    private String filterDesc;
    private String[] filters;

    private int width = 300;
    private int height = 200;

    public DialogView(Window win, DialogOptionPane parent) {
        super(win, parent);
    }

    @Override
    protected DialogOptionPane getParent() {
        return (DialogOptionPane) super.getParent();
    }

    @Override
    protected Component createView() {
        return getSignDialogContentView();
    }

    private int getBodyWidth() {
        return width - padding;
    }

    private int chooseItemHeight = 35;
    private int labWidth = 70;
    private int inputItemHeight = 25;
    private int padding = 15;
    private int margin = 5;

    private Component getSignDialogContentView() {
        JPanel group = new JPanel();
        group.setLayout(null);
        group.setPreferredSize(new Dimension(width, height));

        JPanel choosePanel = new JPanel();
        choosePanel.setSize(new Dimension(getBodyWidth(), chooseItemHeight));
        choosePanel.setLocation(padding / 2, padding / 2);
        final JTextField textSignPath = new JTextField();
        textSignPath.setPreferredSize(new Dimension(getBodyWidth()- buttonWidth - padding / 2 , inputItemHeight));
        textSignPath.setEnabled(false);
        choosePanel.add(textSignPath);

        final JButton btnChoose = new JButton("选择");
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
        final JButton btnCompleted = new JButton("完成");
        btnCompleted.setSize(buttonWidth, buttonHeight);
        btnCompleted.setLocation((getBodyWidth() - buttonWidth) / 2, (inputItemHeight + margin) * 2);
        inputPanel.add(btnCompleted);
        group.add(inputPanel);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == btnChoose) {
                    JFileChooser chooser = showFileChooser(JFileChooser.FILES_ONLY, currentPath, filterDesc, filters);
                    File file = chooser.getSelectedFile();
                    if (file != null) {
                        textSignPath.setText(file.getAbsolutePath());
                    }
                } else if (e.getSource() == btnCompleted) {
                    getParent().setSignPath(textSignPath.getText());
                    getParent().setPassword(textPass.getText());
                    getParent().setAlias(textAlias.getText());
                    if (completedClickListener != null) {
                        completedClickListener.actionPerformed(e);
                    }
                }
            }
        };
        btnCompleted.addActionListener(actionListener);
        btnChoose.addActionListener(actionListener);
        return group;
    }

    @Override
    public void load() {

    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
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
}
