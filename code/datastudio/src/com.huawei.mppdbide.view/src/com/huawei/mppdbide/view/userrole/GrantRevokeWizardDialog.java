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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.presentation.userrole.GrantRevokeCore;
import com.huawei.mppdbide.presentation.userrole.GrantRevokeParameters;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;
import com.huawei.mppdbide.view.utils.InitListener;
import com.huawei.mppdbide.view.utils.MultiCheckSelectionCombo;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeWizardDialog.
 *
 * @since 3.0.0
 */
public class GrantRevokeWizardDialog extends Dialog implements IDialogWorkerInteraction {
    private GrantRevokeCore grantRevokeCore;

    private MessageQueue messageQueue = new MessageQueue();
    private List<Object> selectedObjects = new ArrayList<>();
    private List<PrivilegeModel> grantPrivilegeOptions = new ArrayList<>();
    private List<PrivilegeModel> revokePrivilegeOptions = new ArrayList<>();
    private Composite grantPrivilegeComposite;
    private Composite revokePrivilegeComposite;

    private Object[] treeElements;

    private TabFolder tabFolder;
    private Button finishButton;
    private Button cancelButton;
    private Button nextButton;
    private Button backButton;
    private Button allButton;
    private Button grantButton;
    private Button revokeButton;
    private Text roleText;
    private MultiCheckSelectionCombo userRoleCombo;
    private Label errorMessage;
    private SourceViewer previewSqlSourceViewer;

    /**
     * The with grant option privileges.
     */
    List<String> withGrantOptionPrivileges = new ArrayList<>();

    /**
     * The without grant option privileges.
     */
    List<String> withoutGrantOptionPrivileges = new ArrayList<>();

    /**
     * The all privilege.
     */
    boolean allPrivilege = false;

    /**
     * The all with grant option.
     */
    boolean allWithGrantOption = false;

    /**
     * The revoke grant privileges.
     */
    List<String> revokeGrantPrivileges = new ArrayList<>();

    /**
     * The revoke privileges.
     */
    List<String> revokePrivileges = new ArrayList<>();

    /**
     * The revoke all privilege.
     */
    boolean revokeAllPrivilege = false;

    /**
     * The revoke all grant privilege.
     */
    boolean revokeAllGrantPrivilege = false;

    /**
     * The preview sqls.
     */
    List<String> previewSqls = new ArrayList<>();
    private Group privilegeSelectionGroup;

    /**
     * Instantiates a new grant revoke wizard dialog.
     *
     * @param shell the shell
     * @param object the object
     * @param isBatch the is batch
     */
    public GrantRevokeWizardDialog(Shell shell, Object object, boolean isBatch) {
        super(shell);
        this.grantRevokeCore = new GrantRevokeCore(isBatch, object);
        this.treeElements = grantRevokeCore.getObjectOption();
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite mainComposite = (Composite) super.createDialogArea(mainSc);
        mainSc.setContent(mainComposite);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        mainComposite.setLayoutData(mainCompositeGD);

        Composite folderComp = new Composite(mainComposite, SWT.NONE);
        GridLayout folderCompGL = new GridLayout(1, false);
        folderCompGL.marginBottom = -5;
        folderComp.setLayout(folderCompGL);
        GridData folderCompGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        folderCompGD.heightHint = 340;
        folderComp.setLayoutData(folderCompGD);

        tabFolder = new TabFolder(folderComp, SWT.NONE);
        tabFolder.setLayout(new GridLayout(1, false));
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createObjectSelectionTab();
        createPrivilegeSelectionTab();
        createSqlPreviewTab();

        Composite errComp = new Composite(mainComposite, SWT.NONE);
        GridLayout errCompGL = new GridLayout(1, false);
        errCompGL.marginHeight = 0;
        errComp.setLayout(errCompGL);
        GridData errCompGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        errComp.setLayoutData(errCompGD);

        errorMessage = new Label(errComp, SWT.READ_ONLY | SWT.WRAP);
        GridData errorMessageGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        errorMessage.setLayoutData(errorMessageGD);
        errorMessage.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

        tabFolder.addSelectionListener(tabFolderListener());

        // default to select all objects when window is opened
        allButton.setSelection(true);
        allButton.notifyListeners(SWT.Selection, null);

        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();

        return mainComposite;
    }

    private SelectionAdapter tabFolderListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                TabFolder source = (TabFolder) event.getSource();
                boolean isValid = checkValidation();

                if (source.getSelectionIndex() == 0) {
                    nextButton.setEnabled(true);
                    backButton.setEnabled(false);
                    cancelButton.setEnabled(true);
                    finishButton.setEnabled(false);
                }

                if (source.getSelectionIndex() == 1) {
                    nextButton.setEnabled(true);
                    backButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    finishButton.setEnabled(false);
                }

                if (source.getSelectionIndex() == 2) {
                    previewSqls.clear();
                    previewSqlSourceViewer.getDocument().set("");
                    nextButton.setEnabled(false);
                    backButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    finishButton.setEnabled(true);

                    if (isValid) {
                        analyzePrivilegeSelecionStatus();
                        generatePriviewSql();
                        StringBuffer strBuf = new StringBuffer();
                        previewSqls.stream().forEach(
                                previewSql -> strBuf.append(previewSql).append(MPPDBIDEConstants.NEW_LINE_SIGN));
                        previewSqlSourceViewer.getDocument().set(strBuf.toString());
                    }
                }
            }
        };
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_WIZARD));
        newShellWindow.setSize(630, 440);
        newShellWindow.setImage(IconUtility.getIconImage(IiconPath.ICO_USER_NAMESPACE_GROUP, this.getClass()));
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        GridLayout buttonsGL = new GridLayout(1, false);
        buttonsGL.marginTop = 0;
        parent.setLayout(buttonsGL);

        backButton = createButton(parent, 100,
                MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_BACK_BTN), false);
        backButton.setEnabled(false);
        backButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                tabFolder.setSelection(tabFolder.getSelectionIndex() - 1);
                tabFolder.notifyListeners(SWT.Selection, null);
            }
        });

        nextButton = createButton(parent, 99, MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NEXT_BTN),
                false);
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                tabFolder.setSelection(tabFolder.getSelectionIndex() + 1);
                tabFolder.notifyListeners(SWT.Selection, null);
            }
        });

        cancelButton = createButton(parent, CANCEL, MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL),
                false);

        finishButton = createButton(parent, OK,
                MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_FINISH_BTN), false);
        finishButton.setEnabled(false);
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        finishButton.setEnabled(false);
        cancelButton.setEnabled(false);
        backButton.setEnabled(false);

        if (!checkValidation()) {
            finishButton.setEnabled(true);
            cancelButton.setEnabled(true);
            backButton.setEnabled(true);
            return;
        }

        generatePriviewSql();

        // clear messageQueue
        if (!messageQueue.isEmpty()) {
            for (int i = 0; i < messageQueue.size(); i++) {
                messageQueue.pop();
            }
        }

        ObjectPrivilegeModificationUIWorkerJob worker = new ObjectPrivilegeModificationUIWorkerJob(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_MODIFYING_OBJECT_PRIVILEGE),
                grantRevokeCore.getDatabase(),
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_MODIFYING_OBJECT_PRIVILEGE), this,
                grantRevokeCore, previewSqls, messageQueue);
        worker.schedule();
    }

    /**
     * Creates the object selection tab.
     */
    private void createObjectSelectionTab() {
        TabItem objectSelectionTabItem = new TabItem(tabFolder, SWT.NONE);
        objectSelectionTabItem
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_WIZARD_OBJECT_SELECTION));

        final ScrolledComposite objectSelectionSC = new ScrolledComposite(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
        objectSelectionSC.setLayout(new GridLayout(1, false));
        objectSelectionSC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite objectSelectioncomp = new Composite(objectSelectionSC, SWT.NONE);
        objectSelectioncomp.setLayout(new GridLayout(1, false));
        objectSelectioncomp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        objectSelectionSC.setContent(objectSelectioncomp);

        Group objectSelectionGroup = new Group(objectSelectioncomp, SWT.NONE);

        objectSelectionGroup.setLayout(new GridLayout(1, false));
        objectSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        allButton = new Button(objectSelectionGroup, SWT.CHECK);
        allButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_SELECT_ALL_OBJECT));
        allButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label separatorLabel = new Label(objectSelectionGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        PatternFilter patternFilter = new PatternFilter();
        FilteredTree filteredTree = new FilteredTree(objectSelectionGroup,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CHECK, patternFilter, true);
        TreeViewer treeViewer = filteredTree.getViewer();
        treeViewer.setContentProvider(new NoLeafTreeViewerContentProvider());
        treeViewer.setLabelProvider(new NoLeafTreeViewerLabelProvider());

        Text searchBar = filteredTree.getFilterControl();
        searchBar.addListener(SWT.MenuDetect, new InitListener());
        searchBar.setMessage(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_SEARCH_OBJECT));
        filteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        treeViewer.setInput(treeElements);

        // dynamic search
        searchBar.addModifyListener(searchBarModifyListener(patternFilter, treeViewer));

        // maintain selectedObjectNames
        treeViewer.getTree().addListener(SWT.Selection, treeViewerSelectionListener());

        // handle select/unselect all behavior
        allButton.addSelectionListener(allBtnSelectionListener(treeViewer));
        objectSelectionSC.setExpandHorizontal(true);
        objectSelectionSC.setExpandVertical(true);
        objectSelectionSC.setMinSize(objectSelectioncomp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        objectSelectionSC.pack();
        objectSelectionTabItem.setControl(objectSelectionSC);
    }

    /**
     * All btn selection listener.
     *
     * @param treeViewer the tree viewer
     * @return the selection adapter
     */
    private SelectionAdapter allBtnSelectionListener(TreeViewer treeViewer) {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.getSource();
                if (btn.getSelection()) {
                    selectedObjects.clear();
                    Arrays.asList(treeElements).stream().forEach(treeElement -> selectedObjects.add(treeElement));
                    for (TreeItem treeItem : treeViewer.getTree().getItems()) {
                        checkAll(treeItem);
                    }
                } else {
                    selectedObjects.clear();
                    for (TreeItem treeItem : treeViewer.getTree().getItems()) {
                        unCheckAll(treeItem);
                    }
                }
                treeViewer.refresh();

                checkValidation();
            }
        };
    }

    /**
     * Tree viewer selection listener.
     *
     * @return the listener
     */
    private Listener treeViewerSelectionListener() {
        return new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.CHECK) {
                    TreeItem treeItem = (TreeItem) event.item;
                    if (treeItem.getChecked()) {
                        selectedObjects.add(treeItem.getData());
                    } else {
                        selectedObjects.remove(treeItem.getData());
                        allButton.setSelection(false);
                    }
                    checkValidation();
                }
            }
        };
    }

    /**
     * Search bar modify listener.
     *
     * @param patternFilter the pattern filter
     * @param treeViewer the tree viewer
     * @return the modify listener
     */
    private ModifyListener searchBarModifyListener(PatternFilter patternFilter, TreeViewer treeViewer) {
        return new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent event) {
                Text text = (Text) event.widget;
                patternFilter.setPattern(text.getText());
                treeViewer.refresh();
                for (TreeItem treeItem : treeViewer.getTree().getItems()) {
                    recoverChecked(treeItem);
                }
            }
        };
    }

    /**
     * Creates the privilege selection tab.
     */
    private void createPrivilegeSelectionTab() {
        TabItem privilegeSelectionTabItem = new TabItem(tabFolder, SWT.NONE);
        privilegeSelectionTabItem
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_WIZARD_PRIVILEGE_SELECTION));

        final ScrolledComposite privilegeSelectionSC = new ScrolledComposite(tabFolder, SWT.V_SCROLL | SWT.H_SCROLL);
        privilegeSelectionSC.setLayout(new GridLayout(1, false));
        privilegeSelectionSC.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite privilegeSelectionComp = new Composite(privilegeSelectionSC, SWT.NONE);
        privilegeSelectionComp.setLayout(new GridLayout(1, false));
        privilegeSelectionComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        privilegeSelectionSC.setContent(privilegeSelectionComp);

        privilegeSelectionGroup = new Group(privilegeSelectionComp, SWT.NONE);
        privilegeSelectionGroup.setLayout(new GridLayout(1, false));
        privilegeSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        Composite roleGrpInfo = new Composite(privilegeSelectionGroup, SWT.NONE);
        roleGrpInfo.setLayout(new GridLayout(3, false));
        roleGrpInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        Label roleLabel = new Label(roleGrpInfo, SWT.NONE);
        roleLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_ROLE));
        roleText = new Text(roleGrpInfo, SWT.BORDER | SWT.READ_ONLY);
        roleText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        roleText.addModifyListener(new RoleTextModifyListener());
        roleText.addListener(SWT.MenuDetect, new InitListener());
        addUserRoleCombo(roleGrpInfo);

        addGrantRevokeBtns(privilegeSelectionGroup);

        // create grant privilege part
        addGrantPrivilegeComposite();

        revokeButton.addSelectionListener(new RevokeBtnSelectionListener());

        privilegeSelectionSC.setExpandHorizontal(true);
        privilegeSelectionSC.setExpandVertical(true);
        privilegeSelectionSC.setMinSize(privilegeSelectionComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        privilegeSelectionSC.pack();
        privilegeSelectionTabItem.setControl(privilegeSelectionSC);
    }

    private void addRevokePrivilegeComposite() {
        if (revokePrivilegeComposite == null || revokePrivilegeComposite.isDisposed()) {
            revokePrivilegeComposite = new Composite(privilegeSelectionGroup, SWT.NONE);
            revokePrivilegeComposite.setLayout(new GridLayout(2, false));
            revokePrivilegeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            createRevokePrivilegeComponent(revokePrivilegeComposite);
            revokePrivilegeComposite.setVisible(true);
        }
    }

    private void addGrantPrivilegeComposite() {
        if (grantPrivilegeComposite == null || grantPrivilegeComposite.isDisposed()) {
            grantPrivilegeComposite = new Composite(privilegeSelectionGroup, SWT.NONE);
            grantPrivilegeComposite.setLayout(new GridLayout(2, false));
            grantPrivilegeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            createGrantPrivilegeComponent(grantPrivilegeComposite);
            grantPrivilegeComposite.setVisible(true);
        }
    }

    /**
     * Adds the grant revoke btns.
     *
     * @param privilegeSelectionGroup the privilege selection group
     */
    private void addGrantRevokeBtns(Group privilegeSelectionGroup) {
        Composite grantRevokeComp = new Composite(privilegeSelectionGroup, SWT.NONE);
        grantRevokeComp.setLayout(new GridLayout(2, false));

        grantButton = new Button(grantRevokeComp, SWT.RADIO);
        grantButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_GRANT_OPTION));
        grantButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        grantButton.setSelection(true);
        revokeButton = new Button(grantRevokeComp, SWT.RADIO);
        revokeButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_REVOKE_OPTION));
        revokeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    /**
     * Adds the user role combo.
     *
     * @param privilegeSelectionGroup the privilege selection group
     */
    private void addUserRoleCombo(Composite roleGrpInfo) {
        List<String> userRoleNames = new ArrayList<>();
        userRoleNames.add(MPPDBIDEConstants.PRIVILEGE_GRANTEE_PUBLIC);
        userRoleNames.addAll(grantRevokeCore.getUserRoleOption());

        userRoleCombo = new MultiCheckSelectionCombo(roleGrpInfo, SWT.ARROW | SWT.DOWN);
        userRoleCombo.setItems(userRoleNames.toArray(new String[0]));
        userRoleCombo.addButtonListener();
        userRoleCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        userRoleCombo.addModifyListener(new UserRoleModifyListener());
    }

    /**
     * The listener interface for receiving revokeBtnSelection events. The class
     * that is interested in processing a revokeBtnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addRevokeBtnSelectionListener<code> method. When the
     * revokeBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * RevokeBtnSelectionEvent
     */
    private class RevokeBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            Button button = (Button) event.getSource();
            if (button.getSelection()) {
                if (null != grantPrivilegeComposite) {
                    grantPrivilegeComposite.dispose();
                    grantPrivilegeOptions.clear();
                    addRevokePrivilegeComposite();
                }

            } else {
                if (null != revokePrivilegeComposite) {
                    revokePrivilegeComposite.dispose();
                    revokePrivilegeOptions.clear();
                    addGrantPrivilegeComposite();
                }
            }

            privilegeSelectionGroup.layout();
            privilegeSelectionGroup.redraw();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving userRoleModify events. The class
     * that is interested in processing a userRoleModify event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addUserRoleModifyListener<code>
     * method. When the userRoleModify event occurs, that object's appropriate
     * method is invoked.
     *
     * UserRoleModifyEvent
     */
    private class UserRoleModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            String[] selections = userRoleCombo.getSelections();
            StringBuffer roleStrBuf = new StringBuffer();
            for (int index = 0; index < selections.length; index++) {
                roleStrBuf.append(selections[index]).append(",");
            }
            roleText.setText(roleStrBuf.length() == 0 ? "" : roleStrBuf.substring(0, roleStrBuf.length() - 1));
        }
    }

    /**
     * Creates the grant privilege component.
     *
     * @param composite the composite
     */
    private void createGrantPrivilegeComponent(Composite composite) {
        String[] supportPrivileges = addSupportPrivileges();

        for (int index = 0; index < supportPrivileges.length; index++) {
            String privilegeName = supportPrivileges[index];

            Button privilegeButton = new Button(composite, SWT.CHECK);
            privilegeButton.setText(privilegeName);

            Button withGrantOptionButton = new Button(composite, SWT.CHECK);
            withGrantOptionButton.setText(MPPDBIDEConstants.PRIVILEGE_WITH_GRANT_OPTION);
            withGrantOptionButton.setEnabled(false);

            privilegeButton
                    .addSelectionListener(new PrivilegeBtnSelectionListener1(privilegeName, withGrantOptionButton));

            withGrantOptionButton.addSelectionListener(new WithGrantOptionBtnSelectionListener(privilegeName));

            grantPrivilegeOptions.add(new PrivilegeModel(privilegeName, privilegeButton, withGrantOptionButton));
        }
    }

    /**
     * The listener interface for receiving withGrantOptionBtnSelection events.
     * The class that is interested in processing a withGrantOptionBtnSelection
     * event implements this interface, and the object created with that class
     * is registered with a component using the component's
     * <code>addWithGrantOptionBtnSelectionListener<code> method. When the
     * withGrantOptionBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * WithGrantOptionBtnSelectionEvent
     */
    private class WithGrantOptionBtnSelectionListener implements SelectionListener {
        private String privilegeName;

        /**
         * Instantiates a new with grant option btn selection listener.
         *
         * @param privilegeName the privilege name
         */
        public WithGrantOptionBtnSelectionListener(String privilegeName) {
            this.privilegeName = privilegeName;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            Button button = (Button) event.getSource();
            if (button.getSelection()) {
                addOnWithGrantOptionSelection();
            } else {
                addOnWithoutGrantOptionSelection();
            }
        }

        /**
         * Adds the on without grant option selection.
         */
        private void addOnWithoutGrantOptionSelection() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                grantPrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (!MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getGrantOptionButton().setSelection(false);
                    }
                });
            } else {
                grantPrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getGrantOptionButton().setSelection(false);
                    }
                });
            }
        }

        /**
         * Adds the on with grant option selection.
         */
        private void addOnWithGrantOptionSelection() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                grantPrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (!MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getGrantOptionButton().setSelection(true);
                    }
                });
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PrivilegeBtnSelectionListener1.
     */
    private class PrivilegeBtnSelectionListener1 implements SelectionListener {
        private String privilegeName;
        private Button withGrantOptionButton;

        /**
         * Instantiates a new privilege btn selection listener 1.
         *
         * @param privilegeName the privilege name
         * @param withGrantOptionButton the with grant option button
         */
        public PrivilegeBtnSelectionListener1(String privilegeName, Button withGrantOptionButton) {
            this.privilegeName = privilegeName;
            this.withGrantOptionButton = withGrantOptionButton;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            Button button = (Button) event.getSource();
            if (button.getSelection()) {
                addOnPrivilegeOptionSelection();
            } else {
                addOnPrivilegeBtnNonSelection();
            }
            checkValidation();
        }

        /**
         * Adds the on privilege btn non selection.
         */
        private void addOnPrivilegeBtnNonSelection() {
            withGrantOptionButton.setSelection(false);
            withGrantOptionButton.setEnabled(false);
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                grantPrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (!MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getPrivilegeButton().setSelection(false);
                        privilegeModel.getPrivilegeButton().notifyListeners(SWT.Selection, null);
                    }
                });
            } else {
                grantPrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getPrivilegeButton().setSelection(false);
                        privilegeModel.getGrantOptionButton().setSelection(false);
                        privilegeModel.getGrantOptionButton().setEnabled(false);
                    }
                });
            }
        }

        /**
         * Adds the on privilege option selection.
         */
        private void addOnPrivilegeOptionSelection() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                grantPrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (!MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getPrivilegeButton().setSelection(true);
                        privilegeModel.getPrivilegeButton().notifyListeners(SWT.Selection, null);
                    }
                });
            }
            withGrantOptionButton.setEnabled(true);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Creates the revoke privilege component.
     *
     * @param composite the composite
     */
    private void createRevokePrivilegeComponent(Composite composite) {

        String[] supportPrivileges = addSupportPrivileges();

        for (int index = 0; index < supportPrivileges.length; index++) {
            String privilegeName = supportPrivileges[index];

            Button privilegeButton = new Button(composite, SWT.CHECK);
            privilegeButton.setText(privilegeName);

            Button grantOptionForButton = new Button(composite, SWT.CHECK);
            grantOptionForButton.setText(MPPDBIDEConstants.PRIVILEGE_GRANT_OPTION_FOR);

            privilegeButton.addSelectionListener(new PrivilegeBtnSelectionListener(privilegeName));

            grantOptionForButton.addSelectionListener(new GrantOptionBtnSelectionListener(privilegeName));

            revokePrivilegeOptions.add(new PrivilegeModel(privilegeName, privilegeButton, grantOptionForButton));
        }
    }

    /**
     * The listener interface for receiving grantOptionBtnSelection events. The
     * class that is interested in processing a grantOptionBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addGrantOptionBtnSelectionListener<code> method. When the
     * grantOptionBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * GrantOptionBtnSelectionEvent
     */
    private class GrantOptionBtnSelectionListener implements SelectionListener {
        private String privilegeName;

        /**
         * Instantiates a new grant option btn selection listener.
         *
         * @param privilegeName the privilege name
         */
        public GrantOptionBtnSelectionListener(String privilegeName) {
            this.privilegeName = privilegeName;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            Button button = (Button) event.getSource();
            if (button.getSelection()) {
                addPrivilegeSelectionOnGrantBtnSelection();
            } else {
                addPrivilegeOnNoGrantSelecton();
            }
            checkValidation();
        }

        /**
         * Adds the privilege on no grant selecton.
         */
        private void addPrivilegeOnNoGrantSelecton() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (!MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getGrantOptionButton().setSelection(false);
                    }
                });
            } else {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getGrantOptionButton().setSelection(false);
                    }
                });
            }
        }

        /**
         * Adds the privilege selection on grant btn selection.
         */
        private void addPrivilegeSelectionOnGrantBtnSelection() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    privilegeModel.getGrantOptionButton().setSelection(true);
                    privilegeModel.getPrivilegeButton().setSelection(false);
                });
            } else {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (privilegeModel.getPrivilegeName().equals(privilegeName)) {
                        privilegeModel.getPrivilegeButton().setSelection(false);
                        privilegeModel.getPrivilegeButton().notifyListeners(SWT.Selection, null);
                    }
                });
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving privilegeBtnSelection events. The
     * class that is interested in processing a privilegeBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addPrivilegeBtnSelectionListener<code> method. When the
     * privilegeBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * PrivilegeBtnSelectionEvent
     */
    private class PrivilegeBtnSelectionListener implements SelectionListener {
        private String privilegeName;

        /**
         * Instantiates a new privilege btn selection listener.
         *
         * @param privilegeName the privilege name
         */
        public PrivilegeBtnSelectionListener(String privilegeName) {
            this.privilegeName = privilegeName;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            Button button = (Button) event.getSource();
            if (button.getSelection()) {
                addPrivilegeOnPrivilegeBtnSelection();
            } else {
                addPrivilegeOnNoPrivBtnSelection();
            }
            checkValidation();
        }

        /**
         * Adds the privilege on no priv btn selection.
         */
        private void addPrivilegeOnNoPrivBtnSelection() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (!MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getPrivilegeButton().setSelection(false);
                    }
                });
            } else {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        privilegeModel.getPrivilegeButton().setSelection(false);
                    }
                });
            }
        }

        /**
         * Adds the privilege on privilege btn selection.
         */
        private void addPrivilegeOnPrivilegeBtnSelection() {
            if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeName)) {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    privilegeModel.getPrivilegeButton().setSelection(true);
                    privilegeModel.getGrantOptionButton().setSelection(false);
                });
            } else {
                revokePrivilegeOptions.stream().forEach(privilegeModel -> {
                    if (privilegeModel.getPrivilegeName().equals(privilegeName)) {
                        privilegeModel.getGrantOptionButton().setSelection(false);
                        privilegeModel.getGrantOptionButton().notifyListeners(SWT.Selection, null);
                    }
                });
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Adds the support privileges.
     *
     * @return the string[]
     */
    private String[] addSupportPrivileges() {
        String[] supportPrivileges = new String[] {};
        if (treeElements.length > 0) {
            if (treeElements[0] instanceof TableMetaData || treeElements[0] instanceof ViewMetaData) {
                supportPrivileges = new String[] {MPPDBIDEConstants.PRIVILEGE_ALL, MPPDBIDEConstants.PRIVILEGE_INSERT,
                    MPPDBIDEConstants.PRIVILEGE_SELECT, MPPDBIDEConstants.PRIVILEGE_UPDATE,
                    MPPDBIDEConstants.PRIVILEGE_DELETE, MPPDBIDEConstants.PRIVILEGE_TRUNCATE,
                    MPPDBIDEConstants.PRIVILEGE_REFERENCES};
            } else if (treeElements[0] instanceof DebugObjects) {
                supportPrivileges = new String[] {MPPDBIDEConstants.PRIVILEGE_EXECUTE};
            } else if (treeElements[0] instanceof SequenceMetadata) {
                supportPrivileges = new String[] {MPPDBIDEConstants.PRIVILEGE_ALL, MPPDBIDEConstants.PRIVILEGE_USAGE,
                    MPPDBIDEConstants.PRIVILEGE_SELECT, MPPDBIDEConstants.PRIVILEGE_UPDATE};
            } else if (treeElements[0] instanceof Namespace) {
                supportPrivileges = new String[] {MPPDBIDEConstants.PRIVILEGE_ALL, MPPDBIDEConstants.PRIVILEGE_USAGE,
                    MPPDBIDEConstants.PRIVILEGE_CREATE};
            }
        }
        return supportPrivileges;
    }

    /**
     * Creates the sql preview tab.
     */
    private void createSqlPreviewTab() {
        TabItem sqlPreviewTabItem = new TabItem(tabFolder, SWT.NONE);
        sqlPreviewTabItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_WIZARD_SQL_PREVIEW));

        Composite sqlPreviewComposite = new Composite(tabFolder, SWT.NONE);
        sqlPreviewComposite.setLayout(new GridLayout());
        sqlPreviewComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sqlPreviewTabItem.setControl(sqlPreviewComposite);

        SqlPreviewComponent sqlPreviewComponent = new SqlPreviewComponent(sqlPreviewComposite,
                grantRevokeCore.getDatabase() == null ? null : grantRevokeCore.getDatabase().getSqlSyntax());
        previewSqlSourceViewer = sqlPreviewComponent.getSourceViewer();
        previewSqlSourceViewer.getTextWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    /**
     * Generate priview sql.
     */
    private void generatePriviewSql() {
        previewSqls.clear();

        GrantRevokeParameters params = new GrantRevokeParameters();
        params.setSelectedObjects(selectedObjects);
        params.setUserRolesStr(roleText.getText());
        params.setWithGrantOptionPrivileges(withGrantOptionPrivileges);
        params.setWithoutGrantOptionPrivileges(withoutGrantOptionPrivileges);
        params.setAllPrivilege(allPrivilege);
        params.setAllWithGrantOption(allWithGrantOption);
        params.setRevokePrivileges(revokePrivileges);
        params.setRevokeGrantPrivileges(revokeGrantPrivileges);
        params.setRevokeAllPrivilege(revokeAllPrivilege);
        params.setRevokeAllGrantPrivilege(revokeAllGrantPrivilege);
        params.setGrant(grantButton.getSelection());

        this.previewSqls = grantRevokeCore.generateSql(params);
    }

    /**
     * Analyze privilege selecion status.
     */
    private void analyzePrivilegeSelecionStatus() {
        // rollback to original value
        withGrantOptionPrivileges.clear();
        withoutGrantOptionPrivileges.clear();
        allPrivilege = false;
        allWithGrantOption = false;
        revokePrivileges.clear();
        revokeGrantPrivileges.clear();
        revokeAllPrivilege = false;
        revokeAllGrantPrivilege = false;

        if (grantButton.getSelection()) {
            for (PrivilegeModel privilegeModel : grantPrivilegeOptions) {
                if (!privilegeModel.getPrivilegeButton().getSelection()) {
                    continue;
                }

                if (privilegeModel.getGrantOptionButton().getSelection()) {
                    withGrantOptionPrivileges.add(privilegeModel.getPrivilegeName());
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        allPrivilege = true;
                        allWithGrantOption = true;
                    }
                } else {
                    withoutGrantOptionPrivileges.add(privilegeModel.getPrivilegeName());
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        allPrivilege = true;
                    }
                }
            }
        } else {
            for (PrivilegeModel privilegeModel : revokePrivilegeOptions) {
                if (privilegeModel.getPrivilegeButton().getSelection()) {
                    revokePrivileges.add(privilegeModel.getPrivilegeName());
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        revokeAllPrivilege = true;
                    }
                }

                if (privilegeModel.getGrantOptionButton().getSelection()) {
                    revokeGrantPrivileges.add(privilegeModel.getPrivilegeName());
                    if (MPPDBIDEConstants.PRIVILEGE_ALL.equals(privilegeModel.getPrivilegeName())) {
                        revokeAllGrantPrivilege = true;
                    }
                }
            }
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
    private final class RoleTextModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent event) {
            checkValidation();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class NoLeafTreeViewerContentProvider.
     */
    private static class NoLeafTreeViewerContentProvider implements ITreeContentProvider {

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return new Object[0];
        }

        @Override
        public void dispose() {

        }

        @Override
        public boolean hasChildren(Object element) {
            return false;
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

        @Override
        public Object[] getElements(Object inputElement) {
            return (Object[]) inputElement;
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class NoLeafTreeViewerLabelProvider.
     */
    private static class NoLeafTreeViewerLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            return ((ServerObject) element).getName();
        }

    }

    /**
     * Recover checked.
     *
     * @param treeItem the tree item
     */
    private void recoverChecked(TreeItem treeItem) {
        if (treeItem.getData() == null) {
            return;
        }

        if (treeItem.getItems().length != 0) {
            for (TreeItem innerTreeItem : treeItem.getItems()) {
                recoverChecked(innerTreeItem);
            }
        }

        if (selectedObjects.contains(treeItem.getData())) {
            treeItem.setChecked(true);
        }
    }

    /**
     * Check all.
     *
     * @param treeItem the tree item
     */
    private void checkAll(TreeItem treeItem) {
        if (treeItem.getData() == null) {
            return;
        }

        if (treeItem.getItems().length != 0) {
            for (TreeItem innerTreeItem : treeItem.getItems()) {
                checkAll(innerTreeItem);
            }
        }

        treeItem.setChecked(true);
    }

    /**
     * Un check all.
     *
     * @param treeItem the tree item
     */
    private void unCheckAll(TreeItem treeItem) {
        if (treeItem.getData() == null) {
            return;
        }

        if (treeItem.getItems().length != 0) {
            for (TreeItem innerTreeItem : treeItem.getItems()) {
                unCheckAll(innerTreeItem);
            }
        }

        treeItem.setChecked(false);
    }

    /**
     * Check validation.
     *
     * @return true, if successful
     */
    private boolean checkValidation() {
        StringBuffer errorStrBuf = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (selectedObjects.isEmpty() && (tabFolder.getSelectionIndex() == 0 || tabFolder.getSelectionIndex() == 2)) {
            errorStrBuf.append(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_PLS_SELECT_OBJECT))
                    .append(MPPDBIDEConstants.TAB).append(MPPDBIDEConstants.TAB);
        }

        if (StringUtils.isEmpty(roleText.getText())
                && (tabFolder.getSelectionIndex() == 1 || tabFolder.getSelectionIndex() == 2)) {
            errorStrBuf.append(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_PLS_SELECT_USER_ROLE))
                    .append(MPPDBIDEConstants.TAB);
        }

        boolean isPrivilegeSelected = false;
        if (grantButton.getSelection()) {
            for (PrivilegeModel privilegeModel : grantPrivilegeOptions) {
                if (privilegeModel.getPrivilegeButton().getSelection()) {
                    isPrivilegeSelected = true;
                    break;
                }
            }
        } else {
            for (PrivilegeModel privilegeModel : revokePrivilegeOptions) {
                if (privilegeModel.getPrivilegeButton().getSelection()
                        || privilegeModel.getGrantOptionButton().getSelection()) {
                    isPrivilegeSelected = true;
                    break;
                }
            }
        }

        if (!isPrivilegeSelected && (tabFolder.getSelectionIndex() == 1 || tabFolder.getSelectionIndex() == 2)) {
            errorStrBuf.append(MessageConfigLoader.getProperty(IMessagesConstants.GRANT_REVOKE_PLS_SELECT_PRIVILEGE))
                    .append(MPPDBIDEConstants.TAB);
        }
        errorMessage.setText(errorStrBuf.toString());
        return errorStrBuf.length() == 0;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        close();
        StringBuffer msgStrBuf = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (!messageQueue.isEmpty()) {
            int queueSize = messageQueue.size();
            for (int i = 0; i < queueSize; i++) {
                Message message = messageQueue.pop();
                if (message != null) {
                    msgStrBuf.append(message.getMessage()).append(MPPDBIDEConstants.LINE_SEPARATOR);
                }
            }
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_OBJECT_PRIVILEGE_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PARTIAL_SUCCESS_UPDATE_OBJECT_PRIVILEGE_DETAIL),
                    msgStrBuf.toString(), null);
        } else {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_OBJECT_PRIVILEGE_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SUCCESS_UPDATE_OBJECT_PRIVILEGE_DETAIL));
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param dbCriticalException the db critical exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
        exceptionEventCall(dbCriticalException);
    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        exceptionEventCall(dbOperationException);
    }

    /**
     * On presetup failure UI action.
     *
     * @param mppdbException the mppdb exception
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException mppdbException) {
        exceptionEventCall(mppdbException);
    }

    /**
     * Exception event call.
     *
     * @param exception the exception
     */
    public void exceptionEventCall(Exception exception) {
        backButton.setEnabled(true);
        cancelButton.setEnabled(true);
        String message = null;
        if (exception instanceof MPPDBIDEException) {
            message = ((MPPDBIDEException) exception).getServerMessage();
        } else {
            message = exception.getMessage();
        }

        MPPDBIDEDialogs.generateDSErrorDialog(
                MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_OBJECT_PRIVILEGE_DIALOG_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_UPDATE_OBJECT_PRIVILEGE_DETAIL), message, null);
    }

}
