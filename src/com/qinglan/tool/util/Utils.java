package com.qinglan.tool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qinglan.common.Log;
import com.qinglan.tool.ChannelManager;

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
            Log.dln("######Not Find File:" + jarName);
            return;
        }
        //将文件名命名成备份文件
        String bakJarName = jarName.substring(0, jarName.length() - 3) + System.currentTimeMillis() + ".jar";
        File bakFile = new File(bakJarName);
        boolean isOK = oriFile.renameTo(bakFile);
        if (!isOK) {
            Log.dln("######Remame ERR..........");
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

    public static int execShell(ShellUtils.OnProgressListener listener, String scriptPath, String... args) {

        //解决脚本没有执行权限
//            ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755",scriptPath);

        String argStr = "";
        if (args != null && args.length > 0) {
            for (String arg : args) {
                argStr += arg + " ";
            }
        }
        String cmd = "cmd.exe /C start /b " + scriptPath + " " + argStr;
        Log.dln(cmd);
        Log.ln();
        ShellUtils shell = null;
        if (listener != null) {
            shell = new ShellUtils(listener);
        } else {
            shell = new ShellUtils();
        }
        int result = shell.execute(cmd);
        return result;

    }

    public static int execShell(String scriptPath, String... args) {
        return execShell(null, scriptPath, args);
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


    public static boolean equalsString(String currStr, String compareStr) {
        if (null == currStr && null == compareStr) {
            return true;
        }
        if (currStr != null && currStr.equals(compareStr)) {
            return true;
        }
        return false;
    }

    public static <T> T json2Obj(String json, Class<T> cls) {
        if (isEmpty(json)){
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(json, cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String obj2Json(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
