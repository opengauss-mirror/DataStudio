/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.logger;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.loggerif.ErrorLogWriterIf;
import com.huawei.mppdbide.utils.loggerutil.LoggerUtils;

/**
 * 
 * Title: class
 * 
 * Description: The Class MPPDBIDELoggerUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class MPPDBIDELoggerUtility implements ILogger {

    private static Logger LOGGER;

    private static Logger SECURITY;

    private static Logger OPERATION;

    private static boolean isDetailLog = false;

    private static boolean isLoggerInitialized = false;

    private static String loglevel;

    private static boolean validatLevel = true;

    private static ErrorLogWriterIf errorLogWrapper = new ErrorLogWriter();

    private static final String REGEX_HOST_IPADDRESS = "\\d+\\.\\d+\\.\\d+\\.\\d+\\:\\d+|\\d+\\.\\d+\\.\\d+\\.\\d+";

    private static final String MASK_TEXT_FOR_IPADDRESS = "XX.XX.XX.XX";

    private final static Object Lock;

    static {
        LoggerUtils.setErrorLogWriter(errorLogWrapper);
    }

    static {
        ClassLoader classLoader = MPPDBIDELoggerUtility.class.getClassLoader();
        if (null != classLoader) {
            URL url = classLoader.getResource("log4j2.xml");
            if (null != url) {
                System.setProperty("log4j.configurationFile", url.getPath());
            }
        }
        Lock = new Object();
    }

    /**
     * Gets the parameter.
     *
     * @param argument the argument
     * @return the parameter
     */
    public static String getParameter(String argument) {
        if (argument.split("=").length > 1) {
            return argument.split("=")[1].trim();
        } else {
            return null;
        }
    }

    /**
     * Sets the args.
     *
     * @param arguments the new args
     */
    public static void setArgs(String[] arguments) {
        String[] args = arguments;
        if (null != args) {
            int len = args.length;
            for (int i = 0; i < len; i++) {
                if (args[i] != null) {
                    if (args[i].startsWith("-detailLogging")) {
                        String detailLogValue = getParameter(args[i]);

                        if ("true".equalsIgnoreCase(detailLogValue)) {
                            isDetailLog = true;
                        }
                    }
                    if (args[i].startsWith("-logginglevel")) {
                        loglevel = getParameter(args[i]);
                    }
                }
            }

        }
        checkAndCreateLogger(true);

    }

    /**
     * Checks if is debug enabled.
     *
     * @return true, if is debug enabled
     */
    public static boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
    }

    /**
     * Checks if is info enabled.
     *
     * @return true, if is info enabled
     */
    public static boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
    }

    /**
     * Checks if is trace enabled.
     *
     * @return true, if is trace enabled
     */
    public static boolean isTraceEnabled() {
        return LOGGER.isTraceEnabled();
    }

    /**
     * Validate log level.
     *
     * @return true, if successful
     */
    public static boolean validateLogLevel() {
        return validatLevel;

    }

    /**
     * Check and create logger.
     */
    private static void checkAndCreateLogger() {
        checkAndCreateLogger(false);

    }

    /**
     * Check and create logger.
     *
     * @param isReinit the is reinit
     */
    public static void checkAndCreateLogger(boolean isReinit) {
        synchronized (Lock) {
            if (!isLoggerInitialized) {
                LOGGER = LogManager.getLogger("dslogger");
                SECURITY = LogManager.getLogger("security");
                OPERATION = LogManager.getLogger("operation");
                changeLevelAndReconfigure();
                isLoggerInitialized = true;
            } else if (isReinit) {
                changeLevelAndReconfigure();
            }
        }
    }

    private static void changeLevelAndReconfigure() {
        if (null != loglevel) {
            Level level = null;
            Configurator.setLevel(LOGGER.getName(), Level.toLevel(loglevel));
            if (!LOGGER.getLevel().toString().equalsIgnoreCase(loglevel)) {
                level = Level.WARN;
                validatLevel = false;
            } else {
                level = Level.toLevel(loglevel);
            }
            Configurator.setLevel(LOGGER.getName(), level);
        }
    }

    /**
     * Trace.
     *
     * @param msgParam the msg param
     */
    public static void trace(String msgParam) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();
        LOGGER.trace(msg);
    }

    /**
     * Info.
     *
     * @param msgParam the msg param
     */
    public static void info(String msgParam) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();
        LOGGER.info(msg);
    }

    /**
     * Warn.
     *
     * @param msgParam the msg param
     */
    public static void warn(String msgParam) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();
        LOGGER.warn(msg);
    }

    /**
     * Debug.
     *
     * @param msgParam the msg param
     */
    public static void debug(String msgParam) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();
        LOGGER.debug(msg);
    }

    /**
     * Error.
     *
     * @param msgParam the msg param
     */
    public static void error(String msgParam) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();
        LOGGER.error(msg);
    }

    /**
     * Error.
     *
     * @param msgParam the msg param
     */
    public static void error(String msgParam, Throwable throwable) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();

        if (isDetailLog) {
            filterSensitiveExceptionsAndLogError(throwable, msg);
        } else {
            LOGGER.error(msg);
        }
    }

    private static String maskIPInExceptionMessage(String message, Throwable exp) {
        StringBuffer messageBuilder = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        messageBuilder.append(message);
        messageBuilder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        messageBuilder.append(ExceptionUtils.getStackTrace(exp));

        Pattern pattern = Pattern.compile(REGEX_HOST_IPADDRESS);
        Matcher matcher = pattern.matcher(messageBuilder.toString());
        if (matcher.find()) {
            return matcher.replaceAll(MASK_TEXT_FOR_IPADDRESS);
        }
        return messageBuilder.toString();
    }

    private static void filterSensitiveExceptionsAndLogError(Throwable throwable, String msg) {
        if (ISensitiveExceptionsFilter.isSensitiveException(throwable)
                || ISensitiveExceptionsFilter.isSensitiveException(throwable.getCause())) {
            LOGGER.error(msg);
        } else {
            LOGGER.error(maskIPInExceptionMessage(msg, throwable));
        }
    }

    private static void filterSensitiveExceptionsAndLogFatal(Throwable throwable, String msg) {
        if (ISensitiveExceptionsFilter.isSensitiveException(throwable)
                || ISensitiveExceptionsFilter.isSensitiveException(throwable.getCause())) {
            LOGGER.fatal(msg);
        } else {
            LOGGER.fatal(maskIPInExceptionMessage(msg, throwable));
        }
    }

    /**
     * Fatal.
     *
     * @param msgParam the msg param
     */
    public static void fatal(String msgParam, Throwable throwable) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        checkAndCreateLogger();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        if (isDetailLog) {
            filterSensitiveExceptionsAndLogFatal(throwable, msg);
        } else {
            LOGGER.fatal(msg);
        }
    }

    /**
     * Perf.
     *
     * @param module the module
     * @param operation the operation
     * @param isStart the is start
     */
    public static void perf(String module, String operation, boolean isStart) {
        StringBuilder message = new StringBuilder("[PERF][");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
        message.append(operation).append("][").append(isStart ? "START" : "STOP").append("][").append(module)
                .append("]<<").append(simpleDateFormat.format(new Date())).append(">>");
        trace(message.toString());
    }

    /**
     * None.
     *
     * @param msg the msg
     */
    public static void none(String msg) {
        // donot log anything here. This implementation is for
        // explicit dummy log, to fool static tools.
    }

    /**
     * Perf.
     *
     * @param msg the msg
     */
    public static void perf(String msg) {
        StringBuilder message = new StringBuilder("[PERF][");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
        message.append(simpleDateFormat.format(new Date())).append(" ] ").append(msg);
        trace(message.toString());
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ErrorLogWriter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class ErrorLogWriter implements ErrorLogWriterIf {

        @Override
        public void errorLog(String msg) {
            error(msg);

        }

    }

    /**
     * Info.
     *
     * @param msgParam the msg param
     */
    public static void securityInfo(String msgParam) {
        checkAndCreateLogger();
        SECURITY.info(msgParam);
    }

    /**
     * Info.
     *
     * @param msgParam the msg param
     */
    public static void operationInfo(String msgParam) {
        String msg = msgParam;
        StackTraceElement[] traceElementArr = Thread.currentThread().getStackTrace();
        if (null != traceElementArr && traceElementArr.length > 2 && null != traceElementArr[2]) {
            StackTraceElement traceElement = traceElementArr[2];
            msg = " [" + traceElement.getClassName() + "] " + msg;
        }
        checkAndCreateLogger();
        OPERATION.info(msg);
    }

    /**
     * Info.
     *
     * @param msgParam the msg param
     */
    public static void securityError(String msgParam) {
        checkAndCreateLogger();
        SECURITY.error(msgParam);
    }

}
