package com.tyland.tool;

import com.tyland.tool.entity.YJConfig;
import com.tyland.tool.util.ShellUtils;

import java.io.File;

import static com.tyland.tool.Main.ROOT_PATH;

public abstract class BaseCompiler {
    public static final String BIN_PATH = ROOT_PATH + File.separator + "bin";
    public static final String CLASS2DEX_PATH = BIN_PATH + File.separator + "class2dex.bat";
    public static final String DEX2SMALI_PATH = BIN_PATH + File.separator + "dex2smali.bat";
    public static final String OUT_DIR_NAME = "output";

    protected String jarPath;

    protected YJConfig yjConfig;

    protected ShellUtils.ProgressListener progressListener;

    private BaseCompiler() {
    }

    public BaseCompiler(String jar) {
        jarPath = jar;
    }

    protected String getOutDirPath() {
        return ROOT_PATH + File.separator + OUT_DIR_NAME + File.separator;
    }

    public void setProgressListener(ShellUtils.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

}
