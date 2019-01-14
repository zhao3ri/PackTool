package com.qinglan.tool.ui;

import com.qinglan.tool.ui.widget.IView;
import com.qinglan.tool.ui.widget.impl.BaseView;
import com.qinglan.tool.util.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.File;

import static javax.swing.JOptionPane.*;

public abstract class BasePane<T extends Window> extends JComponent implements WindowListener, ActionListener {
    protected T win;
    protected IView view;
    private Container container;

    protected final int buttonHeight = 28;
    protected final int buttonWidth = 75;
    protected final int defaultHeight = 20;

    private PropertyChangeListener propertyChangeListener;
    private ActionListener actionListener;

    protected int returnCode;
    public static final int CODE_ACTION_CLOSE = -1;
    public static final int CODE_ACTION_CLICK_SUBMIT = 1;
    public static final int CODE_ACTION_CLICK_MORE = 2;
    public static final int CODE_ACTION_MORE_CONFIRM = 3;
    public static final int CODE_ACTION_SIGN_CONFIRM = 4;

    public static final String CHANNEL_RADIO_CHANGED_PROPERTY = "ChannelRadioChangedProperty";

    protected static final String ACTION_SUBMIT_BUTTON_CLICK = "SubmitButtonClickAction";
    protected static final String ACTION_MORE_CONFIRM_BUTTON_CLICK = "MoreConfirmButtonClickAction";

    private BasePane() {
    }

    public BasePane(T win) {
        init(win);
    }

    public BasePane(Component parentComponent) {
        this();
        init((T) getWindowForComponent(parentComponent));
    }

    private void init(T win) {
        this.win = win;
        this.view = createView();
        addActionListener(this);
    }

    protected abstract <V extends IView> V createView();

    protected void addContentView(Component comp) {
        if (container != null) {
            container.add(comp);
        }
    }

    protected void setContainer(Container c) {
        this.container = c;
    }

    public void setLocation(Window win) {
        int location[] = getLocation(win);
        int x = location[0];
        int y = location[1];
        win.setLocation(x, y);
    }

    protected final Window getWindowForComponent(Component parent)
            throws HeadlessException {
        if (parent == null)
            return getRootFrame();
        if (parent instanceof Frame || parent instanceof Dialog)
            return (Window) parent;
        return getWindowForComponent(parent.getParent());
    }

    protected void setText(String text, JTextField textField) {
        if (text == null) {
            textField.setText("");
            return;
        }
        textField.setText(text);
    }

    protected int[] getLocation(Window win) {
        int x = this.win.getX() - win.getWidth() / 2 + this.win.getWidth() / 2;
        int y = this.win.getY() - win.getHeight() / 2 + this.win.getHeight() / 2;
        return new int[]{x, y};
    }

    protected final int getWindowWidth() {
        return win.getWidth();
    }

    protected final int getWindowHeight() {
        return win.getHeight();
    }

    public void setPropertyChangeListener(PropertyChangeListener listener) {
        if (listener != null) {
            propertyChangeListener = listener;
            addPropertyChangeListener(CHANNEL_RADIO_CHANGED_PROPERTY, propertyChangeListener);
        }
    }

    public void setActionListener(ActionListener listener) {
        if (listener != null) {
            removeActionListener(listener);
            actionListener = listener;
            addActionListener(actionListener);
        }
    }

    protected void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    protected void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    protected void fireActionPerformed(String command) {
        fireActionPerformed(returnCode, command);
    }

    protected void fireActionPerformed(Object source, String command) {
        Object[] listeners = listenerList.getListenerList();

        ActionEvent event = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (event == null) {
                    event = getActionEvent(source, command);
                }
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }

    private ActionEvent getActionEvent(Object source, String command) {
        long mostRecentEventTime = EventQueue.getMostRecentEventTime();
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent) currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }

        ActionEvent event = new ActionEvent(source, ActionEvent.ACTION_PERFORMED,
                command, mostRecentEventTime, modifiers);
        return event;
    }

    protected int getReturnCode() {
        return returnCode;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
//    @Override
//    public AccessibleContext getAccessibleContext() {
//        if (accessibleContext == null) {
//            accessibleContext = new AccessibleBasePane();
//        }
//        return accessibleContext;
//    }
//
//    protected class AccessibleBasePane extends AccessibleJComponent {
//        public AccessibleRole getAccessibleRole() {
//            return AccessibleRole.FRAME;
//        }
//
//    }
}
