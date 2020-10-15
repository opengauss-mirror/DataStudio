/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.olap;

import java.util.Locale;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.presentation.SequenceDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.SourceEditorKeyListener;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.handler.connection.PasswordDialog;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.table.UIUtils;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.ControlUtils;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateSequenceDialoge.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateSequenceDialoge extends Dialog {

    private Text sequenceNameText;
    private Text minimumValueText;
    private Text incrementByText;
    private Text maxValueText;
    private Text startValueText;
    private Text cacheText;
    private Button finishButton;
    private Button cancelButton;
    private Namespace namespace;
    private SequenceDataCore sequenceCore;
    private Button btnCycle;
    private Combo schemaCombo;
    private Combo tableCombo;
    private Combo columnCombo;
    private TabItem tabItem;
    private Label sequenceNameLable;
    private Label fetchTableErrLabel;
    private Label lblMinimumValue;
    private Label lblIncrememntBy;
    private Label maxValueLable;
    private Label startValueLable;
    private Label cacheLable;
    private Label schemaLable;
    private Label tableLable;
    private Label columnLable;

    /**
     * The text sql preview.
     */
    protected SourceViewer textSqlPreview;
    private TabItem sqlPreviewTab;
    private TabFolder tabFolder;
    private int createGeneralTab = 0;
    private int createTableSqlPreview = 1;
    private Button checkSequenceCase;

    /**
     * The menu select all.
     */
    protected MenuItem menuSelectAll;

    /**
     * The menu copy.
     */
    protected MenuItem menuCopy;
    private int sequenceError;
    private String invalidSequenceValue;
    private Label txtErrorMsg;
    private SequeHandlerWorkerJob sequenceWorker;
    private SQLSourceViewerDecorationSupport sourceViewerDecorationSupport;
    private static final int SEQ_VALIDATION_CHECKPOINT_MIN_VALUE = 0;
    private static final int SEQ_VALIDATION_CHECKPOINT_MAX_VALUE = 1;
    private static final int SEQ_VALIDATION_CHECKPOINT_INCREMENT_BY_VALUE = 2;
    private static final int SEQ_VALIDATION_CHECKPOINT_START_VALUE = 3;
    private static final int SEQ_VALIDATION_CHECKPOINT_CACHE_VALUE = 4;
    private static final int SEQ_VALIDATION_CHECKPOINT_MISCELLANEOUS = 5;

    private static final String REFRESH_TABLE_LABEL_FAILURE = MessageConfigLoader
            .getProperty(IMessagesConstants.REFRESH_TABLE_LABEL_FAILURE);
    private static final String REFRESH_TABLE_LABEL_SUCCESS = MessageConfigLoader
            .getProperty(IMessagesConstants.REFRESH_TABLE_LABEL_SUCCESS);
    private static final String REFRESH_TABLE_LABEL_IN_PROGRESS = MessageConfigLoader
            .getProperty(IMessagesConstants.REFRESH_TABLE_LABEL_IN_PROGRESS);
    private static final String REFRESH_TABLE = "refreshing selected table";
    private static final String REFRESH_TABLE_DEFAULT = "";

    /**
     * The Constant GENERAL.
     */
    protected static final String GENERAL = MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_MSG);

    /**
     * The Constant SQL_PREVIEW.
     */
    protected static final String SQL_PREVIEW = MessageConfigLoader.getProperty(IMessagesConstants.SQL_PREVIEW);

    /**
     * Instantiates a new creates the sequence dialoge.
     *
     * @param shell the shell
     * @param sequenceDataCore the sequence data core
     * @param ns the ns
     */
    public CreateSequenceDialoge(Shell shell, SequenceDataCore sequenceDataCore, Namespace ns) {
        super(shell);
        this.namespace = ns;
        this.sequenceCore = sequenceDataCore;

    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite maincomp = (Composite) super.createDialogArea(parent);

        tabFolder = new TabFolder(maincomp, SWT.NONE);
        tabFolder.setLayout(new GridLayout(1, true));
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addErrorMsgArea(maincomp);

        addGeneralTab();

        addSqlPreviewTab();

        tabFolder.addSelectionListener(new TabFolderSelectionListener());

        return maincomp;

    }

    private void addGeneralTab() {
        tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(GENERAL);
        Group generalGroup = new Group(tabFolder, SWT.NONE);
        GridLayout grpLayout = new GridLayout(4, false);
        grpLayout.verticalSpacing = 15;
        grpLayout.horizontalSpacing = 15;
        grpLayout.marginTop = 10;
        generalGroup.setLayout(grpLayout);
        generalGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabItem.setControl(generalGroup);

        addSequenceNameUi(generalGroup);

        addCheckSeqCaseBtn(generalGroup);

        addMinimumValueUi(generalGroup);

        addIncrementByUi(generalGroup);

        addMaxValueUi(generalGroup);

        addStartValueUi(generalGroup);
        addCacheUi(generalGroup);

        addCycleBtn(generalGroup);

        setFocusOnText(createGeneralTab);

        addSequenceOwnerDetailsUi(generalGroup);

    }

    private void addCycleBtn(Group generalGroup) {
        btnCycle = new Button(generalGroup, SWT.CHECK);
        btnCycle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        btnCycle.setText(MessageConfigLoader.getProperty(IMessagesConstants.CYCLE_MSG));
    }

    private void addCheckSeqCaseBtn(Group generalGroup) {
        checkSequenceCase = new Button(generalGroup, SWT.CHECK);
        checkSequenceCase.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        checkSequenceCase.setText(MessageConfigLoader.getProperty(IMessagesConstants.CASE_MSG));
    }

    private void addCacheUi(Group generalGroup) {
        cacheLable = new Label(generalGroup, SWT.NONE);
        cacheLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
        cacheLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEQUENCE_CACHE));

        cacheText = new Text(generalGroup, SWT.BORDER);
        cacheText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        cacheText.addVerifyListener(new SequenceHelper(true));
        cacheText.addKeyListener(new ClearErrorMessage());
        setInitTextProperties(cacheText);
    }

    private void addStartValueUi(Group generalGroup) {
        startValueLable = new Label(generalGroup, SWT.NONE);
        startValueLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.START_VALUE));
        startValueLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));

        startValueText = new Text(generalGroup, SWT.BORDER);
        startValueText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        startValueText.addVerifyListener(new SequenceHelper(false));
        startValueText.addKeyListener(new ClearErrorMessage());
        setInitTextProperties(startValueText);
    }

    private void addMaxValueUi(Group generalGroup) {
        maxValueLable = new Label(generalGroup, SWT.NONE);
        maxValueLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.MAXIMUM_VALUE));
        maxValueLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));

        maxValueText = new Text(generalGroup, SWT.BORDER);
        maxValueText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        maxValueText.addVerifyListener(new SequenceHelper(false));
        maxValueText.addKeyListener(new ClearErrorMessage());
        setInitTextProperties(maxValueText);
    }

    private void addIncrementByUi(Group generalGroup) {
        lblIncrememntBy = new Label(generalGroup, SWT.NONE);
        lblIncrememntBy.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
        lblIncrememntBy.setText(MessageConfigLoader.getProperty(IMessagesConstants.INCREMENT_BY));

        incrementByText = new Text(generalGroup, SWT.BORDER);
        incrementByText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        incrementByText.addVerifyListener(new SequenceHelper(false));
        incrementByText.addKeyListener(new ClearErrorMessage());
        setInitTextProperties(incrementByText);
    }

    private void addMinimumValueUi(Group generalGroup) {
        lblMinimumValue = new Label(generalGroup, SWT.NONE);
        lblMinimumValue.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
        lblMinimumValue.setText(MessageConfigLoader.getProperty(IMessagesConstants.MINIMUM_VALUE));

        minimumValueText = new Text(generalGroup, SWT.BORDER);
        minimumValueText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        minimumValueText.addVerifyListener(new SequenceHelper(false));
        minimumValueText.addKeyListener(new ClearErrorMessage());
        setInitTextProperties(minimumValueText);
    }

    private void addSequenceNameUi(Group generalGroup) {
        sequenceNameLable = new Label(generalGroup, SWT.NONE);
        sequenceNameLable.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
        sequenceNameLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.SEQUENCE_NAME));

        sequenceNameText = new Text(generalGroup, SWT.BORDER);
        GridData seqNameData = new GridData(SWT.FILL, SWT.FILL, true, false);
        seqNameData.horizontalSpan = 2;
        sequenceNameText.setLayoutData(seqNameData);
        sequenceNameText.addVerifyListener(new SequenceNameVerifyListener());

        sequenceNameText.addKeyListener(new SequenceNameKeyListener());
        setInitTextProperties(sequenceNameText);
    }

    private void addSqlPreviewTab() {
        sqlPreviewTab = new TabItem(tabFolder, SWT.NONE);
        sqlPreviewTab.setText(SQL_PREVIEW);

        Composite compositeSqlpreview = new Composite(tabFolder, SWT.NONE);
        compositeSqlpreview.setLayout(new GridLayout(1, false));
        compositeSqlpreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        sqlPreviewTab.setControl(compositeSqlpreview);
        createSqlPreviewInfoGui(compositeSqlpreview);
    }

    private void addErrorMsgArea(Composite mainComp) {
        txtErrorMsg = new Label(mainComp, SWT.NONE);
        GridData txtErrData = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtErrorMsg.setLayoutData(txtErrData);
        txtErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        txtErrorMsg.setVisible(false);
    }

    private void addSequenceOwnerDetailsUi(Group generalGroup) {
        Group grpOwner = new Group(generalGroup, SWT.NONE);
        grpOwner.setText(MessageConfigLoader.getProperty(IMessagesConstants.OWNER));
        grpOwner.setLayout(new GridLayout(3, true));
        grpOwner.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

        addSeqOwnerSchemaUi(grpOwner);
        addSeqOwnerTableListUi(grpOwner);
        addSeqOwnerColumnUi(grpOwner);

        // Label to show if table is being loaded or error occurred while
        // loading
        fetchTableErrLabel = new Label(grpOwner, SWT.NONE);
        fetchTableErrLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fetchTableErrLabel.setText("");
        fetchTableErrLabel.setVisible(false);
        fetchTableErrLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
    }

    private void addSeqOwnerColumnUi(Group grpOwner) {
        Composite ownerCol = new Composite(grpOwner, SWT.NONE);
        ownerCol.setLayout(new GridLayout(1, true));
        ownerCol.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true, 1, 2));
        columnLable = new Label(ownerCol, SWT.NONE);
        columnLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_NAME));

        columnCombo = new Combo(ownerCol, SWT.NONE | SWT.READ_ONLY);
        columnCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        columnCombo.addSelectionListener(new ColumnComboSelectionListener());
    }

    private void addSeqOwnerTableListUi(Group grpOwner) {
        Composite ownerTbl = new Composite(grpOwner, SWT.NONE);
        ownerTbl.setLayout(new GridLayout(1, true));
        ownerTbl.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true, 1, 2));
        tableLable = new Label(ownerTbl, SWT.NONE);
        tableLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_NAME));

        tableCombo = new Combo(ownerTbl, SWT.NONE | SWT.READ_ONLY);
        tableCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        tableCombo.addSelectionListener(new TableComboSelectionListener());
        populatetablelist();
    }

    private void addSeqOwnerSchemaUi(Group grpOwner) {
        Composite ownerSchema = new Composite(grpOwner, SWT.NONE);
        ownerSchema.setLayout(new GridLayout(1, true));
        ownerSchema.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true, 1, 2));
        schemaLable = new Label(ownerSchema, SWT.NONE);
        schemaLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.SELECT_SCHEMA_NAME));

        schemaCombo = new Combo(ownerSchema, SWT.NONE | SWT.READ_ONLY);
        GridData schemaComboGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        schemaCombo.setLayoutData(schemaComboGD);
        UIUtils.displayNamespaceList(this.namespace.getDatabase(), this.namespace.getName(), schemaCombo, false);
        schemaCombo.setEnabled(false);
    }

    /**
     * The listener interface for receiving tabFolderSelection events. The class
     * that is interested in processing a tabFolderSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addTabFolderSelectionListener<code> method. When the
     * tabFolderSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * TabFolderSelectionEvent
     */
    private class TabFolderSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            int curTab = tabFolder.getSelectionIndex();
            if (curTab > 0) {
                updateSequenceFields(curTab);
                tabFolder.setSelection(curTab);

            }
            setFocusOnText(curTab);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving columnComboSelection events. The
     * class that is interested in processing a columnComboSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addColumnComboSelectionListener<code> method. When the
     * columnComboSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * ColumnComboSelectionEvent
     */
    private class ColumnComboSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            enabledisablefinishbtn();

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving tableComboSelection events. The
     * class that is interested in processing a tableComboSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addTableComboSelectionListener<code> method. When the
     * tableComboSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * TableComboSelectionEvent
     */
    private class TableComboSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            setFetchTableErrLabel(REFRESH_TABLE_DEFAULT, false, SWT.COLOR_BLUE);
            columnCombo.removeAll();
            loadAndPopulateColumns();
            enabledisablefinishbtn();

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving sequenceNameKey events. The class
     * that is interested in processing a sequenceNameKey event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addSequenceNameKeyListener<code>
     * method. When the sequenceNameKey event occurs, that object's appropriate
     * method is invoked.
     *
     * SequenceNameKeyEvent
     */
    private class SequenceNameKeyListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (!((Text) e.getSource()).getText().isEmpty()) {
                enabledisablefinishbtn();
            } else {
                finishButton.setEnabled(false);
            }

        }

    }

    /**
     * The listener interface for receiving sequenceNameVerify events. The class
     * that is interested in processing a sequenceNameVerify event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addSequenceNameVerifyListener<code> method. When the
     * sequenceNameVerify event occurs, that object's appropriate method is
     * invoked.
     *
     * SequenceNameVerifyEvent
     */
    private static class SequenceNameVerifyListener implements VerifyListener {

        @Override
        public void verifyText(VerifyEvent e) {
            Text text = (Text) e.getSource();

            final String oldSequence = text.getText();
            String newSequence = oldSequence.substring(0, e.start) + e.text + oldSequence.substring(e.end);
            if (newSequence.length() > 63) {
                e.doit = false;
            }

        }

    }

    private void setInitTextProperties(Text ctrl) {
        ctrl.setText("");
        ctrl.addListener(SWT.MenuDetect, new InitListener());
    }

    /**
     * The listener interface for receiving init events. The class that is
     * interested in processing a init event implements this interface, and the
     * object created with that class is registered with a component using the
     * component's <code>addInitListener<code> method. When the init event
     * occurs, that object's appropriate method is invoked.
     *
     * InitEvent
     */
    private static class InitListener implements Listener {
        @Override
        public void handleEvent(Event event) {
            event.doit = false;
        }
    }

    private void createSqlPreviewInfoGui(Composite compositeSqlPreview) {

        textSqlPreview = new SourceViewer(compositeSqlPreview, null,
                SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.WRAP);
        textSqlPreview.setEditable(false);
        textSqlPreview.getTextWidget().setLayout(new GridLayout());
        textSqlPreview.getTextWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        textSqlPreview.configure(new SQLSourceViewerConfig(
                namespace.getDatabase() == null ? null : namespace.getDatabase().getSqlSyntax()));
        ResourceManager resManager = new LocalResourceManager(JFaceResources.getResources(), compositeSqlPreview);
        Font font = resManager.createFont(FontDescriptor.createFrom("Courier New", 10, SWT.NORMAL));

        textSqlPreview.getTextWidget().setFont(font);
        Menu menu = new Menu(getControl());
        textSqlPreview.getTextWidget().setMenu(menu);
        addCopyMenuItem(menu);
        addSelectAllMenuItem(menu);
        menu.addMenuListener(new PLEditorMenuListener());

        textSqlPreview.getTextWidget().addKeyListener(new SourceEditorKeyListener(textSqlPreview));
    }

    /**
     * The listener interface for receiving PLEditorMenu events. The class that
     * is interested in processing a PLEditorMenu event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addPLEditorMenuListener<code>
     * method. When the PLEditorMenu event occurs, that object's appropriate
     * method is invoked.
     *
     * PLEditorMenuEvent
     */
    private final class PLEditorMenuListener implements MenuListener {
        @Override
        public void menuShown(MenuEvent e) {
            contextMenuAboutToShowForSQLPreview();
        }

        @Override
        public void menuHidden(MenuEvent e) {

        }
    }

    /**
     * Context menu about to show for SQL preview.
     */
    protected void contextMenuAboutToShowForSQLPreview() {
        menuCopy.setEnabled(textSqlPreview.getDocument().getLength() > 0);
        menuSelectAll.setEnabled(textSqlPreview.getDocument().getLength() > 0);
    }

    /**
     * Adds the copy menu item.
     *
     * @param menu the menu
     */
    protected void addCopyMenuItem(Menu menu) {
        menuCopy = new MenuItem(menu, SWT.PUSH);
        // DTS2016011900019 Starts
        menuCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COPY));
        // DTS2016011900019 Ends
        menuCopy.addSelectionListener(new PLEditorCopySelectionListener());
        menuCopy.setImage(IconUtility.getIconImage(IiconPath.ICO_COPY, this.getClass()));
    }

    /**
     * Gets the control.
     *
     * @return the control
     */
    public Control getControl() {
        return ControlUtils.getControl(textSqlPreview);
    }

    /**
     * The listener interface for receiving PLEditorCopySelection events. The
     * class that is interested in processing a PLEditorCopySelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addPLEditorCopySelectionListener<code> method. When the
     * PLEditorCopySelection event occurs, that object's appropriate method is
     * invoked.
     *
     * PLEditorCopySelectionEvent
     */
    private final class PLEditorCopySelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            copySelectedDocText();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    /**
     * Adds the select all menu item.
     *
     * @param menu the menu
     */
    protected void addSelectAllMenuItem(Menu menu) {
        menuSelectAll = new MenuItem(menu, SWT.PUSH);
        // DTS2016011900019 Starts
        menuSelectAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_SELECTALL));
        // DTS2016011900019 Ends
        menuSelectAll.addSelectionListener(new PLEditorSelectAllListener());
    }

    /**
     * Copy selected doc text.
     */
    public void copySelectedDocText() {
        textSqlPreview.doOperation(ITextOperationTarget.COPY);
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

    private void updateSequenceFields(int event) {

        try {

            clearData(sequenceCore.getSequenceMetadata());
            setSequenceData(sequenceCore.getSequenceMetadata());
            if (event == createTableSqlPreview) {
                textSqlPreview.setDocument(new Document(formQueries()));
                setDecoration();
                SQLDocumentPartitioner.connectDocument(this.textSqlPreview.getDocument(), 0);
            }
        } catch (NumberFormatException e) {

            invalidSequenceVal();
        }
    }

    /**
     * Sets the decoration.
     */
    protected void setDecoration() {
        ISharedTextColors sharedTextColors = EditorsPlugin.getDefault().getSharedTextColors();

        sourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(this.textSqlPreview, null, null,
                sharedTextColors);
        sourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
                IUserPreference.CURRENTLINE_COLOR);

        sourceViewerDecorationSupport.installDecorations();
    }

    /**
     * The listener interface for receiving PLEditorSelectAll events. The class
     * that is interested in processing a PLEditorSelectAll event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addPLEditorSelectAllListener<code> method. When the
     * PLEditorSelectAll event occurs, that object's appropriate method is
     * invoked.
     *
     * PLEditorSelectAllEvent
     */
    private final class PLEditorSelectAllListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            selectAllDocText();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    /**
     * Select all doc text.
     */
    public void selectAllDocText() {
        textSqlPreview.doOperation(ITextOperationTarget.SELECT_ALL);
    }

    // Clear all input data before updating new data
    private void clearData(SequenceMetadata sequenceMetadat) {
        if (null != sequenceMetadat.getSequenceName()) {
            sequenceMetadat.setSequenceName(sequenceNameText.getText());
        }
        if (null != sequenceMetadat.getMinValue()) {
            sequenceMetadat.setMinValue("");
        }
        if (null != sequenceMetadat.getMaxValue()) {
            sequenceMetadat.setMaxValue("");
        }
        if (null != sequenceMetadat.getIncrementBy()) {
            sequenceMetadat.setIncrementBy("");
        }
        if (null != sequenceMetadat.getStartValue()) {
            sequenceMetadat.setStartValue("");
        }
        if (null != sequenceMetadat.getCacheSize()) {
            sequenceMetadat.setCache("");
        }
        if (sequenceMetadat.isCycle()) {
            sequenceMetadat.setCycle(false);
        }
        if (null != sequenceMetadat.getTableName()) {
            sequenceMetadat.setTableName("");
        }
        if (null != sequenceMetadat.getColumnName()) {
            sequenceMetadat.setColumnName("");
        }

    }

    /**
     * Form queries.
     *
     * @return the string
     */
    public String formQueries() {
        StringBuilder queries = new StringBuilder(sequenceCore.composeQuery());

        return queries.toString();
    }

    private void setFocusOnText(int tab) {
        if (tab == createGeneralTab) {
            sequenceNameText.forceFocus();
        }

    }

    private void enabledisablefinishbtn() {
        if (!sequenceNameText.getText().isEmpty()) {
            if (!tableCombo.getText().isEmpty()) {
                if (!columnCombo.getText().isEmpty()) {
                    finishButton.setEnabled(true);
                } else {
                    finishButton.setEnabled(false);
                }
            } else {
                finishButton.setEnabled(true);
            }
        } else {
            finishButton.setEnabled(false);
        }
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String finishLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_FINISH_BTN)
                + "     ";
        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        finishButton = createButton(parent, UIConstants.OK_ID, finishLabel, true);
        finishButton.setEnabled(false);
        finishButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");

        cancelButton = createButton(parent, CANCEL, cancelLabel, false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
        setButtonLayoutData(finishButton);
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(getWindowTitle());
        newShellWindow.setImage(getWindowImage());
        newShellWindow.setSize(575, 540);
    }

    private Image getWindowImage() {

        return IconUtility.getIconImage(IiconPath.SEQUENCE_OBJECT, this.getClass());
    }

    private String getWindowTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_SEQUENCE);
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        finishButton.setEnabled(false);
        cancelButton.setEnabled(true);
        try {
            clearData(sequenceCore.getSequenceMetadata());
            setSequenceData(sequenceCore.getSequenceMetadata());
            performOkOperation();

        } catch (NumberFormatException e) {
            invalidSequenceVal();
            finishButton.setEnabled(true);
        }
    }

    /**
     * Enable disable button.
     *
     * @param enable the enable
     */
    public void enableDisableButton(boolean enable) {
        finishButton.setEnabled(enable);
        cancelButton.setEnabled(enable);
    }

    private void performOkOperation() {

        boolean isExceptionForInvalidPswd = false;
        boolean isDataBaseConnected = false;
        StatusMessage statMsg = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_SEQUENCE));
        if (namespace != null) {
            isDataBaseConnected = namespace.getDatabase().isConnected();
        }
        while (isDataBaseConnected) {
            try {
                if (namespace.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)
                        || isExceptionForInvalidPswd) {
                    Shell newshell = Display.getDefault().getActiveShell();
                    PasswordDialog dialog = new PasswordDialog(newshell, namespace.getDatabase());
                    int returnVal = dialog.open();
                    if (returnVal != 0) {
                        break;
                    }
                }
                sequenceCore.createConnection();
                break;

            } catch (MPPDBIDEException exception) {
                String ermsg = exception.getServerMessage() != null ? exception.getServerMessage()
                        : exception.getMessage();
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR), ermsg);

                if (ermsg == null || !ermsg.contains("Invalid username/password")) {
                    isExceptionForInvalidPswd = false;
                    break;
                } else if (ermsg.contains("Invalid username/password")) {
                    isExceptionForInvalidPswd = true;
                }
            }
        }
        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(
                sequenceCore.getSequenceMetadata().getSequenceName(),
                sequenceCore.getSequenceMetadata().getSchemaName(),
                sequenceCore.getSequenceMetadata().getNamespace().getDatabaseName(),
                sequenceCore.getSequenceMetadata().getNamespace().getServerName(),
                IMessagesConstants.CREATE_SEQ_PROGRESS_NAME);
        sequenceWorker = new SequeHandlerWorkerJob(statMsg, progressLabel, sequenceCore, this);
        sequenceWorker.setTaskDB(sequenceCore.getSequenceMetadata().getDatabase());
        StatusMessageList.getInstance().push(statMsg);
        final BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (null != btmStatusBar) {

            btmStatusBar.activateStatusbar();
        }
        sequenceWorker.schedule();
    }

    private void invalidSequenceVal() {
        setErrorMsg("");
        if (this.sequenceError == SEQ_VALIDATION_CHECKPOINT_MISCELLANEOUS) {
            return;
        }
        switch (this.sequenceError) {
            case SEQ_VALIDATION_CHECKPOINT_MIN_VALUE: {
                invalidSequenceValue = MessageConfigLoader.getProperty(IMessagesConstants.MINIMUM_VALUE_FIELD);
                break;
            }
            case SEQ_VALIDATION_CHECKPOINT_MAX_VALUE: {
                invalidSequenceValue = MessageConfigLoader.getProperty(IMessagesConstants.MAXIMUM_VALUE_FIELD);
                break;
            }
            case SEQ_VALIDATION_CHECKPOINT_INCREMENT_BY_VALUE: {
                invalidSequenceValue = MessageConfigLoader.getProperty(IMessagesConstants.INCREMENT_BY_FIELD);
                break;
            }
            case SEQ_VALIDATION_CHECKPOINT_START_VALUE: {
                invalidSequenceValue = MessageConfigLoader.getProperty(IMessagesConstants.START_VALUE_FIELD);
                break;
            }

            case SEQ_VALIDATION_CHECKPOINT_CACHE_VALUE: {
                invalidSequenceValue = MessageConfigLoader.getProperty(IMessagesConstants.SEQUENCE_CACHE_FIELD);
                break;
            }
            default: {
                break;
            }

        }

        setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SEQUENCE_INPUT_FORMATE,
                invalidSequenceValue, "" + Long.MIN_VALUE, "" + Long.MAX_VALUE));
    }

    private void setSequenceData(SequenceMetadata sequenceMetadat) {
        sequenceError = SEQ_VALIDATION_CHECKPOINT_MIN_VALUE;
        String sequenceName = sequenceNameText.getText();
        if (!checkSequenceCase.getSelection()) {

            sequenceName = sequenceName.toLowerCase(Locale.ENGLISH);
        }

        sequenceMetadat.setSequenceName(sequenceName);
        setSequenceMinValue(sequenceMetadat);
        sequenceError = SEQ_VALIDATION_CHECKPOINT_MAX_VALUE;
        setSequenceMaxValue(sequenceMetadat);
        sequenceError = SEQ_VALIDATION_CHECKPOINT_INCREMENT_BY_VALUE;
        setSequenceIncreamentByValue(sequenceMetadat);
        sequenceError = SEQ_VALIDATION_CHECKPOINT_START_VALUE;
        setSequenceStartValue(sequenceMetadat);
        sequenceError = SEQ_VALIDATION_CHECKPOINT_CACHE_VALUE;
        setSequenceCache(sequenceMetadat);
        sequenceError = SEQ_VALIDATION_CHECKPOINT_MISCELLANEOUS;

        if (btnCycle.getSelection()) {
            sequenceMetadat.setCycle(true);

        }

        setSequenceobjName(sequenceMetadat);
    }

    private void setSequenceobjName(SequenceMetadata sequenceMetadat) {
        if (!schemaCombo.getText().isEmpty()) {

            sequenceMetadat.setSchemaName(schemaCombo.getText());
        }
        if (!tableCombo.getText().isEmpty()) {

            sequenceMetadat.setTableName(tableCombo.getText());
        }
        if (!columnCombo.getText().isEmpty()) {

            sequenceMetadat.setColumnName(columnCombo.getText());
        }
    }

    private void setSequenceCache(SequenceMetadata sequenceMetadat) {
        long cache = 0;
        if (!cacheText.getText().isEmpty()) {
            cache = Long.parseLong(cacheText.getText());
            sequenceMetadat.setCache(Long.toString(cache));

        } else {
            sequenceMetadat.setCache(null);
        }
    }

    private void setSequenceStartValue(SequenceMetadata sequenceMetadat) {
        long startValue = 0;
        if (!startValueText.getText().isEmpty()) {
            startValue = Long.parseLong(startValueText.getText());
            sequenceMetadat.setStartValue(Long.toString(startValue));

        } else {
            sequenceMetadat.setStartValue(null);
        }
    }

    private void setSequenceIncreamentByValue(SequenceMetadata sequenceMetadat) {
        long incrementBy = 0;
        if (!incrementByText.getText().isEmpty()) {
            incrementBy = Long.parseLong(incrementByText.getText());
            sequenceMetadat.setIncrementBy(Long.toString(incrementBy));

        } else {
            sequenceMetadat.setIncrementBy(null);
        }
    }

    private void setSequenceMaxValue(SequenceMetadata sequenceMetadat) {
        long maxValue = 0;
        if (!maxValueText.getText().isEmpty()) {
            maxValue = Long.parseLong(maxValueText.getText());
            sequenceMetadat.setMaxValue(Long.toString(maxValue));

        } else {
            sequenceMetadat.setMaxValue(null);
        }
    }

    private void setSequenceMinValue(SequenceMetadata sequenceMetadat) {
        long minValue = 0;
        if (!minimumValueText.getText().isEmpty()) {

            minValue = Long.parseLong(minimumValueText.getText());

            sequenceMetadat.setMinValue(Long.toString(minValue));

        } else {
            sequenceMetadat.setMinValue(null);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ClearErrorMessage.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class ClearErrorMessage implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            setErrorMsg("");
        }

    }

    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    public boolean isDisposed() {
        if (null != getShell() && !getShell().isDisposed()) {
            return false;
        }
        return true;

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SequenceHelper.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class SequenceHelper implements VerifyListener {

        private boolean isCache;

        private SequenceHelper(boolean isCache) {
            this.isCache = isCache;
        }

        @Override
        public void verifyText(VerifyEvent e) {

            String eChar = e.text + "";
            try {
                if (isCache && e.character == '-') {
                    e.doit = false;
                }

                // Validates the input is long value only.
                if (e.keyCode != 8 && e.keyCode != 127 && e.keyCode != 16777219 && e.keyCode != 16777220
                        && e.character != '-' && Long.parseLong(eChar) < 0) {
                    e.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                e.doit = false;
            }
        }

    }

    private void populatetablelist() {
        UIUtils.displayTablenameList(namespace, tableCombo, true);
    }

    private void populateColumnlist() {
        TableMetaData dtypeNamespace = UIUtils.getTablemetadataFromCombo(namespace, tableCombo);
        if (null != dtypeNamespace) {
            UIUtils.displayColumnList(dtypeNamespace, columnCombo);
        }
    }

    /**
     * Clean up.
     */
    public void cleanUp() {
        sequenceCore.releaseConnection();
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        sourceViewerDecorationSupport.uninstall();
        sourceViewerDecorationSupport = null;
        this.textSqlPreview.unconfigure();
    }

    private void loadAndPopulateColumns() {
        TableMetaData tableMetaDataOfSelected = UIUtils.getTablemetadataFromCombo(namespace, tableCombo);

        if (tableMetaDataOfSelected != null && !tableMetaDataOfSelected.isLoaded()) {
            LoadTableInSequenceWorker worker = new LoadTableInSequenceWorker(MPPDBIDEConstants.CANCELABLEJOB,
                    tableMetaDataOfSelected);
            worker.schedule();
        } else {
            setFetchTableErrLabel(REFRESH_TABLE_DEFAULT, false, SWT.COLOR_BLUE);
            populateColumnlist();
        }
    }

    private void setFetchTableErrLabel(String message, boolean visible, int color) {
        fetchTableErrLabel.setText(message);
        fetchTableErrLabel.setVisible(visible);
        fetchTableErrLabel.setForeground(Display.getCurrent().getSystemColor(color));
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class LoadTableInSequenceWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class LoadTableInSequenceWorker extends PromptPasswordUIWorkerJob {
        private TableMetaData tableMetaDataOfSelected;
        private DBConnection connection = null;

        @Override
        public boolean preUISetup(Object preHandlerObject) {
            boolean preUISetup = super.preUISetup(preHandlerObject);
            if (preUISetup) {
                setFetchTableErrLabel(REFRESH_TABLE_LABEL_IN_PROGRESS, true, SWT.COLOR_BLUE);
            }
            return preUISetup;
        }

        /**
         * Instantiates a new load table in sequence worker.
         *
         * @param family the family
         * @param tableMetaDataOfSelected the table meta data of selected
         */
        public LoadTableInSequenceWorker(Object family, TableMetaData tableMetaDataOfSelected) {
            super(REFRESH_TABLE, family, IMessagesConstants.REFRESH_TABLE_LABEL_FAILURE);
            this.tableMetaDataOfSelected = tableMetaDataOfSelected;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            connection = tableMetaDataOfSelected.getConnectionManager().getFreeConnection();
            tableMetaDataOfSelected.refreshTableDetails(connection);
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            populateColumnlist();
            setFetchTableErrLabel(REFRESH_TABLE_LABEL_SUCCESS, true, SWT.COLOR_BLUE);
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            setFetchTableErrLabel(REFRESH_TABLE_LABEL_FAILURE, true, SWT.COLOR_RED);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            setFetchTableErrLabel(REFRESH_TABLE_LABEL_FAILURE, true, SWT.COLOR_RED);
        }

        @Override
        public void finalCleanup() {
            super.finalCleanup();
            if (connection != null) {
                getDatabase().getConnectionManager().releaseAndDisconnection(connection);
                ;
            }
        }

        @Override
        public void finalCleanupUI() {

        }

        @Override
        protected Database getDatabase() {
            return tableMetaDataOfSelected.getDatabase();
        }
    }

}
