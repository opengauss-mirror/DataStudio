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

package org.opengauss.mppdbide.explainplan.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractAnalysedPlanNode.
 *
 * @since 3.0.0
 */
public abstract class AbstractAnalysedPlanNode {

    /**
     * The parent.
     */
    protected AnalysedPlanNode parent;

    /**
     * The core stats.
     */
    protected OperationalNode coreStats;

    /**
     * The child node stats.
     */
    protected ArrayList<AnalysedPlanNode> childNodeStats;

    /**
     * The node sequence number.
     */
    protected int nodeSequenceNumber;

    /**
     * The self time.
     */
    protected double selfTime;

    /**
     * The self time contribution in over all plan.
     */
    protected double selfTimeContributionInOverAllPlan;

    /**
     * The self cost.
     */
    protected double selfCost;

    /**
     * The source relationship.
     */
    protected ArrayList<Relationship> sourceRelationship = new ArrayList<Relationship>(1);

    /**
     * The target relationship.
     */
    protected ArrayList<Relationship> targetRelationship = new ArrayList<Relationship>(1);

    /**
     * The is slowest.
     */
    protected boolean isSlowest;

    /**
     * The is costliest.
     */
    protected boolean isCostliest;

    /**
     * The is heaviest.
     */
    protected boolean isHeaviest;

    /**
     * The total time contribution percentage.
     */

    protected double totalTimeContributionPercentage;

    /**
     * Checks if is heaviest.
     *
     * @return true, if is heaviest
     */
    public boolean isHeaviest() {
        return isHeaviest;
    }

    /**
     * Sets the heaviest.
     *
     * @param isHeavist the new heaviest
     */
    public void setHeaviest(boolean isHeavist) {
        this.isHeaviest = isHeavist;
    }

    /**
     * Checks if is slowest.
     *
     * @return true, if is slowest
     */
    public boolean isSlowest() {
        return isSlowest;
    }

    /**
     * Checks if is costliest.
     *
     * @return true, if is costliest
     */
    public boolean isCostliest() {
        return isCostliest;
    }

    /**
     * Gets the child node stats.
     *
     * @return the child node stats
     */
    public ArrayList<AnalysedPlanNode> getChildNodeStats() {
        return childNodeStats;
    }

    /**
     * Gets the total time contribution percentage.
     *
     * @return the total time contribution percentage
     */
    public double getTotalTimeContributionPercentage() {
        return totalTimeContributionPercentage;
    }

    /**
     * Sets the total time contribution percentage.
     *
     * @param totalTimeContributionPercentage the new total time contribution
     * percentage
     */
    public void setTotalTimeContributionPercentage(double totalTimeContributionPercentage) {
        this.totalTimeContributionPercentage = totalTimeContributionPercentage;
    }

    /**
     * Instantiates a new abstract analysed plan node.
     *
     * @param inputStat the input stat
     */
    public AbstractAnalysedPlanNode(OperationalNode inputStat) {
        this.coreStats = inputStat;
        this.childNodeStats = new ArrayList<AnalysedPlanNode>(0);

    }

    /**
     * Adds the child.
     *
     * @param childAnalysedNode the child analysed node
     */
    public void addChild(AnalysedPlanNode childAnalysedNode) {
        this.childNodeStats.add(childAnalysedNode);

    }

    /**
     * Gets the child.
     *
     * @return the child
     */
    public OperationalNode getChild() {
        return coreStats;
    }

    /**
     * Sets the costliest node.
     *
     * @param value the new costliest node
     */
    public void setCostliestNode(boolean value) {
        isCostliest = value;
    }

    /**
     * Sets the heaviest node.
     *
     * @param value the new heaviest node
     */
    public void setHeaviestNode(boolean value) {
        isHeaviest = value;
    }

    /**
     * Sets the slowest node.
     *
     * @param value the new slowest node
     */
    public void setSlowestNode(boolean value) {
        isSlowest = value;

    }

    /**
     * To string.
     *
     * @return the string
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(coreStats.getNodeType());
        sb.append(" {");

        if (isHeaviest) {
            sb.append(" heaviest = true");
            sb.append("; ");
        }

        if (isSlowest) {
            sb.append(" slowest = true");
            sb.append("; ");
        }

        if (isCostliest) {
            sb.append(" costliest = true");
            sb.append("; ");
        }

        sb.append(" cost_contribution: ");
        sb.append(new DecimalFormat("##.##").format(this.totalTimeContributionPercentage));
        sb.append("%");
        sb.append("}");

        sb.append(System.lineSeparator());

        for (AnalysedPlanNode child : childNodeStats) {
            sb.append(child.toString());
        }

        return sb.toString();
    }

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    public String getNodeType() {
        return coreStats.getNodeType();
    }

    /**
     * Sets the parent.
     *
     * @param parent2 the new parent
     */
    public void setParent(AnalysedPlanNode parent2) {
        this.parent = parent2;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public AnalysedPlanNode getParent() {
        return this.parent;
    }

    /**
     * Gets the self cost.
     *
     * @return the self cost
     */
    public double getSelfCost() {
        return selfCost;
    }

    /**
     * Sets the self cost.
     *
     * @param selfCost the new self cost
     */
    public void setSelfCost(double selfCost) {
        this.selfCost = selfCost;
    }

    /**
     * Do auto analysis.
     *
     * @param dvalue the dvalue
     */
    public void doAutoAnalysis(double dvalue) {
        if (Math.abs(dvalue) != 0) {
            this.totalTimeContributionPercentage = this.coreStats.getActualMaxTimeTaken() * 100 / dvalue;
        }
        this.selfTime = 0d;
        double childTotalCost = 0d;
        double childTotalTime = 0d;

        for (AnalysedPlanNode child : childNodeStats) {
            childTotalCost += child.getChild().getTotalCost();
            childTotalTime += child.getChild().getActualTotalTime();
        }

        this.selfCost = this.getChild().getTotalCost() - childTotalCost;
        if (this.selfCost < 0) {
            this.selfCost = 0;
        }

        this.selfTime = this.getChild().getActualTotalTime() - childTotalTime;
        if (this.selfTime < 0) {
            this.selfTime = 0;
        }

        if (Math.abs(dvalue) != 0) {
            this.selfTimeContributionInOverAllPlan = (this.selfTime * 100) / dvalue;
        }
    }

    /**
     * Gets the self time contribution in over all plan.
     *
     * @return the self time contribution in over all plan
     */
    public double getSelfTimeContributionInOverAllPlan() {
        return selfTimeContributionInOverAllPlan;
    }

    /**
     * Gets the source relationship.
     *
     * @return the source relationship
     */
    public List<Relationship> getSourceRelationship() {
        return this.sourceRelationship;
    }

    /**
     * Gets the target relationship.
     *
     * @return the target relationship
     */
    public List<Relationship> getTargetRelationship() {
        return this.targetRelationship;
    }

    /**
     * Adds the source relationship.
     *
     * @param rs the rs
     */
    public void addSourceRelationship(Relationship rs) {
        sourceRelationship.add(rs);
    }

    /**
     * Adds the target relationship.
     *
     * @param rs the rs
     */
    public void addTargetRelationship(Relationship rs) {
        targetRelationship.add(rs);
    }

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    public long getRecordCount() {
        return getChild().getActualRows();

    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    public List<String> getNodeSpecific() {
        return this.getChild().getNodeSpecific();
    }

    /**
     * Gets the output.
     *
     * @return the output
     */
    public String[] getOutput() {
        return this.getChild().getOutput();
    }

    /**
     * Gets the actual max time taken.
     *
     * @return the actual max time taken
     */
    public double getActualMaxTimeTaken() {
        return this.getChild().getActualMaxTimeTaken();
    }

    /**
     * Gets the parent relationship.
     *
     * @return the parent relationship
     */
    public String getParentRelationship() {
        return this.getChild().getParentRelationship();
    }

    /**
     * Gets the plan record count.
     *
     * @return the plan record count
     */
    public long getPlanRecordCount() {
        return getChild().getPlanRows();
    }

    /**
     * Sets the node sequence num.
     *
     * @param seqNumber the new node sequence num
     */
    public void setNodeSequenceNum(int seqNumber) {
        this.nodeSequenceNumber = seqNumber;
    }

    /**
     * Gets the node sequence num.
     *
     * @return the node sequence num
     */
    public int getNodeSequenceNum() {
        return this.nodeSequenceNumber;
    }

    /**
     * Gets the formated output.
     *
     * @param seperator the seperator
     * @return the formated output
     */
    public String getFormatedOutput(String seperator) {
        String[] str = getOutput();
        if (1 == str.length) {
            return str[0];
        }
        StringBuilder sb = new StringBuilder(str[0]);
        for (int index = 1; index < str.length; index++) {
            sb.append(seperator);
            sb.append(str[index]);
        }

        return sb.toString();
    }

}
