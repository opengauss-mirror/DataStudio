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

package com.huawei.mppdbide.view.userrole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.UserRoleManager;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.MultiCheckSelectionCombo;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UIMandatoryAttribute;
import com.huawei.mppdbide.view.utils.UIVerifier;
import com.huawei.mppdbide.view.utils.consts.TOOLTIPS;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateUserRole.
 *
 * @since 3.0.0
 */
public class CreateUserRole extends Dialog implements IDialogWorkerInteraction {

    private Server server;

    /**
     * The current shell.
     */
    protected Shell currentShell;

    /**
     * The Constant CREATE_ROLE_GENERAL_INFO.
     */
    protected static final int CREATE_ROLE_GENERAL_INFO = 0;

    /**
     * The Constant CREATE_ROLE_PRIVILEGE_INFO.
     */
    protected static final int CREATE_ROLE_PRIVILEGE_INFO = 1;

    /**
     * The Constant CREATE_ROLE_SQL_PREVIEW.
     */
    protected static final int CREATE_ROLE_SQL_PREVIEW = 2;

    /**
     * The tab folder.
     */
    protected TabFolder tabFolder;

    /**
     * The Constant GENERAL.
     */
    protected static final String GENERAL = MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_MSG);

    /**
     * The Constant PRIVILEGE.
     */
    protected static final String PRIVILEGE = MessageConfigLoader.getProperty(IMessagesConstants.PRIVILEGE_ADVANCED);

    /**
     * The Constant SQL_PREVIEW.
     */
    protected static final String SQL_PREVIEW = MessageConfigLoader.getProperty(IMessagesConstants.SQL_PREVIEW);

    /**
     * The text user role name.
     */
    protected Text textUserRoleName;

    /**
     * The sysadmin.
     */
    protected Button sysadmin;

    /**
     * The audit admin.
     */
    protected Button auditAdmin;

    /**
     * The createdb.
     */
    protected Button createdb;

    /**
     * The createrole.
     */
    protected Button createrole;

    /**
     * The inherit.
     */
    protected Button inherit;

    /**
     * The login.
     */
    protected Button login;

    /**
     * The replication.
     */
    protected Button replication;

    /**
     * The connect limit.
     */
    protected Spinner connectLimit;

    /**
     * The cmb resouce pool name.
     */
    protected Combo cmbResoucePoolName;

    /**
     * The db.
     */
    protected Database db;

    /**
     * The role text.
     */
    protected MultiCheckSelectionCombo roleText;

    /**
     * The admin como.
     */
    protected Text adminComo;

    /**
     * The btn finish.
     */
    protected Button btnFinish;

    /**
     * The btn next.
     */
    protected Button btnNext;

    /**
     * The btn back.
     */
    protected Button btnBack;

    /**
     * The btn cancel
     */
    protected Button btnCancel;

    /**
     * The begin time.
     */
    protected CDateTime beginTime;

    /**
     * The until time.
     */
    protected CDateTime untilTime;

    /**
     * The password input.
     */
    protected Text passwordInput;

    /**
     * The new user role.
     */
    protected UserRole newUserRole;

    /**
     * The sql previewer.
     */
    protected SqlPreviewComponent sqlPreviewer;

    /**
     * The txt error msg.
     */
    protected Text txtErrorMsg;
    private Text passwordInput2;
    private Text txtErrorSslMsg;
    private Text comment;
    private Button all;
    private Text roleCombo;
    private MultiCheckSelectionCombo adminText;
    private Button user;
    private Button role;
    private DBConnection conn;

    /**
     * Instantiates a new creates the user role.
     *
     * @param shell the shell
     * @param server the server
     * @param conn the conn
     * @param db the db
     */
    public CreateUserRole(Shell shell, Server server, DBConnection conn, Database db) {
        super(shell);
        this.server = server;
        this.newUserRole = new UserRole(server);
        this.conn = conn;
        this.db = db;
    }

    /**
     * Open.
     *
     * @return the object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public Object open() throws MPPDBIDEException {
        Shell parent = getParent();

        currentShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        currentShell.setText(getTitleText());
        currentShell.setImage(getWindowImage());
        createRoleGUI(currentShell);

        Monitor monitor = parent.getMonitor();
        Rectangle bounds = monitor.getBounds();
        int monitorHeight = bounds.height;
        // For resolution
        if (monitorHeight <= 600) {
            currentShell.setSize(533, monitorHeight - 50);
        } else {
            currentShell.setSize(getSize());
        }
        Rectangle rect = currentShell.getBounds();
        int xCordination = bounds.x + (bounds.width - rect.width) / 2;
        int yCordination = bounds.y + (bounds.height - rect.height) / 2;
        /* Place the window in the centre of primary monitor */
        currentShell.setLocation(xCordination, yCordination);

        currentShell.open();
        Display display = parent.getDisplay();
        boolean isDisposed = currentShell.isDisposed();
        while (!isDisposed) {
            if (display != null && !display.readAndDispatch()) {
                display.sleep();
            }
            isDisposed = currentShell.isDisposed();
        }

        return currentShell;
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    protected Point getSize() {
        return new Point(533, 612);
    }

    /**
     * Gets the title text.
     *
     * @return the title text
     */
    protected String getTitleText() {
        return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_ROLE);
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {

        return IconUtility.getIconImage(IiconPath.USER_ROLE_CAN_LOGIN, this.getClass());
    }

    /**
     * Creates the role GUI.
     *
     * @param parent the parent
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void createRoleGUI(Composite parent) throws MPPDBIDEException {
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData());

        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite composite = new Composite(mainSc, SWT.NONE);
        mainSc.setContent(composite);
        GridData gdComposite = new GridData(SWT.FILL, SWT.CENTER, true, true);
        composite.setLayoutData(gdComposite);
        composite.setLayout(new GridLayout());

        addTabFolder(composite);
        addTxtErr(composite);
        addControlPannel(composite);

        createGeneralInfoGui();

        createPrivilegeOptionGui();

        createSqlPreviewInfoGui();

        setFocusOnText(CREATE_ROLE_GENERAL_INFO);

        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();
    }

    private void addTxtErr(Composite composite) {
        txtErrorMsg = new Text(composite, SWT.BOLD | SWT.READ_ONLY);
        txtErrorMsg.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_URL_ERROR_MSG_001");
        txtErrorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        txtErrorMsg.setVisible(false);

        txtErrorSslMsg = new Text(composite, SWT.BOLD | SWT.READ_ONLY);
        txtErrorSslMsg.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_URL_ERROR_MSG_002");
        txtErrorSslMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        if (Display.getCurrent() != null) {
            txtErrorSslMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }
        txtErrorSslMsg.setVisible(false);

        if (!server.getServerConnectionInfo().isSSLEnabled()) {
            setErrorSslMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_SSL_NOT_ENABLE));
        }
    }

    /**
     * Creates the sql preview info gui.
     */
    protected void createSqlPreviewInfoGui() {
        /**
         * STEP: 8 SQL PREVIEW
         */
        TabItem tabItemStepSQLPreview = new TabItem(tabFolder, SWT.NONE);
        tabItemStepSQLPreview.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_URL_SQL_PREVIEW_TAB_001");
        tabItemStepSQLPreview.setText(SQL_PREVIEW);
        Composite compositeSqlpreview = new Composite(tabFolder, SWT.NONE);
        GridLayout sqlLayout = new GridLayout();
        sqlLayout.marginTop = 15;
        compositeSqlpreview.setLayout(sqlLayout);
        tabItemStepSQLPreview.setControl(compositeSqlpreview);

        sqlPreviewer = new SqlPreviewComponent(compositeSqlpreview, db.getSqlSyntax());
        sqlPreviewer.getSourceViewer().getTextWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    }

    /**
     * Sets the focus on text.
     *
     * @param curTab the new focus on text
     */
    protected void setFocusOnText(int curTab) {
        switch (curTab) {
            case CREATE_ROLE_GENERAL_INFO: {
                if (textUserRoleName != null) {
                    textUserRoleName.forceFocus();
                }

                break;
            }

            default: {
                break;
            }

        }
    }

    /**
     * Button toggling.
     *
     * @param curTab the cur tab
     */
    protected void buttonToggling(int curTab) {

        if (curTab != -1) {
            if (curTab == CREATE_ROLE_GENERAL_INFO) {
                btnBack.setVisible(false);
            } else {
                btnBack.setVisible(true);
            }

            if (curTab == CREATE_ROLE_SQL_PREVIEW) {
                btnNext.setVisible(false);
            } else {
                btnNext.setVisible(true);
            }
        }

    }

    /**
     * Adds the control pannel.
     *
     * @param composite the composite
     */
    protected void addControlPannel(Composite composite) {

        Composite btnBar = new Composite(composite, SWT.NONE);
        GridLayout btnLayout = new GridLayout(4, true);
        btnLayout.marginTop = 15;
        btnBar.setLayout(btnLayout);
        btnBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        btnNext = new Button(btnBar, SWT.NONE);
        btnNext.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_URL_NEXT_001");
        btnNext.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        btnBack = new Button(btnBar, SWT.NONE);
        btnBack.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_URL_BACK_001");
        btnBack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        btnFinish = new Button(btnBar, SWT.NONE);
        btnFinish.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_URL_FINISH_001");
        btnFinish.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        btnCancel = new Button(btnBar, SWT.NONE);
        btnCancel.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_URL_CANCEL_001");
        btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        btnFinish.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_FINISH_BTN));
        btnNext.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_NEXT_BTN));
        btnBack.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_BACK_BTN));
        btnCancel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_CANCEL_BTN));

        FinishBtnSelectionAdapter finishBtnSelectionAdapter = new FinishBtnSelectionAdapter();

        btnFinish.addSelectionListener(finishBtnSelectionAdapter);

        BtnNxtSelectionAdapter nxtSelectionAdapter = new BtnNxtSelectionAdapter();

        btnNext.addSelectionListener(nxtSelectionAdapter);

        BtnBackSelectionAdapter btnBackSelectionAdapter = new BtnBackSelectionAdapter();

        btnBack.addSelectionListener(btnBackSelectionAdapter);

        BtnCancelSelectionAdapter btnCancelSelectionAdapter = new BtnCancelSelectionAdapter();

        btnCancel.addSelectionListener(btnCancelSelectionAdapter);
    }

    private void addTabFolder(Composite composite) {
        tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABFOLDER_CREATE_URL_TAB_CONTAINER_001");
        tabFolder.addSelectionListener(new TabFolderSelectionAdapter());

        GridLayout tabLayot = new GridLayout();
        tabLayot.marginBottom = 15;
        tabFolder.setLayout(tabLayot);
        GridData tabFolderCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tabFolderCompositeGD.heightHint = 430;
        tabFolder.setLayoutData(tabFolderCompositeGD);

        tabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int curTab = tabFolder.getSelectionIndex();
                buttonToggling(curTab);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent evetn) {

            }
        });
    }

    /**
     * Validate table inputs.
     *
     * @return true, if successful
     */
    protected boolean validateTableInputs() {
        if (isUserRoleNameInValid()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_ENTER_NM));
            return false;
        }
        if (!isUserRoleNamePatternValid()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_NAME_INVALID));
            return false;
        }
        if (!isUserRoleNamelengthValid()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_NAME_EXCEED_MAX));
            return false;
        }
        if (isPasswordInputInValid()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_ENTER_PAS));
            return false;
        }
        if (isPasswordInputNotMatch()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_PAS_NOT_MATCH));
            return false;
        }

        return true;
    }

    /**
     * Title: class
     * Description: The Class FinishBtnSelectionAdapter.
     */
    protected class FinishBtnSelectionAdapter extends SelectionAdapter {

        /**
         * Widget selected.
         *
         * @param event the event
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
            btnFinish.setEnabled(false);
            setErrorMsg("");

            if (!validateTableInputs()) {
                btnFinish.setEnabled(true);
                return;
            }
            btnFinish.setEnabled(true);
            tabFolder.setSelection(tabFolder.getItemCount() - 1);
            updateTableFields(CREATE_ROLE_SQL_PREVIEW);
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(newUserRole.getName(), "1",
                    "2", "3", IMessagesConstants.CREATE_TABLE_PROGRESS_NAME);
            CreateUserRoleWorker worker = new CreateUserRoleWorker(progressLabel, newUserRole,
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_TABLE), CreateUserRole.this);
            worker.schedule();

        }
    }

    /**
     * Title: class
     * Description: The Class BtnCancelSelectionAdapter.
     */
    private class BtnCancelSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            currentShell.dispose();
        }
    }

    /**
     * Title: class
     * Description: The Class BtnBackSelectionAdapter.
     */
    private class BtnBackSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();
            buttonToggling(curTab - 1);
            if (curTab > 0) {
                updateTableFields(curTab - 1);
                tabFolder.setSelection(curTab - 1);
                setFocusOnText(curTab - 1);
            }

        }
    }

    /**
     * Sets the error msg.
     *
     * @param errMsg the new error msg
     */
    public void setErrorMsg(String errMsg) {
        if (null == errMsg || errMsg.trim().isEmpty()) {
            txtErrorMsg.setVisible(false);
        } else {
            txtErrorMsg.setVisible(true);
        }
        txtErrorMsg.setText(errMsg);
    }

    /**
     * Sets the error ssl msg.
     *
     * @param errMsg the new error ssl msg
     */
    public void setErrorSslMsg(String errMsg) {
        if (null == errMsg || errMsg.trim().isEmpty()) {
            txtErrorSslMsg.setVisible(false);
        } else {
            txtErrorSslMsg.setVisible(true);
        }
        txtErrorSslMsg.setText(errMsg);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BtnNxtSelectionAdapter.
     */
    private class BtnNxtSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();
            setErrorMsg("");
            if (!validateTableInputs() && curTab == 0) {
                tabFolder.setSelection(curTab);
                setFocusOnText(curTab);
            } else {
                buttonToggling(curTab + 1);
                if (curTab + 1 != tabFolder.getItemCount()) {
                    updateTableFields(curTab + 1);

                    tabFolder.setSelection(curTab + 1);

                    setFocusOnText(curTab + 1);
                }
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TabFolderSelectionAdapter.
     */
    private class TabFolderSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();

            setFocusOnText(curTab);

            updateTableFields(curTab);
        }

    }

    /**
     * Update table fields.
     *
     * @param event the event
     */
    protected void updateTableFields(int event) {
        if (event == CREATE_ROLE_SQL_PREVIEW) {
            String userRoleName = textUserRoleName.getText();

            this.newUserRole.setName(userRoleName);
            this.newUserRole.setRolSystemAdmin(sysadmin.getSelection());
            this.newUserRole.setAuditAdmin(auditAdmin.getSelection());
            this.newUserRole.setRolCreateDb(createdb.getSelection());
            this.newUserRole.setRolCreateRole(createrole.getSelection());
            this.newUserRole.setRolInherit(inherit.getSelection());
            this.newUserRole.setRolCanLogin(login.getSelection());
            this.newUserRole.setRolReplication(replication.getSelection());
            this.newUserRole.setRolConnLimit(connectLimit.getSelection());
            this.newUserRole.setBeginTime(beginTime.getText());
            this.newUserRole.setUntilTime(untilTime.getText());
            this.newUserRole.setPasswordInput(passwordInput.getTextChars());
            this.newUserRole.setRoleCombo(roleCombo.getText());
            this.newUserRole.setAdminComo(adminComo.getText());
            this.newUserRole.setComment(comment.getText());
            this.newUserRole.setRolResPool(cmbResoucePoolName.getText());
            this.newUserRole.setUser(user.getSelection());
            this.newUserRole.setRole(role.getSelection());

            btnNext.setVisible(false);
            btnBack.setVisible(true);

            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(UserRoleManager.formCreateQuery(newUserRole));
            sb.append(UserRoleManager.formRoleCommentQuery(newUserRole));
            sqlPreviewer.getSourceViewer().getDocument().set((sb.replace(sb.indexOf("PASSWORD") + 10,
                    sb.indexOf("PASSWORD") + 10 + passwordInput.getTextChars().length, "********")).toString());
        }
    }

    /**
     * Hanlde create user role error.
     *
     * @param e1 the e 1
     */
    protected void hanldeCreateUserRoleError(MPPDBIDEException e1) {

        String msg = e1.getServerMessage();
        if (null == msg || "".equals(msg)) {
            msg = e1.getDBErrorMessage();
        }

        if (null != msg && msg.contains("Position:")) {
            msg = msg.split("Position:")[0];
        }

        setErrorMsg(msg);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_USERROLE_CREATE_ERROR, newUserRole.getName())));
    }

    /**
     * Checks if is user role name in valid.
     *
     * @return true, if is user role name in valid
     */
    protected boolean isUserRoleNameInValid() {

        if (null != textUserRoleName && !(textUserRoleName.isDisposed())) {
            return textUserRoleName.getText().trim().length() < 1;
        } else {
            return false;
        }

    }

    /**
     * Checks if is user role name pattern valid.
     *
     * @return true, if is user role name pattern valid
     */
    protected boolean isUserRoleNamePatternValid() {

        if (null != textUserRoleName && !(textUserRoleName.isDisposed())) {
            return textUserRoleName.getText().matches("^[A-Z|a-z|_][A-Z|a-z|0-9|_|$]*$");
        } else {
            return false;
        }

    }

    /**
     * Checks if is user role namelength valid.
     *
     * @return true, if is user role namelength valid
     */
    protected boolean isUserRoleNamelengthValid() {

        if (null != textUserRoleName && !(textUserRoleName.isDisposed())) {
            return textUserRoleName.getText().length() <= 63;
        } else {
            return false;
        }

    }

    /**
     * Checks if is password input in valid.
     *
     * @return true, if is password input in valid
     */
    protected boolean isPasswordInputInValid() {

        if (null != passwordInput && !(passwordInput.isDisposed())) {
            return passwordInput.getText().trim().length() < 1;
        } else {
            return false;
        }

    }

    /**
     * Checks if is password input not match.
     *
     * @return true, if is password input not match
     */
    protected boolean isPasswordInputNotMatch() {

        if (null != passwordInput && null != passwordInput2 && !(passwordInput.isDisposed())
                && !(passwordInput2.isDisposed())
                && !(Arrays.equals(passwordInput.getTextChars(), passwordInput2.getTextChars()))) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Creates the general info gui.
     */
    protected void createGeneralInfoGui() {
        TabItem tbtmStepIndices = new TabItem(tabFolder, SWT.NONE);
        tbtmStepIndices.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_URL_GENERAL_TAB_001");
        tbtmStepIndices.setText(GENERAL);
        Composite compositeGeneral = new Composite(tabFolder, SWT.NONE);
        compositeGeneral.setLayout(new GridLayout());
        tbtmStepIndices.setControl(compositeGeneral);

        addUserOrRoleSelectionBtn(compositeGeneral);

        addUserRolePropertiesUi(compositeGeneral);

        addUserRolePriviledgeUi(compositeGeneral);

        addCommentsUi(compositeGeneral);
    }

    /**
     * Adds the comments ui.
     *
     * @param compositeGeneral the composite general
     */
    private void addCommentsUi(Composite compositeGeneral) {
        Group comOptions = new Group(compositeGeneral, SWT.NONE);
        comOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_COLUMNS_COMMENTS));
        comOptions.setLayout(new GridLayout());
        comOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label commentName = new Label(comOptions, SWT.NONE);
        commentName.setText(MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_COMMENT));

        comment = new Text(comOptions, SWT.BORDER);
        comment.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_URL_TBL_NAME_001");
        comment.setTextLimit(4000);
        comment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    /**
     * Adds the user role priviledge ui.
     *
     * @param compositeGeneral the composite general
     */
    private void addUserRolePriviledgeUi(Composite compositeGeneral) {
        Group grpOptions = new Group(compositeGeneral, SWT.NONE);
        grpOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_PRIVILEGE));
        grpOptions.setLayout(new GridLayout(3, true));
        grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        all = new Button(grpOptions, SWT.CHECK);
        all.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        all.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        all.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_ALL));

        all.addSelectionListener(new AllBtnSelectionListener());

        inherit = new Button(grpOptions, SWT.CHECK);
        inherit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        inherit.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        inherit.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_INHERIT));

        createrole = new Button(grpOptions, SWT.CHECK);
        createrole.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createrole.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        createrole.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_CREATEROLE));

        auditAdmin = new Button(grpOptions, SWT.CHECK);
        auditAdmin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        auditAdmin.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        auditAdmin.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_AUDITADMIN));

        login = new Button(grpOptions, SWT.CHECK);
        login.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        login.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        login.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_LOGIN));

        replication = new Button(grpOptions, SWT.CHECK);
        replication.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        replication.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        replication.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_REPLICATION));

        createdb = new Button(grpOptions, SWT.CHECK);
        createdb.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createdb.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        createdb.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_CREATEDB));

        sysadmin = new Button(grpOptions, SWT.CHECK);
        sysadmin.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sysadmin.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_URL_WITH_OID_001");
        sysadmin.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_SYSADMIN));
    }

    /**
     * Adds the user role properties ui.
     *
     * @param compositeGeneral the composite general
     */
    private void addUserRolePropertiesUi(Composite compositeGeneral) {
        Group grpTableProperties = new Group(compositeGeneral, SWT.NONE);
        grpTableProperties.setText(MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_NM_PAS));
        grpTableProperties.setLayout(new GridLayout(4, false));
        grpTableProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label lblTableName = new Label(grpTableProperties, SWT.LEFT);
        lblTableName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblTableName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_NAME));

        textUserRoleName = new Text(grpTableProperties, SWT.BORDER);
        textUserRoleName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_URL_TBL_NAME_001");
        GridData textUserRoleNameGD = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
        textUserRoleNameGD.horizontalIndent = 5;
        textUserRoleName.setLayoutData(textUserRoleNameGD);
        UIVerifier.verifyTextSize(textUserRoleName, 63);
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        UIMandatoryAttribute.mandatoryField(textUserRoleName, image, TOOLTIPS.ROLENAME_TOOLTIPS);

        Label passwordName = new Label(grpTableProperties, SWT.LEFT);
        passwordName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        passwordName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_PASSWORD));

        int txtProp = SWT.BORDER | SWT.SINGLE | SWT.PASSWORD;

        passwordInput = new Text(grpTableProperties, txtProp);
        GridData passwordInputGD = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
        passwordInputGD.horizontalIndent = 5;
        passwordInput.setLayoutData(passwordInputGD);
        UIVerifier.verifyTextSize(passwordInput, 32);
        Image imagePassword = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        UIMandatoryAttribute.mandatoryField(passwordInput, imagePassword, TOOLTIPS.PASSWORD_TOOLTIPS);

        Label passwordName2 = new Label(grpTableProperties, SWT.LEFT);
        passwordName2.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        passwordName2.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_PASSWORD_TWICE));

        passwordInput2 = new Text(grpTableProperties, txtProp);
        GridData passwordInput2GD = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
        passwordInput2GD.horizontalIndent = 5;
        passwordInput2.setLayoutData(passwordInput2GD);
        UIVerifier.verifyTextSize(passwordInput2, 32);
        Image imagePassword2 = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        UIMandatoryAttribute.mandatoryField(passwordInput2, imagePassword2, TOOLTIPS.PASSWORD_TWICE_TOOLTIPS);  
    }

    /**
     * Adds the user or role selection btn.
     *
     * @param compositeGeneral the composite general
     */
    private void addUserOrRoleSelectionBtn(Composite compositeGeneral) {
        Composite selectComp = new Composite(compositeGeneral, SWT.NONE);
        GridLayout selGrid = new GridLayout(2, false);
        selGrid.marginLeft = 20;
        selGrid.marginTop = 8;
        selGrid.marginBottom = 7;
        selectComp.setLayout(selGrid);

        user = new Button(selectComp, SWT.RADIO);
        user.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USER));
        user.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        user.setSelection(true);
        user.addSelectionListener(new UserBtnSelectionListener());

        role = new Button(selectComp, SWT.RADIO);
        role.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_ROLE));
        role.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));
        role.addSelectionListener(new RoleBtnSelectionListener());
    }

    /**
     * The listener interface for receiving allBtnSelection events. The class
     * that is interested in processing a allBtnSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addAllBtnSelectionListener<code>
     * method. When the allBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * AllBtnSelectionEvent
     */
    private class AllBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent evrnt) {

            if (all.getSelection()) {
                contentEnableDisableOnAllSelection();
            } else {
                contentEnableDisableOnNotSelectingAll();
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * Content enable disable on not selecting all.
     */
    private void contentEnableDisableOnNotSelectingAll() {
        sysadmin.setSelection(false);
        sysadmin.notifyListeners(SWT.Selection, null);
        auditAdmin.setSelection(false);
        auditAdmin.notifyListeners(SWT.Selection, null);
        createdb.setSelection(false);
        createdb.notifyListeners(SWT.Selection, null);
        createrole.setSelection(false);
        createrole.notifyListeners(SWT.Selection, null);
        inherit.setSelection(false);
        inherit.notifyListeners(SWT.Selection, null);
        login.setSelection(false);
        login.notifyListeners(SWT.Selection, null);
        replication.setSelection(false);
        replication.notifyListeners(SWT.Selection, null);
    }

    /**
     * Content enable disable on all selection.
     */
    private void contentEnableDisableOnAllSelection() {
        sysadmin.setSelection(true);
        sysadmin.notifyListeners(SWT.Selection, null);
        auditAdmin.setSelection(true);
        auditAdmin.notifyListeners(SWT.Selection, null);
        createdb.setSelection(true);
        createdb.notifyListeners(SWT.Selection, null);
        createrole.setSelection(true);
        createrole.notifyListeners(SWT.Selection, null);
        inherit.setSelection(true);
        inherit.notifyListeners(SWT.Selection, null);
        if (user.getSelection()) {
            login.setSelection(true);
        }
        login.notifyListeners(SWT.Selection, null);
        replication.setSelection(true);
        replication.notifyListeners(SWT.Selection, null);
    }

    /**
     * The listener interface for receiving roleBtnSelection events. The class
     * that is interested in processing a roleBtnSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addRoleBtnSelectionListener<code>
     * method. When the roleBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * RoleBtnSelectionEvent
     */
    private class RoleBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            login.setEnabled(false);
            login.setSelection(false);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving userBtnSelection events. The class
     * that is interested in processing a userBtnSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addUserBtnSelectionListener<code>
     * method. When the userBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * UserBtnSelectionEvent
     */
    private class UserBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            login.setEnabled(true);
            if (all.getSelection()) {
                login.setSelection(true);
            }
        }

        /**
         * Widget default selected.
         *
         * @param e the e
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * Creates the privilege option gui.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    protected void createPrivilegeOptionGui() throws MPPDBIDEException {

        TabItem tbtmStepColumns = new TabItem(tabFolder, SWT.NONE);
        tbtmStepColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_URL_PRIVILEGE_TAB_001");
        tbtmStepColumns.setText(PRIVILEGE);
        Composite compositePrivilege = new Composite(tabFolder, SWT.NONE);
        GridLayout compGrid = new GridLayout();
        compGrid.marginTop = 10;
        compositePrivilege.setLayout(compGrid);

        tbtmStepColumns.setControl(compositePrivilege);

        Group grpTableProperties = new Group(compositePrivilege, SWT.NONE);
        grpTableProperties.setText(MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_PRIVILEGE_OPTION));
        GridLayout grpLayout = new GridLayout(4, false);
        grpLayout.marginHeight = 15;
        grpTableProperties.setLayout(grpLayout);
        grpTableProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        createTableNameUi(grpTableProperties);

        createConnectionLimitUi(grpTableProperties);

        createValidBeginUi(grpTableProperties);

        createValidUntilUi(grpTableProperties);

        createResourcePoolUi(grpTableProperties);

        createRoleUi(grpTableProperties);

        createAdminui(grpTableProperties);
    }

    /**
     * Creates the adminui.
     *
     * @param grpTableProperties the grp table properties
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void createAdminui(Group grpTableProperties) throws MPPDBIDEException {
        Label adminComboName = new Label(grpTableProperties, SWT.NONE);
        adminComboName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        adminComboName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_ADMIN_GROUP_MEMBER));

        adminComo = new Text(grpTableProperties, SWT.BORDER | SWT.READ_ONLY);
        adminComo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        adminText = new MultiCheckSelectionCombo(grpTableProperties, SWT.ARROW | SWT.DOWN);
        adminText.setItems(getRoleList());
        adminText.addButtonListener();
        adminText.addModifyListener(new AdminTextModifyListener());

    }

    /**
     * Creates the role ui.
     *
     * @param grpTableProperties the grp table properties
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void createRoleUi(Group grpTableProperties) throws MPPDBIDEException {
        Label roleComboName = new Label(grpTableProperties, SWT.NONE);
        roleComboName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        roleComboName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_ROLE_GROUP_MEMBER));

        roleCombo = new Text(grpTableProperties, SWT.BORDER | SWT.READ_ONLY);
        roleCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        roleText = new MultiCheckSelectionCombo(grpTableProperties, SWT.ARROW | SWT.DOWN);
        roleText.setItems(getRoleList());
        roleText.addButtonListener();
        roleText.addModifyListener(new RoleTextModifyListener());
    }

    /**
     * Creates the resource pool ui.
     *
     * @param grpTableProperties the grp table properties
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void createResourcePoolUi(Group grpTableProperties) throws MPPDBIDEException {
        Label resoucePool = new Label(grpTableProperties, SWT.NONE);
        resoucePool.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        resoucePool.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_RES_POOL));

        cmbResoucePoolName = new Combo(grpTableProperties, SWT.NONE | SWT.READ_ONLY);
        cmbResoucePoolName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CREATE_TBL_TBLSPACE_NAME_001");
        cmbResoucePoolName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

        getResoucePoolList(cmbResoucePoolName);
    }

    /**
     * Creates the valid until ui.
     *
     * @param grpTableProperties the grp table properties
     */
    private void createValidUntilUi(Group grpTableProperties) {
        Label validUntil = new Label(grpTableProperties, SWT.NONE);
        validUntil.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        validUntil.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_VALID_UNTIL));

        untilTime = new CDateTime(grpTableProperties, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN | CDT.BORDER);
        untilTime.setPattern("yyyy-MM-dd HH:mm:ss");
        untilTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
    }

    /**
     * Creates the valid begin ui.
     *
     * @param grpTableProperties the grp table properties
     */
    private void createValidBeginUi(Group grpTableProperties) {
        Label validBegin = new Label(grpTableProperties, SWT.NONE);
        validBegin.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        validBegin.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_VALID_BEGIN));

        beginTime = new CDateTime(grpTableProperties, CDT.DATE_SHORT | CDT.TIME_SHORT | CDT.DROP_DOWN | CDT.BORDER);
        beginTime.setPattern("yyyy-MM-dd HH:mm:ss");
        beginTime.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
    }

    /**
     * Creates the connection limit ui.
     *
     * @param grpTableProperties the grp table properties
     */
    private void createConnectionLimitUi(Group grpTableProperties) {
        connectLimit = new Spinner(grpTableProperties, SWT.BORDER);
        connectLimit.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_SPINNER_COLUMNUI_PRECI_SIZE_001");
        connectLimit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        connectLimit.setMinimum(-1);
        connectLimit.setSelection(-1);

        new Label(grpTableProperties, SWT.NONE).setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false, 1, 1));
    }

    /**
     * Creates the table name ui.
     *
     * @param grpTableProperties the grp table properties
     */
    private void createTableNameUi(Group grpTableProperties) {
        Label lblTableName = new Label(grpTableProperties, SWT.NONE);
        lblTableName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblTableName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_USERROLE_CONNECT_LIMIT));
    }

    /**
     * The listener interface for receiving adminTextModify events. The class
     * that is interested in processing a adminTextModify event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addAdminTextModifyListener<code>
     * method. When the adminTextModify event occurs, that object's appropriate
     * method is invoked.
     *
     * AdminTextModifyEvent
     */
    private class AdminTextModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent event) {

            String[] selections = adminText.getSelections();

            StringBuffer roleStrBuf = new StringBuffer();

            for (int index = 0; index < selections.length; index++) {
                roleStrBuf.append(selections[index]).append(",");
            }

            adminComo.setText(roleStrBuf.length() == 0 ? "" : roleStrBuf.substring(0, roleStrBuf.length() - 1));

        }

    }

    /**
     * The listener interface for receiving roleTextModify events. The class
     * that is interested in processing a roleTextModify event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addRoleTextModifyListener<code>
     * method. When the roleTextModify event occurs, that object's appropriate
     * method is invoked.
     *
     * RoleTextModifyEvent
     */
    private class RoleTextModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent event) {

            String[] selections = roleText.getSelections();

            StringBuffer roleStrBuf = new StringBuffer();

            for (int index = 0; index < selections.length; index++) {
                roleStrBuf.append(selections[index]).append(",");
            }

            roleCombo.setText(roleStrBuf.length() == 0 ? "" : roleStrBuf.substring(0, roleStrBuf.length() - 1));

        }
    }

    /**
     * Gets the resouce pool list.
     *
     * @param cmbResoucePoolName2 the cmb resouce pool name 2
     * @return the resouce pool list
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void getResoucePoolList(Combo cmbResoucePoolName2) throws MPPDBIDEException {
        String query = "select distinct respool_name from pg_resource_pool;";

        ResultSet respoolResultSet = this.conn.execSelectAndReturnRs(query);
        boolean successFlag = false;
        try {
            boolean hasNext = respoolResultSet.next();
            while (hasNext) {
                cmbResoucePoolName2.add(respoolResultSet.getString("respool_name"));
                hasNext = respoolResultSet.next();
            }
            successFlag = true;
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            MPPDBIDELoggerUtility.trace(MessageFormat.format("Fetch resource pool in Object Browser {0}",
                    successFlag ? "successfully" : "fail"));

            this.conn.closeResultSet(respoolResultSet);
        }

    }

    /**
     * Gets the role list.
     *
     * @return the role list
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private String[] getRoleList() throws MPPDBIDEException, MPPDBIDEException {
        String query = "select rolname from pg_roles;";
        ArrayList<String> roleList = new ArrayList<String>();

        ResultSet roleResultSet = this.conn.execSelectAndReturnRs(query);
        boolean successFlag = false;
        try {
            boolean hasNext = roleResultSet.next();
            while (hasNext) {
                roleList.add(ServerObject.getQualifiedObjectName(roleResultSet.getString("rolname")));
                hasNext = roleResultSet.next();
            }
            successFlag = true;
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            MPPDBIDELoggerUtility.trace(MessageFormat.format("Fetch resource pool in Object Browser {0}",
                    successFlag ? "successfully" : "fail"));

            this.conn.closeResultSet(roleResultSet);
        }
        return (String[]) roleList.toArray(new String[0]);

    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_USERROLE_CREATE_SUCCESS, newUserRole.getName())));
        UserRoleObjectGroup userRoleObjectGroup = (UserRoleObjectGroup) newUserRole.getParent();
        try {
            userRoleObjectGroup.getServer().refreshUserRoleObjectGroup();
        } catch (MPPDBIDEException mppDbException) {
            MPPDBIDELoggerUtility.error("CreateUserRoleWorkerJob: refresh failed.", mppDbException);
        }

        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();

        if (objectBrowserModel != null) {
            objectBrowserModel.setSelection(newUserRole.getParent());
            objectBrowserModel.refreshObject(newUserRole.getParent());
        }

        if (!currentShell.isDisposed()) {
            currentShell.dispose();
        }

    }

    /**
     * On critical exception UI action.
     *
     * @param dbCriticalException the db critical exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {

        hanldeCreateUserRoleError(dbCriticalException);

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {

        hanldeCreateUserRoleError(dbOperationException);
    }

    /**
     * On presetup failure UI action.
     *
     * @param mppDbException the mpp db exception
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException mppDbException) {

    }

    /**
     * Gets the shell.
     *
     * @return the shell
     */
    @Override
    public Shell getShell() {
        return currentShell;
    }

}
