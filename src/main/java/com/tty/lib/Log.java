package com.tty.lib;

import org.bukkit.Bukkit;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static volatile Logger LOGGER;
    private static volatile boolean DEBUG;
    private static final String PREFIX_DEBUG = "[DEBUG] ";

    private static final String[] ANSI_COLORS = {
            "\u001B[31m",
            "\u001B[32m",
            "\u001B[33m",
            "\u001B[34m",
            "\u001B[35m",
            "\u001B[36m",
            "\u001B[91m",
            "\u001B[92m",
            "\u001B[93m",
            "\u001B[94m"
    };

    private static final String ANSI_RESET = "\u001B[0m";

    private static volatile boolean ENABLE_COLOR = true;

    public static void init(Logger logger, boolean isDebug) {
        if (logger == null) {
            //noinspection UnstableApiUsage
            Bukkit.getLogger().log(Level.SEVERE, "init logger error.");
            return;
        }
        LOGGER = logger;
        DEBUG = isDebug;
    }

    public static void setEnableColor(boolean enable) {
        ENABLE_COLOR = enable;
    }

    private static boolean isLoggerNotReady() {
        return LOGGER == null;
    }

    private static String randomColor() {
        return ANSI_COLORS[ThreadLocalRandom.current().nextInt(ANSI_COLORS.length)];
    }

    /**
     * 将文本按当前配置着色（如果 ENABLE_COLOR=false 则直接返回原文）
     */
    private static String colorize(String text) {
        if (!ENABLE_COLOR || text == null || text.isEmpty()) {
            return text;
        }
        return randomColor() + text + ANSI_RESET;
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
                String argStr = String.valueOf(args[i]);
                if (ENABLE_COLOR) {
                    sb.append(randomColor()).append(argStr).append(ANSI_RESET);
                } else {
                    sb.append(argStr);
                }
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

    public static void warn(Throwable throwable) {
        if (isLoggerNotReady()) return;
        String msg = "[" + getCallerClassName() + "] " + throwable.getMessage();
        LOGGER.log(Level.WARNING, msg, throwable);
    }

    public static void warn(Throwable throwable, String msg, Object... args) {
        if (isLoggerNotReady()) return;
        LOGGER.log(Level.WARNING, formatMessage(msg, args), throwable);
    }

    public static void error(Throwable throwable) {
        if (isLoggerNotReady()) return;
        String msg = "[" + getCallerClassName() + "] " + throwable.getMessage();
        LOGGER.log(Level.SEVERE, msg, throwable);
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
            if (!className.equals(Log.class.getName()) && !className.startsWith("java.lang.Thread")) {
                return colorize(className);
            }
        }
        return colorize("Unknown");
    }
}
