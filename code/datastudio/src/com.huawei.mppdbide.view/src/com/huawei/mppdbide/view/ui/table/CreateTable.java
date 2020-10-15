/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ColumnUtil;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.bl.serverdatacache.TableValidatorRules;
import com.huawei.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.autorefresh.RefreshObjects;
import com.huawei.mppdbide.view.core.SourceEditorKeyListener;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.ControlUtils;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UIMandatoryAttribute;
import com.huawei.mppdbide.view.utils.UIVerifier;
import com.huawei.mppdbide.view.utils.consts.TOOLTIPS;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CreateTable extends Dialog implements IDialogWorkerInteraction {

    /**
     * The text table name.
     */
    protected Text textTableName;

    /**
     * The chk tbl name case.
     */
    protected Button chkTblNameCase;

    /**
     * The cmb schema name.
     */
    protected Combo cmbSchemaName;

    /**
     * The cmb tblspc name.
     */
    protected Combo cmbTblspcName;

    /**
     * The chk ifexists.
     */
    protected Button chkIfexists;

    /**
     * The chk withoid.
     */
    protected Button chkWithoid;

    /**
     * The cmb table type.
     */
    protected Combo cmbTableType;

    /**
     * The spinner fill factor.
     */
    protected Spinner spinnerFillFactor;

    /**
     * The text table description.
     */
    protected StyledText textTableDescription;

    /**
     * The table orientation.
     */
    protected Combo tableOrientation;

    /**
     * The orientation type.
     */
    protected TableOrientation orientationType;

    /**
     * The column UI.
     */
    protected ColumnUI columnUI;

    /**
     * The table column list.
     */
    protected Table tableColumnList;

    /**
     * The data distribution ui.
     */
    protected DataDistributionUI dataDistributionUi;

    /**
     * The constraint UI.
     */
    protected ConstraintUI constraintUI;

    /**
     * The table tbl constraints.
     */
    protected Table tableTblConstraints;

    /**
     * The index ui.
     */
    protected IndexUI indexUi;

    /**
     * The text sql preview.
     */
    protected SourceViewer textSqlPreview;

    /**
     * The current shell.
     */
    protected Shell currentShell;

    /**
     * The tab folder.
     */
    protected TabFolder tabFolder;

    /**
     * The btn next.
     */
    protected Button btnNext;

    /**
     * The btn back.
     */
    protected Button btnBack;

    /**
     * The btn finish.
     */
    protected Button btnFinish;

    /**
     * The new table.
     */
    protected TableMetaData newTable;

    /**
     * The db.
     */
    protected Database db;

    /**
     * The namespace.
     */
    protected Namespace namespace;

    /**
     * The is clm update.
     */
    protected boolean isClmUpdate;

    /**
     * The Constant CREATE_TABLE_GENERAL_INFO.
     */
    protected static final int CREATE_TABLE_GENERAL_INFO = 0;

    /**
     * The Constant CREATE_TABLE_COLUMN_INFO.
     */
    protected static final int CREATE_TABLE_COLUMN_INFO = 1;

    /**
     * The Constant CREATE_TABLE_CONSTRAINTS.
     */
    protected static final int CREATE_TABLE_CONSTRAINTS = 3;

    /**
     * The Constant CREATE_TABLE_INDEXES.
     */
    protected static final int CREATE_TABLE_INDEXES = 4;

    /**
     * The Constant CREATE_TABLE_SQL_PREVIEW.
     */
    protected static final int CREATE_TABLE_SQL_PREVIEW = 5;

    /**
     * The Constant GENERAL.
     */
    protected static final String GENERAL = MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_MSG);

    /**
     * The Constant DATA_DISTRIBUTION.
     */
    protected static final String DATA_DISTRIBUTION = MessageConfigLoader.getProperty(IMessagesConstants.DATA_DIST);

    /**
     * The Constant TABLE_CONSTRAINTS.
     */
    protected static final String TABLE_CONSTRAINTS = MessageConfigLoader.getProperty(IMessagesConstants.TABLE_CONS);

    /**
     * The Constant INDEXES.
     */
    protected static final String INDEXES = MessageConfigLoader.getProperty(IMessagesConstants.INDEX_MSG);

    /**
     * The Constant SQL_PREVIEW.
     */
    protected static final String SQL_PREVIEW = MessageConfigLoader.getProperty(IMessagesConstants.SQL_PREVIEW);

    /**
     * The tbl indexes.
     */
    protected Table tblIndexes;

    private Server server;

    /**
     * The Constant ADD.
     */
    protected static final String ADD = MessageConfigLoader.getProperty(IMessagesConstants.ADD_MSG);

    /**
     * The Constant EDIT.
     */
    protected static final String EDIT = MessageConfigLoader.getProperty(IMessagesConstants.EDIT_MSG);

    /**
     * The Constant DELETE.
     */
    protected static final String DELETE = MessageConfigLoader.getProperty(IMessagesConstants.DELETE_MSG);

    /**
     * The Constant COLUMN_ORIENTATION_INDEX.
     */
    public static final int COLUMN_ORIENTATION_INDEX = TableOrientation.COLUMN.ordinal();

    /**
     * The Constant ROW_ORIENTATION_INDEX.
     */
    protected static final int ROW_ORIENTATION_INDEX = TableOrientation.ROW.ordinal();

    /**
     * The txt error msg.
     */
    protected Text txtErrorMsg;

    /**
     * The status message.
     */
    protected StatusMessage statusMessage;

    /**
     * The menu copy.
     */
    protected MenuItem menuCopy;

    /**
     * The menu select all.
     */
    protected MenuItem menuSelectAll;

    /**
     * The btn add column.
     */
    protected Button btnAddColumn = null;

    /**
     * The btn del column.
     */
    protected Button btnDelColumn = null;

    /**
     * The btn edit column.
     */
    protected Button btnEditColumn = null;

    /**
     * The btn move up.
     */
    protected Button btnMoveUp = null;

    /**
     * The btn move down.
     */
    protected Button btnMoveDown = null;

    /**
     * The composite columns.
     */
    protected Composite compositeColumns;

    /**
     * The composite data distribution.
     */
    protected Composite compositeDataDistribution;

    /**
     * The composite constraints.
     */
    protected Composite compositeConstraints;

    /**
     * The composite indices.
     */
    protected Composite compositeIndices;

    /**
     * The grp table properties.
     */
    protected Group grpTableProperties;

    /**
     * The validator.
     */
    protected TableValidatorRules validator;

    /**
     * The copy edit col name.
     */
    protected String copyEditColName;
    private TableItem row;

    /**
     * The table UI validator.
     */
    protected TableUIValidator tableUIValidator;

    /**
     * The builder.
     */
    protected StringBuilder builder;

    /**
     * The is constraints update.
     */
    protected boolean isConstraintsUpdate;

    /**
     * The is constraint data present.
     */
    protected boolean isConstraintDataPresent;
    private SQLSourceViewerDecorationSupport sourceViewerDecorationSupport;

    /**
     * The Constant ORIENTATION.
     */
    protected static final String ORIENTATION = "orientation";

    /**
     * Instantiates a new creates the table.
     *
     * @param shell the shell
     * @param server the server
     * @param ns the ns
     * @param type the type
     */
    @Inject
    public CreateTable(Shell shell, Server server, Namespace ns, OBJECTTYPE type) {
        super(shell);
        newTable = CreateTableFactory.createTable(type, ns);
        indexUi = CreateTableFactory.createIndexUI(type, newTable, server);
        this.server = server;
        this.namespace = ns;
        this.db = namespace.getDatabase();

        // Added for display data

    }

    /**
     * Instantiates a new creates the table.
     *
     * @param shell the shell
     * @param server the server
     * @param ns the ns
     */
    public CreateTable(Shell shell, Server server, Namespace ns) {
        this(shell, server, ns, OBJECTTYPE.TABLEMETADATA);
        newTable.setOrientation(TableOrientation.ROW);

    }

    /**
     * Open.
     *
     * @return the object
     */
    public Object open() {
        Shell parent = getParent();

        currentShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

        // Create table
        currentShell.setText(getTitleText());
        currentShell.setImage(getWindowImage());
        createTableGUI(currentShell);
        registerTableOrientationListener(getTableOrientationCombo());

        Monitor monitor = parent.getMonitor();

        Rectangle bounds = monitor.getBounds();

        int monitorHeight = bounds.height;
        // Height of Dialog is dynamic. For screen Resolutions having height
        // below 800 pixels, height of
        // composite is set getting the monitor's height minus a buffer size 100
        // in case there are two taskbars such as in some linux machines.

        // Width of dialog is fixed to 700 pixels.
        if (monitorHeight <= 800) {
            currentShell.setSize(700, monitorHeight - 100);
        } else {
            currentShell.setSize(getSize());
        }

        Rectangle rect = currentShell.getBounds();
        int xCordination = bounds.x + (bounds.width - rect.width) / 2;
        int yCordination = bounds.y + (bounds.height - rect.height) / 2;
        /* Place the window in the centre of primary monitor */
        currentShell.setLocation(xCordination, yCordination);

        currentShell.open();
        this.validator = new TableValidatorRules(newTable);
        this.tableUIValidator = new TableUIValidator(indexUi, constraintUI, validator, columnUI, dataDistributionUi);
        this.tableUIValidator.setTableMetatadata(newTable);
        Display display = parent.getDisplay();
        disposeDialog(display);

        return currentShell;
    }

    /**
     * Dispose dialog.
     *
     * @param display the display
     */
    private void disposeDialog(Display display) {
        boolean isDisposed = currentShell.isDisposed();
        while (!isDisposed) {
            sleepDisplay(display);
            isDisposed = currentShell.isDisposed();
        }
    }

    /**
     * Sleep display.
     *
     * @param display the display
     */
    private void sleepDisplay(Display display) {
        if (!display.readAndDispatch()) {
            display.sleep();
        }
    }

    /**
     * Gets the size.
     *
     * @return the size
     */
    protected Point getSize() {
        return new Point(700, 738);
    }

    /**
     * Gets the title text.
     *
     * @return the title text
     */
    protected String getTitleText() {
        return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_TABLE);
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {

        return IconUtility.getIconImage(IiconPath.ICO_TABLE, this.getClass());
    }

    /**
     * Register table orientation listener.
     *
     * @param tableOrientationCombo the table orientation combo
     */
    protected void registerTableOrientationListener(final Combo tableOrientationCombo) {

        tableOrientationCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                newTable.setOrientation(TableOrientation.valueOf(tableOrientationCombo.getText()));
                if (COLUMN_ORIENTATION_INDEX == tableOrientationCombo.getSelectionIndex()) {
                    columnOrientationSelected();
                } else {
                    // Setting value orientationType is ROW
                    setRowOrientationValues();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });

    }

    /**
     * Column orientation selected.
     */
    private void columnOrientationSelected() {
        orientationType = TableOrientation.COLUMN;
        newTable.setOrientation(orientationType);
        spinnerFillFactor.setEnabled(false);
        chkWithoid.setEnabled(false);
        chkWithoid.setSelection(false);

        cmbTableType.setEnabled(true);
        shoeMsgOnEditOrientationCahange();
        tableUIValidator.indexHandleRowColumnSelection(compositeIndices);

        tableUIValidator.constraintHandleColumnSelection(compositeConstraints);

        tableUIValidator.columnsComponents();
        tableUIValidator.distributionHandleRowColSelection(compositeDataDistribution);
        UIUtils.displayTablespaceList(db, cmbTblspcName, true, orientationType);
        tableUIValidator.removeDataDistributionOnOrientationChange();
    }

    /**
     * Gets the table orientation combo.
     *
     * @return the table orientation combo
     */
    protected Combo getTableOrientationCombo() {
        return tableOrientation;
    }

    /**
     * Creates the column pannel.
     */
    protected void createColumnPannel() {
        Group grpColumns = columnUI.getGrpControl();
        grpColumns.setLayout(new GridLayout(2, false));
        GridData grpColumnsGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        grpColumnsGD.heightHint = 150;
        grpColumns.setLayoutData(grpColumnsGD);

        Composite tableForClmnComposite = new Composite(grpColumns, SWT.NONE);
        tableForClmnComposite.setLayout(new GridLayout(1, false));
        GridData tableForClmnCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableForClmnCompositeGD.widthHint = 480;
        tableForClmnComposite.setLayoutData(tableForClmnCompositeGD);
        addTableForColumnList(tableForClmnComposite);

        Composite buttonsForClmnComposite = new Composite(grpColumns, SWT.NONE);
        buttonsForClmnComposite.setLayout(new GridLayout(1, false));
        GridData buttonsForClmnCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonsForClmnComposite.setLayoutData(buttonsForClmnCompositeGD);
        addButtonsForColumnUi(buttonsForClmnComposite);

    }

    /**
     * Adds the buttons for column ui.
     *
     * @param grpColumns the grp columns
     */
    private void addButtonsForColumnUi(Composite grpColumns) {
        addAddColumnBtnUi(grpColumns);

        addDeleteColumnBtnUi(grpColumns);

        // DTS2014103006724 start
        addEditColumnUi(grpColumns);
        // DTS2014103006724 end
        addMoveUoColumnUi(grpColumns);

        addMoveDownClmUi(grpColumns);

    }

    /**
     * Adds the move down clm ui.
     *
     * @param grpColumns the grp columns
     */
    private void addMoveDownClmUi(Composite grpColumns) {
        btnMoveDown = new Button(grpColumns, SWT.NONE);
        GridData btnMoveDownGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnMoveDownGD.heightHint = 30;
        btnMoveDown.setLayoutData(btnMoveDownGD);
        btnMoveDown.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_MOVE_DOWN_COLUMN_001");
        btnMoveDown.addSelectionListener(new MoveDownSelectionListener());
        btnMoveDown.setText(MessageConfigLoader.getProperty(IMessagesConstants.DOWN_MSG));
    }

    /**
     * Adds the move uo column ui.
     *
     * @param grpColumns the grp columns
     */
    private void addMoveUoColumnUi(Composite grpColumns) {
        btnMoveUp = new Button(grpColumns, SWT.NONE);
        GridData btnMoveUpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnMoveUpGD.heightHint = 30;
        btnMoveUp.setLayoutData(btnMoveUpGD);
        btnMoveUp.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_MOVE_UP_COLUMN_001");
        btnMoveUp.addSelectionListener(new MoveUpSelectionListener());
        btnMoveUp.setText(MessageConfigLoader.getProperty(IMessagesConstants.UP_MSG));
    }

    /**
     * Adds the edit column ui.
     *
     * @param grpColumns the grp columns
     */
    private void addEditColumnUi(Composite grpColumns) {
        btnEditColumn = new Button(grpColumns, SWT.NONE);
        GridData btnEditColumnGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnEditColumnGD.heightHint = 30;
        btnEditColumn.setLayoutData(btnEditColumnGD);
        btnEditColumn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_EDIT_COLUMN_001");
        btnEditColumn.addSelectionListener(new EditColumnSelectionListener());
        btnEditColumn.setText(EDIT);
    }

    /**
     * Adds the delete column btn ui.
     *
     * @param grpColumns the grp columns
     */
    private void addDeleteColumnBtnUi(Composite grpColumns) {
        btnDelColumn = new Button(grpColumns, SWT.NONE);
        GridData btnDelColumnGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnDelColumnGD.heightHint = 30;
        btnDelColumn.setLayoutData(btnDelColumnGD);
        btnDelColumn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_DEL_COLUMN_001");
        btnDelColumn.addSelectionListener(new DelColumnSelectionListener());
        btnDelColumn.setText(DELETE);
    }

    /**
     * Adds the add column btn ui.
     *
     * @param grpColumns the grp columns
     */
    private void addAddColumnBtnUi(Composite grpColumns) {
        btnAddColumn = new Button(grpColumns, SWT.NONE);
        GridData btnAddColumnGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnAddColumnGD.heightHint = 30;
        btnAddColumn.setLayoutData(btnAddColumnGD);
        btnAddColumn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_ADD_COLUMN_001");
        btnAddColumn.addSelectionListener(new AddColumnBtnSelectionListener());
        btnAddColumn.setText(ADD);
    }

    /**
     * Adds the table for column list.
     *
     * @param grpColumns the grp columns
     */
    private void addTableForColumnList(Composite grpColumns) {
        tableColumnList = new Table(grpColumns, SWT.BORDER | SWT.FULL_SELECTION);
        tableColumnList.setLayout(new GridLayout(1, false));
        GridData tableColumnListGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableColumnList.setLayoutData(tableColumnListGD);
        tableColumnList.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CREATE_TBL_COLUMN_LIST_001");

        tableColumnList.setHeaderVisible(true);
        tableColumnList.setLinesVisible(true);
        tableColumnList.addMouseListener(new TableColumnListMouseListener());

        TableColumn tblclmnColumnName = new TableColumn(tableColumnList, SWT.NONE);

        tblclmnColumnName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CREATE_TBL_COLUMN_LIST_COL_NAME_001");
        tblclmnColumnName.setWidth(100);
        tblclmnColumnName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CLM_NAME));

        TableColumn tblclmnColumnDtype = new TableColumn(tableColumnList, SWT.NONE);
        tblclmnColumnDtype.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CREATE_TBL_COLUMN_LIST_COL_DATATYPE_001");
        tblclmnColumnDtype.setWidth(100);
        tblclmnColumnDtype.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_DATA_TYPE));

        TableColumn tblclmnColumnConstraint = new TableColumn(tableColumnList, SWT.NONE);
        tblclmnColumnConstraint.setData(MPPDBIDEConstants.SWTBOT_KEY,
                "ID_TBL_COL_CREATE_TBL_COLUMN_LIST_COL_CONSTRAINT_001");
        tblclmnColumnConstraint.setWidth(200);
        tblclmnColumnConstraint.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CONSTRAINT));

        TableColumn tblclmnColumnComment = new TableColumn(tableColumnList, SWT.NONE);
        tblclmnColumnName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CREATE_TBL_COLUMN_LIST_COL_COLUMNDESC_001");
        tblclmnColumnComment.setWidth(180);
        tblclmnColumnComment.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_CLM_COMMENT));
        columnUI.setColumnUITable(tableColumnList);
        new SwtTableDataToolTipListener(tableColumnList);
    }

    /**
     * The listener interface for receiving tableColumnListMouse events. The
     * class that is interested in processing a tableColumnListMouse event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addTableColumnListMouseListener<code> method. When the
     * tableColumnListMouse event occurs, that object's appropriate method is
     * invoked.
     *
     * TableColumnListMouseEvent
     */
    private class TableColumnListMouseListener implements MouseListener {

        @Override
        public void mouseUp(MouseEvent event) {

        }

        @Override
        public void mouseDown(MouseEvent event) {

        }

        @Override
        public void mouseDoubleClick(MouseEvent event) {
            if (!isTableNameInValid()) {
                if (!isClmUpdate) {
                    setErrorMsg("");
                }
                editColumn();
            } else {
                return;
            }

        }
    }

    /**
     * The listener interface for receiving moveDownSelection events. The class
     * that is interested in processing a moveDownSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addMoveDownSelectionListener<code> method. When the
     * moveDownSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * MoveDownSelectionEvent
     */
    private class MoveDownSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            moveColumn(false);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }
    }

    /**
     * The listener interface for receiving moveUpSelection events. The class
     * that is interested in processing a moveUpSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addMoveUpSelectionListener<code>
     * method. When the moveUpSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * MoveUpSelectionEvent
     */
    private class MoveUpSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            moveColumn(true);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving editColumnSelection events. The
     * class that is interested in processing a editColumnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addEditColumnSelectionListener<code> method. When the
     * editColumnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * EditColumnSelectionEvent
     */
    private class EditColumnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            editColumn();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving delColumnSelection events. The class
     * that is interested in processing a delColumnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addDelColumnSelectionListener<code> method. When the
     * delColumnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * DelColumnSelectionEvent
     */
    private class DelColumnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            removeColumn();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving addColumnBtnSelection events. The
     * class that is interested in processing a addColumnBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addAddColumnBtnSelectionListener<code> method. When the
     * addColumnBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * AddColumnBtnSelectionEvent
     */
    private class AddColumnBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            addColumn();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Edits the column.
     */
    protected void editColumn() {
        if (0 == tableColumnList.getSelectionCount()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_EDIT_MSG));
            return;
        } else {
            isClmUpdate = true;
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_EDIT_MESSAGE));
            enableDisableItems(MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_MSG),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_MSG), false);

            tableColumnList.getSelection()[0].setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            initcolumnMetaData();
            showInfoMessage();

        }
    }

    /**
     * Show info message.
     */
    private void showInfoMessage() {
        String clm = "column";
        if (validateConstraintPartition()) {

            showInfoMessageToUserOnEditOrientationChange(clm, false);
        }
    }

    /**
     * Validate constraint partition.
     *
     * @return true, if successful
     */
    private boolean validateConstraintPartition() {
        return validateConstraintIndex() || validatePartitionDistribution();
    }

    /**
     * Validate constraint index.
     *
     * @return true, if successful
     */
    private boolean validateConstraintIndex() {
        return isConstraintDataPresent || getIndexOnEdit();
    }

    /**
     * Validate partition distribution.
     *
     * @return true, if successful
     */
    private boolean validatePartitionDistribution() {
        return isPartitionPresent() || isDataPresentOnDataDistribution();
    }

    /**
     * Initcolumn meta data.
     */
    protected void initcolumnMetaData() {
        String copyEditClm = "(?<!\\w)";
        String copyEditClmEnd = "(?!\\w)";
        ColumnMetaData columnMetaDataItem = this.newTable.getColumns().getItem(tableColumnList.getSelectionIndex());

        columnUI.setColumnDetails(columnMetaDataItem,

                tableColumnList.getSelectionIndex());

        copyEditColName = copyEditClm + columnMetaDataItem.getDisplayName() + copyEditClmEnd;
        isConstraintDataPresent(columnMetaDataItem);

    }

    /**
     * Checks if is data on other tabs present.
     *
     * @return true, if is data on other tabs present
     */
    protected boolean isDataOnOtherTabsPresent() {
        if (validateCountOfObjects()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Validate count of objects.
     *
     * @return true, if successful
     */
    private boolean validateCountOfObjects() {
        return dataDistributionUi.validateTableHmCols() || (this.tableTblConstraints.getItemCount() > 0)
                || (this.tblIndexes.getItemCount() > 0);
    }

    /**
     * Checks if is data present on data distribution.
     *
     * @return true, if is data present on data distribution
     */
    protected boolean isDataPresentOnDataDistribution() {
        boolean metaDataPresentOnEdit = false;
        if (dataDistributionUi.validateTableHmCols()) {
            metaDataPresentOnEdit = isMetaDataPresentOnEdit(copyEditColName, dataDistributionUi.getTableHmSelCols());
        }
        return metaDataPresentOnEdit;
    }

    /**
     * Checks if is meta data present on edit.
     *
     * @param tempName the temp name
     * @param tblList the tbl list
     * @return true, if is meta data present on edit
     */
    protected boolean isMetaDataPresentOnEdit(String tempName, Table tblList) {

        int itemCount = tblList.getItemCount();
        boolean matched = false;
        Pattern pattern = Pattern.compile(tempName);
        for (int index = 0; index < itemCount; index++) {
            Matcher matcher = pattern.matcher(tblList.getItem(index).getText());
            matched = matcher.find();
            if (matched) {
                break;
            }
        }

        return matched;
    }

    /**
     * Checks if is constraint data present.
     *
     * @param columnMetaDataItem the column meta data item
     * @return true, if is constraint data present
     */
    protected boolean isConstraintDataPresent(ColumnMetaData columnMetaDataItem) {
        if (validateTableConstraints(columnMetaDataItem)) {
            isConstraintDataPresent = isMetaDataPresentOnEdit(copyEditColName, tableTblConstraints);
        }
        return isConstraintDataPresent;
    }

    /**
     * Validate table constraints.
     *
     * @param columnMetaDataItem the column meta data item
     * @return true, if successful
     */
    private boolean validateTableConstraints(ColumnMetaData columnMetaDataItem) {
        return validateConstraint(columnMetaDataItem);
    }

    /**
     * Gets the index on edit.
     *
     * @return the index on edit
     */
    protected boolean getIndexOnEdit() {
        boolean isIndexDataPresent = false;
        if (null != row) {
            indexUi.txtWhereExpr.setText("");
            isIndexDataPresent = isMetaDataPresentOnEdit(copyEditColName.replace("\"", ""), tblIndexes);

        }
        return isIndexDataPresent;
    }

    /**
     * Show info message to user on edit orientation change.
     *
     * @param name the name
     * @param ispartiton the ispartiton
     */
    protected void showInfoMessageToUserOnEditOrientationChange(String name, boolean ispartiton) {

        StringBuilder toggleOrientationColumn = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        toggleOrientationColumn.append(name);
        builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        builder.append(MPPDBIDEConstants.SPACE_CHAR);
        addDataDistibutionTab();
        addConstraintsTab();
        addIndexTab();

        addPartitionTab(ispartiton);
        builder.deleteCharAt(builder.length() - 1);
        if (!builder.toString().isEmpty()) {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_USER_MSG_ON_TABLE_UPDATE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_USER_MSG_ON_TABLE_UPDATE_BODY,
                            toggleOrientationColumn.toString(), builder.toString()),
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
        }
    }

    /**
     * Adds the partition tab.
     *
     * @param ispartiton the ispartiton
     */
    private void addPartitionTab(boolean ispartiton) {
        if (ispartiton) {
            builder.append(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB));
            builder.append(MPPDBIDEConstants.COMMA_SEPARATE);
        }
    }

    /**
     * Adds the index tab.
     */
    private void addIndexTab() {
        if (this.tblIndexes.getItemCount() > 0) {
            builder.append(MessageConfigLoader.getProperty(IMessagesConstants.INDEXES_TAB));
            builder.append(MPPDBIDEConstants.COMMA_SEPARATE);
        }
    }

    /**
     * Adds the constraints tab.
     */
    private void addConstraintsTab() {
        if (this.tableTblConstraints.getItemCount() > 0) {
            builder.append(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_TAB));
            builder.append(MPPDBIDEConstants.COMMA_SEPARATE);
        }
    }

    /**
     * Adds the data distibution tab.
     */
    private void addDataDistibutionTab() {
        if (dataDistributionUi.validateTableHmCols()) {

            builder.append(MessageConfigLoader.getProperty(IMessagesConstants.DATA_DISTRIBUTION_TAB));
            builder.append(",");

        }
    }

    /**
     * Removes the column.
     */
    protected void removeColumn() {
        if (isClmUpdate) {
            removeColumnForUpdateClm();

        } else {
            removeColumnForClm();
        }
        isClmUpdate = false;
    }

    /**
     * Removes the column for clm.
     */
    private void removeColumnForClm() {
        if (0 == tableColumnList.getSelectionCount()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_DELETE_MSG));
            return;
        }

        initcolumnMetaData();
        removeExpressions();
        this.newTable.removeColumnByIdx(tableColumnList.getSelectionIndex());
        removeColumnInAllColumnTables(tableColumnList.getSelectionIndex());
    }

    /**
     * Removes the column for update clm.
     */
    private void removeColumnForUpdateClm() {
        int lengthOfItems = 0;
        columnUI.clearColumnDetails();
        enableDisableItems(ADD, DELETE, true);
        tableColumnList.getItemCount();
        lengthOfItems = tableColumnList.getItems().length;

        for (int index = 0; index < lengthOfItems; index++) {
            tableColumnList.getItems()[index].setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
    }

    /**
     * Creates the constraint pannel.
     *
     * @param compConstraints the comp constraints
     */
    protected void createConstraintPannel(Composite compConstraints) {
        Group grpAddConstrains = constraintUI.getGrpControl(compConstraints);
        grpAddConstrains.setLayout(new GridLayout(2, false));
        GridData grpAddConstrainsGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        grpAddConstrainsGD.heightHint = 200;
        grpAddConstrains.setLayoutData(grpAddConstrainsGD);

        Composite tableAddConstrantComposite = new Composite(grpAddConstrains, SWT.NONE);
        tableAddConstrantComposite.setLayout(new GridLayout(1, false));
        GridData tableAddConstrantCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableAddConstrantComposite.setLayoutData(tableAddConstrantCompositeGD);

        tableTblConstraints = new Table(tableAddConstrantComposite, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tableTblConstraintsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableTblConstraintsGD.widthHint = 455;
        tableTblConstraints.setLayoutData(tableTblConstraintsGD);
        tableTblConstraints.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CREATE_TBL_CONSTRAINT_001");
        tableTblConstraints.setHeaderVisible(true);
        tableTblConstraints.setLinesVisible(true);

        TableColumn tblclmnConstriant = new TableColumn(tableTblConstraints, SWT.NONE);
        tblclmnConstriant.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CREATE_TBL_CONSTRAINT_COL_CONSTRAINT_001");
        tblclmnConstriant.setWidth(455);
        tblclmnConstriant.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CONSTRAINT));
        constraintUI.setConstraintUITable(tableTblConstraints);

        createConstraintButtons(grpAddConstrains);
    }

    private void createConstraintButtons(Group grpAddConstrains) {
        Composite tableConstraintBtnsComposite = new Composite(grpAddConstrains, SWT.NONE);
        tableConstraintBtnsComposite.setLayout(new GridLayout(1, false));
        GridData tableConstraintBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableConstraintBtnsCompositeGD.verticalAlignment = SWT.CENTER;
        tableConstraintBtnsComposite.setLayoutData(tableConstraintBtnsCompositeGD);

        Button btnAddConstraint = new Button(tableConstraintBtnsComposite, SWT.NONE);
        GridData btnAddConstraintGD = new GridData(SWT.FILL, SWT.TOP, true, true);
        btnAddConstraintGD.heightHint = 30;
        btnAddConstraint.setLayoutData(btnAddConstraintGD);
        btnAddConstraint.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_TBL_CONSTRAINT_ADD_CONSTRAINT_001");
        btnAddConstraint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                setErrorMsg("");
                addConstraint();
            }
        });
        btnAddConstraint.setText(ADD);

        Button btnDelConstraint = new Button(tableConstraintBtnsComposite, SWT.NONE);
        GridData btnDelConstraintGD = new GridData(SWT.FILL, SWT.CENTER, true, true);
        btnDelConstraintGD.heightHint = 30;
        btnDelConstraint.setLayoutData(btnDelConstraintGD);
        btnDelConstraint.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_TBL_CONSTRAINT_DEL_CONSTRAINT_001");
        btnDelConstraint.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                setErrorMsg("");
                removeConstraint();
            }
        });
        btnDelConstraint.setText(DELETE);

        // DTS2014103006724 start
        Button btnEditConstrnt = new Button(tableConstraintBtnsComposite, SWT.NONE);
        GridData btnEditConstrntGD = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
        btnEditConstrntGD.heightHint = 30;
        btnEditConstrnt.setLayoutData(btnEditConstrntGD);
        btnEditConstrnt.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_TBL_CONSTRAINT_ADD_CONSTRAINT_001");
        btnEditConstrnt.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                setErrorMsg("");
                editConstraint();
            }
        });
        btnEditConstrnt.setText(EDIT);

        // DTS2014103006724 end

    }

    /**
     * Enable disable items.
     *
     * @param name1 the name 1
     * @param name2 the name 2
     * @param value the value
     */
    private void enableDisableItems(String name1, String name2, boolean value) {
        btnAddColumn.setText(name1);
        btnDelColumn.setText(name2);
        btnEditColumn.setVisible(value);
        btnMoveDown.setVisible(value);
        btnMoveUp.setVisible(value);
        setTabEnableDisable(value);

        btnNext.setEnabled(value);
        btnBack.setEnabled(value);
        btnFinish.setEnabled(value);
    }

    /**
     * Sets the tab enable disable.
     *
     * @param value the new tab enable disable
     */
    private void setTabEnableDisable(boolean value) {
        int curTab = tabFolder.getSelectionIndex();
        if (curTab == CREATE_TABLE_COLUMN_INFO) {
            setTableFolderEnable(value, curTab);
        }
    }

    /**
     * Sets the table folder enable.
     *
     * @param value the value
     * @param curTab the cur tab
     */
    private void setTableFolderEnable(boolean value, int curTab) {
        for (int index = 0; index < tabFolder.getItemCount(); index++) {
            enableTabFolder(value, curTab, index);
        }
    }

    /**
     * Enable tab folder.
     *
     * @param value the value
     * @param curTab the cur tab
     * @param index the i
     */
    private void enableTabFolder(boolean value, int curTab, int index) {
        if (curTab != index) {
            tabFolder.getItem(index).getControl().setEnabled(value);
        }
    }

    /**
     * Adds the constraint.
     */
    protected void addConstraint() {
        TableItem tableItemRow = null;
        ConstraintMetaData cons = null;
        constraintUI.setParentTable(newTable);
        cons = constraintUI.getConstraint(!isConstraintsUpdate);

        if (null == cons) {
            // DTS2014103006593 start
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CONSTRAINT_MSG));
            // DTS2014103006593 end
            return;
        }

        tableItemRow = new TableItem(tableTblConstraints, SWT.NONE);
        tableItemRow.setText(cons.formConstraintString());

        this.newTable.addConstraint(cons);
        constraintUI.setParentTable(null);
        // DTS2014103006724 start
        constraintUI.clearConstraintData();
        // DTS2014103006724 end
    }

    /**
     * Edits the constraint.
     */
    protected void editConstraint() {
        int selectedindex = tableTblConstraints.getSelectionIndex();

        if (selectedindex > -1) {
            constraintUI.setConstraintData(newTable.getConstraints().getItem(selectedindex));
            removeConstraint();
        } else {
            // DTS2016012702337 Fix starts
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CONST_EDIT_MSG));
            // DTS2016012702337 Fix ends
        }
        isConstraintsUpdate = true;
    }

    // DTS2014103006724 end

    /**
     * Removes the constraint.
     */
    protected void removeConstraint() {
        int selected = UIUtils.removeSelectedCol(tableTblConstraints);
        validateAndRemoveConstraint(selected);
        isConstraintsUpdate = false;
    }

    /**
     * Validate and remove constraint.
     *
     * @param selected the selected
     */
    private void validateAndRemoveConstraint(int selected) {
        if (selected >= 0) {
            this.newTable.removeConstraint(selected);
        } else {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CONST_DELETE));
        }
    }

    /**
     * Adds the column.
     */
    protected void addColumn() {
        setErrorMsg("");

        ColumnMetaData newTempColumn = columnUI.getDBColumn(this.tableColumnList, isClmUpdate,
                newTable.getOrientation());
        setErrorMsg("");

        if (columnUI.isDuplicateName()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_COLUMN_EXISTS,
                    columnUI.getColumnName().getText()));
            columnUI.setDuplicateNameDefaultValue();
            return;
        }

        if (ColumnUtil.isColumnNameValid(newTempColumn)) {
            // start DTS2014103006593
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_VALID_NAME));
            // DTS2014103006593 end
            return;
        }
        if (ColumnUtil.isDataTypeValid(newTempColumn)) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.PLS_SELECT_DATA_TYPE));
            return;
        }
        if (isClmUpdate) {
            enableDisableItems(ADD, DELETE, true);
        }
        // DTS2014103006724 start
        updateColumnByIndex(newTempColumn);
        // DTS2014103006724 end
        addColumnInAllColumnTables(newTempColumn.columnDetails(3, true));
        columnUI.clearColumnDetails();
        isClmUpdate = false;
    }

    private void updateColumnByIndex(ColumnMetaData newTempColumn) {
        if (columnUI.getEditIndex() > -1) {
            if (isClmUpdate) {
                this.newTable.removeColumnByIdx(columnUI.getEditIndex());
            }

            this.newTable.addColumnAtIndex(newTempColumn, columnUI.getEditIndex());
        } else {
            this.newTable.addColumn(newTempColumn);
        }
    }

    /**
     * Move column.
     *
     * @param up the up
     */
    protected void moveColumn(boolean up) {

        int selectedIndex = tableColumnList.getSelectionIndex();
        int targetIndex = up ? selectedIndex - 1 : selectedIndex + 1;

        /*
         * boundary check for none selected / top most item moving up / bottom
         * most item moving down
         */
        if (validateColumnDetails(selectedIndex, targetIndex)) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NO_CLM_MSG));
            return;
        }

        moveSelectedClm(up, selectedIndex, targetIndex);

    }

    /**
     * Move selected clm.
     *
     * @param up the up
     * @param selectedIndex the selected index
     * @param targetIndex the target index
     */
    private void moveSelectedClm(boolean up, int selectedIndex, int targetIndex) {
        this.newTable.moveColumn(selectedIndex, up);

        updateColumnDetails(selectedIndex, targetIndex);
    }

    /**
     * Update column details.
     *
     * @param selectedIndex the selected index
     * @param targetIndex the target index
     */
    private void updateColumnDetails(int selectedIndex, int targetIndex) {
        /* moving the item */
        updateColumnDetailsInAllColumnTables(selectedIndex,
                newTable.getColumns().getItem(selectedIndex).columnDetails(3, true));
        updateColumnDetailsInAllColumnTables(targetIndex,
                newTable.getColumns().getItem(targetIndex).columnDetails(3, true));

        tableColumnList.setSelection(targetIndex);
    }

    /**
     * Validate column details.
     *
     * @param selectedIndex the selected index
     * @param targetIndex the target index
     * @return true, if successful
     */
    private boolean validateColumnDetails(int selectedIndex, int targetIndex) {
        return selectedIndex == -1 || targetIndex < 0 || targetIndex >= tableColumnList.getItemCount();
    }

    /**
     * Update table fields.
     *
     * @param event the event
     */
    protected void updateTableFields(int event) {
        if (event == CREATE_TABLE_SQL_PREVIEW) {
            createTableSqlPreview();
        }
    }

    /**
     * Creates the table sql preview.
     */
    protected void createTableSqlPreview() {
        String unLogged = MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_UNLOGGED);
        String tableName = textTableName.getText();

        tableName = checkTableName(tableName);

        this.newTable.setName(tableName);
        setTablespace();

        this.newTable.setUnLoggedTable(cmbTableType.getText().equals(unLogged));

        this.newTable.setHasOid(chkWithoid.getSelection());
        this.newTable.setIfExists(chkIfexists.getSelection());
        setFillFactor();
        this.newTable.setOrientation(ConvertToOrientation.convertToOrientationEnum(tableOrientation.getText()));

        this.newTable.setDescription(textTableDescription.getText());

        StringBuilder distributeOpt = new StringBuilder(256);

        this.dataDistributionUi.getDistributionString(distributeOpt);

        this.newTable.setDistributeOptions(distributeOpt.toString());
        btnNext.setVisible(false);
        btnBack.setVisible(true);

        this.textSqlPreview.setDocument(new Document(formQueries()));
        setDecoration();
        SQLDocumentPartitioner.connectDocument(this.textSqlPreview.getDocument(), 0);
    }

    /**
     * Sets the fill factor.
     */
    private void setFillFactor() {
        if (spinnerFillFactor.isEnabled()) {

            this.newTable.setFillfactor(spinnerFillFactor.getSelection());
        } else {
            // Seted max value while when fill factor is disable fill factor
            // value will not be taken during form qury
            this.newTable.setFillfactor(MPPDBIDEConstants.MAX_FILL_FACTOR);
        }
    }

    /**
     * Sets the tablespace.
     */
    private void setTablespace() {
        if (0 != cmbTblspcName.getSelectionIndex()) {
            this.newTable.setTablespaceName(cmbTblspcName.getText());
        } else {
            this.newTable.setTablespaceName(null);
        }
    }

    /**
     * Check table name.
     *
     * @param tableNameParam the table name param
     * @return the string
     */
    private String checkTableName(String tableNameParam) {
        String tableName = tableNameParam;
        if (!chkTblNameCase.getSelection()) {
            tableName = tableName.toLowerCase(Locale.ENGLISH);
        }
        return tableName;
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
     * Form queries.
     *
     * @return the string
     */
    public String formQueries() {
        StringBuilder queries = new StringBuilder(newTable.formCreateQuery());
        queries.append(newTable.formTableCommentQuery());
        queries.append(newTable.formColumnCommentQuery());
        queries.append(newTable.formIndexQueries());

        return queries.toString();
    }

    /**
     * Adds the control pannel.
     *
     * @param composite the composite
     */
    protected void addControlPannel(Composite composite) {

        createControlButtons(composite);

        FinishBtnSelectionAdapter finishBtnSelectionAdapter = new FinishBtnSelectionAdapter();

        btnFinish.addSelectionListener(finishBtnSelectionAdapter);

        BtnNxtSelectionAdapter nxtSelectionAdapter = new BtnNxtSelectionAdapter();

        btnNext.addSelectionListener(nxtSelectionAdapter);

        BtnBackSelectionAdapter btnBackSelectionAdapter = new BtnBackSelectionAdapter();

        btnBack.addSelectionListener(btnBackSelectionAdapter);

        tabFolder.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int curTab = tabFolder.getSelectionIndex();
                buttonToggling(curTab);
                if (isTableNameInValid()) {
                    return;
                }

                if (!isClmUpdate) {
                    setErrorMsg("");
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });

    }

    /**
     * Creates the control buttons.
     *
     * @param composite the composite
     */
    protected void createControlButtons(Composite composite) {
        Composite buttonControlComposite = new Composite(composite, SWT.NONE);
        buttonControlComposite.setLayout(new GridLayout(3, false));
        GridData buttonControlCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        buttonControlCompositeGD.heightHint = 40;
        buttonControlComposite.setLayoutData(buttonControlCompositeGD);

        btnBack = new Button(buttonControlComposite, SWT.NONE);
        GridData btnBackGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnBack.setLayoutData(btnBackGD);
        btnBack.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_TBL_BACK_001");
        btnBack.setVisible(false);

        btnNext = new Button(buttonControlComposite, SWT.NONE);
        GridData btnNextGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnNext.setLayoutData(btnNextGD);
        btnNext.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_TBL_NEXT_001");

        btnFinish = new Button(buttonControlComposite, SWT.NONE);
        GridData btnFinishGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnFinish.setLayoutData(btnFinishGD);
        btnFinish.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_COL_CREATE_TBL_FINISH_001");

        btnNext.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NEXT_BTN));
        btnFinish.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_FINISH_BTN));
        btnBack.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_BACK_BTN));
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BtnBackSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class BtnBackSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();
            buttonToggling(curTab - 1);
            if (curTab > 0) {
                updateTableFields(curTab - 1);
                if (CREATE_TABLE_INDEXES == curTab - 1) {
                    indexUi.refreshColumns();
                }
                tabFolder.setSelection(curTab - 1);
                // DTS2014102906540 start
                setFocusOnText(curTab - 1);
                // DTS2014102906540 end
            }

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BtnNxtSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class BtnNxtSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();
            setErrorMsg("");
            buttonToggling(curTab + 1);
            switch (curTab) {
                case 0: {
                    try {
                        validateTableFillFactor();
                    } catch (DatabaseOperationException e1) {
                        setErrorMsg(e1.getMessage());
                        return;
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            if (curTab + 1 != tabFolder.getItemCount()) {
                updateTableFields(curTab + 1);
                if (CREATE_TABLE_INDEXES == curTab + 1) {
                    indexUi.refreshColumns();
                }
                tabFolder.setSelection(curTab + 1);

                // DTS2014102906540 start
                setFocusOnText(curTab + 1);
                // DTS2014102906540 end
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TabFolderSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class TabFolderSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            int curTab = tabFolder.getSelectionIndex();
            // DTS2014102906540 start
            setFocusOnText(curTab);
            // DTS2014102906540 end

            if (CREATE_TABLE_GENERAL_INFO != curTab) {
                try {
                    validateTableFillFactor();
                } catch (DatabaseOperationException e1) {
                    setErrorMsg(e1.getMessage());
                    event.doit = false;
                    tabFolder.setSelection(CREATE_TABLE_GENERAL_INFO);
                    return;
                }
            }

            updateTableFields(curTab);
            if (CREATE_TABLE_INDEXES == curTab) {
                indexUi.refreshColumns();
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
            enableBackButtonForGeneral(curTab);
            enableNextButtonForSqlPreview(curTab);
        }

    }

    /**
     * Enable next button for sql preview.
     *
     * @param curTab the cur tab
     */
    private void enableNextButtonForSqlPreview(int curTab) {
        if (curTab == CREATE_TABLE_SQL_PREVIEW) {
            btnNext.setVisible(false);
            btnFinish.setFocus();
        } else {
            btnNext.setVisible(true);
        }
    }

    /**
     * Enable back button for general.
     *
     * @param curTab the cur tab
     */
    private void enableBackButtonForGeneral(int curTab) {
        if (curTab == CREATE_TABLE_GENERAL_INFO) {
            btnBack.setVisible(false);
        } else {
            btnBack.setVisible(true);
        }
    }

    /**
     * Sets the focus on text.
     *
     * @param curTab the new focus on text
     */
    protected void setFocusOnText(int curTab) {
        switch (curTab) {
            case CREATE_TABLE_GENERAL_INFO: {
                setTableNameinFocus();
                break;
            }
            case CREATE_TABLE_COLUMN_INFO: {
                columnUI.setFocusOnColumnName();
                break;
            }
            case CREATE_TABLE_CONSTRAINTS: {
                constraintUI.setFocusOnConstraintName();
                break;
            }
            case CREATE_TABLE_INDEXES: {
                indexUi.setFocusOnIndexName();
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Sets the table namein focus.
     */
    private void setTableNameinFocus() {
        if (textTableName != null) {
            textTableName.forceFocus();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class FinishBtnSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class FinishBtnSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            btnFinish.setEnabled(false);
            setErrorMsg("");

            try {
                if (!validateTableInputs()) {
                    btnFinish.setEnabled(true);
                    return;
                }

            } catch (MPPDBIDEException e1) {
                hanldeCreateTableError(e1);
                btnFinish.setEnabled(true);
                return;
            } catch (OutOfMemoryError e1) {
                btnFinish.setEnabled(true);
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                        .getProperty(IMessagesConstants.CREATE_TABLE_CREATE_ERROR, newTable.getDisplayName())));
                return;
            }
            btnFinish.setEnabled(true);
            tabFolder.setSelection(tabFolder.getItemCount() - 1);
            updateTableFields(CREATE_TABLE_SQL_PREVIEW);
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(newTable.getName(),
                    newTable.getNamespace().getName(), newTable.getDatabaseName(), newTable.getServerName(),
                    IMessagesConstants.CREATE_TABLE_PROGRESS_NAME);
            CreateTableWorker worker = new CreateTableWorker(progressLabel, newTable,
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CREATE_TABLE), CreateTable.this);
            worker.schedule();

        }
    }

    /**
     * Validate table inputs.
     *
     * @return true, if successful
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws OutOfMemoryError the out of memory error
     */
    protected boolean validateTableInputs() throws MPPDBIDEException, OutOfMemoryError {
        // Bala issue List #16 start
        if (isTableNameInValid()) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_ENTER_TABLE_NM));
            return false;
        }
        if (tableColumnList.getItemCount() == 0) {
            setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_EMPTY_COLUMN));
            return false;
        }
        // Bala issue List #16 end
        try {
            validateTableFillFactor();
        } catch (DatabaseOperationException ex) {
            setErrorMsg(ex.getMessage());
            tabFolder.setSelection(CREATE_TABLE_GENERAL_INFO);
            return false;
        }
        return true;
    }

    /**
     * Validate table fill factor.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    protected void validateTableFillFactor() throws DatabaseOperationException {
        if (null == spinnerFillFactor) {
            return;
        }

        validateFillFactor();
    }

    /**
     * Validate fill factor.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateFillFactor() throws DatabaseOperationException {
        int fillFactor = Integer.parseInt(spinnerFillFactor.getText());
        validateFillFactorLessThanTen(fillFactor);
        validateFillFactorMoreThanHundred(fillFactor);
    }

    /**
     * Validate fill factor more than hundred.
     *
     * @param fillFactor the fill factor
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateFillFactorMoreThanHundred(int fillFactor) throws DatabaseOperationException {
        if (fillFactor > 100) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_FILLFACTOR_MORE_THAN_100));
            throw new DatabaseOperationException(IMessagesConstants.ERR_FILLFACTOR_MORE_THAN_100);
        }
    }

    /**
     * Validate fill factor less than ten.
     *
     * @param fillFactor the fill factor
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateFillFactorLessThanTen(int fillFactor) throws DatabaseOperationException {
        if (fillFactor < 10) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_FILLFACTOR_LESS_THAN_10));
            throw new DatabaseOperationException(IMessagesConstants.ERR_FILLFACTOR_LESS_THAN_10);
        }
    }

    /**
     * Hanlde create table error.
     *
     * @param e1 the e 1
     */
    protected void hanldeCreateTableError(MPPDBIDEException e1) {
        // start DTS2014103006491
        String msg = e1.getServerMessage();
        msg = getDBErrorMessage(e1, msg);

        msg = getPosition(msg);

        // DTS2014103006491 end
        setErrorMsg(msg);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_TABLE_CREATE_ERROR, newTable.getBottombarDisplayName())));
    }

    /**
     * Gets the position.
     *
     * @param msgParam the msg param
     * @return the position
     */
    private String getPosition(String msgParam) {
        String msg = msgParam;
        if (validatePositionMsg(msg)) {
            msg = msg.split("Position:")[0];
        }
        return msg;
    }

    /**
     * Validate position msg.
     *
     * @param msg the msg
     * @return true, if successful
     */
    private boolean validatePositionMsg(String msg) {
        return null != msg && msg.contains("Position:");
    }

    /**
     * Gets the DB error message.
     *
     * @param e1 the e 1
     * @param msgParam the msg param
     * @return the DB error message
     */
    private String getDBErrorMessage(MPPDBIDEException e1, String msgParam) {
        String msg = msgParam;
        if (validateMsg(msg)) {
            msg = e1.getDBErrorMessage();
        }
        if (msg.trim().startsWith("SQL Error Code")) {
            int ind = msg.indexOf("ERROR");
            if (ind != -1) {
                msg = msg.substring(ind);
            }
        }
        return msg;
    }

    /**
     * Validate msg.
     *
     * @param msg the msg
     * @return true, if successful
     */
    private boolean validateMsg(String msg) {
        return null == msg || "".equals(msg);
    }

    /**
     * Creates the table GUI.
     *
     * @param parent the parent
     */
    protected void createTableGUI(Composite parent) {
        /*
         * Adding column exclusively for table
         */
        boolean isCreateTable = true;

        parent.setLayout(new GridLayout(1, false));
        GridData parentGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        parent.setLayoutData(parentGD);

        columnUI = new ColumnUI(db, newTable);
        dataDistributionUi = new DataDistributionUI();
        constraintUI = new ConstraintUI(db);

        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite parentComposite = new Composite(mainSc, SWT.NONE);
        mainSc.setContent(parentComposite);
        parentComposite.setLayout(new GridLayout(1, false));
        GridData parentCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        parentComposite.setLayoutData(parentCompositeGD);

        Composite tabFolderComposite = new Composite(parentComposite, SWT.NONE);
        tabFolderComposite.setLayout(new GridLayout(1, false));
        GridData tabFolderCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        tabFolderCompositeGD.heightHint = 608;
        tabFolderCompositeGD.widthHint = 570;
        tabFolderComposite.setLayoutData(tabFolderCompositeGD);

        tabFolder = new TabFolder(tabFolderComposite, SWT.NONE);
        tabFolder.setLayout(new GridLayout(1, false));
        GridData tabFolderGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tabFolder.setLayoutData(tabFolderGD);
        tabFolder.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABFOLDER_CREATE_TBL_TAB_CONTAINER_001");
        tabFolder.addSelectionListener(new TabFolderSelectionAdapter());

        createStepIndicesTab();
        createColumnTab(isCreateTable);
        createDistributionTab();
        createConstraintsTab();
        createIndexTab();
        createSqlPreviewTab(parentComposite);
        setFocusOnText(CREATE_TABLE_GENERAL_INFO);

        Composite errorAndButtonComposite = new Composite(parentComposite, SWT.NONE);
        errorAndButtonComposite.setLayout(new GridLayout(1, false));
        GridData errorAndButtonCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        errorAndButtonComposite.setLayoutData(errorAndButtonCompositeGD);

        addTxtErrorMsg(errorAndButtonComposite);
        addControlPannel(errorAndButtonComposite);

        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(parentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();
    }

    /**
     * Creates the sql preview tab.
     *
     * @param composite the composite
     */
    private void createSqlPreviewTab(Composite composite) {
        TabItem tabItemStepSQLPreview = new TabItem(tabFolder, SWT.NONE);
        tabItemStepSQLPreview.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_SQL_PREVIEW_TAB_001");
        tabItemStepSQLPreview.setText(SQL_PREVIEW);
        Composite compositeSqlpreview = new Composite(tabFolder, SWT.NONE);
        tabItemStepSQLPreview.setControl(compositeSqlpreview);
        createSqlPreviewInfoGui(compositeSqlpreview);
    }

    /**
     * Creates the index tab.
     */
    private void createIndexTab() {
        TabItem tbtmStepIndexes = new TabItem(tabFolder, SWT.NONE);
        tbtmStepIndexes.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_TNDEX_TAB_001");
        tbtmStepIndexes.setText(INDEXES);
        compositeIndices = new Composite(tabFolder, SWT.NONE);
        tbtmStepIndexes.setControl(compositeIndices);

        createIndexInfoGui(compositeIndices);
    }

    /**
     * Creates the constraints tab.
     */
    private void createConstraintsTab() {
        TabItem tbtmStepConstraints = new TabItem(tabFolder, SWT.NONE);
        tbtmStepConstraints.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_CONSTRAINT_TAB_001");
        tbtmStepConstraints.setText(TABLE_CONSTRAINTS);
        compositeConstraints = new Composite(tabFolder, SWT.NONE);
        tbtmStepConstraints.setControl(compositeConstraints);

        constraintUI.createConstraintsInfoGui(compositeConstraints);
        createConstraintPannel(compositeConstraints);
    }

    /**
     * Creates the distribution tab.
     */
    private void createDistributionTab() {
        TabItem tabItemStepDistribution = new TabItem(tabFolder, SWT.NONE);
        tabItemStepDistribution.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_DATA_DIST_TAB_001");
        tabItemStepDistribution.setText(DATA_DISTRIBUTION);
        compositeDataDistribution = new Composite(tabFolder, SWT.NONE);
        tabItemStepDistribution.setControl(compositeDataDistribution);
        dataDistributionUi.createDataDistributionInfoGui(compositeDataDistribution);
    }

    /**
     * Creates the column tab.
     *
     * @param isCreateTable the is create table
     */
    private void createColumnTab(boolean isCreateTable) {
        TabItem tbtmStepColumns = new TabItem(tabFolder, SWT.NONE);
        tbtmStepColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_COLUMNS_TAB_001");
        tbtmStepColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_MSG));
        compositeColumns = new Composite(tabFolder, SWT.NONE);
        compositeColumns.setLayout(new GridLayout(1, false));
        GridData compositeColumnsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeColumns.setLayoutData(compositeColumnsGD);
        tbtmStepColumns.setControl(compositeColumns);
        columnUI.createColumnInfoGui(compositeColumns, isCreateTable);
        createColumnPannel();
    }

    /**
     * Creates the step indices tab.
     */
    private void createStepIndicesTab() {
        TabItem tbtmStepIndices = new TabItem(tabFolder, SWT.NONE);
        tbtmStepIndices.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TABITEM_CREATE_TBL_GENERAL_TAB_001");
        tbtmStepIndices.setText(GENERAL);
        Composite compositeGeneral = new Composite(tabFolder, SWT.NONE);
        tbtmStepIndices.setControl(compositeGeneral);
        createGeneralInfoGui(compositeGeneral);
    }

    /**
     * Adds the txt error msg.
     */
    private void addTxtErrorMsg(Composite comp) {
        txtErrorMsg = new Text(comp, SWT.BOLD | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData txtErrorMsgGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtErrorMsgGD.heightHint = 20;
        txtErrorMsg.setLayoutData(txtErrorMsgGD);
        txtErrorMsg.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_TBL_ERROR_MSG_001");
        txtErrorMsg.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        txtErrorMsg.setVisible(false);
    }

    /**
     * Creates the general info gui.
     *
     * @param compositeGeneral the composite general
     */
    protected void createGeneralInfoGui(Composite compositeGeneral) {
        compositeGeneral.setLayout(new GridLayout(1, false));
        GridData compositeGeneralGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeGeneral.setLayoutData(compositeGeneralGD);

        Label lblProvideTheGeneral = new Label(compositeGeneral, SWT.WRAP);
        lblProvideTheGeneral.setText(MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_OPTION_TEXT));
        GridData lblProvideTheGeneralGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblProvideTheGeneralGD.heightHint = 30;
        lblProvideTheGeneral.setLayoutData(lblProvideTheGeneralGD);

        grpTableProperties = new Group(compositeGeneral, SWT.NONE);
        grpTableProperties.setLayout(new GridLayout(2, false));
        GridData grpTablePropertiesGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        grpTableProperties.setLayoutData(grpTablePropertiesGD);
        grpTableProperties.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_PROPERTIES_GROUP));

        Composite tablePropComposite = new Composite(grpTableProperties, SWT.NONE);
        tablePropComposite.setLayout(new GridLayout(2, false));
        GridData tablePropCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablePropComposite.setLayoutData(tablePropCompositeGD);

        Composite tablePropCompositeLeft = new Composite(tablePropComposite, SWT.NONE);
        tablePropCompositeLeft.setLayout(new GridLayout(1, false));
        GridData tablePropCompositeLeftGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablePropCompositeLeft.setLayoutData(tablePropCompositeLeftGD);
        createTableNameComposite(tablePropCompositeLeft);
        createTablespaceComposite(tablePropCompositeLeft);
        createTableOrientationComposite(tablePropCompositeLeft);

        Composite tablePropCompositeRight = new Composite(tablePropComposite, SWT.NONE);
        tablePropCompositeRight.setLayout(new GridLayout(1, false));
        GridData tablePropCompositeRightGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablePropCompositeRight.setLayoutData(tablePropCompositeRightGD);
        createSchemaNameComposite(tablePropCompositeRight);
        addTableType(tablePropCompositeRight);

        addtableoptions(compositeGeneral);

        addTableDescription(compositeGeneral);

        Label lblITableOptions = new Label(compositeGeneral, SWT.WRAP);
        lblITableOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_OPTIONS_TEXT));
        GridData lblITableOptionsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblITableOptionsGD.heightHint = 50;
        lblITableOptions.setLayoutData(lblITableOptionsGD);
    }

    private void createTablespaceComposite(Composite comp) {
        Composite tablespaceComposite = new Composite(comp, SWT.NONE);
        tablespaceComposite.setLayout(new GridLayout(1, false));
        GridData tablespaceCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablespaceComposite.setLayoutData(tablespaceCompositeGD);

        Label lblTablespace = new Label(tablespaceComposite, SWT.NONE);
        lblTablespace.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblTablespace.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_TABLESPACE));
        lblTablespace.pack();

        cmbTblspcName = new Combo(tablespaceComposite, SWT.NONE | SWT.READ_ONLY);
        GridData cmbTblspcNameGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbTblspcName.setLayoutData(cmbTblspcNameGD);
        cmbTblspcName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CREATE_TBL_TBLSPACE_NAME_001");
        getTablespaceList(TableOrientation.UNKNOWN);
    }

    private void createTableOrientationComposite(Composite comp) {
        Composite tableOrientationComposite = new Composite(comp, SWT.NONE);
        tableOrientationComposite.setLayout(new GridLayout(1, false));
        GridData tableOrientationCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableOrientationComposite.setLayoutData(tableOrientationCompositeGD);

        Label lblColumnOrientation = new Label(tableOrientationComposite, SWT.NONE);
        lblColumnOrientation.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblColumnOrientation.setText(MessageConfigLoader.getProperty(IMessagesConstants.TABLE_ORIENTATION));
        lblColumnOrientation.pack();

        tableOrientation = new Combo(tableOrientationComposite, SWT.NONE | SWT.READ_ONLY);
        GridData tableOrientationGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableOrientation.setLayoutData(tableOrientationGD);
        tableOrientation.setItems(new String[] {TableOrientation.ROW.toString(), TableOrientation.COLUMN.toString()});
        tableOrientation.select(0);
    }

    private void createSchemaNameComposite(Composite comp) {
        Composite schemaNameComposite = new Composite(comp, SWT.NONE);
        schemaNameComposite.setLayout(new GridLayout(1, false));
        GridData schemaNameCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        schemaNameComposite.setLayoutData(schemaNameCompositeGD);

        Label lblSchema = new Label(schemaNameComposite, SWT.NONE);
        lblSchema.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblSchema.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_SCEMA));
        lblSchema.pack();

        cmbSchemaName = new Combo(schemaNameComposite, SWT.BORDER);
        GridData cmbSchemaNameGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        cmbSchemaName.setLayoutData(cmbSchemaNameGD);
        cmbSchemaName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CREATE_TBL_SCHEMA_NAME_001");
        UIUtils.displayNamespaceList(this.db, this.namespace.getName(), cmbSchemaName, false);
        cmbSchemaName.setEnabled(false);
    }

    private void createTableNameComposite(Composite comp) {
        Composite tableNameComposite = new Composite(comp, SWT.NONE);
        tableNameComposite.setLayout(new GridLayout(2, false));
        GridData tableNameCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableNameComposite.setLayoutData(tableNameCompositeGD);

        Label lblTableName = new Label(tableNameComposite, SWT.NONE);
        lblTableName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblTableName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_TABLE_NAME));
        lblTableName.pack();

        chkTblNameCase = new Button(tableNameComposite, SWT.CHECK);
        GridData chkTblNameCaseGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        chkTblNameCaseGD.horizontalAlignment = SWT.END;
        chkTblNameCase.setLayoutData(chkTblNameCaseGD);
        chkTblNameCase.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_TBL_TBL_NAME__QUOTED_001");
        chkTblNameCase.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CASE));

        addTableNameText(tableNameComposite);
    }

    /**
     * 
     * Adds the table type.
     */
    private void addTableType(Composite comp) {
        Composite tableTypeComposite = new Composite(comp, SWT.NONE);
        tableTypeComposite.setLayout(new GridLayout(1, false));
        GridData tableTypeCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableTypeComposite.setLayoutData(tableTypeCompositeGD);

        Label lblTableType = new Label(tableTypeComposite, SWT.NONE);
        lblTableType.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_TYPE));
        lblTableType.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, grpTableProperties.getParent()));
        lblTableType.pack();

        cmbTableType = new Combo(tableTypeComposite, SWT.READ_ONLY);
        GridData cmbTableTypeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        cmbTableType.setLayoutData(cmbTableTypeGD);
        cmbTableType.setItems(new String[] {MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NORMAL),
            MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_UNLOGGED)});
        cmbTableType.select(0);
        cmbTableType.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CREATE_TBL_TBLTYPE_001");
    }

    /**
     * Adds the table name text.
     */
    private void addTableNameText(Composite comp) {
        textTableName = new Text(comp, SWT.BORDER);
        GridData textTableNameGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        textTableNameGD.horizontalSpan = 2;
        textTableName.setLayoutData(textTableNameGD);
        textTableName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_TBL_TBL_NAME_001");
        UIVerifier.verifyTextSize(textTableName, 63);
        final ControlDecoration deco = new ControlDecoration(textTableName, SWT.TOP | SWT.LEFT);
        textTableName.addModifyListener(new TableNameModifyListener());
        // code to be removed... added for check style
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        UIMandatoryAttribute.mandatoryField(textTableName, image, TOOLTIPS.TABLENAME_TOOLTIPS);
        // code ends.
    }

    /**
     * Adds the table description.
     *
     * @param compositeGeneral the composite general
     */
    private void addTableDescription(Composite compositeGeneral) {
        Group grpDescriptions = new Group(compositeGeneral, SWT.NONE);
        grpDescriptions.setLayout(new GridLayout(1, true));
        GridData grpDescriptionsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        grpDescriptionsGD.heightHint = 110;
        grpDescriptions.setLayoutData(grpDescriptionsGD);
        grpDescriptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENTER_DESCRIPTIONS_TEXT));

        textTableDescription = new StyledText(grpDescriptions, SWT.BORDER);
        GridData textTableDescriptionGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textTableDescription.setLayoutData(textTableDescriptionGD);
        textTableDescription.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CREATE_TBL_TBLDESC_001");
        textTableDescription.addVerifyListener(new TextLengthVerifyListner());
    }

    /**
     * Addtableoptions.
     *
     * @param compositeGeneral the composite general
     */
    private void addtableoptions(Composite compositeGeneral) {
        Group grpOptions = new Group(compositeGeneral, SWT.NONE);
        grpOptions.setLayout(new GridLayout(3, true));
        GridData grpOptionsGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        grpOptions.setLayoutData(grpOptionsGD);
        grpOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_OPTIONS));

        chkIfexists = new Button(grpOptions, SWT.CHECK);
        GridData chkIfexistsGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        chkIfexistsGD.verticalAlignment = SWT.CENTER;
        chkIfexistsGD.horizontalAlignment = SWT.CENTER;
        chkIfexists.setLayoutData(chkIfexistsGD);
        chkIfexists.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NOT_EXISTS));
        chkIfexists.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_TBL_TBL_IF_EXISTS_001");

        Composite fillFactorComposite = new Composite(grpOptions, SWT.NONE);
        fillFactorComposite.setLayout(new GridLayout(2, false));
        GridData fillFactorCompositeGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        fillFactorCompositeGD.verticalAlignment = SWT.CENTER;
        fillFactorCompositeGD.horizontalAlignment = SWT.CENTER;
        fillFactorComposite.setLayoutData(fillFactorCompositeGD);

        Label lblFillFactor = new Label(fillFactorComposite, SWT.NONE);
        lblFillFactor.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_FILL_FACTOR));
        lblFillFactor.pack();

        setFillFactor(fillFactorComposite);

        chkWithoid = new Button(grpOptions, SWT.CHECK);
        GridData chkWithoidGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        chkWithoidGD.verticalAlignment = SWT.CENTER;
        chkWithoidGD.horizontalAlignment = SWT.CENTER;
        chkWithoid.setLayoutData(chkWithoidGD);
        chkWithoid.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CREATE_TBL_WITH_OID_001");
        chkWithoid.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_OIDS));
    }

    /**
     * The listener interface for receiving tableNameModify events. The class
     * that is interested in processing a tableNameModify event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addTableNameModifyListener<code>
     * method. When the tableNameModify event occurs, that object's appropriate
     * method is invoked.
     *
     * TableNameModifyEvent
     */
    private class TableNameModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent event) {

            String tableName = textTableName.getText();

            tableName = checkTableName(tableName);

            newTable.setName(tableName);
        }
    }

    /**
     * Sets the fill factor.
     *
     * @param grpOptions the new fill factor
     */
    private void setFillFactor(Composite comp) {
        spinnerFillFactor = new Spinner(comp, SWT.BORDER);
        GridData spinnerFillFactorGD = new GridData(SWT.NONE, SWT.NONE, true, true);
        spinnerFillFactorGD.verticalAlignment = SWT.CENTER;
        spinnerFillFactorGD.widthHint = 30;
        spinnerFillFactor.setLayoutData(spinnerFillFactorGD);
        spinnerFillFactor.setMinimum(10);
        spinnerFillFactor.setSelection(100);
        spinnerFillFactor.setMaximum(100);
        spinnerFillFactor.setIncrement(1);
        spinnerFillFactor.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_SPINNER_CREATE_TBL_FILLFACTOR_001");
    }

    /**
     * Gets the tablespace list.
     *
     * @param rowOrient the row orient
     * @return the tablespace list
     */
    private void getTablespaceList(TableOrientation rowOrient) {
        UIUtils.displayTablespaceList(this.db, cmbTblspcName, true, rowOrient);
    }

    /**
     * Adds the column in all column tables.
     *
     * @param rowdata the rowdata
     */
    protected void addColumnInAllColumnTables(String[] rowdata) {
        TableItem tableItem = null;
        // DTS2014103006724 start
        tableItem = createTableItemForColumns();

        tableItem.setText(rowdata);

        addColumnsInConstraintsUI(rowdata);
        // DTS2014103006724 end

        resetIndexExpression();
    }

    /**
     * Reset index expression.
     */
    private void resetIndexExpression() {
        if (validateWhrCondition()) {
            resetWhrExpression();
            removeExpressions();
            clearConstraintSelCol();
        }
    }

    /**
     * Validate whr condition.
     *
     * @return true, if successful
     */
    private boolean validateWhrCondition() {
        return null != indexUi.getWhrExpr() && null != copyEditColName;
    }

    /**
     * Reset whr expression.
     */
    private void resetWhrExpression() {
        indexUi.resetWhereExpression(copyEditColName);
    }

    /**
     * Adds the columns in constraints UI.
     *
     * @param rowdata the rowdata
     */
    private void addColumnsInConstraintsUI(String[] rowdata) {
        int editIndex = columnUI.getEditIndex();
        if (editIndex > -1) {
            addConstraintClmForUpdate(editIndex);
            this.dataDistributionUi.addColumn(rowdata, editIndex);

            this.constraintUI.addColumn(rowdata, editIndex);
        } else {
            this.dataDistributionUi.addColumn(rowdata);

            this.constraintUI.addColumn(rowdata);
        }
    }

    /**
     * Adds the constraint clm for update.
     *
     * @param editIndex the edit index
     */
    private void addConstraintClmForUpdate(int editIndex) {
        if (isClmUpdate) {
            this.dataDistributionUi.removeColumn(editIndex);
            this.constraintUI.removeColumn(editIndex);
        }
    }

    /**
     * Creates the table item for columns.
     *
     * @return the table item
     */
    private TableItem createTableItemForColumns() {
        TableItem tableItem = null;
        int index = columnUI.getEditIndex();
        if (index > -1) {
            removeOnClmUpdate(index);
            tableItem = new TableItem(tableColumnList, SWT.NONE, index);
        } else {
            tableItem = new TableItem(tableColumnList, SWT.NONE);
        }
        return tableItem;
    }

    /**
     * Removes the on clm update.
     *
     * @param index the index
     */
    private void removeOnClmUpdate(int index) {
        if (isClmUpdate) {
            tableColumnList.remove(index);
        }
    }

    /**
     * Removes the column in all column tables.
     *
     * @param index the index
     */
    protected void removeColumnInAllColumnTables(int index) {
        tableColumnList.remove(index);
        this.dataDistributionUi.removeColumn(index);
        this.constraintUI.removeColumn(index);

    }

    /**
     * Update column details in all column tables.
     *
     * @param index the index
     * @param rowdata the rowdata
     */
    protected void updateColumnDetailsInAllColumnTables(int index, String[] rowdata) {
        TableItem tableItem = null;
        tableItem = tableColumnList.getItem(index);
        tableItem.setText(rowdata);
        this.dataDistributionUi.updateColumn(index, rowdata);
        this.constraintUI.updateColumn(index, rowdata);
    }

    /**
     * Creates the index info gui.
     *
     * @param compIndices the comp indices
     */
    protected void createIndexInfoGui(Composite compIndices) {
        compIndices.setLayout(new GridLayout(4, false));
        GridData compIndicesGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compIndices.setLayoutData(compIndicesGD);

        indexUi.createUI(compIndices);
        final Namespace tableNamspace = this.namespace;

        Composite indexDefinitionComposite = new Composite(compIndices, SWT.NONE);
        indexDefinitionComposite.setLayout(new GridLayout(2, false));
        GridData indexDefinitionCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        indexDefinitionComposite.setLayoutData(indexDefinitionCompositeGD);

        tblIndexes = new Table(indexDefinitionComposite, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tblIndexesGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblIndexesGD.horizontalIndent = 10;
        tblIndexesGD.heightHint = 120;
        tblIndexesGD.widthHint = 450;
        tblIndexes.setLayoutData(tblIndexesGD);
        tblIndexes.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CREATE_TBL_TBL_INDEX_001");
        tblIndexes.setHeaderVisible(true);
        tblIndexes.setLinesVisible(true);
        indexUi.setTableIndexesUI(tblIndexes);
        TableColumn tblclmnIndexName = new TableColumn(tblIndexes, SWT.NONE);
        tblclmnIndexName.setData(MPPDBIDEConstants.SWTBOT_KEY,
                "ID_TBL_COL_CREATE_TBL_TBL_INDEX_COL_INDEXDEFINITION_001");
        tblclmnIndexName.setWidth(450);
        // DTS2014102908367 start
        tblclmnIndexName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_INDEX_DEF));
        // DTS2014102908367 end

        Composite indexDefButtonsComposite = getIndexDefBtnComp(indexDefinitionComposite);

        addNewBtn(tableNamspace, indexDefButtonsComposite);

        addDeleteBtn(indexDefButtonsComposite);

        addEditBtn(indexDefButtonsComposite);
    }

    private void addEditBtn(Composite indexDefButtonsComposite) {
        Button btnEdit = new Button(indexDefButtonsComposite, SWT.NONE);
        GridData btnEditGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnEditGD.heightHint = 30;
        btnEdit.setLayoutData(btnEditGD);
        btnEdit.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_EDIT_INDEX_001");
        btnEdit.setText(EDIT);
        btnEdit.addSelectionListener(new EditBtnSelectionListener());
    }

    private void addDeleteBtn(Composite indexDefButtonsComposite) {
        Button btnDelete = new Button(indexDefButtonsComposite, SWT.NONE);
        GridData btnDeleteGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnDeleteGD.heightHint = 30;
        btnDelete.setLayoutData(btnDeleteGD);
        btnDelete.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_DELETE_INDEX_001");
        btnDelete.setText(DELETE);
        btnDelete.addSelectionListener(new DeleteBtnSelectionListener());
    }

    private void addNewBtn(final Namespace tableNamspace, Composite indexDefButtonsComposite) {
        Button btnNewButton = new Button(indexDefButtonsComposite, SWT.NONE);
        GridData btnNewButtonGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnNewButtonGD.heightHint = 30;
        btnNewButton.setLayoutData(btnNewButtonGD);
        btnNewButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CREATE_TBL_ADD_INDEX_001");
        btnNewButton.setText(ADD);
        btnNewButton.addSelectionListener(new NewBtnSelectionListener(tableNamspace));
    }

    private Composite getIndexDefBtnComp(Composite indexDefinitionComposite) {
        Composite indexDefButtonsComposite = new Composite(indexDefinitionComposite, SWT.NONE);
        indexDefButtonsComposite.setLayout(new GridLayout(1, false));
        GridData indexDefButtonsCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        indexDefButtonsCompositeGD.verticalAlignment = SWT.CENTER;
        indexDefButtonsComposite.setLayoutData(indexDefButtonsCompositeGD);
        return indexDefButtonsComposite;
    }

    /**
     * The listener interface for receiving editBtnSelection events. The class
     * that is interested in processing a editBtnSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addEditBtnSelectionListener<code>
     * method. When the editBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * EditBtnSelectionEvent
     */
    private class EditBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            int idx = tblIndexes.getSelectionIndex();
            if (-1 == idx) {
                setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CLM_EDIT));
                return;
            }
            indexUi.setIndexObject(newTable.getIndexArrayList().get(idx));
            newTable.removeIndex(idx);
            tblIndexes.remove(idx);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving deleteBtnSelection events. The class
     * that is interested in processing a deleteBtnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addDeleteBtnSelectionListener<code> method. When the
     * deleteBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * DeleteBtnSelectionEvent
     */
    private class DeleteBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            setErrorMsg("");
            int idx = tblIndexes.getSelectionIndex();
            if (-1 == idx) {
                setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CLM_DELETE));
                return;
            }
            newTable.removeIndex(idx);
            tblIndexes.remove(idx);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving newBtnSelection events. The class
     * that is interested in processing a newBtnSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addNewBtnSelectionListener<code>
     * method. When the newBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * NewBtnSelectionEvent
     */
    private class NewBtnSelectionListener implements SelectionListener {
        Namespace tableNamspace;

        /**
         * Instantiates a new new btn selection listener.
         *
         * @param tableNamspace the table namspace
         */
        public NewBtnSelectionListener(Namespace tableNamspace) {
            this.tableNamspace = tableNamspace;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            IndexMetaData index = null;

            setErrorMsg("");
            try {
                index = indexUi.getIndexMetaData();
                if (null == index.getNamespace()) {
                    index.setNamespace(tableNamspace);
                }
            } catch (DatabaseOperationException e1) {
                setErrorMsg(e1.getMessage());
                return;
            }

            newTable.addIndex(index);

            row = new TableItem(tblIndexes, SWT.NONE);
            row.setText(index.formCreateQuery(true));
            indexUi.clear();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Creates the sql preview info gui.
     *
     * @param compositeSqlpreview the composite sqlpreview
     */
    protected void createSqlPreviewInfoGui(Composite compositeSqlpreview) {
        /**
         * STEP: 8 SQL PREVIEW
         */
        compositeSqlpreview.setLayout(new GridLayout(1, false));
        GridData compositeSqlpreviewGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeSqlpreview.setLayoutData(compositeSqlpreviewGD);

        textSqlPreview = new SourceViewer(compositeSqlpreview, null,
                SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
        GridData textSqlPreviewGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textSqlPreview.getTextWidget().setLayoutData(textSqlPreviewGD);
        textSqlPreview.setEditable(false);

        textSqlPreview.configure(new SQLSourceViewerConfig(db.getSqlSyntax()));
        textSqlPreview.getTextWidget().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_STYLEDTEXT_CREATE_TBL_PREVIEW_001");
        ResourceManager resMangr = new LocalResourceManager(JFaceResources.getResources(), compositeSqlpreview);
        Font font = resMangr.createFont(FontDescriptor.createFrom("Courier New", 10, SWT.NORMAL));

        textSqlPreview.getTextWidget().setFont(font);
        // DTS2016050415166 start
        Menu createSeqMenu = new Menu(getControl());
        textSqlPreview.getTextWidget().setMenu(createSeqMenu);
        addCopyMenuItem(createSeqMenu);
        addSelectAllMenuItem(createSeqMenu);
        createSeqMenu.addMenuListener(new PLEditorMenuListener());

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
        public void menuShown(MenuEvent event) {
            contextMenuAboutToShowForSQLPreview();
        }

        @Override
        public void menuHidden(MenuEvent event) {

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
     * Gets the control.
     *
     * @return the control
     */
    private Control getControl() {
        return ControlUtils.getControl(textSqlPreview);
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
    protected final class PLEditorCopySelectionListener implements SelectionListener {

        /**
         * Widget selected.
         *
         * @param event the event
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
            copySelectedDocText();
        }

        /**
         * Widget default selected.
         *
         * @param event the event
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
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
    protected final class PLEditorSelectAllListener implements SelectionListener {

        /**
         * Widget selected.
         *
         * @param event the event
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
            selectAllDocText();
        }

        /**
         * Widget default selected.
         *
         * @param event the event
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Select all doc text.
     */
    private void selectAllDocText() {
        textSqlPreview.doOperation(ITextOperationTarget.SELECT_ALL);
    }

    /**
     * Copy selected doc text.
     */
    private void copySelectedDocText() {
        textSqlPreview.doOperation(ITextOperationTarget.COPY);
    }

    /**
     * Sets the error msg.
     *
     * @param errMsg the new error msg
     */
    public void setErrorMsg(String errMsg) {
        if (isErrorMsgExists(errMsg)) {
            txtErrorMsg.setVisible(false);
        } else {
            txtErrorMsg.setVisible(true);
        }
        txtErrorMsg.setText(errMsg);
    }

    /**
     * Checks if is error msg exists.
     *
     * @param errMsg the err msg
     * @return true, if is error msg exists
     */
    private boolean isErrorMsgExists(String errMsg) {
        return null == errMsg || errMsg.trim().isEmpty();
    }

    /**
     * On focus.
     */
    @Focus
    public void onFocus() {

    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * Checks if is table name in valid.
     *
     * @return true, if is table name in valid
     */
    protected boolean isTableNameInValid() {

        if (isTableNameExists()) {
            return textTableName.getText().trim().length() < 1;
        } else {
            return false;
        }

    }

    /**
     * Checks if is table name exists.
     *
     * @return true, if is table name exists
     */
    private boolean isTableNameExists() {
        return null != textTableName && !(this.textTableName.isDisposed());
    }

    /**
     * Removes the expressions.
     */

    protected void removeExpressions() {
        isClmUpdate = true;
        ColumnMetaData newTempColumn = columnUI.getDBColumn(this.tableColumnList, isClmUpdate,
                newTable.getOrientation());

        int tableConstraintSize = tableTblConstraints.getItemCount();
        int tableIndexSize = tblIndexes.getItemCount();
        int sizeOfConstraintMetaDataList = newTable.getConstraintMetaDataList().size();
        int sizeOfIndexMetaDataList = newTable.getIndexMetaDataList().size();

        // removing ConstraintExpressionsIfPresent
        removeConstraintExpressionIfPresent(newTempColumn, tableConstraintSize, sizeOfConstraintMetaDataList);
        // removing IndexExpressionsIfPresent

        removeIndexExpressionIfPresent(tableIndexSize, sizeOfIndexMetaDataList);
    }

    /**
     * Removes the index expression if present.
     *
     * @param tableIndexSize the table index size
     * @param sizeOfIndexMetaDataList the size of index meta data list
     */
    private void removeIndexExpressionIfPresent(int tableIndexSize, int sizeOfIndexMetaDataList) {
        if (null != row) {
            removeIndexOnEditClmName(tableIndexSize, sizeOfIndexMetaDataList);
        }
    }

    /**
     * Removes the index on edit clm name.
     *
     * @param tableIndexSize the table index size
     * @param sizeOfIndexMetaDataList the size of index meta data list
     */
    private void removeIndexOnEditClmName(int tableIndexSize, int sizeOfIndexMetaDataList) {
        if (null != copyEditColName) {
            indexUi.txtWhereExpr.setText("");

            removeIndexes(tableIndexSize, sizeOfIndexMetaDataList);
        }
    }

    /**
     * Removes the indexes.
     *
     * @param tableIndexSize the table index size
     * @param sizeOfIndexMetaDataList the size of index meta data list
     */
    private void removeIndexes(int tableIndexSize, int sizeOfIndexMetaDataList) {
        for (int index = 0; index < tableIndexSize; index++) {
            Pattern pattern1 = Pattern.compile(copyEditColName.replace("\"", ""));
            Matcher matcher1 = pattern1.matcher(tblIndexes.getItem(tableIndexSize - (1 + index)).getText());
            removeIndexOnMatch(tableIndexSize, sizeOfIndexMetaDataList, index, matcher1);
        }
    }

    /**
     * Removes the index on match.
     *
     * @param tableIndexSize the table index size
     * @param sizeOfIndexMetaDataList the size of index meta data list
     * @param index the i
     * @param matcher1 the matcher 1
     */
    private void removeIndexOnMatch(int tableIndexSize, int sizeOfIndexMetaDataList, int index, Matcher matcher1) {
        if (matcher1.find()) {
            tblIndexes.remove(tableIndexSize - (1 + index));
            newTable.getIndexMetaDataList().remove(sizeOfIndexMetaDataList - (index + 1));
        }
    }

    /**
     * Removes the constraint expression if present.
     *
     * @param newTempColumn the new temp column
     * @param tableConstraintSize the table constraint size
     * @param sizeOfConstraintMetaDataList the size of constraint meta data list
     */
    private void removeConstraintExpressionIfPresent(ColumnMetaData newTempColumn, int tableConstraintSize,
            int sizeOfConstraintMetaDataList) {
        if (validateConstraint(newTempColumn)) {
            removeConstraints(tableConstraintSize, sizeOfConstraintMetaDataList);
        }
    }

    /**
     * Validate constraint.
     *
     * @param newTempColumn the new temp column
     * @return true, if successful
     */
    private boolean validateConstraint(ColumnMetaData newTempColumn) {
        return null != newTempColumn && tableTblConstraints.getItemCount() > 0 && null != copyEditColName;
    }

    /**
     * Removes the constraints.
     *
     * @param tableConstraintSize the table constraint size
     * @param sizeOfConstraintMetaDataList the size of constraint meta data list
     */
    private void removeConstraints(int tableConstraintSize, int sizeOfConstraintMetaDataList) {
        for (int index = 0; index < tableConstraintSize; index++) {
            Pattern pattern = Pattern.compile(copyEditColName);
            Matcher matcher = pattern.matcher(tableTblConstraints.getItem(tableConstraintSize - (index + 1)).getText());

            removeConstraintOnMatch(tableConstraintSize, sizeOfConstraintMetaDataList, index, matcher);

        }
    }

    /**
     * Removes the constraint on match.
     *
     * @param tableConstraintSize the table constraint size
     * @param sizeOfConstraintMetaDataList the size of constraint meta data list
     * @param index the i
     * @param matcher the matcher
     */
    private void removeConstraintOnMatch(int tableConstraintSize, int sizeOfConstraintMetaDataList, int index,
            Matcher matcher) {
        if (matcher.find()) {

            tableTblConstraints.remove(tableConstraintSize - (index + 1));
            newTable.getConstraintMetaDataList().remove(sizeOfConstraintMetaDataList - (index + 1));

        }
    }

    /**
     * Clear constraint sel col.
     */
    protected void clearConstraintSelCol() {
        Table getTable = constraintUI.getSelectedColumns();
        if (getTable.getItemCount() > 0) {
            removeCOnstraintClmFromTable(getTable);
        }

    }

    /**
     * Removes the C onstraint clm from table.
     *
     * @param getTable the get table
     */
    private void removeCOnstraintClmFromTable(Table getTable) {
        for (int index = 0; index < getTable.getItemCount(); index++) {
            removeConstraintClm(getTable, index);
        }
    }

    /**
     * Removes the constraint clm.
     *
     * @param getTable the get table
     * @param index the i
     */
    private void removeConstraintClm(Table getTable, int index) {
        if (getTable.getItem(index).getText(index).contains(copyEditColName)) {
            getTable.remove(index);
        }
    }

    /**
     * Checks if is partition present.
     *
     * @return true, if is partition present
     */
    protected boolean isPartitionPresent() {
        return false;
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    protected Server getServer() {
        return server;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                .getProperty(IMessagesConstants.CREATE_TABLE_CREATE_SUCCESS, newTable.getBottombarDisplayName())));
        if (obj instanceof TableMetaData) {
            newTable = (TableMetaData) obj;
        }
        refreshObjInObjectBrowser();
        disposeCurrShell();

    }

    /**
     * Dispose curr shell.
     */
    private void disposeCurrShell() {
        if (!currentShell.isDisposed()) {
            currentShell.dispose();
        }
    }

    /**
     * Refresh obj in object browser.
     */
    private void refreshObjInObjectBrowser() {
        RefreshObjectDetails refreshObj = new RefreshObjectDetails();
        refreshObj.setObjectName(newTable.getName());
        refreshObj.setNamespace(newTable.getNamespace());
        refreshObj.setOperationType("CREATE_TABLE");
        refreshObj.setParent(newTable.getNamespace().getTablesGroup());
        refreshObj.setObjToBeRefreshed(newTable);
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            RefreshObjects.refreshObjectsInTreeViewer(refreshObj, objectBrowserModel.getTreeViewer());
        }  
    }

    /**
     * On critical exception UI action.
     *
     * @param dbCrticalException the db crtical exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCrticalException) {
        hanldeCreateTableError(dbCrticalException);

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        hanldeCreateTableError(dbOperationException);
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

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        sourceViewerDecorationSupport.uninstall();
        sourceViewerDecorationSupport = null;
        this.textSqlPreview.unconfigure();
    }

    /**
     * Sets the row orientation values.
     */
    private void setRowOrientationValues() {
        orientationType = TableOrientation.ROW;
        newTable.setOrientation(orientationType);
        spinnerFillFactor.setEnabled(true);
        chkWithoid.setEnabled(true);
        cmbTableType.setEnabled(true);

        shoeMsgOnEditOrientationCahange();
        tableUIValidator.constraintHandleRowSelection(compositeConstraints);
        tableUIValidator.columnsComponents();
        tableUIValidator.indexHandleRowColumnSelection(compositeIndices);
        tableUIValidator.distributionHandleRowColSelection(compositeDataDistribution);
        UIUtils.displayTablespaceList(db, cmbTblspcName, true, orientationType);
        tableUIValidator.removeDataDistributionOnOrientationChange();
    }

    /**
     * Shoe msg on edit orientation cahange.
     */
    private void shoeMsgOnEditOrientationCahange() {
        if (isDataOnOtherTabsPresent()) {
            showInfoMessageToUserOnEditOrientationChange(ORIENTATION, false);
        }
    }

    /**
     * 
     * Title: class
     * 
     * : The Class TextLengthVerifyListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class TextLengthVerifyListner implements VerifyListener {
        @Override
        public void verifyText(VerifyEvent event) {
            String textStr = ((StyledText) event.widget).getText() + event.text;
            try {
                if (textStr.length() > 5000) {
                    event.doit = false;
                }
            } catch (NumberFormatException e) {
                event.doit = false;
            }
        }
    }
    // DTS2018091600275 end

}
