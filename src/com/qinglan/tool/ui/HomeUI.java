package com.qinglan.tool.ui;

import com.qinglan.common.Log;
import com.qinglan.tool.util.Utils;
import com.qinglan.tool.entity.Channel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class HomeUI extends BaseUI implements ItemListener, ActionListener {
    private static final String LINE = "________________________________________________________";
    private Container container;
    private JButton btnChoose;
    private JTextField textApkPath;
    private JButton btnSubmit;
    private JButton btnMore;
    private JPanel panelCheckBox;
    private JTextField textAppId;
    private JTextField textAppKey;
    private JTextField textPubKey;
    private JTextField textSecKey;
    private JTextField textCpId;
    private JTextField textCpKey;
    private JTextField textSuffix;
    private JTextField textDrawable;
    private JButton btnDrawableChoose;
    private JLabel labMsg;
    private JLabel labApkInfo;

    //    private MoreUI moreUI;
    private OnCloseListener closeListener;
    private OnSubmitClickListener submitClickListener;
    private OnChangedChannelListener changedChannelListener;
    private MoreUI.OnConfirmClickListener onConfirmClickListener;

    private String currentPath;

    private String drawablePath;
    private String minSdk;
    private String targetSdk;
    private String versionCode;
    private String versionName;

    public HomeUI(List<Channel> channelList, String path) {
        super();
        currentPath = path;
        container = frame.getContentPane();
//        addApkChoose();
        addChannelListCheckBox(channelList);
        addChooseDrawable();
        addConfigPanel();
        addButton();
        addLabel();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (closeListener != null)
                    closeListener.onClose();
            }
        });
    }

    @Override
    protected JFrame createFrame() {
        frame = new JFrame();
        frame.setTitle("QL打包工具");
        frame.setLayout(new FlowLayout());
        frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT)); //设置窗口的大小
        frame.setLocation(300, 200);//设置窗口的初始位置
        frame.setResizable(false);
        frame.addComponentListener(this);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        return frame;
    }

    private void addApkChoose() {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(FRAME_WIDTH, 15));
//        panel.setLayout(new GridLayout(1, 2));
        textApkPath = new JTextField(35);
        panel.add(textApkPath);

        btnChoose = new JButton("选择");
        btnChoose.setSize(50, 15);
        btnChoose.addActionListener(this);
        panel.add(btnChoose);
        container.add(panel);
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

    private void addChooseDrawable() {
        JPanel panel = new JPanel();
//        textDrawable = new JTextField(35);
        textDrawable = setConfigPanel("选择替换图片目录:", panel, 25);
        textDrawable.setEnabled(false);
        panel.add(textDrawable);
        btnDrawableChoose = new JButton("选择");
        btnDrawableChoose.addActionListener(this);
        panel.add(btnDrawableChoose);
        container.add(panel);
    }

    private void addConfigPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 5, 5));
        textAppId = setConfigPanel("AppId:", panel, 20);
        textAppKey = setConfigPanel("AppKey:", panel, 20);
        textPubKey = setConfigPanel("PublicKey:", panel, 20);
        textSecKey = setConfigPanel("SecretKey:", panel, 20);
        textCpId = setConfigPanel("CpId:", panel, 20);
        textCpKey = setConfigPanel("CpKey:", panel, 20);
        textSuffix = setConfigPanel("包名后缀:", panel, 20);
        container.add(panel);
    }

    private void addButton() {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(FRAME_WIDTH, 0));
        panel.setLayout(new GridLayout(1, 2, 5, 10));
        btnSubmit = new JButton("提交");
        btnSubmit.setSize(50, 15);
        btnSubmit.addActionListener(this);
        panel.add(btnSubmit);

        btnMore = new JButton("更多");
        btnMore.setSize(50, 15);
        btnMore.addActionListener(this);
        panel.add(btnMore);
        container.add(panel);
    }

    private void addLabel() {
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(FRAME_WIDTH, 50));
        panel.setLayout(new GridLayout(3, 1, 0, 5));
        labApkInfo = new JLabel("", JLabel.CENTER);
        labApkInfo.setForeground(Color.RED);
        panel.add(labApkInfo);

        JLabel line = new JLabel(LINE, JLabel.CENTER);
        line.setForeground(Color.LIGHT_GRAY);
        panel.add(line);

        labMsg = new JLabel("", JLabel.CENTER);
        labMsg.setMinimumSize(new Dimension(FRAME_WIDTH, 0));
//        Font fnt = new Font("Default", Font.BOLD, 30);
//        lab.setFont(fnt);
        labMsg.setForeground(Color.BLUE);
        panel.add(labMsg);
        container.add(panel);
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
                submitClickListener.onClick(textAppId.getText(), textAppKey.getText(), textPubKey.getText()
                        , textSecKey.getText(), textCpId.getText(), textCpKey.getText(), textSuffix.getText());
        } else if (e.getSource() == btnChoose) {
            JFileChooser chooser = showFileChooser("APK", JFileChooser.FILES_ONLY, "apk", currentPath);
            File file = chooser.getSelectedFile();
            textApkPath.setText(file.getAbsoluteFile().toString());
        } else if (e.getSource() == btnDrawableChoose) {
            JFileChooser chooser = showFileChooser(null, JFileChooser.DIRECTORIES_ONLY, drawablePath);
            File file = chooser.getSelectedFile();
            drawablePath = file.getAbsoluteFile().toString();
            textDrawable.setText(drawablePath);
        } else if (e.getSource() == btnMore) {
            MoreUI moreUI = new MoreUI();
            moreUI.setOnConfirmClickListener(onConfirmClickListener);
            moreUI.setMinSDK(minSdk);
            moreUI.setTargetSDK(targetSdk);
            moreUI.setVersionCode(versionCode);
            moreUI.setVersionName(versionName);
            moreUI.show();
        }
    }

    public void setUIEnable(boolean enable) {
        int count = panelCheckBox.getComponentCount();
        for (int i = 0; i < count; i++) {
            Object obj = panelCheckBox.getComponent(i);
            if (obj instanceof JRadioButton) {
                ((JRadioButton) obj).setEnabled(enable);
            }
        }
        textAppId.setEnabled(enable);
        textAppKey.setEnabled(enable);
        textPubKey.setEnabled(enable);
        textSecKey.setEnabled(enable);
        textCpId.setEnabled(enable);
        textCpKey.setEnabled(enable);
        textSuffix.setEnabled(enable);
        btnSubmit.setEnabled(enable);
        btnMore.setEnabled(enable);
        btnDrawableChoose.setEnabled(enable);
    }

    public void setDrawablePath(String path) {
        setText(path, textDrawable);
        drawablePath = path;
    }

    public void setAppIdText(String id) {
        setText(id, textAppId);
    }

    public void setAppKeyText(String key) {
        setText(key, textAppKey);
    }

    public void setPublicKeyText(String key) {
        setText(key, textPubKey);
    }

    public void setSecretKeyText(String key) {
        setText(key, textSecKey);
    }

    public void setCpIdText(String id) {
        setText(id, textCpId);
    }

    public void setCpKeyText(String key) {
        setText(key, textCpKey);
    }

    public void setSuffixText(String suffix) {
        setText(suffix, textSuffix);
    }

    private void setText(String text, JTextField textField) {
        if (text == null) {
            textField.setText("");
        } else {
            textField.setText(text);
        }
    }

    public void setMessage(String msg) {
        labMsg.setText(msg);
    }

    public void setApkInfoText(String text) {
        labApkInfo.setText(text);
    }

    public String getDrawablePath() {
        return drawablePath;
    }

    public void setMinSDK(String min) {
        minSdk = min;
    }

    public void setTargetSDK(String target) {
        targetSdk = target;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void showSignChooseDialog(final OnSignChooseClickListener chooseClickListener, final OnSignCompleteClickListener completeClickListener
            , final OnCloseListener closeListener, final String filterDesc, final String... filters) {
        final JDialog dialog = new JDialog(frame, "Choose keystore", false);
        dialog.setSize(300, 200);
        setLocation(dialog);
        JPanel panel = new JPanel();
        panel.setMinimumSize(new Dimension(FRAME_WIDTH, 15));
//        panel.setLayout(new GridLayout(1, 2));
        final JTextField textSignPath = new JTextField(15);
        textSignPath.setEnabled(false);
        panel.add(textSignPath);

        JButton btnChoose = new JButton("选择");
        btnChoose.setSize(50, 15);
        btnChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = showFileChooser(filterDesc, JFileChooser.FILES_ONLY, currentPath, filters);
                textSignPath.setText(chooser.getSelectedFile().getAbsolutePath());
                if (chooseClickListener != null)
                    chooseClickListener.onClick(textSignPath);
            }
        });
        panel.add(btnChoose);
        final JTextField textPass = setConfigPanel("请输入密码", panel, 15);
        final JTextField textAlias = setConfigPanel("请输入别名", panel, 15);
        JButton btnCompleted = new JButton("完成");
        panel.add(btnCompleted);
        btnChoose.setSize(50, 15);
        btnCompleted.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                if (completeClickListener != null)
                    completeClickListener.onClick(textSignPath.getText(), textPass.getText(), textAlias.getText());
            }
        });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (closeListener != null)
                    closeListener.onClose();
            }
        });
        dialog.add(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    public JFileChooser showFileChooser(String filterDesc, int mode, String path, String... filters) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(mode);
        if (Utils.isEmpty(path)) {
            path = currentPath;
        }
        chooser.setCurrentDirectory(new File(path));
        if (!Utils.isEmpty(filterDesc) && null != filters) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDesc, filters);
            chooser.setFileFilter(filter);
        }
        chooser.showDialog(new JLabel(), "选择");
        return chooser;
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

    public void setOnConfirmClickListener(MoreUI.OnConfirmClickListener listener) {
        onConfirmClickListener = listener;
    }

    public interface OnCloseListener {
        void onClose();
    }

    public interface OnSubmitClickListener {
        void onClick(String appId, String appKey, String pubKey, String secretKey, String cpId, String cpKey, String suffix);
    }

    public interface OnChangedChannelListener {
        void onChange(int id);
    }

    public interface OnDialogButtonClickListener {
        void onPositive();

        void onNegative();
    }

    public interface OnSignCompleteClickListener {
        void onClick(String path, String passwords, String alias);
    }

    public interface OnSignChooseClickListener {
        void onClick(JTextField text);
    }

}
