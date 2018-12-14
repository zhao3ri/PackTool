package com.qinglan.tool.ui;

import com.qinglan.common.Log;
import com.qinglan.tool.xml.Channel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class HomeUI extends ComponentAdapter implements ItemListener, ActionListener {
    private JFrame frame;
    private Container container;
    private JButton btnSubmit;
    private JPanel panelCheckBox;
    private JTextField textAppId;
    private JTextField textAppKey;
    private JTextField textPubKey;
    private JTextField textSecKey;
    private JTextField textCpId;
    private JLabel labMsg;

    private OnCloseListener closeListener;
    private OnSubmitClickListener submitClickListener;
    private OnChangedChannelListener changedChannelListener;

    private static final int FRAME_WIDTH = 500;
    private static final int FRAME_HEIGHT = 500;
    private int frame_locx;
    private int frame_locy;

    public HomeUI(List<Channel> channelList) {
        frame = new JFrame();
        frame.setTitle("QL打包工具");
        frame.setLayout(new FlowLayout());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT); //设置窗口的大小
        frame.setLocation(300, 200);//设置窗口的初始位置
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口
        frame.setResizable(false);
        frame.addComponentListener(this);
        container = frame.getContentPane();
        addChannelListCheckBox(channelList);
        addConfigPanel();
        btnSubmit = addButton("提交");
        btnSubmit.addActionListener(this);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (closeListener != null)
                    closeListener.onClose();
            }
        });
    }

    private void addChannelListCheckBox(List<Channel> channelList) {
        panelCheckBox = new JPanel();
        panelCheckBox.setBorder(BorderFactory.createTitledBorder("选择需要打包的渠道："));
        panelCheckBox.setLayout(new FlowLayout()); // 设置组件的排版
        ButtonGroup buttonGroup = new ButtonGroup();

        for (Channel channel : channelList) {
//            JCheckBox checkBox = new JCheckBox(channel.getName());
            JRadioButton radioButton = new JRadioButton(channel.getName());
            radioButton.setName(String.valueOf(channel.getId()));
            radioButton.addItemListener(this);
            buttonGroup.add(radioButton);
            panelCheckBox.add(radioButton);
        }

        JScrollPane pane = new JScrollPane(panelCheckBox);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        container.add(panelCheckBox);

    }

    private JButton addButton(String text) {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(FRAME_WIDTH, 0));
        panel.setLayout(new GridLayout(2, 1));
        JButton button = new JButton(text);
        button.setSize(50, 15);
        panel.add(button);
        labMsg = addLabelMsg();
        panel.add(labMsg);
        container.add(panel);
        return button;
    }

    private void addConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        textAppId = setConfigPanel("AppId:", panel);
        textAppKey = setConfigPanel("AppKey:", panel);
        textPubKey = setConfigPanel("PublicKey:", panel);
        textSecKey = setConfigPanel("SecretKey:", panel);
        textCpId = setConfigPanel("CpId:", panel);
        container.add(panel);
    }

    private JTextField setConfigPanel(String text, JPanel panel) {
        JLabel label = new JLabel(text);
//        labelPublicKey.setHorizontalAlignment(SwingConstants.CENTER);
        JTextField textField = new JTextField(20);
        panel.add(label);
        panel.add(textField);
        return textField;
    }

    private JLabel addLabelMsg() {
//        JLabel line = new JLabel("-----------------------------------", JLabel.CENTER);
//        container.add(line);
        JLabel lab = new JLabel("Welcome", JLabel.CENTER);
        lab.setLayout(new GridLayout(1, 1));
        lab.setMinimumSize(new Dimension(FRAME_WIDTH, 0));
//        Font fnt = new Font("Default", Font.BOLD, 30);
//        lab.setFont(fnt);
        lab.setForeground(Color.BLUE);
        container.add(lab);
        return lab;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JRadioButton radioButton = (JRadioButton) e.getItem();
        Log.d(radioButton.isSelected() ? "select " : "cancel ");
        Log.dln(radioButton.getText() + " id=" + radioButton.getName());
        if (changedChannelListener != null)
            changedChannelListener.onChange(radioButton.isSelected() ? Integer.valueOf(radioButton.getName()) : 0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSubmit) {
            if (submitClickListener != null)
                submitClickListener.onClick(textAppId.getText(), textAppKey.getText()
                        , textPubKey.getText(), textSecKey.getText(), textCpId.getText());
        }
    }

    public void setRadioButtonsEnable(boolean enable) {
        int count = panelCheckBox.getComponentCount();
        for (int i = 0; i < count; i++) {
            Object obj = panelCheckBox.getComponent(i);
            if (obj instanceof JRadioButton) {
                ((JRadioButton) obj).setEnabled(enable);
            }
        }
    }

    public void showDialog(String msg) {
        showDialog(msg, false);
    }

    public void showDialog(String msg, boolean addBtn) {
        showDialog(msg, addBtn, null);
    }

    public void showDialog(String msg, boolean addBtn, final OnDialogButtonClickListener listener) {
        final JDialog dialog = new JDialog(frame, "Tips", false);
        dialog.setSize(200, 100);

        int x = frame_locx - dialog.getWidth() / 2 + FRAME_WIDTH / 2;
        int y = frame_locy - dialog.getHeight() / 2 + FRAME_HEIGHT / 2;
        dialog.setLocation(x, y);
        dialog.setLayout(new GridLayout(2, 1));
        Label label = new Label();
        label.setText(msg);
        dialog.add(label);
        if (addBtn) {
            JPanel panel = new JPanel();
            JButton btnYes = new JButton("是");
            JButton btnNo = new JButton("否");
            btnYes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    if (listener != null)
                        listener.onPositive();
                }
            });
            btnNo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    if (listener != null)
                        listener.onNegative();
                }
            });
            FlowLayout fl = (FlowLayout) panel.getLayout();
            fl.setHgap(10);//水平间距
            fl.setVgap(10);//组件垂直间距
            panel.setLayout(fl);
            panel.add(btnYes);
            panel.add(btnNo);
            dialog.add(panel);
        }
        dialog.setResizable(false);
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dialog.setVisible(false);
            }
        });
    }

    @Override
    public void componentResized(ComponentEvent e) {
        super.componentResized(e);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        Component comp = e.getComponent();

        //更新当前窗口所在的坐标
        frame_locx = comp.getX();
        frame_locy = comp.getY();
    }

    public void setCloseListener(OnCloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public void setSubmitClickListener(OnSubmitClickListener submitClickListener) {
        this.submitClickListener = submitClickListener;
    }

    public void setChangedChannelListener(OnChangedChannelListener changedChannelListener) {
        this.changedChannelListener = changedChannelListener;
    }

    public void setMessage(String msg) {
        labMsg.setText(msg);
    }

    public interface OnCloseListener {
        void onClose();
    }

    public interface OnSubmitClickListener {
        void onClick(String appId, String appKey, String pubKey, String secretKey, String cpId);
    }

    public interface OnChangedChannelListener {
        void onChange(int id);
    }

    public interface OnDialogButtonClickListener {
        void onPositive();

        void onNegative();
    }
}
