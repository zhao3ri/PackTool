package com.qinglan.tool.ui;

import com.qinglan.tool.ui.widget.IDialogView;
import com.qinglan.tool.ui.widget.IView;
import com.qinglan.tool.ui.widget.impl.DialogView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;


public class DialogOptionPane extends BasePane {
    private String signPath;
    private String password;
    private String alias;

    public DialogOptionPane(Window win) {
        super(win);
    }

    public DialogOptionPane(Component parentComponent) {
        super(parentComponent);
    }

    @Override
    protected IDialogView createView() {
        IDialogView view = new DialogView(win, this);
        return view;
    }

    private IDialogView getView() {
        return (IDialogView) view;
    }

    public String getSignPath() {
        return signPath;
    }

    public void setSignPath(String signPath) {
        this.signPath = signPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int showSignDialog(String currentPath, String filterDesc, String... filters) {
        JDialog dialog = createSignDialog(currentPath, filterDesc, filters);
        dialog.setVisible(true);
        dialog.dispose();

        return returnCode;
    }

    private JDialog createSignDialog(String currentPath, String filterDesc, String... filters) {
        JDialog dialog;
        String title = "Choose keystore";
        Window window = getWindowForComponent(win);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, title, true);
        } else {
            dialog = new JDialog((Dialog) window, title, true);
        }
        dialog.setSize(view.getWidth(), view.getHeight());
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(win);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnCode = CODE_ACTION_CLOSE;
            }
        });
        getView().setCompletedClickAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnCode = CODE_ACTION_SIGN_CONFIRM;
            }
        });
        getView().setCurrentPath(currentPath);
        getView().setFilter(filterDesc, filters);
        dialog.add(view.getContentView());
        return dialog;
    }

}
