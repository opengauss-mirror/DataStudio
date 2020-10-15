/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConfigCheckHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConfigCheckHandler {

    private static final java.util.List<String> CHAR_ENCODING_LIST = new ArrayList<String>(
            MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    static {
        addCharEncodingFirstList();
        addCharEncodingSecondList();

    }

    private static void addCharEncodingSecondList() {
        CHAR_ENCODING_LIST.add("LATIN9");
        CHAR_ENCODING_LIST.add("LATIN10");
        CHAR_ENCODING_LIST.add("MULE_INTERNAL");
        CHAR_ENCODING_LIST.add("SJIS");
        CHAR_ENCODING_LIST.add("SHIFT_JIS_2004");
        CHAR_ENCODING_LIST.add("SQL_ASCII");
        CHAR_ENCODING_LIST.add("UHC");
        CHAR_ENCODING_LIST.add("UTF8");
        CHAR_ENCODING_LIST.add("UTF-8");
        CHAR_ENCODING_LIST.add("csUTF8");
        CHAR_ENCODING_LIST.add("WIN866");
        CHAR_ENCODING_LIST.add("WIN874");
        CHAR_ENCODING_LIST.add("WIN1250");
        CHAR_ENCODING_LIST.add("WIN1251");
        CHAR_ENCODING_LIST.add("WIN1252");
        CHAR_ENCODING_LIST.add("WIN1253");
        CHAR_ENCODING_LIST.add("WIN1254");
        CHAR_ENCODING_LIST.add("WIN1255");
        CHAR_ENCODING_LIST.add("WIN1256");
        CHAR_ENCODING_LIST.add("WIN1257");
        CHAR_ENCODING_LIST.add("WIN1258");
    }

    private static void addCharEncodingFirstList() {
        CHAR_ENCODING_LIST.add("BIG5");
        CHAR_ENCODING_LIST.add("EUC_CN");
        CHAR_ENCODING_LIST.add("EUC_JP");
        CHAR_ENCODING_LIST.add("EUC_JIS_2004");
        CHAR_ENCODING_LIST.add("EUC_KR");
        CHAR_ENCODING_LIST.add("EUC_TW");
        CHAR_ENCODING_LIST.add("GB18030");
        CHAR_ENCODING_LIST.add("GBK");
        CHAR_ENCODING_LIST.add("CP936");
        CHAR_ENCODING_LIST.add("MS936");
        CHAR_ENCODING_LIST.add("windows-936");
        CHAR_ENCODING_LIST.add("csGBK");
        CHAR_ENCODING_LIST.add("ISO_8859_5");
        CHAR_ENCODING_LIST.add("ISO_8859_6");
        CHAR_ENCODING_LIST.add("ISO_8859_7");
        CHAR_ENCODING_LIST.add("ISO_8859_8");
        CHAR_ENCODING_LIST.add("JOHAB");
        CHAR_ENCODING_LIST.add("KOI8R");
        CHAR_ENCODING_LIST.add("KOI8U");
        CHAR_ENCODING_LIST.add("LATIN1");
        CHAR_ENCODING_LIST.add("iso-ir-100");
        CHAR_ENCODING_LIST.add("ISO_8859-1");
        CHAR_ENCODING_LIST.add("ISO-8859-1");
        CHAR_ENCODING_LIST.add("ISO88591");
        CHAR_ENCODING_LIST.add("latin1");
        CHAR_ENCODING_LIST.add("l1");
        CHAR_ENCODING_LIST.add("IBM819");
        CHAR_ENCODING_LIST.add("CP819");
        CHAR_ENCODING_LIST.add("csISOLatin1");
        CHAR_ENCODING_LIST.add("LATIN2");
        CHAR_ENCODING_LIST.add("LATIN3");
        CHAR_ENCODING_LIST.add("LATIN4");
        CHAR_ENCODING_LIST.add("LATIN5");
        CHAR_ENCODING_LIST.add("LATIN6");
        CHAR_ENCODING_LIST.add("LATIN7");
        CHAR_ENCODING_LIST.add("LATIN8");
    }

    /**
     * Check config file.
     *
     * @return true, if successful
     */
    public static boolean checkConfigFile() {
        ConfigCheckHandler configCheck = new ConfigCheckHandler();
        String[] args = Platform.getApplicationArgs();
        String loginTimeout = null;
        if (null != args) {
            int len = args.length;

            for (int i = 0; i < len; i++) {
                if (isLoginTimeout(args, i)) {
                    String[] split = args[i].split("=");
                    if (split.length > 1) {
                        loginTimeout = split[1].trim();
                    } else {
                        return configCheck.handleException();
                    }

                    try {
                        int loginTimeoutInt = Integer.parseInt(loginTimeout);
                        if (loginTimeoutInt < 1) {
                            throw new NumberFormatException(MessageConfigLoader
                                    .getProperty(IMessagesConstants.NEGATIVE_NUMBER, loginTimeoutInt));
                        }
                        break;
                    } catch (NumberFormatException e) {
                        return configCheck.handleException();
                    }
                }
            }
        }

        if (System.getProperty("file.encoding") != null) {

            String encodeValue = null;
            encodeValue = System.getProperty("file.encoding");

            if (CHAR_ENCODING_LIST.contains(encodeValue)) {
                UIVersionHandler.setVersionCompatible(true);
                return true;

            } else {
                addDefaultCharEncoding(encodeValue);
                return true;
            }
        } else if (System.getProperty("file.encoding") == null) {
            handleOnFileEncodingIsNull();
            return true;
        }

        return validateJDKVersion();

    }

    /**
     * Adds the default char encoding.
     *
     * @param encodeValue the encode value
     */
    private static void addDefaultCharEncoding(String encodeValue) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DS_DEFAULT_ENCODING, encodeValue)));

        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.DS_DEFAULT_ENCODING, encodeValue));

        System.setProperty("file.encoding", "UTF8");
        UIVersionHandler.setVersionCompatible(true);
    }

    /**
     * Handle on file encoding is null.
     */
    private static void handleOnFileEncodingIsNull() {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                .getProperty(IMessagesConstants.DS_DEFAULT_ENCODING, System.getProperty("file.encoding"))));

        MPPDBIDELoggerUtility.info(MessageConfigLoader.getProperty(IMessagesConstants.DS_DEFAULT_ENCODING,
                System.getProperty("file.encoding")));

        System.setProperty("file.encoding", "UTF8");
        UIVersionHandler.setVersionCompatible(true);
    }

    /**
     * Validate JDK version.
     *
     * @return true, if successful
     */
    private static boolean validateJDKVersion() {
        if (!checkJDKVersion()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.JDK_VERSION_CHECK_FAILED),
                    MessageConfigLoader.getProperty(IMessagesConstants.IDE_JDK_VERSION_CHECK_FAILED) + ' '
                            + MessageConfigLoader.getProperty(IMessagesConstants.ENTER_VALID_JDK_VERSION));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.IDE_JDK_VERSION_CHECK_FAILED)
                    + ' ' + MessageConfigLoader.getProperty(IMessagesConstants.ENTER_VALID_JDK_VERSION));
            UIVersionHandler.setVersionCompatible(false);
            return false;
        }
        return true;
    }

    /**
     * Checks if is login timeout.
     *
     * @param args the args
     * @param i the i
     * @return true, if is login timeout
     */
    private static boolean isLoginTimeout(String[] args, int i) {
        return args[i] != null && args[i].startsWith("-loginTimeout");
    }

    /**
     * Handle exception.
     *
     * @return true, if successful
     */
    private boolean handleException() {
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.LOGIN_TIMEOUT_CHECKED_FAILED),
                MessageConfigLoader.getProperty(IMessagesConstants.TIMEOUT_VALUE_CHECK_FAILED) + ' '
                        + MessageConfigLoader.getProperty(IMessagesConstants.ENTER_VALID_TIMEOUT_VALUE),
                MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
        UIVersionHandler.setVersionCompatible(false);
        return false;
    }

    /**
     * Check JDK version.
     *
     * @return true, if successful
     */
    private static boolean checkJDKVersion() {
        if ("1.5".equalsIgnoreCase(System.getProperty("osgi.requiredJavaVersion"))
                || "1.6".equalsIgnoreCase(System.getProperty("osgi.requiredJavaVersion"))
                || "1.7".equalsIgnoreCase(System.getProperty("osgi.requiredJavaVersion"))
                || "1.8".equalsIgnoreCase(System.getProperty("osgi.requiredJavaVersion"))) {
            return true;
        }
        return false;
    }

}
