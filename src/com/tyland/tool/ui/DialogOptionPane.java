package com.tyland.tool.ui;

import com.tyland.tool.ui.widget.IDialogView;
import com.tyland.tool.ui.widget.impl.DialogView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class DialogOptionPane extends BasePane {

    public DialogOptionPane(Window win) {
        super(win);
        resetView();
    }

    @Override
    protected IDialogView createView() {
        IDialogView view = new DialogView(win, this);
        return view;
    }

    @Override
    protected IDialogView getView() {
        return (IDialogView) view;
    }

    public int showDialog(String currentPath, String filterDesc, String... filters) {
        JDialog dialog = createDialog(currentPath, filterDesc, filters);
        dialog.setVisible(true);
        dialog.dispose();

        return returnCode;
    }

    private JDialog createDialog(String currentPath, String filterDesc, String... filters) {
        final JDialog dialog;
        String title = "打开";
        Window window = getWindowForComponent(win);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame) window, title, true);
        } else {
            dialog = new JDialog((Dialog) window, title, true);
        }
        dialog.setSize(view.getWidth(), view.getHeight());
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(win);
        getView().setCompletedClickAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnCode = CODE_ACTION_FILE_CONFIRM;
                dialog.setVisible(false);
            }
        });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnCode = CODE_ACTION_CLOSE;
            }
        });
        getView().setCurrentPath(currentPath);
        getView().setFilter(filterDesc, filters);
        dialog.add(getView().getContentView());
        return dialog;
    }

    public String getChoosePath(){
        return getView().getChooseFilePath();
    }
}
