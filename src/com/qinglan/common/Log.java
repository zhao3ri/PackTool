package com.qinglan.common;

/**
 * Created by zhaoj on 2018/10/29.
 */
public class Log {
    public static final int VERBOSE = 1;

    public static final int DEBUG = 2;

    public static final int INFO = 3;

    public static final int WARN = 4;

    public static final int ERROR = 5;

    private static int LEVEL = VERBOSE;

    public static final void v(Object msg) {
        if (LEVEL <= VERBOSE)
            System.out.print(msg);
    }

    public static final void vln(Object msg) {
        if (LEVEL <= VERBOSE)
            System.out.println(msg);
    }

    public static final void d(Object msg) {
        if (LEVEL <= DEBUG)
            System.out.print(msg);
    }

    public static final void dln(Object msg) {
        if (LEVEL <= DEBUG)
            System.out.println(msg);
    }

    public static final void i(Object msg) {
        if (LEVEL <= INFO)
            System.out.print(msg);
    }

    public static final void iln(Object msg) {
        if (LEVEL <= INFO)
            System.out.println(msg);
    }

    public static final void w(Object msg) {
        if (LEVEL <= WARN)
            System.out.print(msg);
    }

    public static final void wln(Object msg) {
        if (LEVEL <= WARN)
            System.out.println(msg);
    }

    public static final void e(Object msg) {
        if (LEVEL <= ERROR)
            System.err.print(msg);
    }

    public static final void eln(Object msg) {
        if (LEVEL <= ERROR)
            System.err.println(msg);
    }

    public static final void ln() {
        System.out.println();
    }

    public static final void ln(int level) {
        if (LEVEL <= level)
            System.out.println();
    }

    public static void setLevel(int level) {
        Log.LEVEL = level;
    }
}
