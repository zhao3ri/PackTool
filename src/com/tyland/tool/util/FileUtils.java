package com.tyland.tool.util;

import com.tyland.common.Log;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    private static final String APK_SUFFIX = ".apk";

    public static File searchApk(String path) {
        File dir = new File(path);
        String[] files = dir.list();
        for (String name : files) {
            if (name.endsWith(APK_SUFFIX) && (new File(getPath(dir.getAbsolutePath()) + name)).isFile()) {
                Log.dln("find apk:" + name);
                File apk = new File(getPath(dir.getAbsolutePath()) + name);
                return apk;
            }
        }
        return null;
    }

    /**
     * 创建文件存储并返回相应路径
     *
     * @param fileDir
     * @return
     */
    public static String createFileDir(String fileDir) {
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
            Log.dln("create dir:" + fileDir);
        }
        return fileDir;
    }

    public static void copyFile(File source, File dest) {
        if (!source.exists()) {
            Log.e("not find file");
            return;
        }
        if (dest.exists()) {
            dest.delete();
        }
        FileChannel input = null;
        FileChannel output = null;
        try {
            input = new FileInputStream(source).getChannel();
            output = new FileOutputStream(dest).getChannel();
            output.transferFrom(input, 0, input.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 复制某个目录及目录下的所有子目录和文件到新文件夹
    public static void copyFolder(File source, File dest) {
        try {
            Log.iln("source file == " + source.getCanonicalPath() + ", dest file== " + dest.getCanonicalPath());
            // 如果文件夹不存在，则建立新文件夹
            if (!dest.exists())
                dest.mkdirs();
            // 读取整个文件夹的内容到file字符串数组，下面设置一个游标i，不停地向下移开始读这个数组
            String[] file = source.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                temp = new File(getPath(source.getCanonicalPath()) + file[i]);
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(getPath(dest.getCanonicalPath()) + (temp.getName()));
                    byte[] bufferarray = new byte[1024 * 64];
                    int prereadlength;
                    while ((prereadlength = input.read(bufferarray)) != -1) {
                        output.write(bufferarray, 0, prereadlength);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } else if (temp.isDirectory()) {
                    copyFolder(new File(getPath(source.getCanonicalPath()) + file[i]), new File(getPath(dest.getCanonicalPath()) + file[i]));
                }
            }
        } catch (Exception e) {
            Log.e("copy folder is error");
        }
    }

    public static final void copyApk(File source, File dest) throws IOException {
        if (dest.exists())
            dest.delete();
        byte[] buf = new byte[1024];
        ZipInputStream zin = new ZipInputStream(new FileInputStream(source));
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(dest));
        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            // Add ZIP entry to output stream.
            zout.putNextEntry(new ZipEntry(name));
            // Transfer bytes from the ZIP file to the output file
            int len;
            while ((len = zin.read(buf)) > 0) {
                zout.write(buf, 0, len);
            }
            entry = zin.getNextEntry();
        }
        zin.closeEntry();
        zin.close();
        zout.close();
    }

    /**
     * 删除指定文件夹
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            deleteFile(folderPath);//删除空文件夹
//            String filePath = folderPath;
//            File file = new File(filePath);
//            file.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static final void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean isExists(String path) {
        try {
            File f = new File(getPath(path));
            if (f.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public static String getPath(String path) {
        return path.endsWith(File.separator) ? path : path + File.separator;
    }

    public static File createFile(String path, String fileName) throws IOException {
        File file = new File(getPath(path) + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static boolean renameFile(String source, String dest) {
        return renameFile(new File(source), new File(dest));
    }

    public static boolean renameFile(File source, File dest) {
        return source.renameTo(dest);
    }

    public static String findFile(String path, String regex) {
        File dir = new File(path);
        String[] files = dir.list();
        if (files != null)
            for (String name : files) {
                if (Utils.matches(regex, name)) {
                    return name;
                }
            }
        return null;
    }

    public static boolean delMatchFile(String path, String fileName, String regex) {
        File dir = new File(getPath(path) + fileName);
        if (Utils.matches(regex, fileName)) {
            Log.dln("regex=" + regex + " ,delete file: " + dir.getAbsolutePath());
            if (dir.isDirectory()) {
                delAllFile(dir.getAbsolutePath());
            } else {
                deleteFile(dir.getAbsolutePath());
            }
            return true;
        }
        return false;
    }

    public static void deleteEmptyDir(String path) {
        File root = new File(path);
        if (root.isDirectory()) {
            File[] dirs = root.listFiles();
            if (dirs != null && dirs.length > 0) {
                for (int i = 0; i < dirs.length; i++) {
                    removeEmptyDir(dirs[i]);
                }
            }
        }
    }

    private static void removeEmptyDir(File dir) {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            if (fs != null && fs.length > 0) {
                for (int i = 0; i < fs.length; i++) {
                    File tmpFile = fs[i];
                    if (tmpFile.isDirectory()) {
                        removeEmptyDir(tmpFile);
                    }
                    if (tmpFile.isDirectory() && tmpFile.listFiles().length <= 0) {
                        tmpFile.delete();
                    }
                }
            }
            if (dir.isDirectory() && dir.listFiles().length == 0) {
                dir.delete();
            }
        }
    }

    public static String readFile(String path) {
        return replaceFile(path, null, null);
    }

    public static String replaceFile(String path, String target, String replace) {
        if (null != replace && !replace.isEmpty() && null != target && !target.isEmpty()) {
            return readAndReplaceFile(path, new String[]{target}, new String[]{replace});
        }
        return readAndReplaceFile(path, null, null);
    }

    public static String readAndReplaceFile(String path, String[] targets, String[] replaces) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        if (!file.isFile()) {
            throw new IllegalArgumentException("the " + path + " is not a file");
        }
        BufferedReader buf = null;
        String line = null;
        try {
            buf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            while ((line = buf.readLine()) != null) {
                if (line.trim().startsWith("<!--") || line.trim().isEmpty()) {
                    continue;
                }
                if (null != replaces && replaces.length != 0 && null != targets && targets.length != 0 && targets.length == replaces.length) {
                    for (int i = 0; i < targets.length; i++) {
                        if (line.contains(targets[i]) && !Utils.isEmpty(replaces[i])) {
                            line = line.replace(targets[i], replaces[i]);
                        }
                    }
                }
                sb.append(line);
                sb.append("\n");
                Log.dln(line);
            }
            Log.ln();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static void writer2File(String path, String content) {
        Log.dln("write to " + path);
        File file = new File(path);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(file));
            printWriter.println(content);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

    }
}
