/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.consts.WHICHOPTION;

/**
 * 
 * Title: class
 * 
 * Description: The Class MPPDBIDEDialogsWithDoNotShowAgain.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class MPPDBIDEDialogsWithDoNotShowAgain extends MPPDBIDEDialogs {

    /**
     * Generate yes no message dialog.
     *
     * @param option the option
     * @param msgDialogType the msg dialog type
     * @param image the image
     * @param modeType the mode type
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @return the int
     */
    public static int generateYesNoMessageDialog(WHICHOPTION option, MESSAGEDIALOGTYPE msgDialogType, Image image,
            boolean modeType, String msgDialogTitle, String message) {
        return generateMessageDialog(option, msgDialogType, modeType, image, msgDialogTitle, message,
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));
    }

    /**
     * Generate message dialog.
     *
     * @param option the option
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param image the image
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @return the int
     */
    public static int generateMessageDialog(WHICHOPTION option, MESSAGEDIALOGTYPE msgDialogType, boolean modeType,
            Image image, String msgDialogTitle, String message, String... messageButtonNames) {
        return generateMessageDialog(option, msgDialogType, modeType, image, msgDialogTitle, message,
                messageButtonNames, 0);
    }

    /**
     * Generate message dialog.
     *
     * @param option the option
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param image the image
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @param defaultIndex the default index
     * @return the int
     */
    public static int generateMessageDialog(WHICHOPTION option, MESSAGEDIALOGTYPE msgDialogType, boolean modeType,
            Image image, String msgDialogTitle, String message, String[] messageButtonNames, int defaultIndex) {
        setResult(0);
        setDialogType(0);
        Shell shell = Display.getDefault().getActiveShell();

        setMsgDialog(MPPDBIDEDialogsWithDoNotShowAgain.createDialogBox(option, msgDialogType, image, msgDialogTitle,
                message, messageButtonNames, defaultIndex, shell));

        return returnResult(modeType);
    }

    /**
     * Creates the dialog box.
     *
     * @param option the option
     * @param msgDialogType the msg dialog type
     * @param image the image
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @param defaultIndex the default index
     * @param shell the shell
     * @return the message dialog
     */
    protected static MessageDialog createDialogBox(WHICHOPTION option, MESSAGEDIALOGTYPE msgDialogType, Image image,
            String msgDialogTitle, String message, String[] messageButtonNames, int defaultIndex, Shell shell) {
        setDialogType(msgDialogType);

        DSMessageDialogWithDoNotShowAgain msgDialog = new DSMessageDialogWithDoNotShowAgain(shell, msgDialogTitle,
                image, message, getDialogType(), messageButtonNames, defaultIndex, option);
        return msgDialog;
    }

}
