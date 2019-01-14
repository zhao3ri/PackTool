package com.qinglan.tool.ui.widget;

import java.awt.event.ActionListener;

public interface IDialogView extends IView {
    void setCompletedClickAction(ActionListener listener);

    void setCurrentPath(String path);

    void setFilter(String filterDesc, String... filters);
}
