/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.ui.model;

import java.util.List;

import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExplainAnalyzePlanNodeTreeDisplayData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExplainAnalyzePlanNodeTreeDisplayData implements IDSGridDataRow {
    private int id;
    private ExplainAnalyzePlanNodeTreeDisplayData parent;

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public ExplainAnalyzePlanNodeTreeDisplayData getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(ExplainAnalyzePlanNodeTreeDisplayData parent) {
        this.parent = parent;
    }

    private String nodeType;
    private double startupCost;
    private double totalCost;
    private long planRows;
    private long planWidth;
    private String additionalInfo;

    private double actualStartupTime;
    private double actualTotalTime;
    private long actualRows;
    private long actualLoops;

    private boolean heaviest;
    private boolean costliest;
    private boolean slowest;

    private boolean isAnalyze;

    /**
     * Gets the additional info.
     *
     * @return the additional info
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the additional info.
     *
     * @param additionalInfo the new additional info
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Gets the actual startup time.
     *
     * @return the actual startup time
     */
    public double getActualStartupTime() {
        return actualStartupTime;
    }

    /**
     * Sets the actual startup time.
     *
     * @param actualStartupTime the new actual startup time
     */
    public void setActualStartupTime(double actualStartupTime) {
        this.actualStartupTime = actualStartupTime;
    }

    /**
     * Gets the actual total time.
     *
     * @return the actual total time
     */
    public double getActualTotalTime() {
        return actualTotalTime;
    }

    /**
     * Sets the actual total time.
     *
     * @param actualTotalTime the new actual total time
     */
    public void setActualTotalTime(double actualTotalTime) {
        this.actualTotalTime = actualTotalTime;
    }

    /**
     * Gets the actual rows.
     *
     * @return the actual rows
     */
    public long getActualRows() {
        return actualRows;
    }

    /**
     * Sets the actual rows.
     *
     * @param actualRows the new actual rows
     */
    public void setActualRows(int actualRows) {
        this.actualRows = actualRows;
    }

    /**
     * Gets the actual loops.
     *
     * @return the actual loops
     */
    public long getActualLoops() {
        return actualLoops;
    }

    /**
     * Sets the actual loops.
     *
     * @param actualLoops the new actual loops
     */
    public void setActualLoops(int actualLoops) {
        this.actualLoops = actualLoops;
    }

    /**
     * Checks if is heaviest.
     *
     * @return true, if is heaviest
     */
    public boolean isHeaviest() {
        return heaviest;
    }

    /**
     * Sets the heaviest.
     *
     * @param heaviest the new heaviest
     */
    public void setHeaviest(boolean heaviest) {
        this.heaviest = heaviest;
    }

    /**
     * Checks if is costliest.
     *
     * @return true, if is costliest
     */
    public boolean isCostliest() {
        return costliest;
    }

    /**
     * Sets the costliest.
     *
     * @param costliest the new costliest
     */
    public void setCostliest(boolean costliest) {
        this.costliest = costliest;
    }

    /**
     * Checks if is slowest.
     *
     * @return true, if is slowest
     */
    public boolean isSlowest() {
        return slowest;
    }

    /**
     * Sets the slowest.
     *
     * @param slowest the new slowest
     */
    public void setSlowest(boolean slowest) {
        this.slowest = slowest;
    }

    /**
     * Instantiates a new explain analyze plan node tree display data.
     *
     * @param id the id
     * @param analysedNode the analysed node
     * @param isAnalyze the is analyze
     */
    public ExplainAnalyzePlanNodeTreeDisplayData(int id, AnalysedPlanNode analysedNode, boolean isAnalyze) {
        this.id = id;
        OperationalNode node = analysedNode.getChild();
        this.nodeType = node.getNodeName();
        this.startupCost = node.getStartupCost();
        this.totalCost = node.getTotalCost();
        this.planRows = node.getPlanRows();
        this.planWidth = node.getPlanWidth();
        this.isAnalyze = isAnalyze;
        this.additionalInfo = buildAdditionalInfo(analysedNode);

        if (this.isAnalyze) {
            this.actualLoops = node.getActualLoopCount();
            this.actualRows = node.getActualRows();
            this.actualStartupTime = node.getActualStartupTime();
            if (this.actualStartupTime == 0) {
                this.actualStartupTime = node.getActualMaxStartupTime();
            }
            this.actualTotalTime = node.getActualTotalTime();
            if (this.actualTotalTime == 0) {
                this.actualTotalTime = node.getActualMaxTotalTime();
            }
        }
        this.heaviest = analysedNode.isHeaviest();
        this.costliest = analysedNode.isCostliest();
        this.slowest = analysedNode.isSlowest();
    }

    private String buildAdditionalInfo(AnalysedPlanNode node) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        List<String> info = node.getAdditionalInfo(isAnalyze);
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

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Sets the node type.
     *
     * @param nodeType the new node type
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * Gets the startup cost.
     *
     * @return the startup cost
     */
    public double getStartupCost() {
        return startupCost;
    }

    /**
     * Sets the startup cost.
     *
     * @param startupCost the new startup cost
     */
    public void setStartupCost(double startupCost) {
        this.startupCost = startupCost;
    }

    /**
     * Gets the total cost.
     *
     * @return the total cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the total cost.
     *
     * @param totalCost the new total cost
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Gets the plan rows.
     *
     * @return the plan rows
     */
    public long getPlanRows() {
        return planRows;
    }

    /**
     * Sets the plan rows.
     *
     * @param planRows the new plan rows
     */
    public void setPlanRows(long planRows) {
        this.planRows = planRows;
    }

    /**
     * Gets the plan width.
     *
     * @return the plan width
     */
    public long getPlanWidth() {
        return planWidth;
    }

    /**
     * Sets the plan width.
     *
     * @param planWidth the new plan width
     */
    public void setPlanWidth(long planWidth) {
        this.planWidth = planWidth;
    }

    /**
     * Compare to.
     *
     * @param explainAnalysePlanNodeTreeData the explainAnalysePlanNodeTreeData
     * @return the int
     */
    public int compareTo(ExplainAnalyzePlanNodeTreeDisplayData explainAnalysePlanNodeTreeData) {
        return id - explainAnalysePlanNodeTreeData.getId();
    }

    @Override
    public int hashCode() {
        return 42; // any arbitrary constant will do
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExplainAnalyzePlanNodeTreeDisplayData)) {
            return false;
        }
        return id == ((ExplainAnalyzePlanNodeTreeDisplayData) obj).getId();
    }

    private int getId() {
        return this.id;
    }

    @Override
    public Object[] getValues() {
        if (this.isAnalyze) {
            return new Object[] {nodeType, startupCost, totalCost, planRows, planWidth, actualStartupTime,
                actualTotalTime, actualRows, actualLoops, additionalInfo};
        }
        return new Object[] {nodeType, startupCost, totalCost, planRows, planWidth, additionalInfo};
    }

    @Override
    public Object getValue(int columnIndex) {
        return getValues()[columnIndex];
    }

    @Override
    public Object[] getClonedValues() {
        return getValues().clone();
    }
}
