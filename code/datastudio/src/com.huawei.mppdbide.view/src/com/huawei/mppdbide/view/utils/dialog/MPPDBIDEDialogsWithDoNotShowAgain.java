/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
 * @since 3.0.0
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
