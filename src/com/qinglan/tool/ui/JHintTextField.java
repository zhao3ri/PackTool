package com.qinglan.tool.ui;

import com.qinglan.tool.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class JHintTextField extends JFormattedTextField implements FocusListener {
    private String mHintText;
    private String mText;
    private Color mHintTextColor;
    private Color mDefaultTextColor;
    private boolean isHint;

    public JHintTextField() {
        super();
        init();
    }

    public JHintTextField(Object value) {
        super(value);
        init();
    }

    private void init() {
        mDefaultTextColor = getForeground();
        mHintTextColor = Color.GRAY;
        addFocusListener(this);
    }

    public void setHintText(String hintText) {
        this.mHintText = hintText;
        isHint = true;
        setText(hintText);
        setForeground(mHintTextColor);
    }

    public String getHintText() {
        return mHintText;
    }

    @Override
    public void setText(String t) {
        super.setText(t);
        if (!isHint) {
            mText = t;
        }
        isHint = false;
    }

    @Override
    public void focusGained(FocusEvent e) {
        //获取焦点时，清空提示内容
        String temp = getText();
        if (Utils.isEmpty(temp)) {
            setHintText(mHintText);
        } else {
            setText(temp);
            setForeground(mDefaultTextColor);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        String temp = getText();
        if (Utils.isEmpty(temp)) {
            setHintText(mHintText);
        }
    }
}
