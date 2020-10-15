/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnalysedPlanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AnalysedPlanNode extends AbstractAnalysedPlanNode {

    /**
     * Instantiates a new analysed plan node.
     *
     * @param inputStat the input stat
     */
    public AnalysedPlanNode(OperationalNode inputStat) {
        super(inputStat);

    }

    /**
     * Gets the self total time.
     *
     * @return the self total time
     */
    public double getSelfTotalTime() {
        return this.selfTime;
    }

    /**
     * Gets the node name.
     *
     * @return the node name
     */
    public String getNodeName() {
        return this.coreStats.getNodeType();
    }

    /**
     * Gets the node unique name with type.
     *
     * @return the node unique name with type
     */
    public String getNodeUniqueNameWithType() {
        return this.getNodeSequenceNum() + ". " + this.getNodeName();
    }

    /**
     * Gets the node unique name.
     *
     * @return the node unique name
     */
    public String getNodeUniqueName() {
        return this.getNodeSequenceNum() + ". " + this.getNodeCategoryName();
    }

    private String getNodeCategoryName() {
        return this.coreStats.getNodeCategoryName();
    }

    /**
     * Gets the node specific properties.
     *
     * @param props the props
     * @return the node specific properties
     */
    public List<Object[]> getNodeSpecificProperties(List<Object[]> props) {
        props.addAll(this.coreStats.getNodeSpecificProperties());
        return props;
    }

    /**
     * Gets the node specific DN properties.
     *
     * @return the node specific DN properties
     */
    public Map<String, List<Object>> getNodeSpecificDNProperties() {
        if (this.coreStats.hasDNData()) {
            Map<String, List<Object>> inputMap = new HashMap<String, List<Object>>(1);
            return this.coreStats.getPerDNSpecificDetails(inputMap);
        }

        return null;
    }

    /**
     * Gets the per DN specific column grouping info.
     *
     * @return the per DN specific column grouping info
     */
    public List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo() {
        List<DNIntraNodeDetailsColumn> colGroup = new ArrayList<DNIntraNodeDetailsColumn>(5);
        return this.coreStats.getPerDNSpecificColumnGroupingInfo(colGroup);
    }

    /**
     * Gets the DN involved.
     *
     * @param dnsInvolved the dns involved
     * @return the DN involved
     */
    public ArrayList<String> getDNInvolved(ArrayList<String> dnsInvolved) {
        return this.coreStats.getDNInvolved(dnsInvolved);
    }

    /**
     * Adds the node DN plan view.
     *
     * @param dnViewofExplainPlan the dn viewof explain plan
     * @param idx the idx
     */
    public void addNodeDNPlanView(Map<String, Object[]> dnViewofExplainPlan, int idx) {
        this.coreStats.addNodeDNPlanView(dnViewofExplainPlan, idx);

    }

    /**
     * Gets the analysis.
     *
     * @return the analysis
     */
    public String getAnalysis() {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        boolean needLineSeperator = false;

        if (this.isHeaviest()) {
            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_HEAVIEST));
            needLineSeperator = true;
        }
        if (this.isCostliest()) {
            if (needLineSeperator) {
                sb.append(", ");
            }

            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_COSTLIEST));
            needLineSeperator = true;
        }
        if (this.isSlowest()) {
            if (needLineSeperator) {
                sb.append(", ");
            }

            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_ANALYSIS_SLOWEST));
            needLineSeperator = true;
        }
        if (needLineSeperator) {
            return sb.toString();
        } else {
            return "---NA---";
        }
    }

    /**
     * Gets the total cost.
     *
     * @return the total cost
     */
    public double getTotalCost() {
        return this.coreStats.getTotalCost();
    }

    /**
     * Gets the plan deviation by record count.
     *
     * @param plannedRecCount the planned rec count
     * @param actualRecordCount the actual record count
     * @return the plan deviation by record count
     */
    public double getPlanDeviationByRecordCount(long plannedRecCount, long actualRecordCount) {
        return calculateDeviation(plannedRecCount, actualRecordCount) * 100;
    }

    private double calculateDeviation(long point, long cnt) {
        if (point == 0) {
            return cnt;
        }

        return (double) (cnt - point) / point;

    }

    /**
     * Gets the item name.
     *
     * @return the item name
     */
    public String getItemName() {
        return this.coreStats.getItemName();
    }

    /**
     * Gets the item details.
     *
     * @return the item details
     */
    public String getItemDetails() {
        return this.coreStats.getItemDetails();
    }

    /**
     * To text.
     *
     * @param isAnalyze the is analyze
     * @return the string
     */
    public String toText(boolean isAnalyze) {
        return coreStats.toText(isAnalyze);
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        return coreStats.getAdditionalInfo(isAnalyze);
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    public int hashCode() {
        // just an extra line so that this does not fall over when the tool is
        // used incorrectly
        if (coreStats.getNodeType() == null) {
            return super.hashCode();
        }

        if (null == this.getParent()) {
            return coreStats.getNodeType().hashCode();
        }
        String schemaName = this.getParent().getNodeType();
        return schemaName.hashCode() + coreStats.getNodeType().hashCode();
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof AnalysedPlanNode) {
            AnalysedPlanNode node = (AnalysedPlanNode) obj;
            AnalysedPlanNode objectParent = node.getParent();
            AnalysedPlanNode currentParent = this.getParent();
            if (node.getNodeType().equals(this.getNodeType())) {
                if (objectParent == null && currentParent == null) {
                    return true;
                } else {
                    if (null != objectParent && null != currentParent
                            && objectParent.getNodeType().equals(currentParent.getNodeType())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
