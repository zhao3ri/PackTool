package com.qinglan.tool;

import com.qinglan.tool.entity.GameChannelConfig;
import com.qinglan.tool.util.FileUtils;
import com.qinglan.tool.util.Utils;

import java.io.File;
import java.io.IOException;

import static com.qinglan.tool.Main.ROOT_PATH;

public class ConfigManager {
    private static final String DATA_PATH = ROOT_PATH + File.separator + "conf" + File.separator;

    public boolean exists(String pkg, int cid) {
        File file = new File(getPath(pkg, cid));
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    public void saveConfig(String pkg, int cid, GameChannelConfig conf) {
        String pkgDir = FileUtils.createFileDir(DATA_PATH + pkg.hashCode());
        try {
            File confFile = FileUtils.createFile(pkgDir, cid + "");
            String content = Utils.obj2Json(conf);
            FileUtils.writer2File(confFile.getCanonicalPath(), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameChannelConfig readConfig(String pkg, int cid) {
        String content = FileUtils.readFile(getPath(pkg, cid));
        GameChannelConfig config = Utils.json2Obj(content, GameChannelConfig.class);
        return config;
    }

    private String getPath(String pkg, int cid) {
        return DATA_PATH + pkg.hashCode() + File.separator + cid;
    }
}
