/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import java.util.Arrays;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class PromptPasswordUIWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class PromptPasswordUIWorkerJob extends UIWorkerJob {
    private char[] encrpytedProfilePrd;
    private String passwdValidfailErrorHeader = IMessagesConstants.ERR_PREFIX_DB_MESSAGE;
    private int retVal;

    /**
     * Instantiates a new prompt password UI worker job.
     *
     * @param name the name
     * @param family the family
     * @param errorWindowTitle the error window title
     */
    public PromptPasswordUIWorkerJob(String name, Object family, String errorWindowTitle) {
        super(name, family);
        passwdValidfailErrorHeader = errorWindowTitle;
    }

    /**
     * Checks if is prompt cancelled by user.
     *
     * @return true, if is prompt cancelled by user
     */
    public boolean isPromptCancelledByUser() {
        if (retVal != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        return promptAndValidatePassword();
    }

    /**
     * Prompt and validate password.
     *
     * @return true, if successful
     */
    protected boolean promptAndValidatePassword() {
        Database db = getDatabase();
        DBConnection freeConnection = null;
        if (null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
            PasswordDialog helper = new PasswordDialog(Display.getDefault().getActiveShell(), db);
            boolean isPasswordCorrect = false;
            while (!isPasswordCorrect) {
                retVal = helper.open();
                if (0 != retVal) {
                    return false;
                }

                try {
                    setEncrpytedProfilePrd(db.getServer().getEncrpytedProfilePrd());
                    freeConnection = db.getConnectionManager().getFreeConnection();
                    isPasswordCorrect = true;
                    passwordValidationSuccess();
                } catch (MPPDBIDEException exception) {
                    passwordValidationFailed(exception);
                    isPasswordCorrect = false;
                } finally {
                    if (freeConnection != null) {
                        db.getConnectionManager().releaseConnection(freeConnection);
                    }
                }
            }

            return true;
        }

        return true;
    }

    /**
     * Clear saved password.
     */
    private void clearSavedPassword() {
        /* Clear saved password */
        if (null != encrpytedProfilePrd) {
            for (int i = 0; i < encrpytedProfilePrd.length; i++) {
                encrpytedProfilePrd[i] = 0;
            }
        }

    }

    /**
     * Final cleanup.
     */
    public void finalCleanup() {
        clearSavedPassword();
        cleanServerPwd();
    }

    /**
     * Clean server pwd.
     */
    private void cleanServerPwd() {
        if (null != getDatabase()) {
            getDatabase().getServer().clearPrds();
        }
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    protected abstract Database getDatabase();

    /**
     * Password validation success.
     */
    protected void passwordValidationSuccess() {
        MPPDBIDELoggerUtility.info("connection validated");
    }

    /**
     * Password validation failed.
     *
     * @param exception the e
     */
    protected void passwordValidationFailed(MPPDBIDEException exception) {
        String msg = exception.getServerMessage() != null ? exception.getServerMessage() : exception.getMessage();
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(passwdValidfailErrorHeader), msg);
    }

    /**
     * Gets the encrpyted profile prd.
     *
     * @return the encrpyted profile prd
     */
    public String getEncrpytedProfilePrd() {
        if (null == encrpytedProfilePrd) {
            return null;
        }
        return Arrays.toString(encrpytedProfilePrd);
    }

    /**
     * Sets the encrpyted profile prd.
     *
     * @param encrpytedProfilePrd the new encrpyted profile prd
     */
    private void setEncrpytedProfilePrd(String encrpytedProfilePrd) {
        this.encrpytedProfilePrd = encrpytedProfilePrd.toCharArray();
    }

    /**
     * Sets the server pwd.
     *
     * @param flag the new server pwd
     */
    protected void setServerPwd(boolean flag) {
        if (flag && getEncrpytedProfilePrd() != null && null != getDatabase()) {
            getDatabase().getServer().setPrd(getEncrpytedProfilePrd());
        }
    }
}
