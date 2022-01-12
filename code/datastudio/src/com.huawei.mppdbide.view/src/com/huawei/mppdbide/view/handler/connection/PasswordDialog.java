/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class PasswordDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PasswordDialog extends UserInputDialog {

    /**
     * Instantiates a new password dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     */
    public PasswordDialog(Shell parent, Object serverObject) {
        super(parent, serverObject);
        setPasswordprompt(true);
    }

    /**
     * Instantiates a new password dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     * @param defaultLabelmsg the default labelmsg
     */
    public PasswordDialog(Shell parent, Object serverObject, String defaultLabelmsg) {
        super(parent, serverObject, defaultLabelmsg);
        setPasswordprompt(true);
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    @Override
    protected String getWindowTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ENTRE_CIPHER);
    }

    /**
     * Gets the header.
     *
     * @return the header
     */
    @Override
    protected String getHeader() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ENTER_CURRENT_USER_CIPHER);
    }

    /**
     * Checks if is password.
     *
     * @return true, if is password
     */
    @Override
    protected boolean isPassword() {
        return true;
    }

    /**
     * Checks if is connect DB.
     *
     * @return true, if is connect DB
     */
    @Override
    protected boolean isConnectDB() {
        return true;
    }

    /**
     * Perform ok operation.
     */
    @Override
    protected void performOkOperation() {
        SecureUtil sec = new SecureUtil();
        Database db = (Database) getObject();
        db.getServer().setSavePrdOption(getSavePswdOption());
        String path = ConnectionProfileManagerImpl.getInstance()
                .getProfilePath(db.getServer().getServerConnectionInfo());
        sec.setPackagePath(path);
        try {
            db.getServer().setPrd(sec.encryptPrd(setServerConnectionInfo()));
        } catch (DataStudioSecurityException exception) {
            MPPDBIDELoggerUtility.error("Error occured while getting encrypted data..", exception);
        }
        close();
    }

    /**
     * Gets the save pswd option.
     *
     * @return the save pswd option
     */
    public int getSavePswdOption() {
        if (UserPreference.getInstance().getEnablePermanentPasswordSaveOption()) {
            return super.getSavePswdOption();
        } else {
            return super.getSavePswdOption() + 1;
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {

    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {

    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException e) {

    }

    /**
     * On presetup failure UI action.
     *
     * @param exception the e
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException exception) {

    }
}
