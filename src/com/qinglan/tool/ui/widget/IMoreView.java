package com.qinglan.tool.ui.widget;

import java.awt.event.ActionListener;

public interface IMoreView extends IView {
    void setConfirmClickAction(ActionListener listener);

    String getMinSdkText();

    String getTargetSdkText();

    String getVersionCodeText();

    String getVersionNameText();
}
