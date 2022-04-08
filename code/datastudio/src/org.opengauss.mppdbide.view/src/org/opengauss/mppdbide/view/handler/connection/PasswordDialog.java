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

package org.opengauss.mppdbide.view.handler.connection;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class PasswordDialog.
 *
 * @since 3.0.0
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
