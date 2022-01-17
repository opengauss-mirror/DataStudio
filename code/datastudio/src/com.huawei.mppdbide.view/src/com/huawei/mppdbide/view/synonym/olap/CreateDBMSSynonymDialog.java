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

package com.huawei.mppdbide.view.synonym.olap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.presentation.synonym.olap.SynonymInfo;
import com.huawei.mppdbide.presentation.synonym.olap.SynonymWrapper;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.synonym.SynonymCommonUtil;
import com.huawei.mppdbide.view.synonym.olap.CreateSynonymWorker;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;
import com.huawei.mppdbide.view.utils.CommonSqlPreview;
import com.huawei.mppdbide.view.utils.ControlUtils;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: Class
 * 
 * Description: The Class CreateDBMSSynonymDialog.
 *
 * @since 3.0.0
 */
public class CreateDBMSSynonymDialog extends Dialog implements IDialogWorkerInteraction {

    /**
     * The Constant SYNONYM_NAME_MAXIMAL_LENGTH.
     */
    private static final int SYNONYM_NAME_MAXIMAL_LENGTH = 64;

    /**
     * The Constant SPACE_CHAR.
     */
    public static final String SPACE_CHAR = "     ";

    /**
     * The tab folder.
     */
    private TabFolder tabFolder;

    /**
     * The general tab.
     */
    private TabItem generalTab;

    /**
     * The sql preview tab.
     */
    private TabItem previewTab;

    /**
     * The Text synonym name.
     */
    private Text synonymNameText;

    /**
     * the object owner.
     */
    private Combo objectOwnerSelector;

    /**
     * the object name.
     */
    private Combo objectNameSelector;

    /**
     * the object type.
     */
    private Combo objectTypeSelector;

    /**
     * the replace if exist.
     */
    private Button replaceIfExist;

    /**
     * the Button ok.
     */
    private Button okButton = null;

    /**
     * the Button Cancle.
     */
    private Button cancelButton = null;

    /**
     * The error msg txt.
     */
    private Text errorMsgTxt;

    /**
     * The sql preview source viewer.
     */
    private SourceViewer sqlPreviewSourceViewer;

    /**
     * The sql source viewer decoration support.
     */
    private SQLSourceViewerDecorationSupport synonymSrcViewerDecoratnSupport;

    /**
     * The synonym object group.
     */
    private SynonymObjectGroup synonymObjectGroup;

    /**
     * The synonym info.
     */
    private SynonymInfo synonymInfo;

    /**
     * Initialize viewer sql syntax.
     */
    public void initializeViewerSqlSyntax() {
    }

    /**
     * The synonym wrapper.
     */
    private SynonymWrapper wrapper;
    private DBConnection conn;
    private List<String> objectNameList = new ArrayList<>();
    private List<String> objectOwnerList = new ArrayList<>();
    private List<String> objectTypeList = new ArrayList<>();

    /**
     * Instantiates a new creates the synonym dialog.
     * 
     * @param parentShell the parent shell
     * @param SynonymObjectGroup the synonym group
     * @param conn the DB connection
     */
    public CreateDBMSSynonymDialog(Shell parentShell, SynonymObjectGroup synonymObjectGroup, DBConnection conn) {
        super(parentShell);
        this.synonymObjectGroup = synonymObjectGroup;
        this.synonymInfo = new SynonymInfo(synonymObjectGroup);
        this.conn = conn;
    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_SYNONYM, this.getClass()));
        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_SYNONYM));
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData mainGData = new GridData();
        SynonymCommonUtil sysnonymutil = new SynonymCommonUtil();
        sysnonymutil.gridComponentStyle(mainGData, SWT.DEFAULT, 570, 5, 5);
        mainComposite.setLayoutData(mainGData);

        try {
            // add tab folder
            addTabFolder(mainComposite);
            // create general info ui
            addGeneralInfoGUI();
            // create sql preview info ui
            addSqlPreviewInfoGUI();
            tabFolder.addSelectionListener(new AddFolderSelectionListener());
            // add error msg ui
            addErrorMsgUI(parent);
        } catch (MPPDBIDEException mppdbIdeException) {
            hanldeCreateSynonymError(mppdbIdeException);
        }

        return mainComposite;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String okLabel = SPACE_CHAR + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK)
                + SPACE_CHAR;
        final String cancelLabel = SPACE_CHAR
                + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC) + SPACE_CHAR;
        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
        okButton.setEnabled(false);
        cancelButton = createButton(parent, CANCEL, cancelLabel, false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
        setButtonLayoutData(okButton);
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        performOkOperation();
    }

    /**
     * @Title: addTabFolder
     * @Description: add tab folder
     *
     * @param composite the composite
     *
     */
    private void addTabFolder(Composite composite) {
        tabFolder = new TabFolder(composite, SWT.NONE);
        GridData mainCompositeGData = new GridData();
        addMainCompositeGDataProperties(mainCompositeGData);
        tabFolder.setLayoutData(mainCompositeGData);
    }

    /**
     * @Title: addMainCompositeGDataProperties
     * @Description: add main composite grid data properties
     *
     * @param mainCompositeGData the grid data
     */
    private void addMainCompositeGDataProperties(GridData mainCompositeGData) {
        mainCompositeGData.horizontalAlignment = SWT.FILL;
        mainCompositeGData.verticalAlignment = SWT.FILL;
        mainCompositeGData.grabExcessHorizontalSpace = false;
        mainCompositeGData.grabExcessVerticalSpace = true;
        SynonymCommonUtil sysnonymutil = new SynonymCommonUtil();
        sysnonymutil.gridComponentStyle(mainCompositeGData, 400, 520, SWT.DEFAULT, SWT.DEFAULT);
    }

    /**
     * 
     * Title: Class
     * 
     * Description: The class addFolderSelectionListener
     */
    private class AddFolderSelectionListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            if (tabFolder.getSelectionIndex() == 1) {
                sqlPreviewSourceViewer.setDocument(new Document(showDDL()));
                SQLDocumentPartitioner.connectDocument(sqlPreviewSourceViewer.getDocument(), 0);
            }
        }
    }

    /**
     * @Title: createGeneralInfoGUI
     * @Description: Create the genaral info gui.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     *
     */
    private void addGeneralInfoGUI() throws MPPDBIDEException {
        generalTab = new TabItem(tabFolder, SWT.NONE);
        generalTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_MSG));
        SynonymCommonUtil synonymutil = new SynonymCommonUtil();
        Group container = synonymutil.getGroupContainer(tabFolder);
        Composite innerGroup = synonymutil.getInnerGroupComposite(container);

        // add synonym name ui
        addSynonymNameUI(innerGroup);

        // add object owner selector ui
        addObjectOwnerSelectorUI(innerGroup);

        // add object type selector ui
        addObjectTypeSelectorUI(innerGroup);

        // add object name selector ui
        addObjectNameSelectorUI(innerGroup);

        // add if not exist ui
        addIfNotExistUI(innerGroup);

        generalTab.setControl(container);
    }

    /**
     * @Title: addSynonymName
     * @Description: add the synonym name ui
     *
     * @param composite the composite
     */
    private void addSynonymNameUI(Composite composite) {
        Label synonymNameLabel = new Label(composite, SWT.NONE);
        synonymNameLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.SYNONYM_NAME));
        synonymNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        synonymNameText = new Text(composite, SWT.BORDER);
        synonymNameText.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));
        synonymNameText.setTextLimit(SYNONYM_NAME_MAXIMAL_LENGTH);
        synonymNameText.addVerifyListener(new SynonymNameLengthVerifier());
        synonymNameText.addKeyListener(new SynonymNameKeyListener());
    }

    /**
     * 
     * Title: Class
     * 
     * Description: The Class SynonymNameLengthVerifier
     */
    private static class SynonymNameLengthVerifier implements VerifyListener {
        private static final int MAX_FILE_LENGTH = 65;

        @Override
        public void verifyText(VerifyEvent event) {
            String filetext = ((Text) event.widget).getText() + event.text;
            try {
                if (filetext.length() > MAX_FILE_LENGTH) {
                    event.doit = false;
                }
            } catch (NumberFormatException exception) {
                event.doit = false;
            }
        }
    }

    /**
     * 
     * Title: Class
     * 
     * Description: the Class SynonymNameKeyListener
     */
    private class SynonymNameKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent event) {

            if (isOkButtonToEnabled()) {
                enableButtons();
            } else {
                toggleOKButtons(false);
            }
        }

        @Override
        public void keyPressed(KeyEvent event) {

        }
    }

    /**
     * @Title: addObjectOwnerSelectorUi
     * @Description: Add the object owner ui.
     *
     * @param composite the composite
     */
    private void addObjectOwnerSelectorUI(Composite composite) {
        Label objectOwnerLabel = new Label(composite, SWT.NONE);
        objectOwnerLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_OWNER));
        objectOwnerLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        objectOwnerSelector = new Combo(composite, SWT.READ_ONLY);
        objectOwnerSelector.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true));
        getObjectOwnerSelector(objectOwnerSelector);
    }

    /**
     * @Title: addObjectTypeSelectorUi
     * @Description: Add the object type ui.
     *
     * @param composite the composite
     */
    private void addObjectTypeSelectorUI(Composite composite) {
        Label objectTypeLabel = new Label(composite, SWT.NONE);
        objectTypeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_TYPE));
        objectTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

        objectTypeSelector = new Combo(composite, SWT.READ_ONLY);
        objectTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true));
        getObjectTypeSelector(objectTypeSelector, true);
    }

    /**
     * @Title: addObjectNameSelectorUi
     * @Description: Add the object name ui.
     *
     * @param composite the composite
     */
    private void addObjectNameSelectorUI(Composite composite) {
        Label objectNameLabel = new Label(composite, SWT.NONE);
        objectNameLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_NAME));

        objectNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        objectNameSelector = new Combo(composite, SWT.READ_ONLY);

        objectNameSelector.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        getObjectNameSelector(objectNameSelector, true);
        objectNameSelector.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (isOkButtonToEnabled()) {
                    toggleOKButtons(true);
                } else {
                    toggleOKButtons(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }

        });
    }

    /**
     * Adds the if not exist UI.
     *
     * @param composite the composite
     *
     * @Title: addIfNotExistUI
     * @Description: add if not exist ui
     */
    private void addIfNotExistUI(Composite composite) {
        Label ifNotExistLabel = new Label(composite, SWT.NONE);
        ifNotExistLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.REPLACE_IF_EXIST));
        ifNotExistLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        replaceIfExist = new Button(composite, SWT.CHECK);
    }

    /**
     * @Title: getObjectOwnerSelector
     * @Description: get the object owner selector
     *
     * @param objectOwnerSelector the combo
     */
    private void getObjectOwnerSelector(final Combo objectOwnerSelector) {
        ArrayList<Namespace> namespaceList = synonymObjectGroup.getDatabase().getAllNameSpaces();
        namespaceList.stream().forEach(object -> {
            objectOwnerSelector.add(object.getQualifiedObjectName());
            objectOwnerList.add(object.getQualifiedObjectName());
        });
        objectOwnerSelector.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                validateObjectType();
                synonymInfo.setObjectOwner(objectOwnerSelector.getItem(objectOwnerSelector.getSelectionIndex()));

                if (isOkButtonToEnabled()) {
                    toggleOKButtons(true);
                } else {
                    toggleOKButtons(false);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    /**
     * @Title: getObjectTypeSelector
     * @Description: get the object owner selector
     * @param objectTypeSelector the combo
     *
     */
    private void getObjectTypeSelector(final Combo objectTypeSelector, boolean change) {
        if (change) {
            objectTypeList.stream().forEach(object -> {
                objectTypeSelector.add(object);
            });
        }
        objectTypeSelector.select(0);
        if (objectOwnerSelector.getSelectionIndex() != -1) {
            validateObjectName();
            synonymInfo.setObjectType(objectTypeSelector.getItem(objectTypeSelector.getSelectionIndex()));
            if (objectNameSelector.getItemCount() != 0) {
                objectNameSelector.select(0);
            }
        }
        objectTypeSelector.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                validateObjectName();
                synonymInfo.setObjectType(objectTypeSelector.getItem(objectTypeSelector.getSelectionIndex()));

                if (isOkButtonToEnabled()) {
                    toggleOKButtons(true);
                } else {
                    toggleOKButtons(false);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
            }
        });
    }

    /**
     * @Title: validateObjectType
     * @Description: When the object type is switched, verify the objectType.
     *
     */
    private void validateObjectType() {
        String preObjectOwnerSelector = synonymInfo.getObjectOwner();
        if (null != preObjectOwnerSelector && !"".equals(preObjectOwnerSelector)) {
            if (preObjectOwnerSelector.equals(objectOwnerSelector.getItem(objectOwnerSelector.getSelectionIndex()))) {
                getObjectTypeSelector(objectTypeSelector, false);
            } else {
                objectTypeList.clear();
                objectTypeSelector.removeAll();
                objectTypeList = getObjectTypeList();
                getObjectTypeSelector(objectTypeSelector, true);
            }
        } else {
            objectTypeList = getObjectTypeList();
            getObjectTypeSelector(objectTypeSelector, true);
        }
    }

    /**
     * @Title: validateObjectName
     * @Description: When the object owner is switched, verify the objectName.
     *
     */
    private void validateObjectName() {
        String preObjectTypeSelector = synonymInfo.getObjectType();
        String preObjectOwnerSelector = synonymInfo.getObjectOwner();

        if (null != preObjectTypeSelector && !"".equals(preObjectTypeSelector)
                && (null != preObjectOwnerSelector && !"".equals(preObjectOwnerSelector))) {
            if (preObjectTypeSelector.equals(objectTypeSelector.getItem(objectTypeSelector.getSelectionIndex()))
                    && preObjectOwnerSelector
                            .equals(objectOwnerSelector.getItem(objectOwnerSelector.getSelectionIndex()))) {

                getObjectNameSelector(objectNameSelector, false);
            } else {
                objectNameList.clear();
                objectNameSelector.removeAll();
                objectNameList = getObjectNameList();
                getObjectNameSelector(objectNameSelector, true);
            }
        } else {
            objectNameList = getObjectNameList();
            getObjectNameSelector(objectNameSelector, true);
        }
    }

    /**
     * @Title: getObjectNameList
     * @Description: get object name List
     * @return the object name list
     *
     */
    private List<String> getObjectNameList() {
        List<String> objNameList = null;
        try {
            objNameList = SynonymObjectGroup.fetchObjectName(getSelectedNamespace(), this.conn,
                    objectOwnerSelector.getItem(objectOwnerSelector.getSelectionIndex()),
                    objectTypeSelector.getItem(objectTypeSelector.getSelectionIndex()));
        } catch (DatabaseOperationException databaseOperationException) {
            setErrorMsg(databaseOperationException.getMessage());
            MPPDBIDELoggerUtility.error("CreateSynonym:fail to get objectNames", databaseOperationException);
        } catch (DatabaseCriticalException databaseCriticalException) {
            setErrorMsg(databaseCriticalException.getMessage());
            MPPDBIDELoggerUtility.error("CreateSynonym:fail to get objectNames", databaseCriticalException);
        }
        return objNameList;
    }

    /**
     * @Title: getObjectTypeList
     * @Description: get object type List
     * @return the object type list
     *
     */
    private List<String> getObjectTypeList() {
        objectTypeList.add(MPPDBIDEConstants.PRIVILEGE_ALL);
        objectTypeList.add(MessageConfigLoader.getProperty(IMessagesConstants.FUNCTION_PROCEDURE_NAME));
        objectTypeList.add(MessageConfigLoader.getProperty(IMessagesConstants.VIEWS_NAME));
        objectTypeList.add(MessageConfigLoader.getProperty(IMessagesConstants.TABLES_NAME));
        return objectTypeList;
    }

    /**
     * gets the SelectedNamespace
     * 
     * @return namespace the namespace
     */
    public Namespace getSelectedNamespace() {
        ArrayList<Namespace> namespaceList = synonymObjectGroup.getDatabase().getAllNameSpaces();
        Namespace namespace = namespaceList.get(objectOwnerSelector.getSelectionIndex());
        return namespace;
    }

    /**
     * @Title: getObjectTypeSelector
     * @Description: get object type selector
     * @param objectNameSelector the object type selector
     * @param isChanged true,if object type is changed
     *
     */
    private void getObjectNameSelector(final Combo objectTypeSelector, boolean isChanged) {
        if (null != objectNameList) {
            objectNameList.stream().forEach(objectName -> {
                if (isChanged) {
                    objectNameSelector.add(objectName);
                }
            });
        }
    }

    /**
     * @Title: addSqlPreviewInfoGUI
     * @Description: add the sql preview info ui
     *
     */
    private void addSqlPreviewInfoGUI() {

        previewTab = new TabItem(tabFolder, SWT.NONE);
        previewTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_PREVIEW));

        Composite compositeSqlpreview = new Composite(tabFolder, SWT.NONE);

        previewTab.setControl(compositeSqlpreview);

        compositeSqlpreview.setLayout(new GridLayout(1, true));

        createViewer(compositeSqlpreview);

    }

    /**
     * Creates the viewer.
     *
     * @param parent the parent
     * @return the source viewer
     */
    private SourceViewer createViewer(Composite parent) {
        sqlPreviewSourceViewer = new SourceViewer(parent, null,
                SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        sqlPreviewSourceViewer.setEditable(false);

        sqlPreviewSourceViewer
                .configure(new SQLSourceViewerConfig(synonymInfo.getNamespace().getDatabase().getSqlSyntax()));
        ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);
        Font font = resourceManager.createFont(FontDescriptor.createFrom("Courier New", 10, SWT.NORMAL));
        sqlPreviewSourceViewer.getTextWidget().setFont(font);
        Menu menu = new Menu(getControl());
        sqlPreviewSourceViewer.getTextWidget().setMenu(menu);

        GridData sqlPreviewGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        sqlPreviewSourceViewer.getTextWidget().setLayoutData(sqlPreviewGridData);
        setDecoration();
        initializeViewerSqlSyntax();
        sqlPreviewSourceViewer.setEditable(false);
        CommonSqlPreview synComm = new CommonSqlPreview();
        synComm.addMenuItemsToViewer(sqlPreviewSourceViewer);
        return sqlPreviewSourceViewer;
    }

    private Control getControl() {
        return ControlUtils.getControl(sqlPreviewSourceViewer);
    }

    /**
     * Sets the decoration.
     */
    private void setDecoration() {
        ISharedTextColors sharedTextColors = EditorsPlugin.getDefault().getSharedTextColors();

        synonymSrcViewerDecoratnSupport = new SQLSourceViewerDecorationSupport(this.sqlPreviewSourceViewer, null, null,
                sharedTextColors);
        synonymSrcViewerDecoratnSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
                IUserPreference.CURRENTLINE_COLOR);

        synonymSrcViewerDecoratnSupport.installDecorations();
    }

    /**
     * Show DDL.
     *
     * @return the string
     */
    String showDDL() {
        setSynonymInfo();
        return synonymInfo.generateCreateSynonymSql();
    }

    /**
     * @Title: setSynonymInfo
     * @Description: set the synonym info
     *
     */
    private void setSynonymInfo() {
        synonymInfo.setSynonymName(synonymNameText.getText());
        synonymInfo.setObjectOwner(objectOwnerSelector.getText());
        synonymInfo.setObjectType(objectTypeSelector.getText());
        synonymInfo.setObjectName(objectNameSelector.getText());
        synonymInfo.setReplaceIfExist(replaceIfExist.getSelection());
        synonymInfo.setOwner(objectOwnerSelector.getText());
    }

    /**
     * @Title: addErrorMsgUI
     * @Description: add error msg ui
     * @param composite the composite
     *
     */
    private void addErrorMsgUI(Composite composite) {
        errorMsgTxt = new Text(composite, SWT.BOLD | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
        GridData errorMsgTxtgd = new GridData(SWT.FILL, SWT.FILL, true, false);
        errorMsgTxtgd.heightHint = 35;
        errorMsgTxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        errorMsgTxt.setLayoutData(errorMsgTxtgd);
        errorMsgTxt.setVisible(false);
    }

    /**
     * @Title: setErrorMsg
     * @Description: set the error msg
     * @param errMsg the error msg
     *
     */
    private void setErrorMsg(String errMsg) {
        if (null == errMsg || errMsg.trim().isEmpty()) {
            errorMsgTxt.setVisible(false);
        } else {
            errorMsgTxt.setVisible(true);
        }
        errorMsgTxt.setText(errMsg);
    }

    /**
     * 
     * @Title: isSynonymNameSelectorEmpty
     * @Description: Checks if is synonym name empty.
     * @return true, if the synonym name is empty
     *
     */
    private boolean isSynonymNameSelectorEmpty() {
        if (null != synonymNameText && !(synonymNameText.isDisposed())) {
            return synonymNameText.getText().length() < 1;
        } else {
            return false;
        }
    }

    /**
     * @Title: isSynonymNamelengthValid
     * @Description: Checks if is synonym name length valid.
     * @return true, if is the synonym name length valid
     *
     */
    private boolean isSynonymNamelengthValid() {

        if (null != synonymNameText && !(synonymNameText.isDisposed())) {
            return synonymNameText.getText().length() <= SYNONYM_NAME_MAXIMAL_LENGTH;
        } else {
            return false;
        }
    }

    /**
     * @Title: isObjectOwnerSelectorEmpty
     * @Description: Checks if is object owner empty.
     * @return true, if the object owner is empty
     *
     */
    private boolean isObjectOwnerSelectorEmpty() {
        if (null != objectOwnerSelector && !(objectOwnerSelector.isDisposed())) {
            return objectOwnerSelector.getText().length() < 1;
        } else {
            return false;
        }
    }

    /**
     * @Title: isObjectNameSelectorEmpty
     * @Description: Checks if is object name empty.
     * @return true, if the object name is empty
     *
     */
    private boolean isObjectNameSelectorEmpty() {
        if (null != objectNameSelector && !(objectNameSelector.isDisposed())) {
            return objectNameSelector.getText().length() < 1;
        } else {
            return false;
        }
    }

    /**
     * @Title: isObjectTypeSelectorEmpty
     * @Description: Checks if is object type empty.
     * @return true, if the object type is empty
     *
     */
    private boolean isObjectTypeSelectorEmpty() {
        if (null != objectTypeSelector && !(objectTypeSelector.isDisposed())) {
            return objectTypeSelector.getText().length() < 1;
        } else {
            return false;
        }
    }

    /**
     * @Title: validateTextInputs
     * @Description: validate the input text
     * @return true, if successful
     *
     */
    private boolean validateTextInputs() {
        if (isSynonymNameSelectorEmpty()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.SYNONYM_NAME_ENTER_NM));
            return false;
        }
        if (!isSynonymNamelengthValid()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.SYNONYM_NAME_EXCEED_MAX));
            return false;
        }
        if (isObjectOwnerSelectorEmpty()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_OWNER_SELECT_NM));
            return false;
        }
        if (isObjectTypeSelectorEmpty()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_TYPE_SELECT_NM));
            return false;
        }
        if (isObjectNameSelectorEmpty()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_NAME_SELECT_NM));
            return false;
        }
        errorMsgTxt.setVisible(false);
        return true;
    }

    /**
     * Enable buttons.
     */
    private void enableButtons() {
        if (okButton.isDisposed() || cancelButton.isDisposed()) {
            return;
        }
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Toggle OK buttons.
     *
     * @param value the value
     */
    private void toggleOKButtons(boolean value) {
        if (!okButton.isDisposed()) {
            okButton.setEnabled(value);
        }
    }

    /**
     * Checks if is ok button to enabled.
     *
     * @return true, if is ok button to enabled
     */
    private boolean isOkButtonToEnabled() {
        return !isSynonymNameSelectorEmpty() && !isObjectOwnerSelectorEmpty() && !isObjectNameSelectorEmpty()
                && !isObjectTypeSelectorEmpty();
    }

    private void performOkOperation() {
        if (validateTextInputs()) {
            sqlPreviewSourceViewer.setDocument(new Document(showDDL()));
            SQLDocumentPartitioner.connectDocument(sqlPreviewSourceViewer.getDocument(), 0);
            tabFolder.setSelection(tabFolder.getItemCount() - 1);
            wrapper = new SynonymWrapper(synonymObjectGroup, synonymInfo);
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForSynonymObjectsWithMsg(
                    synonymInfo.getSynonymName(), synonymInfo.getNameSpaceName(),
                    synonymInfo.getNamespace().getDatabase().getDbName(), IMessagesConstants.CREATE_SEQ_PROGRESS_NAME);
            CreateSynonymWorker worker = new CreateSynonymWorker(progressLabel, wrapper,
                    MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_SYNONYM), this);
            worker.schedule();
        }

    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader.getProperty(
                IMessagesConstants.CREATED_SYNONYM_SUCESS, synonymInfo.getOwner(), synonymInfo.getSynonymName())));

        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(wrapper.getDatabase());
        }
        close();
    }

    /**
     * On critical exception UI action.
     *
     * @param databaseCriticalException the database critical exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException databaseCriticalException) {
        hanldeCreateSynonymError(databaseCriticalException);
    }

    /**
     * On operational exception UI action.
     *
     * @param databaseOperationException the database operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException databaseOperationException) {
        hanldeCreateSynonymError(databaseOperationException);
    }

    /**
     * On presetup failure UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException exception) {

    }

    /**
     * @Title: hanldeCreateSynonymError
     * @Description: hanlde create synonym error
     * @param mppdbIdeException the mppdbide exception
     * 
     */
    private void hanldeCreateSynonymError(MPPDBIDEException mppdbideException) {
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
        String msg = mppdbideException.getServerMessage();
        setErrorMsg(msg);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_SYNONYM_ERROR, wrapper.getMetadata().getQualifiedObjectName())));
    }
}
