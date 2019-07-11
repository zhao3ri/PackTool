package com.tyland.tool;

import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;

import java.io.File;
import java.io.IOException;

import static com.tyland.tool.ChannelManager.*;

public class Decoder extends BaseCompiler {
    private String jarName;

    public Decoder(String jar) {
        super(jar);
    }

    public int decode() {
        int result = STATUS_FAIL;
        if (Utils.isEmpty(jarPath)) {
            return STATUS_NO_FIND;
        }
        try {
            String jarFileName = jarPath.substring(jarPath.lastIndexOf(File.separator) + 1);
            jarName = jarFileName.substring(0, jarFileName.lastIndexOf("."));
            String outDexFile = getOutDirPath() + jarName + ".dex";
            String outSmaliPath = getOutDirPath() + jarName;
            String cmd = "%s %s %s";
            if (jarPath.endsWith(".dex")) {
                FileUtils.copyFile(new File(jarPath), new File(outDexFile));
                result = dex2smail(cmd, outSmaliPath, outDexFile);
            } else {
                String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/cmd, CLASS2DEX_PATH, outDexFile, jarPath);
                result = Utils.execShell(progressListener, scriptPath);
                if (result == STATUS_SUCCESS) {
                    result = dex2smail(cmd, outSmaliPath, outDexFile);
                }
            }
            FileUtils.deleteFile(outDexFile);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private int dex2smail(String cmd, String outSmaliPath, String outDexFile) {
        String scriptPath = String.format(/*"%s d %s -o %s -s -f",*/cmd, DEX2SMALI_PATH, outSmaliPath, outDexFile);
        int result = Utils.execShell(progressListener, scriptPath);
        return result;
    }

    public String getOutSmaliPath() {
        try {
            return new File(getOutDirPath()).getCanonicalPath() + File.separator + jarName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getOutDirPath() + jarName;
    }
}
