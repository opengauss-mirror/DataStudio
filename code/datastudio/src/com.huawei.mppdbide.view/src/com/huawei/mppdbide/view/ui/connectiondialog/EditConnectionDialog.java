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

package com.huawei.mppdbide.view.ui.connectiondialog;

import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class EditConnectionDialog.
 *
 * @since 3.0.0
 */
public class EditConnectionDialog extends DBConnectionDialog {
    private Server server;

    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    private IServerConnectionInfo info;

    // Dialog Buttons

    /**
     * Instantiates a new edits the connection dialog.
     *
     * @param parentShell the parent shell
     * @param modelService the model service
     * @param application the application
     * @param commandService the command service
     * @param handlerService the handler service
     */
    public EditConnectionDialog(Shell parentShell, EModelService modelService, MApplication application,
            ECommandService commandService, EHandlerService handlerService) {
        super(parentShell, modelService, application, false);
        this.commandService = commandService;
        this.handlerService = handlerService;
    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_CONNECTION_WIZARD_TITLE));
        shell.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_WND_CONNECTION_WIZARD_001");
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()));
    }

    /**
     * Creates the header.
     *
     * @param formComposite the form composite
     */
    @Override
    protected void createHeader(Composite formComposite) {
        super.createHeader(formComposite);
        lblInfo.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_CONNECTION_WIZARD_TITLE));
        lblSubInfo.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_CONNECTION_SUBTITLE));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Control createContents = super.createContents(parent);
        getDriverCombotext = dbTypeCombo.getText();
        btnRemove.dispose();
        /**
         * Thread has been introduced to update the dialog only after data has
         * been populated through populateConnectionInfoFromPreference() to
         * maintain the consistency of the dialog i.e to restrict the size of
         * the dialog
         */
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                populateConnectionInfoFromPreference(info);
                gaussConnectionName.setEnabled(false);
                dbTypeCombo.setEnabled(false);
                validateData();
            }
        });

        return createContents;
    }

    private void getServerInfo() {
        info = getProfileManager().getProfile(server.getServerConnectionInfo().getConectionName());
    }

    /**
     * Clear fields.
     */
    protected void clearFields() {
        clearAllFields();
    }

    /**
     * Load connections.
     */
    @Override
    protected void loadConnections() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof Server) {
            this.server = (Server) obj;
        }

        getServerInfo();
        int index = 0;
        String[][] values = new String[1][3];
        for (; index < 1; index++) {
            if (info != null && values[index].length >= 3) {
                values[index][0] = info.getConectionName();
                values[index][1] = info.getDsUsername() + '@' + info.getServerIp() + ':' + info.getServerPort() + '/'
                        + info.getDatabaseName();
                values[index][2] = CustomStringUtility.parseServerVersion(info.getDBVersion());
            }
        }
        getViewer().setInput(values);
        getViewer().getTable().setRedraw(true);
        ConnectionProfileManagerImpl.getInstance().clearExceptionList();
    }

    private boolean isProfileParametersChanged() {
        int port = Integer.parseInt(gaussHostPort.getText());
        char[] profilePwd = null;
        try {
            SecureUtil secureUtil = new SecureUtil();
            String path = ConnectionProfileManagerImpl.getInstance().getProfilePath(info);
            secureUtil.setPackagePath(path);
            profilePwd = secureUtil.decryptPrd(server.getEncrpytedProfilePrd());
        } catch (DataStudioSecurityException e) {
            return true;
        }
        boolean isPasswordSame = Arrays.equals(profilePwd, gaussPrd.getText().toCharArray());

        boolean isPwdSaveOptionSame = savePswdOptions
                .getSelectionIndex() == getComboSelectionIndex(info.getSavePrdOption());

        Server exitstsDbConnection = DBConnProfCache.getInstance().getServerByName(gaussConnectionName.getText());

        boolean isProfileParamsChanged = gaussHostAddr.getText().equalsIgnoreCase(info.getServerIp())
                && isDatabaseNameNotChaned() && gaussUserName.getText().equals(info.getDsUsername()) && isPasswordSame
                && isPwdSaveOptionSame;

        if (null != exitstsDbConnection && port == info.getServerPort() && isProfileParamsChanged) {
            return false;
        }
        SecureUtil.clearPassword(profilePwd);
        return true;
    }

    private boolean isDatabaseNameNotChaned() {
        if (null != gaussDbName && gaussDbName.isDisposed()) {
            return true;
        }
        return gaussDbName == null || gaussDbName.getText().equalsIgnoreCase(info.getDatabaseName());
    }

    private boolean isSSLParametersChanged() {
        boolean sslEnableChanged = gaussSSLEnableButton.getSelection() ^ info.isSSLEnabled();

        if (!sslEnableChanged && clSSLCertFilePathText.getText().equalsIgnoreCase(info.getClientSSLCertificate())
                && clSSLKeyFilePathText.getText().equalsIgnoreCase(info.getClientSSLKey())
                && rootCertFilePathText.getText().equalsIgnoreCase(info.getRootCertificate())
                && sslModeOptions.getText().equals(info.getSSLMode())) {
            return false;
        }
        return true;
    }

    /**
     * api to detect any changes in advance tab parameters
     */
    private boolean isAdvanceParamtersChanged() {
        if ("".equals(loadLimit.getText().trim())) {
            loadLimit.setText(DEFAULT_LOAD_LIMIT);
        }

        boolean canLoadChildObjChanged = btnToLoadChildObj.getSelection() == info.canLoadChildObjects();

        boolean isLoadLimitChanged = Integer.parseInt(loadLimit.getText()) == info.getLoadLimit();

        if (canLoadChildObjChanged && isLoadLimitChanged
                && (schemaInclude.getText().equals(convertListtoString(info.getSchemaInclusionList())))
                && (schemaExclude.getText().equals(convertListtoString(info.getSchemaExclusionList())))
                && (enablePrivilege.getSelection() == info.isPrivilegeBasedObAccessEnabled())) {
            return false;
        }

        return true;
    }

    /**
     * Checks if is connection parameters changed.
     *
     * @return true, if is connection parameters changed
     */
    public boolean isConnectionParametersChanged() {
        boolean profileParametersChanged = isProfileParametersChanged();
        boolean sslParametersChanged = isSSLParametersChanged();
        boolean advanceParametersChanged = isAdvanceParamtersChanged();

        if (!profileParametersChanged && !sslParametersChanged && !advanceParametersChanged) {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.INF_NOEDIT_DONE_MSG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.INF_NOEDIT_DONE_MSG_BODY),
                    MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK));
            // Enable the Connection Dialog Window
            formComposite.setEnabled(true);

            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the combo selection index.
     *
     * @param option the option
     * @return the combo selection index
     */
    protected int getComboSelectionIndex(SavePrdOptions option) {
        if (UserPreference.getInstance().getEnablePermanentPasswordSaveOption()) {
            return option.ordinal();
        } else {
            return option.ordinal() - 1;
        }
    }

    /**
     * Check edit connection constraints.
     *
     * @return true, if successful
     */
    @Override
    protected boolean checkEditConnectionConstraints() {
        String succesConst = "SUCCESS";

        if (!isConnectionParametersChanged()) {
            return false;
        }

        Server serverByName = DBConnProfCache.getInstance().getServerByName(server.getName());

        if (serverByName == null) {
            return true;
        }

        Command cmd = commandService.getCommand("com.huawei.mppdbide.command.id.removeserver");
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);

        Object executeHandler = handlerService.executeHandler(parameterizedCmd);
        if (succesConst.equals(executeHandler)) {
            return true;
        }

        return false;
    }

    /**
     * Toggle check progress.
     *
     * @param state the state
     */
    @Override
    protected void toggleCheckProgress(boolean state) {
        if (!isDisposed()) {
            // ARUN
            if (state) {
                closeButton.setText(cancelLabel);
                viewer.removeDoubleClickListener(doubleclickEvent);
                enableDisableGeneralAttributes(false);
                enableDisableAdvancedTabAttributes(false);
                enableDisableSSLTabAttributes(false);
            } else {
                closeButton.setText(closeLabel);
                viewer.addDoubleClickListener(doubleclickEvent);
                enableDisableGeneralAttributes(true);
                enableDisableAdvancedTabAttributes(true);
                enableDisableSSLTabAttributes(gaussSSLEnableButton.getSelection());
            }
        }
        if (!progressBar.isDisposed()) {
            progressBar.setVisible(state);
            container.layout();
        }
    }
}
