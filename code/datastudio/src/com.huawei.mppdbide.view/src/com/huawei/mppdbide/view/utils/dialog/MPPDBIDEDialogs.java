/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.errorlocator.IErrorLocator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class MPPDBIDEDialogs.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class MPPDBIDEDialogs {
    private static MessageDialog msgDialog;
    private static int result;
    private static int dialogType;
    private static final Object INSTANCE_LOCK = new Object();

    /**
     * Gets the dialog type.
     *
     * @return the dialog type
     */
    public static int getDialogType() {
        return dialogType;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    protected static int getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param index the new result
     */
    protected static void setResult(int index) {
        result = index;
    }

    /**
     * Sets the dialog type.
     *
     * @param cnt the new dialog type
     */
    protected static void setDialogType(int cnt) {
        dialogType = cnt;
    }

    /**
     * Sets the msg dialog.
     *
     * @param mDialog the new msg dialog
     */
    protected static void setMsgDialog(MessageDialog mDialog) {
        msgDialog = mDialog;
    }

    /**
     * Gets the msg dialog.
     *
     * @return the msg dialog
     */
    public static MessageDialog getMsgDialog() {
        return msgDialog;
    }

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum MESSAGEDIALOGTYPE.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public enum MESSAGEDIALOGTYPE {

        /**
         * The error.
         */
        ERROR,
        /**
         * The information.
         */
        INFORMATION,
        /**
         * The warning.
         */
        WARNING,
        /**
         * The question.
         */
        QUESTION
    }

    /**
     * Generate message dialog.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @return the int
     */
    public static int generateMessageDialog(MESSAGEDIALOGTYPE msgDialogType, boolean modeType, String msgDialogTitle,
            String message, String... messageButtonNames) {
        return generateMessageDialog(msgDialogType, modeType, null, msgDialogTitle, message, messageButtonNames);
    }

    /**
     * Generate message dialog.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param image the image
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @return the int
     */
    public static int generateMessageDialog(MESSAGEDIALOGTYPE msgDialogType, boolean modeType, Image image,
            String msgDialogTitle, String message, String... messageButtonNames) {
        return generateMessageDialog(msgDialogType, modeType, image, msgDialogTitle, message, messageButtonNames, 0);
    }

    /**
     * Generate message dialog.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param image the image
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @param defaultIndex the default index
     * @return the int
     */
    public static int generateMessageDialog(MESSAGEDIALOGTYPE msgDialogType, boolean modeType, Image image,
            String msgDialogTitle, String message, String[] messageButtonNames, int defaultIndex) {
        result = 0;
        dialogType = 0;
        Shell shell = Display.getDefault().getActiveShell();

        msgDialog = createDialogBox(msgDialogType, image, msgDialogTitle, message, messageButtonNames, defaultIndex,
                shell);

        return returnResult(modeType);
    }

    /**
     * Return result.
     *
     * @param modeType the mode type
     * @return the int
     */
    protected static int returnResult(boolean modeType) {
        if (!modeType) {
            msgDialog.setBlockOnOpen(false);
        }

        result = msgDialog.open();

        return result;
    }

    /**
     * Creates the dialog box.
     *
     * @param msgDialogType the msg dialog type
     * @param imageParam the image param
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @param messageButtonNames the message button names
     * @param defaultIndex the default index
     * @param shell the shell
     * @return the message dialog
     */
    protected static MessageDialog createDialogBox(MESSAGEDIALOGTYPE msgDialogType, Image imageParam,
            String msgDialogTitle, String message, String[] messageButtonNames, int defaultIndex, Shell shell) {
        Image image = imageParam;
        setDialogType(msgDialogType);

        if (null == image) {
            image = IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, MPPDBIDEDialogs.class);
        }

        MessageDialog msgDalog = new MessageDialog(shell, msgDialogTitle, image, message, dialogType,
                messageButtonNames, defaultIndex);
        return msgDalog;

    }

    /**
     * Sets the dialog type.
     *
     * @param msgDialogType the new dialog type
     */
    protected static void setDialogType(MESSAGEDIALOGTYPE msgDialogType) {
        switch (msgDialogType) {
            case ERROR: {
                dialogType = MessageDialog.ERROR;
                break;
            }
            case INFORMATION: {
                dialogType = MessageDialog.INFORMATION;
                break;
            }
            case QUESTION: {
                dialogType = MessageDialog.QUESTION;
                break;
            }
            case WARNING: {
                dialogType = MessageDialog.WARNING;
                break;
            }
            default: {
                dialogType = MessageDialog.NONE;
                break;
            }
        }
    }

    /**
     * Generate yes no message dialog.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @return the int
     */
    public static int generateYesNoMessageDialog(MESSAGEDIALOGTYPE msgDialogType, boolean modeType,
            String msgDialogTitle, String message) {
        return generateMessageDialog(msgDialogType, modeType, msgDialogTitle, message,
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));
    }

    /**
     * Generate OK cancel message dialog.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @return the int
     */
    public static int generateOKCancelMessageDialog(MESSAGEDIALOGTYPE msgDialogType, boolean modeType,
            String msgDialogTitle, String message) {
        return generateMessageDialog(msgDialogType, modeType, msgDialogTitle, message,
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));
    }

    /**
     * Generate OK message dialog.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     * @return the int
     */
    public static int generateOKMessageDialog(MESSAGEDIALOGTYPE msgDialogType, boolean modeType, String msgDialogTitle,
            String message) {

        return generateMessageDialog(msgDialogType, modeType, msgDialogTitle, message,
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
    }

    /**
     * Generate OK message dialog in UI.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     */
    public static void generateOKMessageDialogInUI(final MESSAGEDIALOGTYPE msgDialogType, final boolean modeType,
            final String msgDialogTitle, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                synchronized (INSTANCE_LOCK) {
                    generateMessageDialog(msgDialogType, modeType, msgDialogTitle, message,
                            MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
                }

            }
        });
    }

    /**
     * Generate OK message dialog in UI.
     *
     * @param msgDialogType the msg dialog type
     * @param modeType the mode type
     * @param image the image
     * @param msgDialogTitle the msg dialog title
     * @param message the message
     */

    public static void generateOKMessageDialogInUI(final MESSAGEDIALOGTYPE msgDialogType, final boolean modeType,
            final Image image, final String msgDialogTitle, final String message) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                synchronized (INSTANCE_LOCK) {
                    generateMessageDialog(msgDialogType, modeType, image, msgDialogTitle, message,
                            MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
                }

            }
        });
    }

    /**
     * Generate error dialog.
     *
     * @param errorDialogTitle the error dialog title
     * @param message the message
     * @param exception the e
     */
    public static void generateErrorDialog(final String errorDialogTitle, final String message, Exception exception) {

        String pluginId = MPPDBIDEDialogs.class.getCanonicalName();

        // Temp holder of child statuses
        List<Status> childStatuses = new ArrayList<Status>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        String reason = exception.getLocalizedMessage();

        final MultiStatus info = new MultiStatus(pluginId, IStatus.ERROR,
                childStatuses.toArray(new Status[childStatuses.size()]), reason, exception);

        if (null == Display.getCurrent()) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    Shell shell = Display.getDefault().getActiveShell();
                    ErrorDialog.openError(shell, errorDialogTitle, message, info);
                }
            });
        } else {
            ErrorDialog.openError(Display.getCurrent().getActiveShell(), errorDialogTitle, message, info);
        }
    }

    /**
     * Generate DS error dialog.
     *
     * @param errorDialogTitle the error dialog title
     * @param message the message
     * @param errorDetails the error details
     * @param exception the e
     */
    public static void generateDSErrorDialog(final String errorDialogTitle, final String message, String errorDetails,
            Exception exception) {
        String pluginId = MPPDBIDEDialogs.class.getCanonicalName();

        // Temp holder of child statuses
        List<Status> childStatuses = new ArrayList<Status>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        // Details for showing detailed error message
        Status childStatus = new Status(IStatus.WARNING, pluginId, errorDetails);
        childStatuses.add(childStatus);

        MultiStatus info = null;
        if (exception != null) {
            info = new MultiStatus(pluginId, IStatus.WARNING, childStatuses.toArray(new Status[childStatuses.size()]),
                    exception.getLocalizedMessage(), null);
            showErrorDialog(errorDialogTitle, message, info);
        }

        else {
            info = new MultiStatus(pluginId, IStatus.WARNING, childStatuses.toArray(new Status[childStatuses.size()]),
                    message, null);
            showErrorDialog(errorDialogTitle, null, info);
        }
    }

    private static void showErrorDialog(final String errorDialogTitle, final String message, final MultiStatus info) {
        if (null == Display.getCurrent()) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    Shell shell = Display.getDefault().getActiveShell();
                    DSErrorDialog.openDSError(shell, errorDialogTitle, null, info);
                }
            });
        } else {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    DSErrorDialog.openDSError(Display.getDefault().getActiveShell(), errorDialogTitle, message, info);
                }
            });
        }
    }

    /**
     * Close dialog.
     */
    public static void closeDialog() {
        if (msgDialog != null) {
            msgDialog.close();
        }
    }

    /**
     * Clear existing dialog.
     */
    public static void clearExistingDialog() {
        if (msgDialog != null) {
            if (dialogType != MessageDialog.INFORMATION) {
                msgDialog.close();
            }
        }
    }

    /**
     * Generate error popup.
     *
     * @param dialogTitle the dialog title
     * @param shell the shell
     * @param exception the exception
     * @param hintMsg the hint msg
     * @param popUpMsgStr the pop up msg str
     * @param errStr the err str
     * @return the int
     */
    public static int generateErrorPopup(String dialogTitle, Shell shell, Exception exception, String hintMsg,
            String popUpMsgStr, String errStr) {
        int btnPressedValue = 0;
        ShowExportDDLErrorDialog dialog;
        MultiStatus info;

        String pluginId = IErrorLocator.class.getCanonicalName();
        // Temp holder of child statuses
        List<Status> childStatuses = new ArrayList<Status>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        Status childStatus = null;
        // Details for showing detailed error message
        if (errStr != null) {
            childStatus = new Status(IStatus.WARNING, pluginId, errStr + MPPDBIDEConstants.SPACE_CHAR);
        } else if (exception instanceof MPPDBIDEException) {
            childStatus = new Status(IStatus.WARNING, pluginId,
                    ((MPPDBIDEException) exception).getServerMessage() + MPPDBIDEConstants.SPACE_CHAR);
        } else {
            childStatus = new Status(IStatus.WARNING, pluginId,
                    exception.getLocalizedMessage() + MPPDBIDEConstants.SPACE_CHAR);
        }
        childStatuses.add(childStatus);
        info = new MultiStatus(pluginId, IStatus.WARNING, childStatuses.toArray(new Status[childStatuses.size()]),

                popUpMsgStr + MPPDBIDEConstants.SPACE_CHAR
                        + MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILURE_DETAILS_DESCRIPTION)
                        + hintMsg,
                null);

        dialog = new ShowExportDDLErrorDialog(shell, dialogTitle, null, info, IStatus.WARNING);

        btnPressedValue = dialog.open();

        return btnPressedValue;
    }
}
