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

package org.opengauss.mppdbide.presentation.visualexplainplan;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.ResultSetColumn;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.edittabledata.DSResultSetGridColumnDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanTextDisplayGrid.
 *
 * @since 3.0.0
 */
public class ExecutionPlanTextDisplayGrid implements IDSGridDataProvider {
    private List<IDSGridDataRow> rows;
    private IDSGridColumnProvider columnProvider;
    private UIModelAnalysedPlanNode planRootNode;
    private IQueryExecutionSummary summary;
    private boolean analyze;
    private double totalRuntime;

    /**
     * The first batch over.
     */
    boolean firstBatchOver;

    /**
     * Instantiates a new execution plan text display grid.
     *
     * @param uiModelAnalysedPlanNode the ui model analysed plan node
     * @param totalRuntime the total runtime
     */
    public ExecutionPlanTextDisplayGrid(UIModelAnalysedPlanNode uiModelAnalysedPlanNode, double totalRuntime) {
        this.planRootNode = uiModelAnalysedPlanNode;
        this.analyze = uiModelAnalysedPlanNode.isAnalyze();
        this.totalRuntime = totalRuntime;
    }

    /**
     * Inits the.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void init() throws DatabaseOperationException, DatabaseCriticalException {
        ResultSetColumn[] col = new ResultSetColumn[1];
        col[0] = new ResultSetColumn(0);
        col[0].setColumnName(MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_HEADER_QUERY_PLAN));
        col[0].setDatatypeName("STRING");
        col[0].setDataType(Types.VARCHAR);
        DSResultSetGridColumnDataProvider colProvider = new DSResultSetGridColumnDataProvider();
        colProvider.init(1, col);
        this.columnProvider = colProvider;

        this.rows = addToDataRows(this.planRootNode, 0, this.analyze);

        if (this.analyze) {
            StringBuilder sb = new StringBuilder("Total runtime: ");
            sb.append(this.totalRuntime);
            sb.append("ms"); // unit check with Kalyan
            DataRow runtime = new DataRow(1);
            runtime.setValue(0, sb.toString());
            this.rows.add(runtime);
        }
        this.firstBatchOver = false;
    }

    private List<IDSGridDataRow> addToDataRows(UIModelAnalysedPlanNode node, int spacesParam, boolean isAnalyze) {
        int spaces = spacesParam;
        List<IDSGridDataRow> allRows = new ArrayList<IDSGridDataRow>(2);
        DataRow subplanName = new DataRow(1);

        if (!("".equals(node.getSubplanName()))) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            for (int i = 0; i < spaces; i++) {
                sb.append("        ");
            }
            sb.append(node.getSubplanName());
            subplanName.setValue(0, sb.toString());
            allRows.add(subplanName);
            spaces = spaces + 1;
        }

        DataRow nodeHead = new DataRow(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (int i = 0; i < spaces; i++) {
            sb.append("        ");
        }
        if (spaces != 0) {
            sb.append("->  ");
        }
        sb.append(node.toText(isAnalyze));

        nodeHead.setValue(0, sb.toString());

        List<String> additional = new ArrayList<String>(2);
        List<DataRow> additionalInfo = new ArrayList<DataRow>(2);
        if (node.getAdditionalInfo(isAnalyze) != null && node.getAdditionalInfo(isAnalyze).size() != 0) {
            additional.addAll(node.getAdditionalInfo(isAnalyze));
        }

        for (String oneInfo : additional) {
            StringBuilder sb1 = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            for (int i = 0; i < spaces; i++) {
                sb1.append("        ");
            }
            sb1.append("      ");
            sb1.append(oneInfo);
            DataRow addi = new DataRow(1);
            addi.setValue(0, sb1.toString());
            additionalInfo.add(addi);
        }
        allRows.add(nodeHead);
        allRows.addAll(additionalInfo);

        List<UIModelAnalysedPlanNode> children = node.getChildren();
        for (UIModelAnalysedPlanNode child : children) {
            List<IDSGridDataRow> rowForChild = addToDataRows(child, spaces + 1, isAnalyze);
            allRows.addAll(rowForChild);
        }
        return allRows;
    }

    /**
     * Close.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public void close() throws DatabaseOperationException, DatabaseCriticalException {

    }

    /**
     * Gets the next batch.
     *
     * @return the next batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {
        if (!this.firstBatchOver) {
            this.firstBatchOver = true;
            return rows;
        }
        return null;
    }

    /**
     * Gets the all fetched rows.
     *
     * @return the all fetched rows
     */
    @Override
    public List<IDSGridDataRow> getAllFetchedRows() {
        return rows;
    }

    /**
     * Checks if is end of records.
     *
     * @return true, if is end of records
     */
    @Override
    public boolean isEndOfRecords() {
        return this.firstBatchOver;
    }

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    @Override
    public int getRecordCount() {
        return rows.size();
    }

    /**
     * Gets the column data provider.
     *
     * @return the column data provider
     */
    @Override
    public IDSGridColumnProvider getColumnDataProvider() {
        return this.columnProvider;
    }

    /**
     * Pre destroy.
     */
    @Override
    public void preDestroy() {
        this.planRootNode = null;
        this.summary = null;
        if (this.rows != null) {
            this.rows.clear();
        }
    }

    /**
     * Gets the summary.
     *
     * @return the summary
     */
    public IQueryExecutionSummary getSummary() {
        return summary;
    }

    /**
     * Gets the result tab dirty flag.
     *
     * @return the result tab dirty flag
     */
    @Override
    public boolean getResultTabDirtyFlag() {

        return false;
    }

    /**
     * Gets the column group provider.
     *
     * @return the column group provider
     */
    @Override
    public IDSGridColumnGroupProvider getColumnGroupProvider() {

        return null;
    }

    /**
     * Sets the result tab dirty flag.
     *
     * @param flag the new result tab dirty flag
     */
    @Override
    public void setResultTabDirtyFlag(boolean flag) {

    }

    /**
     * Sets the summary.
     *
     * @param summary the new summary
     */
    public void setSummary(IQueryExecutionSummary summary) {
        this.summary = summary;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DataRow.
     */
    static class DataRow implements IDSGridDataRow {
        
        /**
         * The values.
         */
        String[] values;

        /**
         * Instantiates a new data row.
         *
         * @param cols the cols
         */
        DataRow(int cols) {
            values = new String[cols];
        }

        /**
         * Gets the values.
         *
         * @return the values
         */
        @Override
        public Object[] getValues() {
            return values;
        }

        /**
         * Gets the value.
         *
         * @param columnIndex the column index
         * @return the value
         */
        @Override
        public Object getValue(int columnIndex) {
            return values[columnIndex];
        }

        /**
         * Gets the cloned values.
         *
         * @return the cloned values
         */
        @Override
        public Object[] getClonedValues() {
            return values.clone();
        }

        /**
         * Sets the value.
         *
         * @param columnIndex the column index
         * @param value the value
         */
        public void setValue(int columnIndex, String value) {
            values[columnIndex] = value;
        }

    }

    /**
     * Gets the table.
     *
     * @return the table
     */
    @Override
    public TableMetaData getTable() {

        return null;
    }

    /**
     * Gets the databse.
     *
     * @return the databse
     */
    @Override
    public Database getDatabse() {

        return null;
    }

    /**
     * init
     */
    @Override
    public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException {   
    }

    /**
     * gets the next batch
     */
    @Override
    public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
            throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    @Override
    public void setFuncProcExport(boolean isFuncProcExport) {        
    }

    @Override
    public boolean isFuncProcExport() {
        return false;
    }

}
