/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionColumnExpr;
import com.huawei.mppdbide.bl.serverdatacache.PartitionColumnType;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTypeEnum;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.connectiondialog.PartitionValueDialog;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.UIVerifier;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionUI.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PartitionUI {

    private Server server;
    private PartitionTable pTable;

    private Table tblAvailCols;
    private Table tblSelCols;

    private List<PartitionColumnExpr> availCols;
    private List<PartitionColumnExpr> selCols = new ArrayList<PartitionColumnExpr>(4);

    private Text txtPartitionName;
    private Text txtPartitionValue;
    private Text txtIntervalPartitionExpr;
    private Combo cmbTablespace;
    private List<Long> tablespaceOids = new ArrayList<Long>(4);
    private Table tblPartitions;
    private PartitionMetaData partitionMetadata;
    private Label lblErrorMsg;
    private Label orientationselected;
    private Combo partitionTypeCombo;
    private String partitionTypeString;
    private Group grpPartitions;
    private boolean isPartitionUpdate = false;
    private Button btnNewButton = null;
    private Button btnDelete = null;
    private Button btnEdit = null;
    private Button btnUp = null;
    private Button btnDown = null;
    private Button btnAddCol = null;
    private Button btnRemCol = null;
    private Button btnMoveUp = null;
    private Button btnMoveDown = null;
    private TabFolder tabFolder;
    private static final int CREATE_TABLE_PARTITION = 5;
    private CreatePartitionTable createPartitionTable;
    private ControlDecoration decofk;
    private Button btnPartitionValue = null;
    private LinkedHashMap<String, String> partitionValueMap = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> editPartitionValueMap = new LinkedHashMap<String, String>();
    private List<String> partitionValueList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    private static final String PART_VALUE_BUTTON = "...";

    /**
     * Instantiates a new partition UI.
     *
     * @param server the server
     * @param partitionTable the partition table
     */
    public PartitionUI(Server server, PartitionTable partitionTable) {

        this.server = server;
        this.pTable = partitionTable;
    }

    /**
     * Gets the decofk.
     *
     * @return the decofk
     */
    public ControlDecoration getDecofk() {
        return decofk;
    }

    /**
     * Gets the tbl sel cols.
     *
     * @return the tbl sel cols
     */
    public Table getTblSelCols() {
        return tblSelCols;
    }

    /**
     * Gets the p table.
     *
     * @return the p table
     */
    public PartitionTable getpTable() {
        return pTable;
    }

    /**
     * Creates the partition info gui.
     *
     * @param compositePartition the composite partition
     */
    public void createPartitionInfoGui(Composite compositePartition) {
        final Shell activeShell = compositePartition.getShell();
        compositePartition.setLayout(new GridLayout(1, false));
        addPartitionTypeGroup(compositePartition);

        Group grpPartitionByRowcolumn = new Group(compositePartition, SWT.NONE);
        grpPartitionByRowcolumn.setLayout(new GridLayout(1, false));
        GridData grpPartitionByRowcolumnGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        grpPartitionByRowcolumn.setLayoutData(grpPartitionByRowcolumnGD);
        grpPartitionByRowcolumn
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_PARTITION_BY_RANGE));

        Composite partitionColumnsComposite = new Composite(grpPartitionByRowcolumn, SWT.NONE);
        partitionColumnsComposite.setLayout(new GridLayout(4, false));
        GridData partitionColumnsCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        partitionColumnsComposite.setLayoutData(partitionColumnsCompositeGD);

        addUiForAvailableColumnForPartition(partitionColumnsComposite);
        addLeftRightBtnsForPartition(partitionColumnsComposite);
        addSelectedColumnsForPartition(partitionColumnsComposite);
        addUpDownBtnsForPartition(partitionColumnsComposite);

        Composite partitionInfoComposite = new Composite(grpPartitionByRowcolumn, SWT.NONE);
        partitionInfoComposite.setLayout(new GridLayout(3, false));
        GridData partitionInfoCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        partitionInfoComposite.setLayoutData(partitionInfoCompositeGD);

        addPartitionValueDetails(activeShell, partitionInfoComposite);

        addPartitionTable(grpPartitionByRowcolumn);

        addErrorMsgArea(compositePartition);

    }

    private void addUpDownBtnsForPartition(Composite partitionColumnsComposite) {
        Composite upDownBtnsComposite = new Composite(partitionColumnsComposite, SWT.NONE);
        upDownBtnsComposite.setLayout(new GridLayout(1, false));
        GridData upDownBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        upDownBtnsCompositeGD.verticalIndent = 10;
        upDownBtnsCompositeGD.verticalAlignment = SWT.CENTER;
        upDownBtnsComposite.setLayoutData(upDownBtnsCompositeGD);

        btnMoveUp = new Button(upDownBtnsComposite, SWT.ARROW | SWT.TOP);
        btnMoveUp.addSelectionListener(new MoveUpBtnSelectionListner());
        GridData btnMoveUpGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        btnMoveUpGD.heightHint = 30;
        btnMoveUp.setLayoutData(btnMoveUpGD);

        btnMoveDown = new Button(upDownBtnsComposite, SWT.ARROW | SWT.BOTTOM);
        btnMoveDown.addSelectionListener(new MoveDownBtnSelectionListner());
        GridData btnMoveDownGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        btnMoveDownGD.heightHint = 30;
        btnMoveDown.setLayoutData(btnMoveDownGD);
    }

    private void addLeftRightBtnsForPartition(Composite partitionColumnsComposite) {
        Composite leftRightBtnsComposite = new Composite(partitionColumnsComposite, SWT.NONE);
        leftRightBtnsComposite.setLayout(new GridLayout(1, false));
        GridData leftRightBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        leftRightBtnsCompositeGD.verticalIndent = 10;
        leftRightBtnsCompositeGD.verticalAlignment = SWT.CENTER;
        leftRightBtnsComposite.setLayoutData(leftRightBtnsCompositeGD);

        btnAddCol = new Button(leftRightBtnsComposite, SWT.ARROW | SWT.RIGHT);
        GridData btnAddColGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        btnAddColGD.heightHint = 30;
        btnAddCol.setLayoutData(btnAddColGD);
        btnAddCol.addSelectionListener(new AddBtnSelectionListner());

        btnRemCol = new Button(leftRightBtnsComposite, SWT.ARROW | SWT.LEFT);
        GridData btnRemColGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        btnRemColGD.heightHint = 30;
        btnRemCol.setLayoutData(btnRemColGD);
        btnRemCol.addSelectionListener(new RemoveBtnSelectionListner());
    }

    private void addSelectedColumnsForPartition(Composite partitionColumnsComposite) {
        Composite partitionSelectedColsComposite = new Composite(partitionColumnsComposite, SWT.NONE);
        partitionSelectedColsComposite.setLayout(new GridLayout(1, false));
        GridData partitionSelectedColsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionSelectedColsCompositeGD.widthHint = 215;
        partitionSelectedColsCompositeGD.heightHint = 180;
        partitionSelectedColsComposite.setLayoutData(partitionSelectedColsCompositeGD);

        Label lblPatitionColumn = new Label(partitionSelectedColsComposite, SWT.NONE);
        lblPatitionColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_PARTITION_COLUMN));
        lblPatitionColumn.pack();
        decofk = new ControlDecoration(lblPatitionColumn, SWT.TOP | SWT.LEFT);
        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        // set description and image
        decofk.setDescriptionText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MSG));
        decofk.setImage(image);

        tblSelCols = new Table(partitionSelectedColsComposite, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tblSelColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblSelCols.setLayoutData(tblSelColsGD);
        tblSelCols.setLinesVisible(true);
        tblSelCols.setHeaderVisible(true);

        TableColumn tableColumn = new TableColumn(tblSelCols, SWT.NONE);
        tableColumn.setWidth(120);
        tableColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.COL_NAME));

        TableColumn tableColumn1 = new TableColumn(tblSelCols, SWT.NONE);
        tableColumn1.setWidth(90);
        tableColumn1.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_TYPE));
    }

    private void addUiForAvailableColumnForPartition(Composite partitionColumnsComposite) {
        Composite partitionAvailColsComposite = new Composite(partitionColumnsComposite, SWT.NONE);
        partitionAvailColsComposite.setLayout(new GridLayout(1, false));
        GridData partitionAvailColsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionAvailColsCompositeGD.widthHint = 215;
        partitionAvailColsCompositeGD.heightHint = 180;
        partitionAvailColsComposite.setLayoutData(partitionAvailColsCompositeGD);

        Label lblAvaliableColumn = new Label(partitionAvailColsComposite, SWT.NONE);
        lblAvaliableColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_AVAILABLE_COLUMN));
        lblAvaliableColumn.pack();

        tblAvailCols = new Table(partitionAvailColsComposite, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tblAvailColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblAvailCols.setLayoutData(tblAvailColsGD);
        tblAvailCols.setHeaderVisible(true);
        tblAvailCols.setLinesVisible(true);

        TableColumn tblclmnNewColumn = new TableColumn(tblAvailCols, SWT.NONE);
        tblclmnNewColumn.setWidth(120);
        tblclmnNewColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.COL_NAME));

        TableColumn tblclmnNewColumn1 = new TableColumn(tblAvailCols, SWT.NONE);
        tblclmnNewColumn1.setWidth(90);
        tblclmnNewColumn1.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_TYPE));

        updateAvailableCols();
    }

    /**
     * Adds the partition table.
     *
     * @param grpPartitionByRowcolumn the grp partition by rowcolumn
     */
    private void addPartitionTable(Group grpPartitionByRowcolumn) {
        grpPartitions = new Group(grpPartitionByRowcolumn, SWT.NONE);
        grpPartitions.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_PARTITIONS));
        grpPartitions.setLayout(new GridLayout(2, false));
        GridData grpPartitionsGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        grpPartitions.setLayoutData(grpPartitionsGD);

        tblPartitions = new Table(grpPartitions, SWT.BORDER | SWT.FULL_SELECTION);
        GridData tblPartitionsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblPartitionsGD.widthHint = 450;
        tblPartitions.setLayoutData(tblPartitionsGD);
        tblPartitions.setHeaderVisible(true);
        tblPartitions.setLinesVisible(true);

        TableColumn tblclmnPartitionName = new TableColumn(tblPartitions, SWT.NONE);
        tblclmnPartitionName.setWidth(445);
        tblclmnPartitionName.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_PARTITION_DEF));

        Composite partitionBtnsComposite = new Composite(grpPartitions, SWT.NONE);
        partitionBtnsComposite.setLayout(new GridLayout(1, false));
        GridData partitionBtnsCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionBtnsCompositeGD.verticalAlignment = SWT.CENTER;
        partitionBtnsComposite.setLayoutData(partitionBtnsCompositeGD);

        addPartitionColumnsButtons(partitionBtnsComposite);
    }

    /**
     * Adds the error msg area.
     *
     * @param compositePartition the composite partition
     */
    private void addErrorMsgArea(Composite compositePartition) {
        lblErrorMsg = new Label(compositePartition, SWT.WRAP);
        lblErrorMsg.setText("");
        lblErrorMsg.setFont(FontAndColorUtility.getFont("Segoe UI", 9, SWT.NORMAL, compositePartition));
        GridData lblErrorMsgGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblErrorMsgGD.heightHint = 20;
        lblErrorMsg.setLayoutData(lblErrorMsgGD);
        lblErrorMsg.setVisible(false);
    }

    /**
     * Adds the partition columns buttons.
     *
     * @param Composite the partition column button composite
     */
    private void addPartitionColumnsButtons(Composite partitionBtnsComposite) {
        btnNewButton = new Button(partitionBtnsComposite, SWT.NONE);
        GridData btnNewButtonGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnNewButtonGD.heightHint = 20;
        btnNewButton.setLayoutData(btnNewButtonGD);
        btnNewButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.ADD_MSG));
        btnNewButton.addSelectionListener(new NewButtonSelectionListener());

        btnDelete = new Button(partitionBtnsComposite, SWT.NONE);
        GridData btnDeleteGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnDeleteGD.heightHint = 20;
        btnDelete.setLayoutData(btnDeleteGD);
        btnDelete.setText(MessageConfigLoader.getProperty(IMessagesConstants.DELETE_MSG));
        btnDelete.addSelectionListener(new DeleteBtnSelectionListner());

        btnEdit = new Button(partitionBtnsComposite, SWT.NONE);
        GridData btnEditGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnEditGD.heightHint = 20;
        btnEdit.setLayoutData(btnEditGD);
        btnEdit.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_MSG));
        btnEdit.addSelectionListener(new EditBtnSelectionListner());

        btnUp = new Button(partitionBtnsComposite, SWT.NONE);
        GridData btnUpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnUpGD.heightHint = 20;
        btnUp.setLayoutData(btnUpGD);
        btnUp.setText(MessageConfigLoader.getProperty(IMessagesConstants.UP_MSG));
        btnUp.addSelectionListener(new UpBtnSelectionListner());

        btnDown = new Button(partitionBtnsComposite, SWT.NONE);
        GridData btnDownGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnDownGD.heightHint = 20;
        btnDown.setLayoutData(btnDownGD);
        btnDown.setText(MessageConfigLoader.getProperty(IMessagesConstants.DOWN_MSG));
        btnDown.addSelectionListener(new DownBtnSelectionListner());
    }

    /**
     * Adds the partition value details.
     *
     * @param activeShell the active shell
     * @param grpPartitionByRowcolumn the grp partition by rowcolumn
     */
    private void addPartitionValueDetails(final Shell activeShell, Composite comp) {
        Composite partitionNameComposite = new Composite(comp, SWT.NONE);
        partitionNameComposite.setLayout(new GridLayout(1, false));
        GridData partitionNameCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionNameComposite.setLayoutData(partitionNameCompositeGD);

        Label lblPartitionName = new Label(partitionNameComposite, SWT.NONE);
        lblPartitionName.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_PATITION_NAME));
        lblPartitionName.pack();
        decofk = new ControlDecoration(lblPartitionName, SWT.TOP | SWT.LEFT);
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        decofk.setImage(image);

        txtPartitionName = new Text(partitionNameComposite, SWT.BORDER);
        GridData txtPartitionNameGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtPartitionName.setLayoutData(txtPartitionNameGD);
        UIVerifier.verifyTextSize(txtPartitionName, 63);

        Composite partitionValueComposite = new Composite(comp, SWT.NONE);
        partitionValueComposite.setLayout(new GridLayout(2, false));
        GridData partitionValueCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionValueComposite.setLayoutData(partitionValueCompositeGD);

        Label lblPartitionValue = new Label(partitionValueComposite, SWT.NONE);
        lblPartitionValue.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_PARTITION_VALUE));
        GridData lblPartitionValueGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblPartitionValueGD.horizontalSpan = 2;
        lblPartitionValue.setLayoutData(lblPartitionValueGD);
        lblPartitionValue.pack();
        decofk = new ControlDecoration(lblPartitionValue, SWT.TOP | SWT.LEFT);
        decofk.setImage(image);

        txtPartitionValue = new Text(partitionValueComposite, SWT.BORDER | SWT.READ_ONLY);
        GridData txtPartitionValueGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtPartitionValue.setLayoutData(txtPartitionValueGD);
        txtPartitionValue.setEnabled(false);

        btnPartitionValue = new Button(partitionValueComposite, SWT.NONE);
        GridData btnPartitionValueGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnPartitionValueGD.horizontalAlignment = SWT.LEFT;
        btnPartitionValue.setLayoutData(btnPartitionValueGD);
        btnPartitionValue.setText(PART_VALUE_BUTTON);
        btnPartitionValue.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_VALUE_TOOLTIP));
        btnPartitionValue.addSelectionListener(new PartitionValueSelectionListner(activeShell));

        Composite partitionTablespaceComposite = new Composite(comp, SWT.NONE);
        partitionTablespaceComposite.setLayout(new GridLayout(1, false));
        GridData partitionTablespaceCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionTablespaceComposite.setLayoutData(partitionTablespaceCompositeGD);

        Label lblTablespace = new Label(partitionTablespaceComposite, SWT.NONE);
        lblTablespace.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_TABLESPACE));
        lblTablespace.pack();

        cmbTablespace = new Combo(partitionTablespaceComposite, SWT.READ_ONLY);
        GridData cmbTablespaceGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbTablespace.setLayoutData(cmbTablespaceGD);
        updateTablespaceObject();
    }

    /**
     * Adds the partition type group.
     *
     * @param compositePartition the composite partition
     */
    private void addPartitionTypeGroup(Composite compositePartition) {
        Group grpPartitionType = new Group(compositePartition, SWT.NONE);
        grpPartitionType.setLayout(new GridLayout(3, false));
        GridData grpPartitionTypeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        grpPartitionType.setLayoutData(grpPartitionTypeGD);
        grpPartitionType.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_TYPE));

        Composite orientationComposite = new Composite(grpPartitionType, SWT.NONE);
        orientationComposite.setLayout(new GridLayout(2, false));
        GridData orientationCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        orientationComposite.setLayoutData(orientationCompositeGD);

        Label orientation = new Label(orientationComposite, SWT.NONE);
        orientation.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_ORIENTATION) + " :");
        orientation.pack();

        orientationselected = new Label(orientationComposite, SWT.READ_ONLY);
        orientationselected.setText(TableOrientation.ROW.toString());
        GridData orientationselectedGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        orientationselectedGD.widthHint = 100;
        orientationselected.setLayoutData(orientationselectedGD);

        Composite partitionTypeComposite = new Composite(grpPartitionType, SWT.NONE);
        partitionTypeComposite.setLayout(new GridLayout(2, false));
        GridData partitionTypeCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionTypeComposite.setLayoutData(partitionTypeCompositeGD);

        Label partitionType = new Label(partitionTypeComposite, SWT.NONE);
        partitionType.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TAB_TYPE) + " :");
        partitionType.pack();

        partitionTypeCombo = new Combo(partitionTypeComposite, SWT.READ_ONLY);
        GridData cmbPartitionTypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        partitionTypeCombo.setLayoutData(cmbPartitionTypeGD);
        partitionTypeCombo.setItems(PartitionTypeEnum.getPartitionTypeNameArray());
        partitionTypeCombo.select(0);
        partitionTypeString = partitionTypeCombo.getText();
        partitionTypeCombo.addSelectionListener(new PartitionTypeSelectListener());

        Composite intervalPartitionExprComposite = new Composite(grpPartitionType, SWT.NONE);
        intervalPartitionExprComposite.setLayout(new GridLayout(2, false));
        GridData intervalPartitionExprCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        intervalPartitionExprComposite.setLayoutData(intervalPartitionExprCompositeGD);

        Label intervalPartition = new Label(intervalPartitionExprComposite, SWT.NONE);
        intervalPartition.setText(MessageConfigLoader.getProperty(
                IMessagesConstants.PARTITION_TAB_INTERVAL_PARTITION_EXPR) + " :");
        intervalPartition.pack();

        txtIntervalPartitionExpr = new Text(intervalPartitionExprComposite, SWT.BORDER);
        GridData txtintervalPartitionExprGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtIntervalPartitionExpr.setLayoutData(txtintervalPartitionExprGD);
        txtIntervalPartitionExpr.setEnabled(false);
        txtIntervalPartitionExpr.addModifyListener(new PartitionIntervalValueModifiedListener());
    }

    private class PartitionIntervalValueModifiedListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent e) {
            String intervalPartitionExpr = txtIntervalPartitionExpr.getText();
            if (intervalPartitionExpr != null && !"".equals(intervalPartitionExpr)
                    && partitionMetadata != null) {
                partitionMetadata.setIntervalPartitionExpr(intervalPartitionExpr);
            }
        }
    }

    private class PartitionTypeSelectListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            String curSelectPartitionType = partitionTypeCombo.getText();
            if (partitionTypeString.equals(curSelectPartitionType)) {
                return;
            }
            partitionTypeString = curSelectPartitionType;
            availCols.addAll(selCols);
            selCols.clear();
            rePopulateCols(tblSelCols, selCols);
            rePopulateCols(tblAvailCols, availCols);
            modifyColumnsClear();
            txtIntervalPartitionExpr.setText("");
            txtIntervalPartitionExpr.setEnabled(false);
            txtPartitionValue.setEnabled(true);
            btnPartitionValue.setEnabled(true);
            if (PartitionTypeEnum.BY_HASH.getTypeName().equals(partitionTypeCombo.getText())) {
                txtPartitionValue.setEnabled(false);
                btnPartitionValue.setEnabled(false);
            }
            if (PartitionTypeEnum.BY_INTERVAL.getTypeName().equals(partitionTypeCombo.getText())) {
                txtIntervalPartitionExpr.setEnabled(true);
            }
        }
    }

    /**
     * The listener interface for receiving newButtonSelection events. The class
     * that is interested in processing a newButtonSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addNewButtonSelectionListener<code> method. When the
     * newButtonSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * NewButtonSelectionEvent
     */
    private class NewButtonSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            int idx = tblPartitions.getSelectionIndex();
            if (isPartitionUpdate) {
                enableDisableItems(MessageConfigLoader.getProperty(IMessagesConstants.ADD_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_MSG), true);
                createPartitionTable.setErrorMsg("");
                isPartitionUpdate = false;
                pTable.removePartition(idx);
                tblPartitions.remove(idx);
            }
            PartitionMetaData partition = null;
            showError("");
            try {
                partition = getPartitionMetaData();

            } catch (DatabaseOperationException e1) {
                showError(e1.getMessage());
                return;
            }

            pTable.addPartition(partition);

            TableItem row = new TableItem(tblPartitions, SWT.NONE);
            List<PartitionColumnExpr> selColumns = pTable.getSelColumns();
            if (selColumns != null && selColumns.size() > 0 && selColumns.get(0) != null) {
                partition.setColumnMetadata(selColumns.get(0).getCol());
            }

            if (selColumns != null) {
                String formCreatePartitionsQryString = partition.formCreatePartitionsQry(selColumns);
                row.setText(formCreatePartitionsQryString);
            }
            clear();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DeleteBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class DeleteBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (isPartitionUpdate) {
                enableDisableItems(MessageConfigLoader.getProperty(IMessagesConstants.ADD_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_MSG), true);
                clear();
                createPartitionTable.setErrorMsg("");
                isPartitionUpdate = false;
                tblPartitions.getSelection()[0].setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
            } else {
                showError("");
                int idx = tblPartitions.getSelectionIndex();
                if (-1 == idx) {
                    showError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_PARTITION_DELETE));
                    return;
                }
                pTable.removePartition(idx);
                tblPartitions.remove(idx);
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EditBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class EditBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {

            showError("");
            int idx = tblPartitions.getSelectionIndex();
            if (-1 == idx) {
                showError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_PARTITION_EDIT));
                return;
            } else {
                isPartitionUpdate = true;
                createPartitionTable
                        .setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_EDIT_MESSAGE));
                enableDisableItems(MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_MSG), false);
                tblPartitions.getSelection()[0].setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            }
            setPartitionObject(pTable.getPartitions().getItem(idx));

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class UpBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class UpBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            movePartition(true);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DownBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class DownBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            movePartition(false);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AddBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class AddBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {

            int selectedIdx = tblAvailCols.getSelectionIndex();
            if (selectedIdx > -1) {
                PartitionColumnExpr col = availCols.get(selectedIdx);

                if (isPartitionDefAvailable() && !isPartitionColToBeRemoved()) {
                    return;
                }

                if (validateColumnsCount() && validatePartitionColumnType(col)) {
                    showError("");
                    availCols.remove(selectedIdx);
                    tblAvailCols.remove(selectedIdx);
                    selCols.add(col);
                    modifyColumnsClear();
                    if (isPartitionDefAvailable()) {
                        removePartitionTableEntries(tblPartitions);
                        modifyColumnsClear();
                        pTable.getPartitions().clear();
                    }
                }
                rePopulateCols(tblSelCols, selCols);
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RemoveBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class RemoveBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            int selectedIdx = tblSelCols.getSelectionIndex();
            if (selectedIdx > -1) {
                PartitionColumnExpr col = selCols.get(selectedIdx);

                if (isPartitionDefAvailable() && !isPartitionColToBeRemoved()) {
                    return;
                }
                showError("");
                if (isPartitionDefAvailable()) {
                    pTable.getPartitions().clear();
                }
                modifyColumnsClear();
                selCols.remove(selectedIdx);
                tblSelCols.remove(selectedIdx);
                availCols.add(col);
                rePopulateCols(tblAvailCols, availCols);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class MoveUpBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class MoveUpBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            int selectedIdx = tblSelCols.getSelectionIndex();
            if (selectedIdx > 0) {
                PartitionColumnExpr col = selCols.get(selectedIdx);
                selCols.remove(selectedIdx);
                selCols.add(selectedIdx - 1, col);
                rePopulateCols(tblSelCols, selCols);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class MoveDownBtnSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class MoveDownBtnSelectionListner implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            int selectedIdx = tblSelCols.getSelectionIndex();
            if (selectedIdx > -1 && selectedIdx < (tblSelCols.getItemCount() - 1)) {
                PartitionColumnExpr col = selCols.get(selectedIdx);
                selCols.remove(selectedIdx);
                selCols.add(selectedIdx + 1, col);
                rePopulateCols(tblSelCols, selCols);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PartitionValueSelectionListner.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class PartitionValueSelectionListner implements SelectionListener {
        private Shell activeShell;

        public PartitionValueSelectionListner(Shell activeShell) {
            this.activeShell = activeShell;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            PartitionValueDialog partValueDialog = new PartitionValueDialog(activeShell, selCols, partitionValueMap,
                    isPartitionUpdate, partitionValueList);
            partValueDialog.open();
            if (partValueDialog.getOkPressed()) {
                StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

                for (Map.Entry<String, String> map : partitionValueMap.entrySet()) {
                    sb.append(map.getValue());
                    sb.append(",");
                }

                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.lastIndexOf(","));
                }
                txtPartitionValue.setText(sb.toString());
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * Checks if is partition def available.
     *
     * @return true, if is partition def available
     */
    private boolean isPartitionDefAvailable() {
        return pTable.isPartitionsAvailable();
    }

    /**
     * Clear.
     */
    private void clear() {
        txtPartitionName.setText("");
        txtPartitionValue.setText("");
        editPartitionValueMap.putAll(partitionValueMap);
        partitionValueMap.clear();
        cmbTablespace.clearSelection();
        cmbTablespace.select(0);
        rePopulateCols(tblSelCols, selCols);
    }

    /**
     * Partition type based on orientation
     */
    private void partitionTypeBasedOnOrientation() {
        if (pTable.getOrientation() == TableOrientation.COLUMN) {
            partitionTypeCombo.setItems(PartitionTypeEnum.BY_RANGE.getTypeName());
            txtIntervalPartitionExpr.setText("");
            txtIntervalPartitionExpr.setEnabled(false);
        } else {
            partitionTypeCombo.setItems(PartitionTypeEnum.getPartitionTypeNameArray());
        }
        partitionTypeCombo.select(0);
        partitionTypeString = partitionTypeCombo.getText();
    }

    /**
     * Enable disable items.
     *
     * @param property the property
     * @param property2 the property 2
     * @param value the value
     */
    private void enableDisableItems(String property, String property2, boolean value) {
        btnNewButton.setText(property);
        btnDelete.setText(property2);
        btnEdit.setVisible(value);
        btnUp.setVisible(value);
        btnDown.setVisible(value);
        btnAddCol.setEnabled(value);
        btnRemCol.setEnabled(value);
        btnMoveUp.setEnabled(value);
        btnMoveDown.setEnabled(value);
        int curTab = tabFolder.getSelectionIndex();
        if (curTab == CREATE_TABLE_PARTITION) {
            for (int i = 0; i < tabFolder.getItemCount(); i++) {
                if (curTab != i) {
                    tabFolder.getItem(i).getControl().setEnabled(value);
                }
            }
        }

        createPartitionTable.setButtonEnable(value);
    }

    /**
     * Validate columns count.
     *
     * @return true, if successful
     */
    protected boolean validateColumnsCount() {
        TableOrientation orientation = ConvertToOrientation.convertToOrientationEnum(orientationselected.getText());

        if (validateTableOrientation(orientation)) {
            String partitionType = partitionTypeCombo.getText();
            if (PartitionTypeEnum.BY_RANGE.getTypeName().equals(partitionType)) {
                if (selCols.size() < 4) {
                    return true;
                } else {
                    showError(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_COLUMN_ERROR_THAN_FOUR));
                }
            } else {
                if (selCols.size() < 1)  {
                    return true;
                } else {
                    showError(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_COLUMN_ERROR_THAN_ONE));
                }
            }
        }
        return false;
    }

    /**
     * Validate partition column type.
     *
     * @return true, if successful
     */
    protected boolean validatePartitionColumnType(PartitionColumnExpr col) {
        if (PartitionTypeEnum.BY_INTERVAL.getTypeName().equals(partitionTypeCombo.getText())) {
            String typeName = col.getCol().getDataTypeName();
            if (typeName.contains("time") || typeName.contains("date")) {
                return true;
            } else {
                showError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PARTITION_INTERVAL_COLUMN_TYPE));
                return false;
            }
        }
        return true;
    }

    /**
     * Validate table orientation.
     *
     * @param orientation the orientation
     * @return true, if successful
     */
    private boolean validateTableOrientation(TableOrientation orientation) {
        return TableOrientation.ROW == orientation || TableOrientation.COLUMN == orientation;
    }

    /**
     * Sets the partition object.
     *
     * @param partMetadata the new partition object
     */
    private void setPartitionObject(PartitionMetaData partMetadata) {
        this.partitionMetadata = partMetadata;
        setPartitionName(partMetadata);
        setPartitionValue(partMetadata);
        setTablespaceName(partMetadata);
        setPartitionTypeName(partMetadata);
        if (partitionValueList != null &&
                !PartitionTypeEnum.BY_HASH.getTypeName().equals(partitionTypeCombo.getText())) {
            partitionValueList = partMetadata.getPartitionValuesAsList();
        }
        refreshColumns();
    }

    /**
     * Sets the tablespace name.
     *
     * @param partMetadata the new tablespace name
     */
    private void setTablespaceName(PartitionMetaData partMetadata) {
        if (cmbTablespace != null && cmbTablespace.getSelectionIndex() == 0 && !partMetadata.isTablespaceNull()) {
            cmbTablespace.setText(partMetadata.getTablespaceName());
        }
    }

    /**
     * Sets the partition value.
     *
     * @param partMetadata the new partition value
     */
    private void setPartitionValue(PartitionMetaData partMetadata) {
        if (txtPartitionValue != null &&
                !PartitionTypeEnum.BY_HASH.getTypeName().equals(partitionTypeCombo.getText())) {
            txtPartitionValue.setText(partMetadata.getPartitionValue());
        }
    }

    /**
     * Sets the partition type name.
     *
     * @param PartitionMetaData the new partitionMetadata
     */
    private void setPartitionTypeName(PartitionMetaData partMetadata) {
        if (partitionTypeCombo != null) {
            partitionTypeCombo.setText(partMetadata.getPartitionType());
        }
    }

    /**
     * Sets the partition name.
     *
     * @param partMetadata the new partition name
     */
    private void setPartitionName(PartitionMetaData partMetadata) {
        if (txtPartitionName != null) {
            txtPartitionName.setText(partMetadata.getPartitionName());
        }
    }

    /**
     * Update tablespace object.
     */
    private void updateTablespaceObject() {
        Iterator<Tablespace> tableSpaceItr = this.server.getTablespaces();
        boolean hasNext = tableSpaceItr.hasNext();
        Tablespace tablespace = null;
        cmbTablespace.removeAll();
        cmbTablespace.add(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_UI_SELECT));
        while (hasNext) {
            tablespace = tableSpaceItr.next();
            cmbTablespace.add(tablespace.getName());
            tablespaceOids.add(tablespace.getOid());
            hasNext = tableSpaceItr.hasNext();
        }

        cmbTablespace.select(0);
    }

    /**
     * Gets the selected tablespace.
     *
     * @return the selected tablespace
     */
    private Tablespace getSelectedTablespace() {
        int index = cmbTablespace.getSelectionIndex();
        if (index < 1) {
            return null;
        }

        long tsOid = tablespaceOids.get(index - 1);
        if (this.pTable != null) {
            return this.server.getTablespaceById(tsOid);
        }
        return null;
    }

    /**
     * Update available cols.
     */
    private void updateAvailableCols() {
        Iterator<ColumnMetaData> colItr = this.pTable.getColumnsList().iterator();
        boolean hasNext = colItr.hasNext();
        ColumnMetaData col = null;
        PartitionColumnExpr partitionColumn = null;
        TableItem item = null;
        String[] rowdata = null;

        this.availCols = new ArrayList<PartitionColumnExpr>(4);
        while (hasNext) {
            col = colItr.next();
            rowdata = new String[] {col.toString(), col.getDataTypeName()};

            partitionColumn = new PartitionColumnExpr(PartitionColumnType.COLUMN);
            partitionColumn.setCol(col);

            this.availCols.add(partitionColumn);
            item = new TableItem(tblAvailCols, SWT.NONE);

            item.setText(rowdata);

            hasNext = colItr.hasNext();
        }
    }

    /**
     * Re populate cols.
     *
     * @param tblCols the tbl cols
     * @param cols the cols
     */
    public void rePopulateCols(Table tblCols, List<PartitionColumnExpr> cols) {
        TableItem item = null;
        tblCols.removeAll();
        Iterator<PartitionColumnExpr> colsItr = cols.iterator();
        boolean hasNext = colsItr.hasNext();
        PartitionColumnExpr col = null;
        String[] rowdata = null;

        while (hasNext) {
            col = colsItr.next();
            rowdata = new String[] {col.toString(), col.getCol().getDataTypeName()};
            item = new TableItem(tblCols, SWT.NONE);
            item.setText(rowdata);

            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Refresh columns.
     */
    public void refreshColumns() {
        showError("");
        boolean hasNext;
        ArrayList<ColumnMetaData> tblCols = addPartitionColumnsToAvailableColumns();

        Iterator<PartitionColumnExpr> partitionedItr = this.selCols.iterator();
        hasNext = partitionedItr.hasNext();
        PartitionColumnExpr partitionedCol = null;
        while (hasNext) {
            partitionedCol = partitionedItr.next();
            if (!isAvailable(partitionedCol, tblCols)) {
                partitionedItr.remove();
            }
            hasNext = partitionedItr.hasNext();
        }

        repopulatePartitions(tblCols);
        rePopulateCols(tblSelCols, selCols);
        rePopulateCols(tblAvailCols, availCols);
    }

    /**
     * Adds the partition columns to available columns.
     *
     * @return the array list
     */
    private ArrayList<ColumnMetaData> addPartitionColumnsToAvailableColumns() {
        ArrayList<ColumnMetaData> tblCols = pTable.getColumnsList();
        Iterator<ColumnMetaData> tblColItr = tblCols.iterator();
        this.availCols = new ArrayList<PartitionColumnExpr>(4);
        boolean hasNext = tblColItr.hasNext();
        ColumnMetaData col = null;
        while (hasNext) {
            col = tblColItr.next();
            if (!isPartitioned(col)) {
                PartitionColumnExpr partitionColumn = new PartitionColumnExpr(PartitionColumnType.COLUMN);
                partitionColumn.setCol(col);

                this.availCols.add(partitionColumn);
            } else {
                addPartitionExprColumn(col);
            }
            hasNext = tblColItr.hasNext();
        }
        return tblCols;
    }

    /**
     * Adds the partition expr column.
     *
     * @param col the col
     */
    private void addPartitionExprColumn(ColumnMetaData col) {
        ColumnMetaData partitionCol = null;
        int len = this.selCols.size();
        PartitionColumnExpr partitionColExpr = null;
        for (int i = 0; i < len; i++) {
            partitionColExpr = selCols.get(i);
            if (partitionColExpr.validatePartitionColumnType()) {
                partitionColExpr = selCols.get(i);
                partitionCol = partitionColExpr.getCol();
                if (col.getName().equals(partitionCol.getName())) {
                    partitionColExpr.setCol(col);
                    break;
                }
            }
        }
    }

    /**
     * Repopulate partitions.
     *
     * @param tblCols the tbl cols
     */
    private void repopulatePartitions(ArrayList<ColumnMetaData> tblCols) {
        OLAPObjectList<PartitionMetaData> partitionedItr = pTable.getPartitions();
        int partSize = partitionedItr.getSize() - 1;
        ColumnMetaData clmMetaData = null;
        for (int i = partSize; i >= 0; i--) {
            clmMetaData = partitionedItr.getItem(i).getColumnMetadata();
            populateColumnData(tblCols, clmMetaData, i);
        }
    }

    /**
     * Populate column data.
     *
     * @param tblCols the tbl cols
     * @param clmMetaData the clm meta data
     * @param index the index
     */
    private void populateColumnData(ArrayList<ColumnMetaData> tblCols, ColumnMetaData clmMetaData, int index) {
        if (!tblCols.contains(clmMetaData)) {
            // Since we are allowing only one column as of now, I'm using
            // the array index as "0"(selCols.get(0)).
            if (selCols.size() > 0 && clmMetaData.equals(selCols.get(0).getCol())) {
                PartitionColumnExpr partitionColumn = null;

                this.availCols.clear();
                int tblColSize = tblCols.size();
                for (int j = 0; j < tblColSize; j++) {
                    partitionColumn = new PartitionColumnExpr(PartitionColumnType.COLUMN);
                    partitionColumn.setCol(tblCols.get(j));
                    this.availCols.add(partitionColumn);
                }
                selCols.clear();
            }
            pTable.removePartition(index);
            tblPartitions.remove(index);
        }
    }

    /**
     * Checks if is available.
     *
     * @param partitionedCol the partitioned col
     * @param tblCols the tbl cols
     * @return true, if is available
     */
    private boolean isAvailable(PartitionColumnExpr partitionedCol, ArrayList<ColumnMetaData> tblCols) {
        if (!partitionedCol.validatePartitionColumnType()) {
            return true;
        }

        Iterator<ColumnMetaData> tblColItr = tblCols.iterator();
        boolean hasNext = tblColItr.hasNext();
        ColumnMetaData tblCol = null;
        ColumnMetaData partCol = partitionedCol.getCol();
        while (hasNext) {
            tblCol = tblColItr.next();
            if (validatePartitionColumn(tblCol, partCol)) {
                return true;
            }
            hasNext = tblColItr.hasNext();
        }

        return false;
    }

    /**
     * Validate partition column.
     *
     * @param tblCol the tbl col
     * @param partCol the part col
     * @return true, if successful
     */
    private boolean validatePartitionColumn(ColumnMetaData tblCol, ColumnMetaData partCol) {
        return (tblCol.getOid() != 0 && tblCol.getOid() == partCol.getOid())
                || (tblCol.getName().equals(partCol.getName()));
    }

    /**
     * Checks if is partitioned.
     *
     * @param col the col
     * @return true, if is partitioned
     */
    private boolean isPartitioned(ColumnMetaData col) {
        Iterator<PartitionColumnExpr> itr = this.selCols.iterator();

        boolean hasNext = itr.hasNext();
        PartitionColumnExpr partitionColExpr = null;
        ColumnMetaData partitionCol = null;
        while (hasNext) {
            partitionColExpr = itr.next();
            if (!partitionColExpr.validatePartitionColumnType()) {
                hasNext = itr.hasNext();
                continue;
            }

            partitionCol = partitionColExpr.getCol();

            if (validatePartitionColumn(col, partitionCol)) {
                return true;
            }
            hasNext = itr.hasNext();
        }

        return false;
    }

    /**
     * Gets the partition meta data.
     *
     * @return the partition meta data
     * @throws DatabaseOperationException the database operation exception
     */
    private PartitionMetaData getPartitionMetaData() throws DatabaseOperationException {

        validateForPartitionColumn();
        validateForPartitionName();
        validateForPartitionValue();

        validateDuplicatePartitionName();
        validateIntervalPartitionExpr();

        partitionMetadata = new PartitionMetaData(txtPartitionName.getText());
        partitionMetadata.setParent(pTable);
        partitionMetadata.setPartitionType(partitionTypeCombo.getText());
        partitionMetadata.setPartitionName(txtPartitionName.getText());
        partitionMetadata.setIntervalPartitionExpr(txtIntervalPartitionExpr.getText());
        if (!PartitionTypeEnum.BY_HASH.getTypeName().equals(partitionTypeCombo.getText())) {
            partitionMetadata.setPartitionValue(txtPartitionValue.getText());
        }
        if (getSelectedTablespace() != null) {
            partitionMetadata.setTs(getSelectedTablespace());
        }

        return partitionMetadata;
    }

    /**
     * Validate interval partition expr.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateIntervalPartitionExpr() throws DatabaseOperationException {
        if (!PartitionTypeEnum.BY_INTERVAL.getTypeName().equals(partitionTypeCombo.getText())) {
            return;
        }
        if (txtIntervalPartitionExpr == null || txtIntervalPartitionExpr.getText().isEmpty()) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(
                    IMessagesConstants.ERR_PARTITION_INTERVAL_VALUE_EMPTY));
            throw new DatabaseOperationException(IMessagesConstants.ERR_PARTITION_INTERVAL_VALUE_EMPTY);
        }
    }

    /**
     * Validate duplicate partition name.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateDuplicatePartitionName() throws DatabaseOperationException {
        if (pTable != null) {
            pTable.validateForDuplicateName(txtPartitionName.getText().trim());
            pTable.setSelColumns(selCols);
        }
    }

    /**
     * Validate for partition column.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateForPartitionColumn() throws DatabaseOperationException {
        if (PartitionTypeEnum.BY_INTERVAL.getTypeName().equals(partitionTypeCombo.getText())) {
            if (selCols != null && selCols.size() == 1) {
                String typeName = selCols.get(0).getCol().getDisplayName();
                if (typeName.contains("time") || typeName.contains("date")) {
                    return;
                }
            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(
                        IMessagesConstants.ERR_PARTITION_INTERVAL_COLUMN_TYPE));
                throw new DatabaseOperationException(IMessagesConstants.ERR_PARTITION_INTERVAL_COLUMN_TYPE);
            }
        }
        if (selCols == null || selCols.size() < 1) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(
                    IMessagesConstants.ERR_PARTITION_COLUMN_EMPTY));
            throw new DatabaseOperationException(IMessagesConstants.ERR_PARTITION_COLUMN_EMPTY);
        }
    }

    /**
     * Validate for partition name.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateForPartitionName() throws DatabaseOperationException {
        if (txtPartitionName == null || txtPartitionName.getText().isEmpty()) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PARTITION_NAME_EMPTY));
            throw new DatabaseOperationException(IMessagesConstants.ERR_PARTITION_NAME_EMPTY);
        }

    }

    /**
     * Validate for partition value.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateForPartitionValue() throws DatabaseOperationException {
        if (PartitionTypeEnum.BY_HASH.getTypeName().equals(partitionTypeCombo.getText())) {
            return;
        }
        if (txtPartitionValue == null || txtPartitionValue.getText().isEmpty()) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PARTITION_VALUE_EMPTY));
            throw new DatabaseOperationException(IMessagesConstants.ERR_PARTITION_VALUE_EMPTY);
        }
    }

    /**
     * Move partition.
     *
     * @param up the up
     */
    private void movePartition(boolean up) {

        int selectedIndex = tblPartitions.getSelectionIndex();
        int targetIndex = up ? selectedIndex - 1 : selectedIndex + 1;

        if (validateSelectedPartition(selectedIndex, targetIndex)) {
            showError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_NO_PARTITION_MSG));
            return;
        }

        this.pTable.movePartition(selectedIndex, up);
        showError("");
        movePartitionTables(selectedIndex,
                pTable.getPartitions().getItem(selectedIndex).formCreatePartitionsQry(this.pTable.getSelColumns()));
        movePartitionTables(targetIndex,
                pTable.getPartitions().getItem(targetIndex).formCreatePartitionsQry(this.pTable.getSelColumns()));

        tblPartitions.setSelection(targetIndex);

    }

    /**
     * Validate selected partition.
     *
     * @param selectedIndex the selected index
     * @param targetIndex the target index
     * @return true, if successful
     */
    private boolean validateSelectedPartition(int selectedIndex, int targetIndex) {
        return selectedIndex == -1 || targetIndex < 0 || targetIndex >= tblPartitions.getItemCount();
    }

    /**
     * Move partition tables.
     *
     * @param index the index
     * @param partitions the partitions
     */
    private void movePartitionTables(int index, String partitions) {
        TableItem tableItem = null;
        tableItem = tblPartitions.getItem(index);
        tableItem.setText(partitions);
    }

    /**
     * Show error.
     *
     * @param msg the msg
     */
    private void showError(String msg) {
        if (null == msg || msg.trim().isEmpty()) {

            lblErrorMsg.setVisible(false);
        } else {
            lblErrorMsg.setForeground(FontAndColorUtility.getColor(SWT.COLOR_RED));
            lblErrorMsg.setVisible(true);
        }

        lblErrorMsg.setText(msg);
    }

    /**
     * Handle row column selection.
     *
     * @param orientationType the orientation type
     */
    public void handleRowColumnSelection(TableOrientation orientationType) {
        orientationselected.setText(orientationType.toString());
        partitionTypeCombo.select(0);
        enableDisableComponents(true);

        validatePartitionUI();
        partitionTypeBasedOnOrientation();
    }

    /**
     * Enable disable components.
     *
     * @param value the value
     */
    private void enableDisableComponents(boolean value) {
        Control[] partitionGrp = grpPartitions.getChildren();
        for (Control child : partitionGrp) {
            child.setEnabled(value);
        }
        if (!value) {
            for (int i = 0; i < tblPartitions.getItemCount(); i++) {
                tblPartitions.removeAll();
            }
        }
    }

    /**
     * Validate partition UI.
     */
    private void validatePartitionUI() {
        txtPartitionName.setEnabled(true);
        txtPartitionValue.setEnabled(true);
        btnPartitionValue.setEnabled(true);
        cmbTablespace.setEnabled(true);
    }

    /**
     * Sets the tabfolder.
     *
     * @param tabFoldr the new tabfolder
     */
    public void setTabfolder(TabFolder tabFoldr) {
        this.tabFolder = tabFoldr;

    }

    /**
     * Sets the part tab instance.
     *
     * @param createPartitionTble the new part tab instance
     */
    public void setPartTabInstance(CreatePartitionTable createPartitionTble) {
        this.createPartitionTable = createPartitionTble;

    }

    /**
     * Gets the tbl partitions.
     *
     * @return the tbl partitions
     */
    public Table getTblPartitions() {
        return tblPartitions;
    }

    /**
     * Sets the tbl partitions.
     *
     * @param tblPartitions the new tbl partitions
     */
    public void setTblPartitions(Table tblPartitions) {
        this.tblPartitions = tblPartitions;
    }

    /**
     * Gets the txt partition name.
     *
     * @return the txt partition name
     */
    public Text getTxtPartitionName() {
        return txtPartitionName;
    }

    /**
     * Sets the txt partition name.
     *
     * @param txtPartitionName the new txt partition name
     */
    public void setTxtPartitionName(Text txtPartitionName) {
        this.txtPartitionName = txtPartitionName;
    }

    /**
     * Gets the txt partition value.
     *
     * @return the txt partition value
     */
    public Text getTxtPartitionValue() {
        return txtPartitionValue;
    }

    /**
     * Sets the txt partition value.
     *
     * @param txtPartitionValue the new txt partition value
     */
    public void setTxtPartitionValue(Text txtPartitionValue) {
        this.txtPartitionValue = txtPartitionValue;
    }

    /**
     * Gets the sel cols.
     *
     * @return the sel cols
     */
    public List<PartitionColumnExpr> getSelCols() {
        return selCols;
    }

    /**
     * Sets the sel cols.
     *
     * @param selCols the new sel cols
     */
    public void setSelCols(List<PartitionColumnExpr> selCols) {
        this.selCols = selCols;
    }

    /**
     * Clear partition list from query.
     */
    public void clearPartitionListFromQuery() {
        pTable.getPartitions().clear();
    }

    /**
     * Removes the partition table entries.
     *
     * @param partitionTbl the partition tbl
     */
    public void removePartitionTableEntries(Table partitionTbl) {
        int itemCount = partitionTbl.getItemCount();
        if (itemCount > 0) {
            partitionTbl.removeAll();
        }

    }

    /**
     * Checks if is partition col to be removed.
     *
     * @return true, if is partition col to be removed
     */
    private boolean isPartitionColToBeRemoved() {
        int userChoice = UIConstants.CANCEL_ID;
        if (pTable.getPartitions().getSize() > 0) {
            userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TABLE_COL_REMOVE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_TABLE_COL_REMOVE_BODY),
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                        MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                    1);

            if (userChoice == UIConstants.OK_ID) {
                return true;
            }
        }

        return false;

    }

    /**
     * Removes the ALL.
     */
    public void removeALL() {
        if (pTable != null && tblPartitions != null && getSelCols() != null) {
            pTable.removeAllPartition();
            tblPartitions.removeAll();
            getSelCols().clear();

        }
    }

    /**
     * Modify columns clear.
     */
    public void modifyColumnsClear() {
        removePartitionTableEntries(tblPartitions);
        pTable.removeAllPartition();
        partitionValueMap.clear();
        editPartitionValueMap.clear();
        txtPartitionName.setText("");
        txtPartitionValue.setText("");
        cmbTablespace.select(0);
    }
}
