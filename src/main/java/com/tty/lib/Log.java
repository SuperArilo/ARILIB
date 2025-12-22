package com.tty.lib;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static volatile Logger LOGGER;
    private static volatile boolean DEBUG;
    private static final String PREFIX_DEBUG = "[DEBUG] ";

    public static void init(Logger logger, boolean isDebug) {
        LOGGER = logger;
        DEBUG = isDebug;
    }

    private static boolean isLoggerNotReady() {
        return LOGGER == null;
    }

    private static String formatMessage(String msg, Object... args) {
        if (args == null || args.length == 0) {
            return msg;
        }

        String[] parts = msg.split("%s", -1);
        StringBuilder sb = new StringBuilder();

        int len = Math.min(parts.length, args.length + 1);
        for (int i = 0; i < len; i++) {
            sb.append(parts[i]);
            if (i < args.length) {
                sb.append(args[i]);
            }
        }

        if (parts.length > len) {
            for (int i = len; i < parts.length; i++) {
                sb.append(parts[i]);
            }
        }

        return sb.toString();
    }

    public static void info(String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.log(Level.INFO, formatMessage(msg, args));
    }

    public static void warn(String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.log(Level.WARNING, formatMessage(msg, args));
    }

    public static void warn(Throwable throwable, String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.log(Level.WARNING, formatMessage(msg, args), throwable);
    }

    public static void error(String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.log(Level.SEVERE, formatMessage(msg, args));
    }

    public static void error(Throwable throwable, String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.log(Level.SEVERE, formatMessage(msg, args), throwable);
    }

    public static void debug(String msg, Object... args) {
        if (!DEBUG || isLoggerNotReady()) return;
        String message = PREFIX_DEBUG + "[" + getCallerClassName() + "] " + formatMessage(msg, args);
        LOGGER.log(Level.INFO, message);
    }

    public static void debug(Throwable throwable, String msg, Object... args) {
        if (!DEBUG || isLoggerNotReady()) return;
        String message = PREFIX_DEBUG + "[" + getCallerClassName() + "] " + formatMessage(msg, args);
        LOGGER.log(Level.INFO, message, throwable);
    }

    private static String getCallerClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (!className.equals(Log.class.getName())
                    && !className.startsWith("java.lang.Thread")) {
                int idx = className.lastIndexOf('.');
                return idx >= 0 ? className.substring(idx + 1) : className;
            }
        }
        return "Unknown";
    }
}
