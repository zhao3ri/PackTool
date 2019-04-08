package com.tyland.tool.ui.widget.impl;

import com.tyland.tool.ui.BasePane;
import com.tyland.tool.ui.widget.IView;
import com.tyland.tool.util.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;

public abstract class BaseView<T extends BasePane> implements IView, PropertyChangeListener {
    protected Window window;
    protected final int buttonHeight = 28;
    protected final int buttonWidth = 75;
    protected final int defaultHeight = 20;

    protected T parent;

    public BaseView(Window win, T parent) {
        this.window = win;
        this.parent = parent;
    }

    protected T getParent() {
        return parent;
    }

    protected int getWindowWidth() {
        return window.getWidth();
    }

    protected int getWindowHeight() {
        return window.getHeight();
    }

    @Override
    public Component getContentView() {
        Component view = createView();
        parent.addPropertyChangeListener(this);
        return view;
    }

    protected abstract Component createView();

    protected void setText(String text, JTextField textField) {
        if (text == null) {
            textField.setText("");
            return;
        }
        textField.setText(text);
    }

    protected JTextField getLabelWithTextView(String text, Dimension labSize, JPanel panel, int columns, int... locations) {
        JLabel label = new JLabel(text);
//        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (labSize != null) {
            if (panel.getLayout() == null) {
                label.setSize(labSize);
            } else {
                label.setPreferredSize(labSize);
            }
        }
        JTextField textField = new JTextField();
        textField.setFont(window.getFont());
        if (locations != null && locations.length > 1) {
            label.setLocation(locations[0], locations[1]);
            if (locations.length > 3) {
                textField.setLocation(locations[2], locations[3]);
            }
        }
        if (columns > 0) {
            textField.setColumns(columns);
        }
        panel.add(label);
        panel.add(textField);
        return textField;
    }

    protected JFileChooser createFileChooser(int mode, String path, String filterDesc, String... filters) {
        JFileChooser chooser = new JFileChooser();
        chooser.setApproveButtonText("选择");
        chooser.setFileSelectionMode(mode);
        if (!Utils.isEmpty(path)) {
            chooser.setCurrentDirectory(new File(path));
        }
        if (!Utils.isEmpty(filterDesc) && null != filters) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDesc, filters);
            chooser.setFileFilter(filter);
        }
        return chooser;
    }

    protected JFileChooser showFileChooser(int mode, String path, String filterDesc, String... filters) {
        JFileChooser chooser = createFileChooser(mode, path, filterDesc, filters);
        chooser.showDialog(window, null);
        return chooser;
    }
}
