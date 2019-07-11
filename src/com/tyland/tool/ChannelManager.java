package com.tyland.tool;

import com.tyland.tool.util.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

import static com.tyland.tool.BaseCompiler.OUT_DIR_NAME;
import static com.tyland.tool.Main.ROOT_PATH;
import static com.tyland.tool.util.FileUtils.createFileDir;

/**
 * Created by zhaoj on 2018/10/29.
 */
public class ChannelManager {
    private CyclicBarrier cyclicBarrier;
    private OnExecuteFinishListener listener;
    private ShellUtils.ProgressListener progressListener;

    private String jarFilePath;
    private Decoder mDecoder;

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_NO_FIND = 1;
    public static final int STATUS_FAIL = 2;

    public ChannelManager() {
        createFileDir(ROOT_PATH + File.separator + OUT_DIR_NAME + File.separator);
    }

    public void setCyclicBarrier(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    public void setPath(String path) {
        jarFilePath = path;
    }

    public void execute() {
        new SubThread(cyclicBarrier, "BuildApk") {
            @Override
            public void execute() {
                int result = STATUS_FAIL;
                if (Utils.isEmpty(jarFilePath)) {
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                    return;
                }
                try {
                    //初始化Decoder
                    mDecoder = new Decoder(jarFilePath);
                    mDecoder.setProgressListener(progressListener);
                    result = mDecoder.decode();
                } catch (Exception e) {
                    e.printStackTrace();
                    result = STATUS_FAIL;
                } finally {
                    if (listener != null) {
                        listener.onFinish(result);
                    }
                }
            }
        }.start();
    }


    private void updateProgress(String msg) {
        if (progressListener != null) {
            progressListener.publishProgress(msg);
        }
    }

    public void setProgressListener(ShellUtils.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public String getOutApkPath() {
        if (mDecoder != null) {
            return mDecoder.getOutSmaliPath();
        }
        return null;
    }

    public void setExecuteFinishListener(OnExecuteFinishListener listener) {
        this.listener = listener;
    }

    public interface OnExecuteFinishListener {
        void onFinish(int code);
    }

}
