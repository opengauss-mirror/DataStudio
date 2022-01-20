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

package com.huawei.mppdbide.explainplan.ui.model;

import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import ca.odell.glazedlists.TreeList.Format;
/*
 * tree structure that will work as data-provider to tree-table in execution tab
 */

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat.
 *
 * @since 3.0.0
 */
public class ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat
        implements Format<ExplainAnalyzePlanNodeTreeDisplayData>, IDSGridDataProvider {
    private IQueryExecutionSummary summary;
    private boolean isFirstBatchOver;
    private UIModelAnalysedPlanNode node;
    private List<IDSGridDataRow> nodeInfos;
    private List<ExplainAnalyzePlanNodeTreeDisplayData> nodes;
    private IDSGridColumnProvider columnDataProvider;
    private boolean analyzeOperationalNode;
    private List<String[]> listExec;
    private String spaces;

    /**
     * preDestroy
     */
    public void preDestroy() {
        this.summary = null;
        this.node = null;
        if (this.nodeInfos != null) {
            this.nodeInfos.clear();
        }
        this.nodeInfos = null;
        if (this.nodes != null) {
            this.nodes.clear();
        }
        this.nodes = null;
    }

    /**
     * Checks if is heaviest.
     *
     * @param row the row
     * @return true, if is heaviest
     */
    public boolean isHeaviest(int row) {
        return (nodes != null && nodes.size() > row) ? nodes.get(row).isHeaviest() : false;
    }

    /**
     * Checks if is costliest.
     *
     * @param row the row
     * @return true, if is costliest
     */
    public boolean isCostliest(int row) {
        return (nodes != null && nodes.size() > row) ? nodes.get(row).isCostliest() : false;
    }

    /**
     * Checks if is slowest.
     *
     * @param row the row
     * @return true, if is slowest
     */
    public boolean isSlowest(int row) {
        return (nodes != null && nodes.size() > row) ? nodes.get(row).isSlowest() : false;
    }

    /**
     * Gets the column label map.
     *
     * @return the column label map
     */
    public HashMap<String, String> getColumnLabelMap() {
        return ((Column) columnDataProvider).getColumnLabelMap();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class Column.
     */
    public static class Column implements IDSGridColumnProvider {
        private String[] columns;
        private String[] columnLabels;

        /**
         * Instantiates a new column.
         *
         * @param analyze the analyze
         */
        public Column(boolean analyze) {
            if (!analyze) {
                String[] props = {TreeGridColumnHeader.PROP_NODE_TYPE, TreeGridColumnHeader.PROP_STARTUP_COST,
                    TreeGridColumnHeader.PROP_TOTAL_COST, TreeGridColumnHeader.PROP_ROWS,
                    TreeGridColumnHeader.PROP_WIDTH, TreeGridColumnHeader.PROP_ADDITIONAL_INFO};
                String[] popLabels = {TreeGridColumnHeader.LABEL_NODE_TYPE, TreeGridColumnHeader.LABEL_STARTUP_COST,
                    TreeGridColumnHeader.LABEL_TOTAL_COST, TreeGridColumnHeader.LABEL_ROWS,
                    TreeGridColumnHeader.LABEL_WIDTH, TreeGridColumnHeader.LABEL_ADDITIONAL_INFO};
                columns = props;
                columnLabels = popLabels;
            } else {
                String[] props = {TreeGridColumnHeader.PROP_NODE_TYPE, TreeGridColumnHeader.PROP_STARTUP_COST,
                    TreeGridColumnHeader.PROP_TOTAL_COST, TreeGridColumnHeader.PROP_ROWS,
                    TreeGridColumnHeader.PROP_WIDTH, TreeGridColumnHeader.PROP_ACTUAL_STARTUP_TIME,
                    TreeGridColumnHeader.PROP_ACTUAL_TOTAL_TIME, TreeGridColumnHeader.PROP_ACTUAL_ROWS,
                    TreeGridColumnHeader.PROP_ACTUAL_LOOPS, TreeGridColumnHeader.PROP_ADDITIONAL_INFO};
                String[] popLabels = {TreeGridColumnHeader.LABEL_NODE_TYPE, TreeGridColumnHeader.LABEL_STARTUP_COST,
                    TreeGridColumnHeader.LABEL_TOTAL_COST, TreeGridColumnHeader.LABEL_ROWS,
                    TreeGridColumnHeader.LABEL_WIDTH, TreeGridColumnHeader.LABEL_ACTUAL_STARTUP_TIME,
                    TreeGridColumnHeader.LABEL_ACTUAL_TOTAL_TIME, TreeGridColumnHeader.LABEL_ACTUAL_ROWS,
                    TreeGridColumnHeader.LABEL_ACTUAL_LOOPS, TreeGridColumnHeader.LABEL_ADDITIONAL_INFO};
                columns = props;
                columnLabels = popLabels;
            }
        }

        /**
         * Gets the column label map.
         *
         * @return the column label map
         */
        public HashMap<String, String> getColumnLabelMap() {
            HashMap<String, String> columnLabelMap = new HashMap<>();
            for (int index = 0; index < columns.length; index++) {
                columnLabelMap.put(columns[index], columnLabels[index]);
            }
            return columnLabelMap;
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String[] getColumnNames() {
            return columns.clone();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnLabels[columnIndex];
        }

        @Override
        public String getColumnDesc(int columnIndex) {
            return columnLabels[columnIndex];
        }

        @Override
        public int getColumnIndex(String columnLabel) {
            for (int index = 0; index < columnLabels.length; index++) {
                if (columnLabels[index].equals(columnLabel)) {
                    return index;
                }
            }
            return -1;
        }

        @Override
        public Comparator<Object> getComparator(int columnIndex) {
            return null;
        }

        @Override
        public int getColumnDatatype(int columnIndex) {
            return Types.VARCHAR;
        }

        @Override
        public String getColumnDataTypeName(int columnIndex) {
            return "String";
        }

        @Override
        public int getPrecision(int columnIndex) {
            return 0;
        }

        @Override
        public int getScale(int columnIndex) {

            return 0;
        }

        @Override
        public int getMaxLength(int columnIndex) {

            return 0;
        }

        @Override
        public String getDefaultValue(int i) {
            return null;
        }

    }

    /**
     * Gets the node infos.
     *
     * @return the node infos
     */
    public List<ExplainAnalyzePlanNodeTreeDisplayData> getNodes() {
        return nodes;
    }
    
    /**
     * Gets the listExec.
     *
     * @return the list exec
     */

    public List<String[]> getListExec() {
        return listExec;
    }

    /**
     * Instantiates a new explain analyze plan node tree display data tree
     * format.
     *
     * @param node the node
     */
    public ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(UIModelAnalysedPlanNode node) {
        this.isFirstBatchOver = false;
        this.node = node;
        nodeInfos = new ArrayList<IDSGridDataRow>(5);
        nodes = new ArrayList<ExplainAnalyzePlanNodeTreeDisplayData>(5);
        columnDataProvider = new Column(node.isAnalyze()); 
        doDFS();
        analyzeOperationalNode = this.node.isAnalyze();
        OperationalNode parent = this.node.getOperationalNode();
        parent.setIndentationLevel(0);
    }
    
    /**
     * Traverse through Tree data for exporting explain analyze plan node tree
     * display data tree format.
     */

    public void treeExport() {
        listExec = new ArrayList<String[]>();
        doDFS();
        exportTreeFormatData(this.node, this.node.isAnalyze());
    }

    /**
     * Populate path with a list describing the path from a root node to this
     * element. Upon returning, the list must have size is equal to or greater
     * than 1, where the provided element identical to the list's last element.
     */
    public void getPath(List<ExplainAnalyzePlanNodeTreeDisplayData> path,
            ExplainAnalyzePlanNodeTreeDisplayData element) {
        path.add(element);
        ExplainAnalyzePlanNodeTreeDisplayData parent = element.getParent();
        while (parent != null) {
            path.add(parent);
            parent = parent.getParent();
        }
        Collections.reverse(path);
    }

    /**
     * Simply always return <code>true</code>.
     *
     * @return <code>true</code> if this element can have child elements, or
     * <code>false</code> if it is always a leaf node.
     */

    public boolean allowsChildren(ExplainAnalyzePlanNodeTreeDisplayData element) {
        return true;
    }

    /**
     * Returns the comparator used to order path elements of the specified
     * depth. If enforcing order at this level is not intended, this method
     * should return <code>null</code>.
     */
    public Comparator<? super ExplainAnalyzePlanNodeTreeDisplayData> getComparator(int depth) {
        return new Comparator<ExplainAnalyzePlanNodeTreeDisplayData>() {
            @Override
            public int compare(ExplainAnalyzePlanNodeTreeDisplayData o1, ExplainAnalyzePlanNodeTreeDisplayData o2) {
                return o1.compareTo(o2);
            }

        };
    }
    
    private void doDFS() {
        /*
         * depth-first-traversal to create parent-child relation
         */
        Stack<UIModelAnalysedPlanNode> modelStack = new Stack<UIModelAnalysedPlanNode>();
        Stack<ExplainAnalyzePlanNodeTreeDisplayData> dataStack = new Stack<ExplainAnalyzePlanNodeTreeDisplayData>();
        boolean analyze = this.node.isAnalyze();
        modelStack.push(this.node); // node actual has the tree, which we will
                                    // traverse to create the map
        ExplainAnalyzePlanNodeTreeDisplayData nodeData = ExplainAnalyzePlanNodeTreeDisplayDataFactory.getInstance()
                // its structure for display
                .createData(this.node.getAnalysedPlanNode(), analyze);
        nodeData.setParent(null);
        dataStack.push(nodeData);
        this.node.setVisited(false);
        while (!modelStack.isEmpty()) {
            UIModelAnalysedPlanNode parentNode = modelStack.pop();
            ExplainAnalyzePlanNodeTreeDisplayData parentData = dataStack.pop();
            nodeInfos.add((IDSGridDataRow) parentData);
            nodes.add(parentData);
            List<UIModelAnalysedPlanNode> children = parentNode.getChildren();
            for (UIModelAnalysedPlanNode childNode : children) {
                ExplainAnalyzePlanNodeTreeDisplayData childData = ExplainAnalyzePlanNodeTreeDisplayDataFactory
                        .getInstance().createData(childNode.getAnalysedPlanNode(), analyze);
                childData.setParent(parentData);
                modelStack.push(childNode);
                dataStack.push(childData);
                childNode.setVisited(false);
            }
        }
    }

    private void exportTreeFormatData(UIModelAnalysedPlanNode parentNode, boolean isAnalyze) {
        OperationalNode operationalNode = parentNode.getOperationalNode();
        int sizeStr;
        if (isAnalyze) {
            sizeStr = MPPDBIDEConstants.STR_EXPLAIN_PLAN_ANALYZE_SIZE;
        } else {
            sizeStr = MPPDBIDEConstants.STR_EXPLAIN_PLAN_SIZE;
        }
        String[] strInfo = new String[sizeStr];
        spaces = levelIndentation(operationalNode);
        String nodeType = operationalNode.getNodeName();
        strInfo[0] = spaces + nodeType;
        double startupCost = operationalNode.getStartupCost();
        strInfo[1] = getObjecttoString(startupCost);
        double totalCost = operationalNode.getTotalCost();
        strInfo[2] = getObjecttoString(totalCost);
        long planRows = operationalNode.getPlanRows();
        strInfo[3] = getObjecttoString(planRows);
        long planWidth = operationalNode.getPlanWidth();
        strInfo[4] = getObjecttoString(planWidth);
        if (isAnalyze) {
            handleIsAnalyze(operationalNode, strInfo);
        } else {
            String additionalInfo = buildAdditionalInfo(operationalNode);
            strInfo[5] = additionalInfo;
        }
        listExec.add(strInfo);
        ArrayList<UIModelAnalysedPlanNode> children = parentNode.getChildren();
        parentNode.setVisited(true);
        for (UIModelAnalysedPlanNode child : children) {
            if (!child.getVisited()) {
                OperationalNode nodeChild = child.getOperationalNode();
                nodeChild.setIndentationLevel(operationalNode.getIndentationLevel() + 1);
                exportTreeFormatData(child, isAnalyze);
            }
        }
    }

    private void handleIsAnalyze(OperationalNode operationalNode, String[] strInfo) {
        double actualMaxStartUpTime = operationalNode.getActualStartupTime();
        if (actualMaxStartUpTime == 0) {
            actualMaxStartUpTime = operationalNode.getActualMaxStartupTime();
        }
        strInfo[5] = getObjecttoString(actualMaxStartUpTime);
        double actualTotalTime = operationalNode.getActualTotalTime();
        if (actualTotalTime == 0) {
            actualTotalTime = operationalNode.getActualMaxTotalTime();
        }
        strInfo[6] = getObjecttoString(actualTotalTime);
        long actualRows = operationalNode.getActualRows();
        strInfo[7] = getObjecttoString(actualRows);
        long actualLoopCount = operationalNode.getActualLoopCount();
        strInfo[8] = getObjecttoString(actualLoopCount);
        String additionalInfo = buildAdditionalInfo(operationalNode);
        strInfo[9] = additionalInfo;
    }

    private String levelIndentation(OperationalNode opNode) {
        int integerValue = opNode.getIndentationLevel();
        StringBuilder strBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (int i = 0; i < integerValue; i++) {
            strBuilder = strBuilder.append("  ");
        }

        String levelSpace = strBuilder.toString();
        return levelSpace;
    }

    private String buildAdditionalInfo(OperationalNode op) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        List<String> info = op.getAdditionalInfo(analyzeOperationalNode);
        if (info != null && info.size() != 0) {
            for (int i = 0; i < info.size(); i++) {
                String n1 = info.get(i).replaceAll("&gt;", ">");
                String n2 = n1.replaceAll("&lt;", "<");
                sb.append(n2);
                if (i < info.size() - 1) {
                    sb.append(System.lineSeparator());
                }
            }
            return sb.toString();
        }
        return "";
    }

    private String getObjecttoString(Object dataObject) {
        if (dataObject instanceof Long) {
            return Long.toString((Long) dataObject);
        } else if (dataObject instanceof Double) {
            return Double.toString((Double) dataObject);
        } else {
            return null == dataObject ? "" : dataObject.toString();
        }
    }

    @Override
    public void close() throws DatabaseOperationException, DatabaseCriticalException {

    }

    @Override
    public void init() {

    }

    @Override
    public List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {
        this.isFirstBatchOver = true;
        return nodeInfos;
    }

    @Override
    public List<IDSGridDataRow> getAllFetchedRows() {
        return this.nodeInfos;
    }

    @Override
    public boolean isEndOfRecords() {
        return this.isFirstBatchOver;
    }

    @Override
    public int getRecordCount() {
        return nodeInfos.size();
    }

    @Override
    public IDSGridColumnProvider getColumnDataProvider() {
        return this.columnDataProvider;
    }

    @Override
    public IDSGridColumnGroupProvider getColumnGroupProvider() {
        return null;
    }

    @Override
    public TableMetaData getTable() {
        return null;
    }

    @Override
    public Database getDatabse() {
        return null;
    }

    @Override
    public boolean getResultTabDirtyFlag() {
        return false;
    }

    @Override
    public void setResultTabDirtyFlag(boolean flag) {

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
     * Sets the summary.
     *
     * @param summary the new summary
     */
    public void setSummary(IQueryExecutionSummary summary) {
        this.summary = summary;
    }

    /**
     * init
     */
    @Override
    public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValuesList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException {
    }

    /**
     * gets the next batch
     */
    @Override
    public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValuesList)
            throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    @Override
    public void setFuncProcExport(boolean isFuncProcedureExport) {
    }

    @Override
    public boolean isFuncProcExport() {
        return false;
    }

}
