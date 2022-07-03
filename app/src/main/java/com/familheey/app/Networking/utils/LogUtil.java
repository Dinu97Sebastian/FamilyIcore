package com.familheey.app.Networking.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

class LogUtil {

    public static boolean ISDEBUG = true;

    /**
     * Initializes <code>LogUtil.ISDEBUG</code>
     *
     * @param context - current context passed
     */
    public static void init(Context context) {
        boolean isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE == (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
        LogUtil.ISDEBUG = isDebuggable;
    }

    /**
     * Prints given tag and text as debug log, only if in debug mode.
     *
     * @param tag  - Tag to be set for logs
     * @param text - Log message
     */
    public static void debug(String tag, String text) {
        if (LogUtil.ISDEBUG) {
        }
    }

    /**
     * Prints given tag and text as warning log, only if in debug mode.
     *
     * @param tag  - Tag to be set for logs
     * @param text - Log message
     */
    public static void warn(String tag, String text) {
        if (LogUtil.ISDEBUG) {
        }
    }

    /**
     * Prints given tag and text as error log, only if in debug mode.
     *
     * @param tag  - Tag to be set for logs
     * @param text - Log message
     */
    public static void error(String tag, String text) {
        if (LogUtil.ISDEBUG) {
        }
    }

    /**
     * Prints given tag and exception as error log, only if in debug mode with message.
     *
     * @param tag - Tag to be set for logs
     * @param msg - Log message
     * @param ex  - Exception to be logged
     */
    public static void error(String tag, String msg, Throwable ex) {
        if (LogUtil.ISDEBUG) {
        }
    }

    /**
     * Prints given tag and exception as error log without message
     *
     * @param tag - Tag to be set for logs
     * @param ex  - Exception to be logged
     */
    public static void error(String tag, Throwable ex) {
        error(tag, "", ex);
    }
}
