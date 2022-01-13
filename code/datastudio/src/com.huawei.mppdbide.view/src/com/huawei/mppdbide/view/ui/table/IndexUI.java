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

package com.huawei.mppdbide.view.ui.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.AccessMethod;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IndexedColumnComparator;
import com.huawei.mppdbide.bl.serverdatacache.IndexedColumnExpr;
import com.huawei.mppdbide.bl.serverdatacache.IndexedColumnType;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableOrientation;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.UIMandatoryAttribute;
import com.huawei.mppdbide.view.utils.UIVerifier;
import com.huawei.mppdbide.view.utils.consts.TOOLTIPS;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexUI.
 *
 * @since 3.0.0
 */
public class IndexUI {

    /**
     * The txt index name.
     */
    protected Text txtIndexName;

    /**
     * The btn unique index.
     */
    protected Button btnUniqueIndex;

    /**
     * The cmb index type.
     */
    protected Combo cmbIndexType;

    /**
     * The cmb access method.
     */
    protected Combo cmbAccessMethod;

    /**
     * The cmb tablespace.
     */
    protected Combo cmbTablespace;

    /**
     * The txt user expr.
     */
    protected StyledText txtUserExpr;

    /**
     * The tbl avail cols.
     */
    protected Table tblAvailCols;

    /**
     * The tbl index cols.
     */
    protected Table tblIndexCols;

    /**
     * The fill factor.
     */
    protected Spinner fillFactor;

    /**
     * The txt where expr.
     */
    protected StyledText txtWhereExpr;

    /**
     * The server.
     */
    protected Server server;

    /**
     * The table meta data.
     */
    protected TableMetaData tableMetaData;

    /* Data caches */
    private ArrayList<IndexedColumnExpr> availCols;
    private ArrayList<IndexedColumnExpr> indexCols;
    private ArrayList<Long> amOids;
    private ArrayList<Long> tablespaceOids;
    private Comparator<IndexedColumnExpr> comparator;
    private IndexMetaData index;

    /**
     * The lbl partial index where.
     */
    protected Label lblPartialIndexWhere;

    private Table indexExprTable;

    /**
     * The decofk.
     */
    protected ControlDecoration decofk;

    private Label lblor;
    private Label lblUserDefinedExpression;

    /**
     * Sets the UI labels color gray.
     */
    public void setUILabelsColorGray() {
        lblor.setForeground(FontAndColorUtility.getColor(SWT.COLOR_DARK_GRAY));
        lblUserDefinedExpression.setForeground(FontAndColorUtility.getColor(SWT.COLOR_DARK_GRAY));
        // add other labels if needed
    }

    /**
     * Sets the UI labels color black.
     */
    public void setUILabelsColorBlack() {
        lblor.setForeground(FontAndColorUtility.getColor(SWT.COLOR_BLACK));
        lblUserDefinedExpression.setForeground(FontAndColorUtility.getColor(SWT.COLOR_BLACK));
        // add other labels if needed
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
     * Gets the txt user expr.
     *
     * @return the txt user expr
     */
    public StyledText getTxtUserExpr() {
        return txtUserExpr;
    }

    /**
     * Gets the fill factor.
     *
     * @return the fill factor
     */
    public Spinner getFillFactor() {
        return fillFactor;
    }

    /**
     * Gets the txt where expr.
     *
     * @return the txt where expr
     */
    public StyledText getTxtWhereExpr() {
        return txtWhereExpr;
    }

    /**
     * Gets the btn unique index.
     *
     * @return the btn unique index
     */
    public Button getBtnUniqueIndex() {
        return btnUniqueIndex;
    }

    /**
     * Instantiates a new index UI.
     *
     * @param tbl the tbl
     * @param server the server
     */
    public IndexUI(TableMetaData tbl, Server server) {
        this.server = server;
        this.tableMetaData = tbl;
        amOids = new ArrayList<Long>(4);
        tablespaceOids = new ArrayList<Long>(4);

        availCols = new ArrayList<IndexedColumnExpr>(4);
        indexCols = new ArrayList<IndexedColumnExpr>(4);
        comparator = new IndexedColumnComparator();
    }

    /**
     * Creates the UI.
     *
     * @param comp the comp
     */
    public void createUI(Composite comp) {
        Composite indexUiComposite = new Composite(comp, SWT.NONE);
        indexUiComposite.setLayout(new GridLayout(4, false));
        GridData indexUiCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        indexUiCompositeGD.horizontalSpan = 4;
        indexUiComposite.setLayoutData(indexUiCompositeGD);

        createUpperComposite(indexUiComposite);
        createColumnOrExpr(indexUiComposite);
        createWhereClause(indexUiComposite);
    }

    private void createUpperComposite(Composite comp) {
        Composite upperComposite = new Composite(comp, SWT.NONE);
        upperComposite.setLayout(new GridLayout(5, false));
        GridData upperCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        upperComposite.setLayoutData(upperCompositeGD);

        createIndexComposite(upperComposite);
        createIndexType(upperComposite);
        createAMCombo(upperComposite);
        createTablespace(upperComposite);
        createFillFactor(upperComposite);
    }

    private void createIndexComposite(Composite comp) {
        Composite indexComp = new Composite(comp, SWT.NONE);
        indexComp.setLayout(new GridLayout(2, false));
        GridData indexCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        indexComp.setLayoutData(indexCompGD);

        createIndexLabel(indexComp);

        createUniqueIndexButton(indexComp);

        createIndexText(indexComp);
    }

    private void createUniqueIndexButton(Composite comp) {
        btnUniqueIndex = new Button(comp, SWT.CHECK);
        GridData btnUniqueIndexGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        btnUniqueIndexGD.verticalAlignment = SWT.TOP;
        btnUniqueIndexGD.horizontalAlignment = SWT.RIGHT;
        btnUniqueIndex.setLayoutData(btnUniqueIndexGD);
        btnUniqueIndex.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_CHK_BTN_INDEXUI_UNIQUEINDEX_001");
        btnUniqueIndex.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_UNIQUE_INDEX));
    }

    private void createIndexText(Composite comp) {
        txtIndexName = new Text(comp, SWT.BORDER);
        txtIndexName.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_INDEXUI_INDEXNAME_001");
        GridData txtIndexNameGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtIndexNameGD.horizontalSpan = 2;
        txtIndexNameGD.widthHint = 180;
        txtIndexNameGD.verticalIndent = -5;
        txtIndexName.setLayoutData(txtIndexNameGD);

        UIVerifier.verifyTextSize(txtIndexName, 63);
        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        UIMandatoryAttribute.mandatoryField(txtIndexName, image, TOOLTIPS.INDEXNAME_TOOLTIPS);
    }

    private void createIndexLabel(Composite comp) {
        Label lblIndexName = new Label(comp, SWT.NONE);
        lblIndexName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, comp));
        GridData lblIndexNameGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblIndexName.setLayoutData(lblIndexNameGD);
        lblIndexName.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_NAME));
        lblIndexName.pack();
    }

    /**
     * Gets the whr expr.
     *
     * @return the whr expr
     */
    public String getWhrExpr() {
        return txtWhereExpr.getText();
    }

    /**
     * Creates the fill factor.
     *
     * @param comp the comp
     */
    private void createFillFactor(Composite comp) {

        Composite fillFactorComp = new Composite(comp, SWT.NONE);
        fillFactorComp.setLayout(new GridLayout(1, false));
        GridData fillFactorCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        fillFactorComp.setLayoutData(fillFactorCompGD);

        Label lblFillfactor = new Label(fillFactorComp, SWT.NONE);
        lblFillfactor.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_FILLFACTOR));
        lblFillfactor.pack();

        fillFactor = new Spinner(fillFactorComp, SWT.BORDER);
        GridData fillFactorGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        fillFactor.setLayoutData(fillFactorGD);
        fillFactor.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_SPINNER_INDEXUI_FILLFACTOR_001");

        fillFactor.setMinimum(0);
        fillFactor.setMaximum(100);
        fillFactor.setSelection(100);
        fillFactor.addListener(SWT.Verify, new Listener() {
            @Override
            public void handleEvent(Event event) {
                String initialValue = ((Spinner) event.widget).getText();

                final String finalValue = initialValue.substring(0, event.start) + event.text
                        + initialValue.substring(event.end);
                try {
                    if (!finalValue.isEmpty()) {
                        if (Integer.parseInt(finalValue) > 100) {
                            event.doit = false;
                        }
                    }

                } catch (NumberFormatException ex) {
                    event.doit = false;
                }

            }
        });
    }

    /**
     * Creates the where clause.
     *
     * @param comp the comp
     */
    protected void createWhereClause(Composite comp) {
        Composite whereClauseComposite = new Composite(comp, SWT.NONE);
        whereClauseComposite.setLayout(new GridLayout(1, false));
        GridData whereClauseCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        whereClauseCompositeGD.horizontalSpan = 4;
        whereClauseComposite.setLayoutData(whereClauseCompositeGD);

        lblPartialIndexWhere = new Label(whereClauseComposite, SWT.NONE);
        lblPartialIndexWhere.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_PARTIAL_INDEX));
        lblPartialIndexWhere.pack();

        txtWhereExpr = new StyledText(whereClauseComposite, SWT.BORDER);
        txtWhereExpr.setLayout(new GridLayout(1, false));
        GridData txtWhereExprGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtWhereExprGD.heightHint = 20;
        txtWhereExpr.setLayoutData(txtWhereExprGD);
        txtWhereExpr.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_INDEXUI_WHEREEXPR_001");
        lblPartialIndexWhere.setEnabled(true);
        txtWhereExpr.setEnabled(true);

    }

    /**
     * Creates the column or expr.
     *
     * @param comp the comp
     */
    private void createColumnOrExpr(Composite comp) {
        Group group = new Group(comp, SWT.NONE);
        group.setLayout(new GridLayout(4, false));
        GridData groupGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        groupGD.horizontalSpan = 4;
        group.setLayoutData(groupGD);

        createUserDefOrAvailColsComposite(group);
        createAddRemoveIndexComposite(group);
        createIndexColTable(group);
        createUpDownIndexComposite(group);
    }

    private void createUpDownIndexComposite(Group group) {
        Composite upDownIndexComposite = new Composite(group, SWT.NONE);
        upDownIndexComposite.setLayout(new GridLayout(1, false));
        GridData upDownIndexCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        upDownIndexCompositeGD.verticalAlignment = SWT.CENTER;
        upDownIndexComposite.setLayoutData(upDownIndexCompositeGD);

        createIndexColMoveUp(upDownIndexComposite);
        createIndexColMoveDown(upDownIndexComposite);
    }

    private void createAddRemoveIndexComposite(Group group) {
        Composite addRemoveIndexComposite = new Composite(group, SWT.NONE);
        addRemoveIndexComposite.setLayout(new GridLayout(1, false));
        GridData addRemoveIndexCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        addRemoveIndexCompositeGD.verticalAlignment = SWT.END;
        addRemoveIndexComposite.setLayoutData(addRemoveIndexCompositeGD);

        createAddToIndex(addRemoveIndexComposite);
        createRemoveFromIndex(addRemoveIndexComposite);
    }

    private void createUserDefOrAvailColsComposite(Group group) {
        Composite avalColsOrUserExpComp = new Composite(group, SWT.NONE);
        avalColsOrUserExpComp.setLayout(new GridLayout(1, false));
        GridData avalColsOrUserExpCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        avalColsOrUserExpComp.setLayoutData(avalColsOrUserExpCompGD);

        Composite userExprComposite = new Composite(avalColsOrUserExpComp, SWT.NONE);
        userExprComposite.setLayout(new GridLayout(1, false));
        GridData userExprCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        userExprComposite.setLayoutData(userExprCompositeGD);

        lblUserDefinedExpression = new Label(userExprComposite, SWT.NONE);
        lblUserDefinedExpression.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_USER_EXPRESSION));
        lblUserDefinedExpression.pack();

        txtUserExpr = new StyledText(userExprComposite, SWT.BORDER);
        txtUserExpr.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_INDEXUI_USEREXPR_001");
        GridData txtUserExprGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        txtUserExprGD.heightHint = 20;
        txtUserExpr.setLayoutData(txtUserExprGD);

        lblor = new Label(avalColsOrUserExpComp, SWT.NONE);
        lblor.setFont(FontAndColorUtility.getFont("Segoe UI", 9, SWT.ITALIC, avalColsOrUserExpComp));
        lblor.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_OR));
        lblor.pack();

        createAvailableCols(avalColsOrUserExpComp);
    }

    /**
     * Creates the available cols.
     *
     * @param comp the comp
     */
    private void createAvailableCols(Composite comp) {
        tblAvailCols = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION);
        tblAvailCols.setLayout(new GridLayout(1, false));
        GridData tblAvailColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblAvailColsGD.heightHint = 100;
        tblAvailCols.setLayoutData(tblAvailColsGD);

        tblAvailCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_INDEXUI_AVL_COLS_001");
        tblAvailCols.setHeaderVisible(true);
        tblAvailCols.setLinesVisible(true);

        TableColumn tblclmnAvailableColumns = new TableColumn(tblAvailCols, SWT.NONE);
        tblclmnAvailableColumns.setWidth(185);
        tblclmnAvailableColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_INDEXUI_AVL_COLS_001");
        tblclmnAvailableColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_AVA_COLUMNS));
        updateAvailableCols();
    }

    /**
     * Update available cols.
     */
    public void updateAvailableCols() {
        Iterator<ColumnMetaData> colItr = this.tableMetaData.getColumns().getList().iterator();
        boolean hasNext = colItr.hasNext();
        ColumnMetaData col = null;
        IndexedColumnExpr indexColumn = null;
        TableItem item = null;

        this.availCols = new ArrayList<IndexedColumnExpr>(4);
        while (hasNext) {
            col = colItr.next();
            indexColumn = new IndexedColumnExpr(IndexedColumnType.COLUMN);
            indexColumn.setCol(col);

            this.availCols.add(indexColumn);
            item = new TableItem(tblAvailCols, SWT.NONE);
            item.setText(col.getName());

            hasNext = colItr.hasNext();
        }
    }

    /**
     * Creates the index col table.
     *
     * @param comp the comp
     */
    private void createIndexColTable(Composite comp) {
        tblIndexCols = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION);
        tblIndexCols.setLayout(new GridLayout(1, false));
        GridData tblIndexColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblIndexColsGD.horizontalIndent = 5;
        tblIndexCols.setLayoutData(tblIndexColsGD);

        tblIndexCols.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_INDEXUI_INDEX_COLS_001");
        tblIndexCols.setLinesVisible(true);
        tblIndexCols.setHeaderVisible(true);

        decofk = new ControlDecoration(tblIndexCols, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        // set description and image
        decofk.setDescriptionText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MSG));
        decofk.setImage(image);

        TableColumn tblclmnIndexColumns = new TableColumn(tblIndexCols, SWT.NONE);
        tblclmnIndexColumns.setWidth(185);
        tblclmnIndexColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_INDEXUI_INDEX_COLS_001");
        tblclmnIndexColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_INDEX_CLMS));

    }

    /**
     * Creates the add to index.Add/Remove/MoveUp/MoveDown Buttons
     *
     * @param comp the comp
     */
    private void createAddToIndex(Composite comp) {
        Button addToIndex = new Button(comp, SWT.ARROW | SWT.RIGHT);
        GridData addToIndexGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        addToIndexGD.heightHint = 30;
        addToIndex.setLayoutData(addToIndexGD);
        addToIndex.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_ADD_TO_INDEX_001");
        addToIndex.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_ADD_TO));
        addToIndex.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String userExpr = txtUserExpr.getText().trim();
                if (!"".equals(userExpr)) {
                    IndexedColumnExpr expr = new IndexedColumnExpr(IndexedColumnType.EXPRESSION);
                    expr.setExpr(userExpr);
                    indexCols.add(expr);
                    repopulateIndexedCols();
                    txtUserExpr.setText("");
                } else {
                    int selectedIdx = tblAvailCols.getSelectionIndex();
                    if (selectedIdx > -1) {
                        IndexedColumnExpr col = availCols.get(selectedIdx);
                        availCols.remove(selectedIdx);
                        tblAvailCols.remove(selectedIdx);
                        indexCols.add(col);
                        repopulateIndexedCols();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }

    /**
     * Creates the remove from index.
     *
     * @param comp the comp
     */
    private void createRemoveFromIndex(Composite comp) {
        Button removeFromIndex = new Button(comp, SWT.ARROW | SWT.LEFT);
        GridData removeFromIndexGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        removeFromIndexGD.heightHint = 30;
        removeFromIndex.setLayoutData(removeFromIndexGD);
        removeFromIndex.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_REMOVE_FROM_INDEX_001");
        removeFromIndex.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_REMOVE));
        removeFromIndex.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selectedIdx = tblIndexCols.getSelectionIndex();
                if (selectedIdx > -1) {
                    IndexedColumnExpr col = indexCols.get(selectedIdx);
                    indexCols.remove(selectedIdx);
                    tblIndexCols.remove(selectedIdx);

                    if (IndexedColumnType.EXPRESSION == col.getType()) {
                        txtUserExpr.setText(col.toString());
                    } else {
                        int insertionPoint = Collections.binarySearch(availCols, col, comparator);
                        if (insertionPoint < 0) {
                            insertionPoint = -(insertionPoint + 1);
                        }
                        availCols.add(insertionPoint, col);
                        rePopulateAvailCols();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent eevent) {
                // Nothing to do.
            }
        });
    }

    /**
     * Creates the index col move up.
     *
     * @param comp the comp
     */
    private void createIndexColMoveUp(Composite comp) {
        Button moveUp = new Button(comp, SWT.ARROW | SWT.UP);
        GridData moveUpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        moveUpGD.heightHint = 30;
        moveUp.setLayoutData(moveUpGD);
        moveUp.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_MOVE_UP_INDEX_001");
        moveUp.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MOVE_UP));
        moveUp.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selectedIdx = tblIndexCols.getSelectionIndex();
                if (selectedIdx > 0) {
                    IndexedColumnExpr col = indexCols.get(selectedIdx);
                    indexCols.remove(selectedIdx);
                    indexCols.add(selectedIdx - 1, col);
                    repopulateIndexedCols();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }

    /**
     * Creates the index col move down.
     *
     * @param comp the comp
     */
    private void createIndexColMoveDown(Composite comp) {
        Button moveDown = new Button(comp, SWT.ARROW | SWT.DOWN);
        GridData moveDownGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        moveDownGD.heightHint = 30;
        moveDown.setLayoutData(moveDownGD);
        moveDown.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_MOVE_DOWN_INDEX_001");
        moveDown.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MOVE_DOWN));
        moveDown.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selectedIdx = tblIndexCols.getSelectionIndex();
                if (selectedIdx > -1 && selectedIdx < (tblIndexCols.getItemCount() - 1)) {
                    IndexedColumnExpr col = indexCols.get(selectedIdx);
                    indexCols.remove(selectedIdx);
                    indexCols.add(selectedIdx + 1, col);
                    repopulateIndexedCols();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do

            }
        });
    }

    /**
     * Repopulate indexed cols.
     */
    public void repopulateIndexedCols() {
        TableItem item = null;
        tblIndexCols.removeAll();
        Iterator<IndexedColumnExpr> colsItr = this.indexCols.iterator();
        boolean hasNext = colsItr.hasNext();
        IndexedColumnExpr col = null;

        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(tblIndexCols, SWT.NONE);
            item.setText(col.toString());
            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Re populate avail cols.
     */
    public void rePopulateAvailCols() {
        TableItem item = null;
        tblAvailCols.removeAll();
        Iterator<IndexedColumnExpr> colsItr = this.availCols.iterator();
        boolean hasNext = colsItr.hasNext();
        IndexedColumnExpr col = null;

        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(tblAvailCols, SWT.NONE);
            item.setText(col.toString());
            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Refresh columns.
     */
    public void refreshColumns() {
        ArrayList<ColumnMetaData> tblCols = tableMetaData.getColumns().getList();
        Iterator<ColumnMetaData> tblColItr = tblCols.iterator();
        this.availCols = new ArrayList<IndexedColumnExpr>(4);
        boolean hasNext = tblColItr.hasNext();
        ColumnMetaData col = null;
        while (hasNext) {
            col = tblColItr.next();
            if (!isIndexed(col)) {
                IndexedColumnExpr indexColumn = new IndexedColumnExpr(IndexedColumnType.COLUMN);
                indexColumn.setCol(col);

                this.availCols.add(indexColumn);
            }
            hasNext = tblColItr.hasNext();
        }

        Iterator<IndexedColumnExpr> indexeditr = this.indexCols.iterator();
        hasNext = indexeditr.hasNext();
        IndexedColumnExpr indexedCol = null;
        while (hasNext) {
            indexedCol = indexeditr.next();
            if (!isAvailable(indexedCol, tblCols)) {
                indexeditr.remove();
            }
            hasNext = indexeditr.hasNext();
        }

        repopulateIndexedCols();
        rePopulateAvailCols();
    }

    /**
     * Checks if is available.
     *
     * @param indexedCol the indexed col
     * @param tblCols the tbl cols
     * @return true, if is available
     */
    public boolean isAvailable(IndexedColumnExpr indexedCol, ArrayList<ColumnMetaData> tblCols) {
        if (IndexedColumnType.COLUMN != indexedCol.getType()) {
            return true;
        }

        Iterator<ColumnMetaData> tblColItr = tblCols.iterator();
        boolean hasNext = tblColItr.hasNext();
        ColumnMetaData tblCol = null;
        while (hasNext) {
            tblCol = tblColItr.next();
            if ((tblCol.getOid() != 0 && tblCol.getOid() == indexedCol.getCol().getOid())
                    || (tblCol.getName().equals(indexedCol.getCol().getName()))) {
                return true;
            }
            hasNext = tblColItr.hasNext();
        }

        return false;
    }

    /**
     * Checks if is indexed.
     *
     * @param col the col
     * @return true, if is indexed
     */
    public boolean isIndexed(ColumnMetaData col) {
        Iterator<IndexedColumnExpr> itr = this.indexCols.iterator();

        boolean hasNext = itr.hasNext();
        IndexedColumnExpr indexColExpr = null;
        ColumnMetaData indexCol = null;
        while (hasNext) {
            indexColExpr = itr.next();
            if (IndexedColumnType.COLUMN != indexColExpr.getType()) {
                hasNext = itr.hasNext();
                continue;
            }

            indexCol = indexColExpr.getCol();

            if ((col.getOid() != 0 && col.getOid() == indexCol.getOid())
                    || (col.getName().equals(indexCol.getName()))) {
                return true;
            }
            hasNext = itr.hasNext();
        }

        return false;
    }

    /**
     * Creates the index type method
     *
     * @param comp the comp
     */
    protected void createIndexType(Composite comp) {
    }

    /**
     * Creates the AM combo.Access Method
     *
     * @param comp the comp
     */
    private void createAMCombo(Composite comp) {

        Composite accessMethodComposite = new Composite(comp, SWT.NONE);
        accessMethodComposite.setLayout(new GridLayout(1, false));
        GridData accessMethodCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        accessMethodComposite.setLayoutData(accessMethodCompositeGD);

        Label lblAccessMethod = new Label(accessMethodComposite, SWT.NONE);
        lblAccessMethod.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_ACCESS_METHOD));
        lblAccessMethod.pack();

        cmbAccessMethod = new Combo(accessMethodComposite, SWT.READ_ONLY);
        cmbAccessMethod.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_INDEXUI_ACCESS_METHOD_001");
        cmbAccessMethod.addModifyListener(new AccessMethodModifyListener());
        GridData cmbAccessMethodGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbAccessMethod.setLayoutData(cmbAccessMethodGD);
        updateAMCombo();
    }
    
    /**
     * The listener interface for receiving AccessMethodModify events. The class
     * that is interested in processing a AccessMethodModify event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>AccessMethodModifyListener<code> method. When the
     * cmbAccessMethod event occurs, that object's appropriate method is
     * invoked.
     *
     * AccessMethodModifyEvent
     */
    private class AccessMethodModifyListener implements ModifyListener {
        /**
         * Modify text.
         *
         * @param e the e
         */
        public void modifyText(ModifyEvent event) {
            enableDisableFillFactor();
        }

    }

    /**
     * Enable disable size len.
     */
    public void enableDisableFillFactor() {
        if (this.fillFactor != null) {        
            // if access method is "gin" or "psort" disable fill factor
            if (cmbAccessMethod.getSelectionIndex() == 3 || cmbAccessMethod.getSelectionIndex() == 6) {
                this.fillFactor.setEnabled(false);
            } else {
                this.fillFactor.setEnabled(true);
            }
        }
    }


    /**
     * Update AM combo.
     */
    public void updateAMCombo() {
        Iterator<AccessMethod> amItr = this.server.getAccessMethods().iterator();
        boolean hasNext = amItr.hasNext();
        AccessMethod am = null;
        cmbAccessMethod.removeAll();
        while (hasNext) {
            am = amItr.next();
            cmbAccessMethod.add(am.getName());
            amOids.add(am.getOid());
            hasNext = amItr.hasNext();
        }
    }

    /**
     * Update AM combo for column-stored and row-stored table.
     *
     * @param boolean, true if isColumnTable
     */
    public void updateBtreeAMCombo(boolean isColumnTable) {
        Iterator<AccessMethod> amItr = this.server.getAccessMethods().iterator();
        boolean hasNext = amItr.hasNext();
        AccessMethod am = null;
        cmbAccessMethod.removeAll();
        while (hasNext) {
            am = amItr.next();
            if (am.getName().contains("btree")) {
                if (isColumnTable || am.getName().equals("btree")) {
                    cmbAccessMethod.add(am.getName());
                    amOids.add(am.getOid());
                }
            }
            hasNext = amItr.hasNext();
        }
    }

    /**
     * Creates the tablespace.Tablespace
     *
     * @param comp the comp
     */
    private void createTablespace(Composite comp) {

        Composite tablespaceComposite = new Composite(comp, SWT.NONE);
        tablespaceComposite.setLayout(new GridLayout(1, false));
        GridData tablespaceCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tablespaceComposite.setLayoutData(tablespaceCompositeGD);

        Label lblTablespace = new Label(tablespaceComposite, SWT.NONE);
        lblTablespace.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_TABLESPACE));
        lblTablespace.pack();

        cmbTablespace = new Combo(tablespaceComposite, SWT.READ_ONLY);
        cmbTablespace.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_INDEXUI_TBLSPACE_001");
        GridData cmbTablespaceGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbTablespace.setLayoutData(cmbTablespaceGD);
        updateTablespaceObject();
    }

    /**
     * Update index type object.
     */
    public void updateIndexTypeObject() {
        if (cmbIndexType == null) {
            return;
        }
        cmbIndexType.removeAll();
        cmbIndexType.add("GLOBAL");
        cmbIndexType.add("LOCAL");
    }

    /**
     * Update index type object for column-stored table.
     */
    public void updateColumnIndexTypeObject() {
        if (cmbIndexType == null) {
            return;
        }
        cmbIndexType.removeAll();
        cmbIndexType.add("LOCAL");
        cmbIndexType.select(0);
    }

    /**
     * Update tablespace object.
     */
    public void updateTablespaceObject() {
        Iterator<Tablespace> tableSpaceItr = this.server.getTablespaceGroup().getSortedServerObjectList().iterator();
        boolean hasNext = tableSpaceItr.hasNext();
        Tablespace tablespace = null;
        cmbTablespace.removeAll();
        while (hasNext) {
            tablespace = tableSpaceItr.next();
            cmbTablespace.add(tablespace.getName());
            tablespaceOids.add(tablespace.getOid());
            hasNext = tableSpaceItr.hasNext();
        }
    }

    /**
     * Gets the selected index type.
     *
     * @return the selected index type
     */
    private String getSelectedIndexType() {
        if (cmbIndexType == null) {
            return "";
        } 
        int indx = cmbIndexType.getSelectionIndex();
        String [] indexTypeStrings = {"GLOBAL", "LOCAL"};
        if (indx >= 0 && indx <= 1) {
            return indexTypeStrings[indx];
        }
        return "";
    }

    /**
     * Gets the selected access method.
     *
     * @return the selected access method
     */
    private AccessMethod getSelectedAccessMethod() {
        int indx = cmbAccessMethod.getSelectionIndex();
        if (indx < 0) {
            return null;
        }

        long amOid = amOids.get(indx);
        if (this.tableMetaData != null) {
            return this.server.getAccessMethod(amOid);
        }
        return null;
    }

    /**
     * Gets the selected tablespace.
     *
     * @return the selected tablespace
     */
    private Tablespace getSelectedTablespace() {
        int indx = cmbTablespace.getSelectionIndex();
        if (indx < 0) {
            return null;
        }

        long tsOid = tablespaceOids.get(indx);
        if (this.tableMetaData != null) {
            return this.server.getTablespaceGroup().getObjectById(tsOid);
        }
        return null;
    }

    /**
     * Gets the query.
     *
     * @return the query
     * @throws DatabaseOperationException the database operation exception
     */
    public String getQuery() throws DatabaseOperationException {
        IndexMetaData idx = getIndexMetaData();

        return idx.formCreateQuery(false);
    }

    /**
     * Gets the index meta data.
     *
     * @return the index meta data
     * @throws DatabaseOperationException the database operation exception
     */
    public IndexMetaData getIndexMetaData() throws DatabaseOperationException {

        // throws exception when the index name is empty and no index columns
        validateIndexNameAndCols();
        if (tableMetaData != null) {
            List<IndexMetaData> metaDatas = tableMetaData.getIndexArrayList();

            int size = metaDatas.size();
            IndexMetaData data = null;
            for (int idx = 0; idx < size; idx++) {
                data = metaDatas.get(idx);
                if (isDuplicateIndexName(data)) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DUPLICATE_INDEX_NAME));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_DUPLICATE_INDEX_NAME,
                            txtIndexName.getText().trim());
                }

            }
        }

        String userInput = fillFactor.getText();

        int fillFactorValue = Integer.parseInt(userInput);
        if (txtIndexName != null) {
            if (null == index) {
                index = new IndexMetaData(txtIndexName.getText().trim());
            } else {
                index.setName(txtIndexName.getText().trim());
            }
        } else if (null == index) {
            throw new DatabaseOperationException(IMessagesConstants.ERR_INDEX_NAME_EMPTY);
        }
        setIndexTableAndNamespace(fillFactorValue);

        setUniqueIndexSelection();
        index.setAccessMethod(getSelectedAccessMethod());
        index.setTablespace(getSelectedTablespace());
        index.setIndexedColumns(indexCols);
        index.setWhereExpr(txtWhereExpr.getText().trim());

        String indexType = getSelectedIndexType();
        if(indexType != null)
            index.setIndexType(indexType);
        return index;
    }

    /**
     * Sets the index table and namespace.
     *
     * @param fillFactorValue the new index table and namespace
     */
    private void setIndexTableAndNamespace(int fillFactorValue) {
        if (tableMetaData != null) {
            if (tableMetaData.getOrientation() == TableOrientation.ROW) {
                index.setIndexFillFactor(fillFactorValue);
            }

            index.setTable(tableMetaData);
            index.setNamespace(tableMetaData.getNamespace());
        }
    }

    /**
     * Sets the unique index selection.
     */
    private void setUniqueIndexSelection() {
        if (btnUniqueIndex != null) {
            index.setUnique(btnUniqueIndex.getSelection());
        }
    }

    /**
     * Checks if is duplicate index name.
     *
     * @param data the data
     * @return true, if is duplicate index name
     */
    private boolean isDuplicateIndexName(IndexMetaData data) {
        return txtIndexName != null && data.getName().trim().equalsIgnoreCase(txtIndexName.getText().trim());
    }

    /**
     * Validate index name and cols.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void validateIndexNameAndCols() throws DatabaseOperationException {
        if (null != txtIndexName && "".equals(txtIndexName.getText().trim())) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_INDEX_NAME_EMPTY));
            throw new DatabaseOperationException(IMessagesConstants.ERR_INDEX_NAME_EMPTY);
        } else if (null == indexCols || indexCols.size() < 1) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_INDEX_COLS_EMPTY));
            throw new DatabaseOperationException(IMessagesConstants.ERR_INDEX_COLS_EMPTY);
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        index = null;
        if (txtIndexName != null) {
            txtIndexName.setText("");
        }
        txtUserExpr.setText("");
        txtWhereExpr.setText("");
        fillFactor.setSelection(100);
        if (btnUniqueIndex != null) {
            btnUniqueIndex.setSelection(false);
        }

        updateAMCombo();
        updateTablespaceObject();
        updateAvailableCols();
        updateIndexTypeObject();
        this.indexCols = new ArrayList<IndexedColumnExpr>(4);

        rePopulateAvailCols();
        repopulateIndexedCols();
    }

    /**
     * Sets the index object.
     *
     * @param idx the new index object
     */
    public void setIndexObject(IndexMetaData idx) {
        this.index = idx;
        if (txtIndexName != null) {
            txtIndexName.setText(idx.getName());
        }
        txtUserExpr.setText("");
        txtWhereExpr.setText(idx.getWhereExpr());

        fillFactor.setSelection(idx.getFillFactor());
        if (btnUniqueIndex != null) {
            btnUniqueIndex.setSelection(idx.isUnique());
        }

        updateAMCombo();
        if (null != idx.getAccessMethod()) {
            cmbAccessMethod.setText(idx.getAccessMethod().getName());
        }
        updateTablespaceObject();
        if (null != idx.getTablespace()) {
            cmbTablespace.setText(idx.getTablespace().getName());
        }
        updateIndexTypeObject();
        if (null != idx.getIndexType() && !"".equals(idx.getIndexType())) {
            cmbIndexType.setText(idx.getIndexType());
        }
        this.indexCols = idx.getIndexedColumns();
        refreshColumns();
    }

    /**
     * Sets the focus on index name.
     */
    public void setFocusOnIndexName() {
        if (txtIndexName != null) {
            txtIndexName.forceFocus();
        }

    }


    /**
     * Handle ORC selection.
     *
     * @param compositeIndices the composite indices
     */
    public void handleORCSelection(Composite compositeIndices) {

    }

    /**
     * Handle row selection.
     *
     * @param compositeIndices the composite indices
     */
    public void handleRowSelection(Composite compositeIndices) {

    }

    /**
     * Handle column selection.
     *
     * @param compositeIndices the composite indices
     */
    public void handleColumnSelection(Composite compositeIndices) {

    }

    /**
     * Sets the table indexes UI.
     *
     * @param tblIndexes the new table indexes UI
     */
    public void setTableIndexesUI(Table tblIndexes) {

        this.indexExprTable = tblIndexes;

    }

    /**
     * Gets the table indexes UI.
     *
     * @return the table indexes UI
     */
    public Table getTableIndexesUI() {
        return this.indexExprTable;
    }

    /**
     * Reset where expression.
     *
     * @param copyEditColName the copy edit col name
     */
    public void resetWhereExpression(String copyEditColName) {
        if (getWhrExpr().contains(copyEditColName)) {
            txtWhereExpr.setText("");
        }
    }

}
