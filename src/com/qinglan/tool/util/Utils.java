package com.qinglan.tool.util;

import com.qinglan.common.Log;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static void delete(String jarName, List<String> deletes) throws Exception {
        //先备份
        File oriFile = new File(jarName);
        if (!oriFile.exists()) {
            Log.d("######Not Find File:" + jarName);
            return;
        }
        //将文件名命名成备份文件
        String bakJarName = jarName.substring(0, jarName.length() - 3) + System.currentTimeMillis() + ".jar";
        File bakFile = new File(bakJarName);
        boolean isOK = oriFile.renameTo(bakFile);
        if (!isOK) {
            Log.d("######Remame ERR..........");
            return;
        }

        //创建文件（根据备份文件并删除部分）
        JarFile bakJarFile = new JarFile(bakJarName);
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarName));
        Enumeration<JarEntry> entries = bakJarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!deletes.contains(entry.getName())) {
                InputStream inputStream = bakJarFile.getInputStream(entry);
                jos.putNextEntry(entry);
                byte[] bytes = readStream(inputStream);
                jos.write(bytes, 0, bytes.length);
            } else {
                System.out.println("Delete:-------" + entry.getName());
            }
        }
        jos.flush();
        jos.finish();
        jos.close();
        bakJarFile.close();
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    public static int execShell(String scriptPath, String... args) {

        //解决脚本没有执行权限
//            ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755",scriptPath);

        String argStr = "";
        if (args != null && args.length > 0) {
            for (String arg : args) {
                Log.dln("arg==" + arg);
                argStr += arg + " ";
            }
        }
        String cmd = "cmd.exe /C start /b " + scriptPath + " " + argStr;
        Log.dln(cmd);
        Log.ln();
        ShellUtil shell = new ShellUtil();
        int result = shell.execute(cmd);
        return result;
    }

    public static boolean matches(String regex, String input) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static boolean isEmpty(String s) {
        if (null == s || s.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
//        execShell("H:/PackTools/bin/build.bat");
        Log.d(matches("libentryexpro", "libentryexpro"));
    }
}
