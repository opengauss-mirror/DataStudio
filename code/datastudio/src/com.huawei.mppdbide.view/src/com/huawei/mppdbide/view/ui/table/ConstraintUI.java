/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintType;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.UIVerifier;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConstraintUI.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConstraintUI {
    private Text textTblConstraintName;
    private Table tableCheckAvailableCols;

    private StyledText textCheckConsExpr;

    private Spinner spinnerPukFillfactor;
    private Button chkPukDeferable;
    private Button chkPukInitDefered;
    private Table tablePukAvailableCol;

    private Table tablePukSelCols;
    private Combo cmbPukTablespace;
    private Combo cmbConstraintType;

    private Group grpAddConstrains;

    private TableMetaData parentTable;
    private Database db;

    private Composite compositePkeyUnique;
    private Composite compositeCheck;

    private Composite compositeForeign;
    private Table tableForeignCols;
    private Combo cmbForeignNamespace;
    private Combo cmbForeignTable;
    private Combo cmbForeignColumn;

    private Table constraintTableColumn;
    private ControlDecoration deco;
    private ControlDecoration deco1;

    private Label lblChkExpr;
    private Label lblAvailColumns;
    private StackLayout layout;
    private Composite overLayComposite;
    private TableOrientation orientation = TableOrientation.ROW;

    /**
     * Sets the UI labels color gray.
     */
    public void setUILabelsColorGray() {
        lblChkExpr.setForeground(FontAndColorUtility.getColor(SWT.COLOR_DARK_GRAY));
        lblAvailColumns.setForeground(FontAndColorUtility.getColor(SWT.COLOR_DARK_GRAY));
        // set other labels if needed
    }

    /**
     * Sets the UI labels color black.
     */
    public void setUILabelsColorBlack() {
        lblChkExpr.setForeground(FontAndColorUtility.getColor(SWT.COLOR_BLACK));
        lblAvailColumns.setForeground(FontAndColorUtility.getColor(SWT.COLOR_BLACK));
        // set other labels if needed
    }

    /**
     * Gets the cmb constraint type.
     *
     * @return the cmb constraint type
     */
    public Combo getCmbConstraintType() {
        return cmbConstraintType;
    }

    /**
     * Gets the grp add constrains.
     *
     * @return the grp add constrains
     */
    public Group getGrpAddConstrains() {
        return grpAddConstrains;
    }

    /**
     * Gets the table puk available col.
     *
     * @return the table puk available col
     */
    public Table getTablePukAvailableCol() {
        return tablePukAvailableCol;
    }

    /**
     * Gets the table check available cols.
     *
     * @return the table check available cols
     */
    public Table getTableCheckAvailableCols() {
        return tableCheckAvailableCols;
    }

    /**
     * Gets the text tbl constraint name.
     *
     * @return the text tbl constraint name
     */
    public Text getTextTblConstraintName() {
        return textTblConstraintName;
    }

    /**
     * Gets the deco.
     *
     * @return the deco
     */
    public ControlDecoration getDeco() {
        return deco;
    }

    /**
     * Gets the deco 1.
     *
     * @return the deco 1
     */
    public ControlDecoration getDeco1() {
        return deco1;
    }

    /**
     * Instantiates a new constraint UI.
     *
     * @param db2 the db 2
     */
    public ConstraintUI(Database db2) {
        this.db = db2;
    }

    /**
     * Sets the parent table.
     *
     * @param parentTable the new parent table
     */
    public void setParentTable(TableMetaData parentTable) {
        this.parentTable = parentTable;
    }

    /**
     * Gets the selected columns.
     *
     * @return the selected columns
     */
    public Table getSelectedColumns() {
        return tablePukSelCols;
    }

    /**
     * Creates the constraints info gui.
     *
     * @param compositeConstraints the composite constraints
     */
    public void createConstraintsInfoGui(Composite compositeConstraints) {
        /**
         * STEP: 3 CONSTRAINTS
         */
        compositeConstraints.setLayout(new GridLayout(1, false));
        GridData compositeConstraintsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeConstraints.setLayoutData(compositeConstraintsGD);

        addUiForConstraintNameAndType(compositeConstraints);

        overLayComposite = new Composite(compositeConstraints, SWT.NONE);
        GridData overLayCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        overLayCompositeGD.heightHint = 250;
        overLayComposite.setLayoutData(overLayCompositeGD);

        layout = new StackLayout();
        overLayComposite.setLayout(layout);

        compositeCheck = new Composite(overLayComposite, SWT.NONE);
        compositeCheck.setLayout(new GridLayout(1, false));
        GridData compositeCheckGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositeCheck.setLayoutData(compositeCheckGD);
        layout.topControl = compositeCheck;

        compositePkeyUnique = new Composite(overLayComposite, SWT.NONE);
        compositePkeyUnique.setLayout(new GridLayout(1, false));
        GridData compositePkeyUniqueGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        compositePkeyUnique.setLayoutData(compositePkeyUniqueGD);
        
        compositeForeign = createForeignComposite(overLayComposite);

        addUiOnCheckSelection(compositeConstraints);
        addUiForUniqueKeySelection();
    }
    
    private Composite createSingleComposite(Composite parent, int gridLayoutLen, boolean isGroup) {
        Composite singleComposite ;
        if (isGroup) {
            singleComposite = new Group(parent, SWT.NONE);
        } else {
            singleComposite = new Composite(parent, SWT.NONE);
        }
        singleComposite.setLayout(new GridLayout(gridLayoutLen, false));
        GridData singleComGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        singleComposite.setLayoutData(singleComGridData); 
        return singleComposite;
    }
    
    private Composite createForeignComposite(Composite parent) {
        Composite foreignComposite = createSingleComposite(parent, 1, false);
        
        // add group composite
        Composite group = createSingleComposite(foreignComposite, 1, true);
        
        // add sub composite for group
        Composite subComposite = createSingleComposite(group, 4, false);
        
        // add available columns
        Composite availableColumnComposite = createSingleComposite(subComposite, 1, false);
        addUiForAvailableColumnForForeign(availableColumnComposite);
        
        // add namespace
        Composite namespaceParentComposite = createSingleComposite(subComposite, 1, false);
        
        Label namespaceLable = new Label(namespaceParentComposite, SWT.NONE);
        namespaceLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_FOREIGN_NAMESPACE));
    
        // add namespace selecter
        cmbForeignNamespace = new Combo(namespaceParentComposite, SWT.READ_ONLY);
        GridData cmbTablespaceGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbForeignNamespace.setLayoutData(cmbTablespaceGD);
        cmbForeignNamespace.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CONSTRAINTUI_TBLSPACE_999");
        cmbForeignNamespace.addSelectionListener(new ForeignNamespaceSelectionListener());
        UIUtils.displayNamespaceList(this.db, null, cmbForeignNamespace, true);


        // add table selecter
        Composite tableParentComposite = createSingleComposite(subComposite, 1, false);
        
        Label tableLabel = new Label(tableParentComposite, SWT.NONE);
        tableLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_FOREIGE_TABLENAME));
        
        cmbForeignTable = new Combo(tableParentComposite, SWT.NONE);
        GridData cmbTableGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbForeignTable.setLayoutData(cmbTableGD);
        cmbForeignTable.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CONSTRAINTUI_TBLSPACE_998");
        cmbForeignTable.addSelectionListener(new ForeignTableSelectionListener());

        // add column selecter
        Composite columnParentComposite = createSingleComposite(subComposite, 1, false);

        Label columnLabel = new Label(columnParentComposite, SWT.NONE);
        columnLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_FOREIGE_COLUMNNAME));
        
        cmbForeignColumn = new Combo(columnParentComposite, SWT.NONE);
        GridData cmbColumnGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbForeignColumn.setLayoutData(cmbColumnGD);
        cmbForeignColumn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CONSTRAINTUI_TBLSPACE_997");

        return foreignComposite;
    }

    private void addUiForAvailableColumnForForeign(Composite Comp) {
        tableForeignCols = new Table(Comp, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tableForeignAvailableColGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableForeignAvailableColGD.widthHint = 215;
        tableForeignAvailableColGD.heightHint = 100;
        tableForeignCols.setLayoutData(tableForeignAvailableColGD);
        tableForeignCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CONSTRAINTUI_AVL_COLS_PU_001");
        tableForeignCols.setHeaderVisible(true);
        tableForeignCols.setLinesVisible(true);

        TableColumn tblclmnColumnname = new TableColumn(tableForeignCols, SWT.NONE);
        tblclmnColumnname.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CONSTRAINTUI_AVL_COL_NAME_PU_001");
        tblclmnColumnname.setWidth(215);
        tblclmnColumnname.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_AVAILABLE_COLUMNS));
    }

    /**
     * Adds the ui for constraint name and type.
     *
     * @param compositeConstraints the composite constraints
     */
    private void addUiForConstraintNameAndType(Composite compositeConstraints) {
        Composite nameTypeComposite = new Composite(compositeConstraints, SWT.NONE);
        nameTypeComposite.setLayout(new GridLayout(2, false));
        GridData nameTypeCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        nameTypeComposite.setLayoutData(nameTypeCompositeGD);

        Composite typeComposite = new Composite(nameTypeComposite, SWT.NONE);
        typeComposite.setLayout(new GridLayout(1, false));
        GridData typeCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        typeComposite.setLayoutData(typeCompositeGD);

        Label lblConstraintType = new Label(typeComposite, SWT.NONE);
        lblConstraintType.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, compositeConstraints));
        lblConstraintType.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_TYPE));
        lblConstraintType.pack();

        cmbConstraintType = new Combo(typeComposite, SWT.READ_ONLY);
        GridData cmbConstraintTypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbConstraintType.setLayoutData(cmbConstraintTypeGD);
        cmbConstraintType.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CONSTRAINTUI_CONSTRAINT_TYPE_001");
        cmbConstraintType.addSelectionListener(new ConstraintTypeSelectionListener());
        populateConstraintType();

        Composite nameComposite = new Composite(nameTypeComposite, SWT.NONE);
        nameComposite.setLayout(new GridLayout(1, false));
        GridData nameCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        nameComposite.setLayoutData(nameCompositeGD);

        Label lblConstraintName = new Label(nameComposite, SWT.NONE);
        lblConstraintName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, compositeConstraints));
        lblConstraintName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_NAME));
        lblConstraintName.pack();

        textTblConstraintName = new Text(nameComposite, SWT.BORDER);
        GridData textTblConstraintNameGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textTblConstraintName.setLayoutData(textTblConstraintNameGD);
        textTblConstraintName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONSTRAINTUI_CONSTRAINT_NAME_001");
        UIVerifier.verifyTextSize(textTblConstraintName, 63);
    }

    /**
     * Adds the ui on check selection.
     *
     * @param compositeConstraints the composite constraints
     * @param image the image
     */
    private void addUiOnCheckSelection(Composite compositeConstraints) {
        Group group = new Group(compositeCheck, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        group.setLayoutData(groupGD);

        Composite checkSelectionComposite = new Composite(group, SWT.NONE);
        checkSelectionComposite.setLayout(new GridLayout(3, false));
        GridData checkSelectionCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        checkSelectionComposite.setLayoutData(checkSelectionCompositeGD);

        Composite checkAvailColumnsComposite = new Composite(checkSelectionComposite, SWT.NONE);
        checkAvailColumnsComposite.setLayout(new GridLayout(1, false));
        GridData checkAvailColumnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        checkAvailColumnsComposite.setLayoutData(checkAvailColumnsCompositeGD);

        addUiForSelectedColumnsUiForCheck(checkAvailColumnsComposite);

        Button buttonCheckAppendCol = new Button(checkSelectionComposite, SWT.ARROW | SWT.RIGHT);
        GridData buttonCheckAppendColGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        buttonCheckAppendColGD.verticalAlignment = SWT.CENTER;
        buttonCheckAppendColGD.heightHint = 30;
        buttonCheckAppendCol.setLayoutData(buttonCheckAppendColGD);
        buttonCheckAppendCol.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CONSTRAINTUI_APPEND_COL_CHK_001");
        buttonCheckAppendCol.addSelectionListener(new CheckAppendColSelectionListner());
        buttonCheckAppendCol.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_NEW_BTN));

        Composite checkExpressionComposite = new Composite(checkSelectionComposite, SWT.NONE);
        checkExpressionComposite.setLayout(new GridLayout(1, false));
        GridData checkExpressionCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        checkExpressionCompositeGD.horizontalIndent = 5;
        checkExpressionComposite.setLayoutData(checkExpressionCompositeGD);

        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        addUIForCheckExpression(image, checkExpressionComposite);
    }

    /**
     * Adds the UI for check expression.
     *
     * @param compositeConstraints the composite constraints
     * @param image the image
     * @param group the group
     */
    private void addUIForCheckExpression(Image image, Composite comp) {
        lblChkExpr = new Label(comp, SWT.NONE);
        lblChkExpr.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_CHECK_EXPRESSION));
        lblChkExpr.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, comp));
        lblChkExpr.pack();

        textCheckConsExpr = new StyledText(comp, SWT.BORDER | SWT.WRAP);
        GridData textCheckConsExprGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        textCheckConsExprGD.widthHint = 200;
        textCheckConsExpr.setLayoutData(textCheckConsExprGD);
        textCheckConsExpr.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONSTRAINTUI_EXPR_CHK_001");
        deco1 = new ControlDecoration(textCheckConsExpr, SWT.TOP | SWT.LEFT);
        // set description and image
        deco1.setDescriptionText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_EXPRESSION));
        deco1.setImage(image);
        // always show decoration
        deco1.setShowOnlyOnFocus(false);
    }

    /**
     * Adds the ui for selected columns ui for check.
     *
     * @param group the group
     */
    private void addUiForSelectedColumnsUiForCheck(Composite comp) {
        lblAvailColumns = new Label(comp, SWT.NONE);
        lblAvailColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_AVAILABLE_COLUMNS));
        lblAvailColumns.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, comp));

        tableCheckAvailableCols = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tableCheckAvailableColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableCheckAvailableColsGD.widthHint = 215;
        tableCheckAvailableCols.setLayoutData(tableCheckAvailableColsGD);
        tableCheckAvailableCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CONSTRAINTUI_AVL_COLS_CHK_001");
        tableCheckAvailableCols.setLinesVisible(true);
        tableCheckAvailableCols.setHeaderVisible(true);

        TableColumn tableColumn = new TableColumn(tableCheckAvailableCols, SWT.NONE);
        tableColumn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CONSTRAINTUI_AVL_COL_NAME_CHK_001");
        tableColumn.setWidth(100);
        tableColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_COLUMN_NAME));

        TableColumn tableColumn1 = new TableColumn(tableCheckAvailableCols, SWT.NONE);
        tableColumn1.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CONSTRAINTUI_DATATYPE_CHK_001");
        tableColumn1.setWidth(140);
        tableColumn1.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_DATA_TYPE));
    }

    /**
     * Adds the ui for unique key selection.
     *
     * @return the image
     */
    private void addUiForUniqueKeySelection() {
        Group group1 = new Group(compositePkeyUnique, SWT.NONE);
        group1.setLayout(new GridLayout(1, false));
        GridData group1GD = new GridData(SWT.FILL, SWT.FILL, true, true);
        group1.setLayoutData(group1GD);

        Composite uniqueColumnsComposite = new Composite(group1, SWT.NONE);
        uniqueColumnsComposite.setLayout(new GridLayout(4, false));
        GridData uniqueColumnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        uniqueColumnsComposite.setLayoutData(uniqueColumnsCompositeGD);

        addUiForAvailableColumnForUnique(uniqueColumnsComposite);
        addLeftRightBtnsForUnique(uniqueColumnsComposite);
        addSelectedColumnsForUnique(uniqueColumnsComposite);
        addUpDownBtnsForUnique(uniqueColumnsComposite);

        Composite constraintInfoComposite = new Composite(group1, SWT.NONE);
        constraintInfoComposite.setLayout(new GridLayout(3, false));
        GridData constraintInfoCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        constraintInfoComposite.setLayoutData(constraintInfoCompositeGD);

        addUiForConstraintInformation(constraintInfoComposite);
    }

    private void addUpDownBtnsForUnique(Composite comp) {
        Composite upDownBtnsComposite = new Composite(comp, SWT.NONE);
        upDownBtnsComposite.setLayout(new GridLayout(1, false));
        GridData upDownBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        upDownBtnsComposite.setLayoutData(upDownBtnsCompositeGD);

        Button btnpukMoveUp = new Button(upDownBtnsComposite, SWT.ARROW | SWT.UP);
        GridData btnpukMoveUpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnpukMoveUpGD.verticalAlignment = SWT.BOTTOM;
        btnpukMoveUpGD.heightHint = 30;
        btnpukMoveUpGD.widthHint = 30;
        btnpukMoveUp.setLayoutData(btnpukMoveUpGD);
        btnpukMoveUp.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CONSTRAINTUI_MOVE_UP_COL_001");
        btnpukMoveUp.addSelectionListener(new MoveBtnSelectionListener(true));
        btnpukMoveUp.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_NEW_BTN));

        Button btnpukMoveDown = new Button(upDownBtnsComposite, SWT.ARROW | SWT.DOWN);
        GridData btnpukMoveDownGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnpukMoveDownGD.verticalAlignment = SWT.UP;
        btnpukMoveDownGD.heightHint = 30;
        btnpukMoveDownGD.widthHint = 30;
        btnpukMoveDown.setLayoutData(btnpukMoveDownGD);
        btnpukMoveDown.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CONSTRAINTUI_MOVE_DOWN_COL_001");
        btnpukMoveDown.addSelectionListener(new MoveBtnSelectionListener(false));
        btnpukMoveDown.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_NEW_BTN));
    }

    private void addLeftRightBtnsForUnique(Composite comp) {
        Composite leftRightBtnsComposite = new Composite(comp, SWT.NONE);
        leftRightBtnsComposite.setLayout(new GridLayout(1, false));
        GridData leftRightBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        leftRightBtnsComposite.setLayoutData(leftRightBtnsCompositeGD);

        Button btnPukAddCol = new Button(leftRightBtnsComposite, SWT.ARROW | SWT.RIGHT);
        GridData btnPukAddColGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnPukAddColGD.verticalAlignment = SWT.BOTTOM;
        btnPukAddColGD.heightHint = 30;
        btnPukAddColGD.widthHint = 30;
        btnPukAddCol.setLayoutData(btnPukAddColGD);
        btnPukAddCol.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CONSTRAINTUI_ADD_COL_001");
        btnPukAddCol.addSelectionListener(new AddColumnSelectionListener());
        btnPukAddCol.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_NEW_BTN));

        Button btnpukRemoveCol = new Button(leftRightBtnsComposite, SWT.ARROW | SWT.LEFT);
        GridData btnpukRemoveColGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnpukRemoveColGD.verticalAlignment = SWT.UP;
        btnpukRemoveColGD.heightHint = 30;
        btnpukRemoveColGD.widthHint = 30;
        btnpukRemoveCol.setLayoutData(btnpukRemoveColGD);
        btnpukRemoveCol.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_CONSTRAINTUI_REMOVE_COL_001");
        btnpukRemoveCol.addSelectionListener(new RemoveColumnSelectionListener());
        btnpukRemoveCol.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_NEW_BTN));
    }

    /**
     * Adds the ui for constraint information.
     *
     * @param group1 the group 1
     */
    private void addUiForConstraintInformation(Composite comp) {
        Composite tablespaceComposite = new Composite(comp, SWT.NONE);
        tablespaceComposite.setLayout(new GridLayout(1, false));
        GridData tablespaceCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablespaceCompositeGD.horizontalAlignment = SWT.CENTER;
        tablespaceComposite.setLayoutData(tablespaceCompositeGD);

        Label lblTablespace = new Label(tablespaceComposite, SWT.NONE);
        lblTablespace.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_ON_TABLESPACE));
        lblTablespace.pack();

        cmbPukTablespace = new Combo(tablespaceComposite, SWT.READ_ONLY);
        GridData cmbPukTablespaceGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbPukTablespace.setLayoutData(cmbPukTablespaceGD);
        cmbPukTablespace.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_CONSTRAINTUI_TBLSPACE_001");
        UIUtils.displayTablespaceList(this.db, cmbPukTablespace, true, TableOrientation.ROW);

        Composite fillFactorComposite = new Composite(comp, SWT.NONE);
        fillFactorComposite.setLayout(new GridLayout(1, false));
        GridData fillFactorCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        fillFactorCompositeGD.horizontalAlignment = SWT.CENTER;
        fillFactorComposite.setLayoutData(fillFactorCompositeGD);

        Label lblFillfactor = new Label(fillFactorComposite, SWT.NONE);
        lblFillfactor.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_FILL_FACTOR));
        lblFillfactor.pack();

        spinnerPukFillfactor = new Spinner(fillFactorComposite, SWT.BORDER);
        GridData spinnerPukFillfactorGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        spinnerPukFillfactor.setLayoutData(spinnerPukFillfactorGD);
        spinnerPukFillfactor.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_SPINNER_CONSTRAINTUI_FILLFACTOR_001");
        spinnerPukFillfactor.setMinimum(10);
        spinnerPukFillfactor.setSelection(90);
        spinnerPukFillfactor.setMaximum(100);

        Composite chkBtnsComposite = new Composite(comp, SWT.NONE);
        chkBtnsComposite.setLayout(new GridLayout(1, false));
        GridData chkBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        chkBtnsCompositeGD.verticalAlignment = SWT.END;
        chkBtnsCompositeGD.horizontalAlignment = SWT.CENTER;
        chkBtnsComposite.setLayoutData(chkBtnsCompositeGD);

        chkPukDeferable = new Button(chkBtnsComposite, SWT.CHECK);
        GridData chkPukDeferableGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        chkPukDeferable.setLayoutData(chkPukDeferableGD);
        chkPukDeferable.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CONSTRAINTUI_DEFERABLE_001");
        chkPukDeferable.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_DEFERABLE));

        chkPukInitDefered = new Button(chkBtnsComposite, SWT.CHECK);
        GridData chkPukInitDeferedGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        chkPukInitDefered.setLayoutData(chkPukInitDeferedGD);
        chkPukInitDefered.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_CONSTRAINTUI_INIT_DEFERED_001");
        chkPukInitDefered.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_INITIALLY_DEFERRED));
    }

    /**
     * Adds the ui for available column for unique.
     *
     * @param group1 the group 1
     */
    private void addUiForAvailableColumnForUnique(Composite Comp) {
        tablePukAvailableCol = new Table(Comp, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tablePukAvailableColGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablePukAvailableColGD.widthHint = 215;
        tablePukAvailableColGD.heightHint = 100;
        tablePukAvailableCol.setLayoutData(tablePukAvailableColGD);
        tablePukAvailableCol.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CONSTRAINTUI_AVL_COLS_PU_001");
        tablePukAvailableCol.setHeaderVisible(true);
        tablePukAvailableCol.setLinesVisible(true);

        TableColumn tblclmnColumnname = new TableColumn(tablePukAvailableCol, SWT.NONE);
        tblclmnColumnname.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_CONSTRAINTUI_AVL_COL_NAME_PU_001");
        tblclmnColumnname.setWidth(215);
        tblclmnColumnname.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_AVAILABLE_COLUMNS));
    }

    /**
     * Adds the selected columns for unique.
     *
     * @param group1 the group 1
     * @return the image
     */
    private void addSelectedColumnsForUnique(Composite Comp) {
        tablePukSelCols = new Table(Comp, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tablePukSelColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablePukSelColsGD.horizontalIndent = 5;
        tablePukSelColsGD.widthHint = 215;
        tablePukSelColsGD.heightHint = 100;
        tablePukSelCols.setLayoutData(tablePukSelColsGD);
        tablePukSelCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_CONSTRAINTUI_SEL_COLS_001");
        tablePukSelCols.setLinesVisible(true);
        tablePukSelCols.setHeaderVisible(true);

        deco = new ControlDecoration(tablePukSelCols, SWT.TOP | SWT.LEFT);
        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        // set description and image
        deco.setDescriptionText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_DESC));
        deco.setImage(image);
        // always show decoration
        deco.setShowOnlyOnFocus(false);

        TableColumn tblclmnIndexColumns = new TableColumn(tablePukSelCols, SWT.NONE);
        tblclmnIndexColumns.setWidth(215);
        tblclmnIndexColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSTRAINT_SELECTED_COLUMNS));
    }

    /**
     * The listener interface for receiving constraintTypeSelection events. The
     * class that is interested in processing a constraintTypeSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addConstraintTypeSelectionListener<code> method. When the
     * constraintTypeSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * ConstraintTypeSelectionEvent
     */
    private class ConstraintTypeSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            setCompositeVisibility();

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving addColumnSelection events. The class
     * that is interested in processing a addColumnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addAddColumnSelectionListener<code> method. When the
     * addColumnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * AddColumnSelectionEvent
     */
    private class AddColumnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            UIUtils.addSelectedCol(tablePukAvailableCol, tablePukSelCols);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving removeColumnSelection events. The
     * class that is interested in processing a removeColumnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addRemoveColumnSelectionListener<code> method. When the
     * removeColumnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * RemoveColumnSelectionEvent
     */
    private class RemoveColumnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            UIUtils.removeSelectedCol(tablePukSelCols);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving moveBtnSelection events. The class
     * that is interested in processing a moveBtnSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addMoveBtnSelectionListener<code>
     * method. When the moveBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * MoveBtnSelectionEvent
     */
    private class MoveBtnSelectionListener implements SelectionListener {

        private boolean isUp;

        /**
         * Instantiates a new move btn selection listener.
         *
         * @param isUp the is up
         */
        public MoveBtnSelectionListener(boolean isUp) {
            this.isUp = isUp;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            moveColumn(isUp);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CheckAppendColSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class CheckAppendColSelectionListner implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            if (0 <= tableCheckAvailableCols.getSelectionIndex()) {
                textCheckConsExpr.append(
                        ServerObject.getQualifiedObjectName(tableCheckAvailableCols.getSelection()[0].getText(0)));
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * The listener interface for receiving Foreign Namespace selectition events. 
     * that is interested in processing a  event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>ForeignNamespaceSelectionListener<code> method. When the
     * ForeignNamespaceSelectionListener event occurs, that object's appropriate method is
     * invoked.
     *
     * ForeignNamespaceSelectionListener
     */

    private class ForeignNamespaceSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            if (cmbForeignColumn != null) {
                cmbForeignColumn.removeAll();
            }
            if (cmbForeignTable != null) {
                cmbForeignTable.removeAll();
                String nsName = cmbForeignNamespace.getText();
                Namespace ns = db.getUserNamespaces().get(nsName);
                if (ns != null) {
                    UIUtils.displayTablenameList(ns, cmbForeignTable, false);
                    cmbForeignTable.select(0);
                    cmbForeignTable.notifyListeners(SWT.Selection, new Event());
                }
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving Foreign Table selectition events. 
     * that is interested in processing a  event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>ForeignTableSelectionListener<code> method. When the
     * ForeignTableSelectionListener event occurs, that object's appropriate method is
     * invoked.
     *
     * ForeignTableSelectionListener
     */
    private class ForeignTableSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            if (cmbForeignColumn != null) {
                Namespace ns = db.getUserNamespaces().get(cmbForeignNamespace.getText());
                String tableName = cmbForeignTable.getText();
                List<TableMetaData> tables = ns.getAllTablesForNamespace();
                for (TableMetaData table: tables) {
                    if (table.getName().equals(tableName)) {
                        UIUtils.displayColumnList(table, cmbForeignColumn);
                        cmbForeignColumn.select(0);
                        break;
                    }
                }
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Populate constraint type.
     */
    private void populateConstraintType() {
        List<ConstraintType> validConstraintTypes = new ArrayList<ConstraintType>(1);
        if (orientation == TableOrientation.COLUMN) {
            validConstraintTypes.add(ConstraintType.PARTIAL_CLUSTER_KEY);
        } else {
            validConstraintTypes.add(ConstraintType.CHECK_CONSTRSINT);
            validConstraintTypes.add(ConstraintType.UNIQUE_KEY_CONSTRSINT);
            validConstraintTypes.add(ConstraintType.PRIMARY_KEY_CONSTRSINT);
            if (this.db.getServer().versionAfter930()) {
                validConstraintTypes.add(ConstraintType.FOREIGN_KEY_CONSTRSINT);
            }
        }

        String[] items = validConstraintTypes.stream()
                .map(cntType -> cntType.strType)
                .toArray(String[]::new);
        cmbConstraintType.setItems(items);
        cmbConstraintType.select(0);
    }

    /**
     * Gets the constraint.
     *
     * @param isConstraintUpdate the is constraint update
     * @return the constraint
     */
    public ConstraintMetaData getConstraint(boolean isConstraintUpdate) {
        ConstraintMetaData cons = null;
        String name = textTblConstraintName.getText();

        if (parentTable != null) {
            List<ConstraintMetaData> metaDatas = parentTable.getConstraints().getList();

            int size = metaDatas.size();
            ConstraintMetaData data = null;
            for (int index = 0; index < size; index++) {
                data = metaDatas.get(index);
                if (data.getName().trim().equalsIgnoreCase(name.trim())) {
                    return null;
                }
            }
        }

        ConstraintType constraintType = ConstraintType.strTypeConvert(
                cmbConstraintType.getText());
        switch (constraintType) {
            case CHECK_CONSTRSINT: {
                if (textCheckConsExpr.getText().isEmpty()) {
                    return null;
                }
                cons = new ConstraintMetaData(0, name, ConstraintType.CHECK_CONSTRSINT);
                cons.setCheckConstraintExpr(textCheckConsExpr.getText());
                break;
            }
            case UNIQUE_KEY_CONSTRSINT:
            case PRIMARY_KEY_CONSTRSINT:
            case PARTIAL_CLUSTER_KEY: {
                if (tablePukSelCols.getItemCount() <= 0) {
                    return null;
                }
                cons = getPrimaryOrUniqueConstraint(isConstraintUpdate, name);
                break;
            }
            case FOREIGN_KEY_CONSTRSINT: {
                if (tableForeignCols.getItemCount() <= 0) {
                    return null;
                }
                cons = getForeignKeyConstraint(isConstraintUpdate, name);
                break;
            }
            default: {
                return null;
            }
        }
        return cons;
    }

    private ConstraintMetaData getForeignKeyConstraint(boolean isConstraintUpdate, String name) {
        TableItem[] item = tableForeignCols.getSelection();
        if (item.length != 1) {
            return null;
        }
        final String columnName = item[0].getText();
        final String namespace = cmbForeignNamespace.getText();
        final String table = cmbForeignTable.getText();
        final String tableColumn = cmbForeignColumn.getText();
        if ("".equals(namespace) || "".equals(table) || "".equals(tableColumn)) {
            return null;
        }
        return new ConstraintMetaData(0, name, ConstraintType.FOREIGN_KEY_CONSTRSINT) {
            @Override
            public String formConstraintString() {
                StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                appendQuery(sb);
                sb.append(contype.strType);
                sb.append(" (");
                sb.append(ServerObject.getQualifiedObjectName(columnName));
                sb.append(") REFERENCES ");
                sb.append(ServerObject.getQualifiedObjectName(namespace));
                sb.append('.');
                sb.append(ServerObject.getQualifiedObjectName(table));
                sb.append(" (");
                sb.append(tableColumn);
                sb.append(") ");
                return sb.toString();
            }

            private void appendQuery(StringBuilder query) {
                if (!getName().isEmpty()) {
                    query.append("CONSTRAINT ");
                    query.append(ServerObject.getQualifiedObjectName(getName()));
                    query.append(' ');
                }
            }
        };
    }

    private ConstraintMetaData getPrimaryOrUniqueConstraint(boolean isConstraintUpdate, String name) {
        ConstraintMetaData cons = null;
        String columnList = null;
        boolean condeferrable = false;
        boolean condeferred = false;
        ConstraintType type = ConstraintType.strTypeConvert(cmbConstraintType.getText());
        cons = new ConstraintMetaData(0, name, type);
        String tableSpace = null;

        columnList = UIUtils.getColumnwiseString(tablePukSelCols, 0, isConstraintUpdate);

        if (cmbPukTablespace.getSelectionIndex() != 0) {
            tableSpace = cmbPukTablespace.getText();
        }

        condeferrable = chkPukDeferable.getSelection() || chkPukInitDefered.getSelection();
        condeferred = chkPukInitDefered.getSelection();
        cons.setPkeyOrUkeyConstraint(columnList, tableSpace);
        cons.setDeffearableOptions(condeferrable, condeferred);
        cons.setFillfactor(spinnerPukFillfactor.getSelection());
        return cons;
    }

    /**
     * Sets the composite visibility.
     */
    private void setCompositeVisibility() {
        ConstraintType constraintType = ConstraintType.strTypeConvert(cmbConstraintType.getText());
        switch (constraintType) {
            case CHECK_CONSTRSINT: {
                layout.topControl = compositeCheck;
                overLayComposite.layout();
                break;
            }
            case UNIQUE_KEY_CONSTRSINT:
            case PRIMARY_KEY_CONSTRSINT:
            case PARTIAL_CLUSTER_KEY: {
                layout.topControl = compositePkeyUnique;
                overLayComposite.layout();
                break;
            }
            case FOREIGN_KEY_CONSTRSINT: {
                layout.topControl = compositeForeign;
                overLayComposite.layout();
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Checks if is check constraint selected.
     *
     * @return true, if is check constraint selected
     */
    public boolean isCheckConstraintSelected() {
        return ConstraintType.CHECK_CONSTRSINT.strType
                .equals(cmbConstraintType.getText());
    }

    /**
     * Sets the constraint data.
     *
     * @param metaData the new constraint data
     */
    public void setConstraintData(ConstraintMetaData metaData) {
        textTblConstraintName.setText(metaData.getName());
        textCheckConsExpr.setText(metaData.getCheckConstraintExpr() != null ? metaData.getCheckConstraintExpr() : "");

        if (metaData.getConstraintType().equals(ConstraintType.PRIMARY_KEY_CONSTRSINT)
                || metaData.getConstraintType().equals(ConstraintType.UNIQUE_KEY_CONSTRSINT)) {
            int tblSpaceSize = cmbPukTablespace.getItemCount();
            cmbPukTablespace.select(0);
            for (int index = 0; index < tblSpaceSize; index++) {
                if (cmbPukTablespace.getItem(index).equalsIgnoreCase(metaData.getTableSpace())) {
                    cmbPukTablespace.select(index);
                    break;
                }
            }

            tablePukSelCols.removeAll();
            String[] pukStrings = metaData.getColumnList().split(",");
            int colSize = pukStrings.length;
            TableItem tableItem = null;
            for (int index = 0; index < colSize; index++) {
                if (null != pukStrings[index] && pukStrings[index].length() > 0) {
                    tableItem = new TableItem(tablePukSelCols, SWT.NONE);
                    tableItem.setText(pukStrings[index].trim());
                }
            }

            spinnerPukFillfactor.setSelection(metaData.getConstraintFillfactor());
            chkPukDeferable.setSelection(metaData.isDeferable());
            chkPukInitDefered.setSelection(metaData.isCondeferred());
        }

        cmbConstraintType.select(metaData.getConstraintType().ordinal());
        setCompositeVisibility();
    }

    /**
     * Clear constraint data.
     */
    public void clearConstraintData() {
        textTblConstraintName.setText("");
        textCheckConsExpr.setText("");
        tablePukSelCols.removeAll();
        cmbPukTablespace.select(0);
        spinnerPukFillfactor.setSelection(90);
        chkPukDeferable.setSelection(false);
        chkPukInitDefered.setSelection(false);
    }

    /**
     * Adds the column.
     *
     * @param rowdata the rowdata
     */
    public void addColumn(String[] rowdata) {
        TableItem tableItem = null;
        tableItem = new TableItem(tableCheckAvailableCols, SWT.NONE);
        tableItem.setText(rowdata);

        tableItem = new TableItem(tablePukAvailableCol, SWT.NONE);
        tableItem.setText(rowdata);

        tableItem = new TableItem(tableForeignCols, SWT.NONE);
        tableItem.setText(rowdata);
    }

    /**
     * Adds the column.
     *
     * @param rowdata the rowdata
     * @param index the index
     */
    public void addColumn(String[] rowdata, int index) {
        TableItem tableItem = null;
        tableItem = new TableItem(tableCheckAvailableCols, SWT.NONE, index);
        tableItem.setText(rowdata);

        tableItem = new TableItem(tablePukAvailableCol, SWT.NONE, index);
        tableItem.setText(rowdata);

        tableItem = new TableItem(tableForeignCols, SWT.NONE, index);
        tableItem.setText(rowdata);
    }

    /**
     * Removes the column.
     *
     * @param index the index
     */
    public void removeColumn(int index) {
        tableCheckAvailableCols.remove(index);
        tablePukAvailableCol.remove(index);
        tableForeignCols.remove(index);
    }

    /**
     * Update column.
     *
     * @param index the index
     * @param rowdata the rowdata
     */
    public void updateColumn(int index, String[] rowdata) {
        TableItem tableItem = null;
        tableItem = tableCheckAvailableCols.getItem(index);
        tableItem.setText(rowdata);
        tableItem = tablePukAvailableCol.getItem(index);
        tableItem.setText(rowdata);
        tableItem = tableForeignCols.getItem(index);
        tableItem.setText(rowdata);
    }

    /**
     * Gets the grp control.
     *
     * @param compositeConstraints the composite constraints
     * @return the grp control
     */
    public Group getGrpControl(Composite compositeConstraints) {
        grpAddConstrains = new Group(compositeConstraints, SWT.NONE);
        grpAddConstrains.setLayout(new GridLayout(1, false));
        GridData grpAddConstrainsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        grpAddConstrains.setLayoutData(grpAddConstrainsGD);

        return grpAddConstrains;
    }

    /**
     * Move column.
     *
     * @param up the up
     */
    private void moveColumn(boolean up) {
        int selectedIndex = tablePukSelCols.getSelectionIndex();
        int targetIndex = up ? selectedIndex - 1 : selectedIndex + 1;

        /*
         * boundary check for none selected / top most item moving up / bottom
         * most item moving down
         */
        if (selectedIndex == -1 || targetIndex < 0 || targetIndex >= tablePukSelCols.getItemCount()) {
            return;
        }

        String strAtSelectedIndex = tablePukSelCols.getItem(selectedIndex).getText();
        String strAttargetIndex = tablePukSelCols.getItem(targetIndex).getText();

        tablePukSelCols.getItem(selectedIndex).setText(strAttargetIndex);
        tablePukSelCols.getItem(targetIndex).setText(strAtSelectedIndex);
        tablePukSelCols.setSelection(targetIndex);
    }

    /**
     * Sets the focus on constraint name.
     */
    public void setFocusOnConstraintName() {
        if (textTblConstraintName != null) {
            textTblConstraintName.forceFocus();
        }
    }

    /**
     * Sets the constraint UI table.
     *
     * @param tableTblConstraints the new constraint UI table
     */
    public void setConstraintUITable(Table tableTblConstraints) {
        this.constraintTableColumn = tableTblConstraints;
    }

    /**
     * Gets the constraint UI table.
     *
     * @return the constraint UI table
     */
    public Table getConstraintUITable() {
        return this.constraintTableColumn;
    }

    /**
     * addConstraintComponent
     * 
     * @param value boolean
     * @param table object
     */
    public void addConstraintComponent(boolean value, TableMetaData table) {
        orientation = value ? TableOrientation.ROW : TableOrientation.COLUMN;
        populateConstraintType();
        setCompositeVisibility();
        if (!value) {
            getTextTblConstraintName().setText("");
            // clear the table UI component having index expressions
            if (getConstraintUITable() != null) {
                getConstraintUITable().removeAll();
                clearConstraintData();
                // clear the constraint list in tablemetatda to reform query in
                // sql preview
                table.getConstraintMetaDataList().clear();
           }
        }
    }
}
