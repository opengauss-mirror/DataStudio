/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.huawei.mppdbide.view.cmdline.CmdLineCharObject;
import com.huawei.mppdbide.view.init.IDSCommandlineOptionValidationUtils;
import com.huawei.mppdbide.view.init.IDSCommandlineOptions;
import com.huawei.mppdbide.view.prefernces.KeyBindingWrapper;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.connectiondialog.DBConnectionDialog;
import com.huawei.mppdbide.view.ui.connectiondialog.IPasswordExpiryCallback;
import com.huawei.mppdbide.view.utils.PasswordExpiryNotifier;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class NewDbConnection for commandline argument support.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 20 Nov, 2019]
 * @since 20 Nov, 2019
 */
public class NewDbConnectionCommandline {

    @Optional
    @Inject
    private IWorkbench workbench;
    @Inject
    private EModelService modelService;

    @Inject
    private MApplication application;

    /**
     * Instantiates a new new db connection.
     */
    public NewDbConnectionCommandline() {
    }

    /**
     * handler class for connection received through commandline
     * 
     * @param command : command for which this class is a handler
     */
    @Execute
    public void execute(ParameterizedCommand parameterizedCommand ) {
        Map<String, String> parameterMap = parameterizedCommand.getParameterMap();
        CmdLineCharObject cmdLinePassword = promptForPassword(); 
        boolean isValidationSuccess = validateCmdLinePassword(IDSCommandlineOptions.USER_PASSWORD, cmdLinePassword);
        /*
         * Close Datastudio workbench if connection param validation
         * fails
         */
        if (!isValidationSuccess) {
            cmdLinePassword.clearPssrd();
            workbench.close();
            return;
        }
        DBConnectionDialog connectionDialog = new DBConnectionDialog(Display.getDefault().getActiveShell(),
                modelService, application, true);

        IExecTimer execTimer = new ExecTimer("Initial Connection");
        execTimer.start();

        if (!connectionDialog.checkSSLConstraintsCommandline(parameterMap)) {
            return;
        }
       
        connectionDialog.createConnectionNotifier();
        connectionDialog.newConnectionPressedCommandline(parameterMap, cmdLinePassword);
        try {
            execTimer.stop();
        } catch (DatabaseOperationException excep) {
            MPPDBIDELoggerUtility.error("Error getting time", excep);

        }
        if (PreferenceWrapper.getInstance().isPreferenceValid()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, false,
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_LOAD_MSG));
            
            PreferenceWrapper.getInstance().setPreferenceValid(false);
            return;
        }
        boolean flag = connectionDialog.isFlag();
        if (flag) {
            Job job = getCommandlineConnectionJob(connectionDialog);
            job.schedule();
        }
        if (connectionDialog.isExceptionOccured()) {

            workbench.close();
        }
    }

    private Job getCommandlineConnectionJob(DBConnectionDialog connectionDialog) {
        return new Job("Commandline connection job") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
            try {
                Database database = null;
                while (null == database) {
                    Thread.sleep(100);
                    database = DBConnProfCache.getInstance().getDbForProfileId(connectionDialog.getProfileId());
                }
                PasswordExpiryNotifier passwordExpiryNotifier = new PasswordExpiryNotifier(database);

                boolean isCommandLinePopup = connectionDialog.isOpenExpiryPopup(new IPasswordExpiryCallback() {

                    @Override
                    public void call() {
                        checkAndOpenPasswordExpiryNotifier(connectionDialog, passwordExpiryNotifier);
                    }

                });

                if (!isCommandLinePopup) {
                    checkAndOpenPasswordExpiryNotifier(connectionDialog, passwordExpiryNotifier);
                }
            } catch (InterruptedException exception) {
                MPPDBIDELoggerUtility.error("Connection attempt using commandline parameters interrupted", exception);
            }
                return Status.OK_STATUS;
        }
        };
        }

    private void checkAndOpenPasswordExpiryNotifier(DBConnectionDialog connectionDialog,
            PasswordExpiryNotifier passwordExpiryNotifier) {
        passwordExpiryNotifier.checkAndNotifyPasswordExpiry();
        connectionDialog.setFlag(false);
    }
    
    private CmdLineCharObject promptForPassword() {
        boolean isInstanceRestarted = Boolean.FALSE;
        PreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        if (preferenceStore != null) {
                isInstanceRestarted = preferenceStore.getBoolean("IsRestarted");
        }

        if (isInstanceRestarted && preferenceStore != null) {
            try {
                preferenceStore.setValue("IsRestarted", Boolean.FALSE);
                preferenceStore.save();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("Prefence.save returned exception while saving to disk :", exception);
            }
            System.out.println(MessageConfigLoader
                    .getProperty(IMessagesConstants.DS_COMMANDLINE_PRESS_ENTER_FOLLOWED_BY_PASSWORD));
        } else {
            System.out.println(MessageConfigLoader.getProperty(IMessagesConstants.DS_COMMANDLINE_ENTER_PASSWORD));
        }      
        char[] prdArr = System.console().readPassword();
        CmdLineCharObject lCmdLineCharObject = new CmdLineCharObject();
        if (prdArr != null) {
            lCmdLineCharObject.setPrd(prdArr);  
        }
        
        SecureUtil.clearPassword(prdArr);
        return lCmdLineCharObject;
    }
    
    private boolean validateCmdLinePassword(String key, CmdLineCharObject value) {
        if (isNullChar(value)) {
            printToConsole(IDSCommandlineOptionValidationUtils.formNullValueErrorMsg(key));
            return false;
        } else if (!IDSCommandlineOptionValidationUtils.isCharLengthValid(value)) {
            printToConsole(IDSCommandlineOptionValidationUtils.formInvalidTextLengthErrorMsg(key));
            return false;
        } else {
            return true;
        }
    }
    
    private void printToConsole(String msg) {
            System.err.println(msg);
    }
        
    private boolean isNullChar(CmdLineCharObject value) {
        if (value.getPrd().length == 0) {
            return true;
        }
        return false;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return true;
    }
}
