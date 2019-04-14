package com.tyland.tool;

import com.tyland.common.Log;
import com.tyland.tool.util.FileUtils;
import com.tyland.tool.util.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import static com.tyland.tool.BaseCompiler.DRAWABLE_ICON_LAUNCHER;
import static com.tyland.tool.entity.ApkInfo.*;

/**
 * 用于替换图标和闪屏图片
 */
public class DrawableReplaceHelper {
    private String replacePath;
    private Map<String, String> replaceIcons;
    private Map<String, String> replaceScreens;
    //    private Map<String, String> applicationIcons;
    private String iconName;
    private String screenName;
    private static final String SUFFIX_DPI = "dpi";
    private static final String DRAWABLE_MDPI = "mdpi";
    private static final String DRAWABLE_HDPI = "hdpi";
    private static final String DRAWABLE_XHDPI = "xhdpi";
    private static final String DRAWABLE_XXHDPI = "xxhdpi";
    private static final String DRAWABLE_XXXHDPI = "xxxhdpi";

    private static final String DRAWABLE_PREFIX_LAN = "land-";
    private static final String DRAWABLE_NAME_REGEX = "^*-(m|h|xh|xxh|xxxh)dpi";

    private Map<Integer, String> dpiContainer;

    private DrawableReplaceHelper() {
    }

    public DrawableReplaceHelper(Map<String, String> icons, String path) {
        replacePath = path;
        dpiContainer = new HashMap<>();
        dpiContainer.put(0, DRAWABLE_MDPI);
        dpiContainer.put(1, DRAWABLE_HDPI);
        dpiContainer.put(2, DRAWABLE_XHDPI);
        dpiContainer.put(3, DRAWABLE_XXHDPI);
        dpiContainer.put(4, DRAWABLE_XXXHDPI);
        init(icons);
    }

    private void init(Map<String, String> icons) {
        File replaceFile = new File(replacePath);
        if (!replaceFile.exists() || !replaceFile.isDirectory() || null == replaceFile.list() || replaceFile.list().length == 0) {
            return;
        }
        String icon = icons.get(APPLICATION_ICON_160);
        iconName = icon.substring(icon.lastIndexOf("/") + 1, icon.indexOf("."));
        replaceIcons = new HashMap<>();
        replaceScreens = new HashMap<>();
        String[] replaces = replaceFile.list();
        Iterator<String> it = Arrays.asList(replaces).iterator();
        while (it.hasNext()) {
            String name = it.next();
            boolean matches = Utils.matches(DRAWABLE_NAME_REGEX, name);
            if (!matches) {
                continue;
            }
            String suffix = name.substring(name.indexOf("-") + 1, name.indexOf("."));
            Map<String, String> drawableMap;
            if (name.startsWith(DRAWABLE_ICON_LAUNCHER)) {
                drawableMap = replaceIcons;
            } else {
                drawableMap = replaceScreens;
                int index = name.indexOf("-");
                if (index < 0) {
                    continue;
                }
                String prefix = name.substring(0, name.indexOf("-"));//获得闪屏图片的名称
                if (Utils.isEmpty(screenName)) {
                    screenName = prefix;
                }
            }
            putDrawables(drawableMap, suffix, name);
        }
    }

    private void putDrawables(Map<String, String> drawables, String key, String name) {
        drawables.put(key, replacePath + File.separator + name);
    }

    public void replace() throws IOException {
        if (replaceScreens.isEmpty() && replaceIcons.isEmpty()) {
            Log.eln("replace drawable is null");
            return;
        }
//        File resFile = new File(RES_PATH);
        File resFile = new File("");
        File[] drawableFiles = resFile.listFiles(new DrawableFilter());
        Iterator<File> iterator = Arrays.asList(drawableFiles).iterator();
        while (iterator.hasNext()) {
            File drawable = iterator.next();
            if (drawable.isDirectory()) {
                String fileName = drawable.getName();
                int beginIndex = fileName.indexOf("-");
                int endIndex = fileName.indexOf(SUFFIX_DPI);
                String suffix = null;
                if (beginIndex > 0 && endIndex > 0)
                    suffix = fileName.substring(beginIndex + 1, endIndex + SUFFIX_DPI.length());
                Iterator<String> it = Arrays.asList(drawable.list()).iterator();
                while (it.hasNext()) {
                    String drawableName = it.next();
                    String name = drawableName.substring(0, drawableName.indexOf("."));
                    if (name.equals(screenName)) {
                        replace(replaceScreens, drawable.getCanonicalPath(), drawableName, suffix, false);
                    } else if (name.equals(iconName)) {
                        replace(replaceIcons, drawable.getCanonicalPath(), drawableName, suffix, true);
                    }
                }//while
            }
        }
    }

    private boolean replace(Map<String, String> drawables, String drawablePath, String drawableName, String suffix, boolean delete) {
        if (Utils.isEmpty(drawablePath) || Utils.isEmpty(drawableName) || null == drawables || drawables.isEmpty()) {
            return false;
        }
        String path = getPath(drawables, suffix, delete);
        if (Utils.isEmpty(path)) {
            if (delete) {
                FileUtils.deleteFile(drawablePath + File.separator + drawableName);
            }
            return false;
        }
        File file = new File(path);
        String fileName = file.getName();
        FileUtils.copyFile(file, new File(drawablePath + File.separator + fileName));
        FileUtils.deleteFile(drawablePath + File.separator + drawableName);
        FileUtils.renameFile(drawablePath + File.separator + fileName, drawablePath + File.separator + drawableName);
        return true;
    }

    private String getPath(Map<String, String> drawables, String dpi, boolean delete) {
        String path = drawables.get(dpi);
        if (!Utils.isEmpty(path)) {
            return path;
        } else {
            if (delete)
                return null;
        }
        int position;
        if (dpi.startsWith(DRAWABLE_PREFIX_LAN)) {
            position = getPosition(dpi.substring(dpi.indexOf(DRAWABLE_PREFIX_LAN) + DRAWABLE_PREFIX_LAN.length()));
        } else {
            position = getPosition(dpi);
        }
        if (position < 0) {
            return null;
        }
        position++;
        if (position > dpiContainer.size() - 1) {
            return null;
        }
        String nextKey = dpiContainer.get(position);
        if (dpi.startsWith(DRAWABLE_PREFIX_LAN)) {
            nextKey = DRAWABLE_PREFIX_LAN + nextKey;
        }
        return getPath(drawables, nextKey, delete);
    }

    private int getPosition(String dpi) {
        for (Map.Entry<Integer, String> entry : dpiContainer.entrySet()) {
            if (entry.getValue().equals(dpi)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    class DrawableFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith("drawable") || name.startsWith("mipmap");
        }
    }
}
