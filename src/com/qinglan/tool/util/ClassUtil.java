package com.qinglan.tool.util;

import com.qinglan.common.Log;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil {
    /**
     * 获得包下面的所有的class
     *
     * @param pack package完整名称
     * @return List包含所有class的实例
     */
    public static List<Class> getClasssFromPackage(String pack) {
        List<Class> clazzs = new ArrayList<Class>();

        // 是否循环搜索子包
        boolean recursive = true;

        // 包名字
        String packageName = pack;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', File.separatorChar);

        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();

                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    Log.dln("file类型的扫描");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(packageName, filePath, recursive, clazzs);
                } else if ("jar".equals(protocol)) {
                    Log.dln("jar类型的扫描");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clazzs;
    }

    /**
     * 在package对应的路径下找到所有的class
     *
     * @param packageName package名称
     * @param filePath    package对应的路径
     * @param recursive   是否查找子package
     * @param clazzs      找到class以后存放的集合
     */
    public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive, List<Class> clazzs) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从jar文件中读取指定目录下面的所有的class文件
     *
     * @param jarPaht  jar文件存放的位置
     * @param filePaht 指定的文件目录
     * @return 所有的的class的对象
     */
    public List<Class> getClasssFromJarFile(String jarPaht, String filePaht) {
        List<Class> clazzs = new ArrayList<>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPaht);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<JarEntry> jarEntryList = new ArrayList<JarEntry>();
        Enumeration<JarEntry> ee = jarFile.entries();
        while (ee.hasMoreElements()) {
            JarEntry entry = ee.nextElement();
            // 过滤我们出满足我们需求的东西
            if (entry.getName().startsWith(filePaht) && entry.getName().endsWith(".class")) {
                jarEntryList.add(entry);
            }
        }
        for (JarEntry entry : jarEntryList) {
            String className = entry.getName().replace(File.separatorChar, '.');
            className = className.substring(0, className.length() - 6);

            try {
                clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(className));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazzs;
    }

    /**
     * 通过流将jar中的一个文件的内容输出
     *
     * @param jarPaht  jar文件存放的位置
     * @param filePaht 指定的文件目录
     */
    public static void getStream(String jarPaht, String filePaht) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPaht);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<JarEntry> ee = jarFile.entries();

        List<JarEntry> jarEntryList = new ArrayList<>();
        while (ee.hasMoreElements()) {
            JarEntry entry = ee.nextElement();
            // 过滤我们出满足我们需求的东西，这里的fileName是指向一个具体的文件的对象的完整包路径，比如com/mypackage/test.txt
            if (entry.getName().startsWith(filePaht)) {
                jarEntryList.add(entry);
            }
        }
        try {
            InputStream in = jarFile.getInputStream(jarEntryList.get(0));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s = "";

            while ((s = br.readLine()) != null) {
                Log.dln(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
