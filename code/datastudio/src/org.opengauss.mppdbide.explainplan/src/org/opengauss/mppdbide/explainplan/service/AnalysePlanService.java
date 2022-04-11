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

import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.explainplan.nodetypes.RootPlanNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class AnalysePlanService.
 *
 * @since 3.0.0
 */
public class AnalysePlanService {
    private boolean isAnalysisDone;

    /**
     * The root.
     */
    protected RootPlanNode root;
    private AnalysedPlanNode analysedplan;

    private double tempMaxCost;
    private double tempMaxTime;
    private long tempMaxRows;

    private AnalysedPlanNode tempCostliestNode;
    private AnalysedPlanNode tempHeaviestNode;
    private AnalysedPlanNode tempSlowestNode;

    private int nodeSequence = 0;

    private int getSeqNumber() {
        return ++nodeSequence;
    }

    /**
     * Instantiates a new analyse plan service.
     *
     * @param planroot the planroot
     */
    public AnalysePlanService(RootPlanNode planroot) {

        this.root = planroot;
        this.isAnalysisDone = false;
        this.tempMaxCost = 0;
        this.tempMaxTime = 0;
        this.tempMaxRows = 0;
        this.nodeSequence = 0;
        /*
         * get the child(0) as the root has no data in it. All data is stored in
         * its 0th child node.
         */
        tempCostliestNode = new AnalysedPlanNode(this.root.getChildren().get(0));
        tempHeaviestNode = new AnalysedPlanNode(this.root.getChildren().get(0));
        tempSlowestNode = new AnalysedPlanNode(this.root.getChildren().get(0));
    }

    /**
     * Do analysis.
     *
     * @return the analysed plan node
     */
    public AnalysedPlanNode doAnalysis() {
        if (this.isAnalysisDone()) {
            return this.analysedplan;
        }

        this.setAnalysisDone(false);
        this.analysedplan = doDetailedAnalysis(this.root.getChildren().get(0), null);
        this.tempCostliestNode.setCostliestNode(true);
        this.tempHeaviestNode.setHeaviestNode(true);
        this.tempSlowestNode.setSlowestNode(true);
        createParentChildRelationship(this.analysedplan);
        this.setAnalysisDone(true);

        return this.analysedplan;
    }

    private void createParentChildRelationship(AnalysedPlanNode curNode) {
        for (AnalysedPlanNode child : curNode.getChildNodeStats()) {
            Relationship rs = new Relationship(curNode, child);
            curNode.addSourceRelationship(rs);
            child.addTargetRelationship(rs);
            createParentChildRelationship(child);
        }

    }

    private AnalysedPlanNode doDetailedAnalysis(OperationalNode curNode, AnalysedPlanNode parent) {
        AnalysedPlanNode curAnalysedNode = new AnalysedPlanNode(curNode);
        curAnalysedNode.setNodeSequenceNum(this.getSeqNumber());
        curAnalysedNode.setParent(parent);
        AnalysedPlanNode childAnalysedNode = null;

        for (OperationalNode child : curNode.getChildren()) {
            childAnalysedNode = doDetailedAnalysis(child, curAnalysedNode);
            curAnalysedNode.addChild(childAnalysedNode);
        }

        curAnalysedNode.doAutoAnalysis(this.root.getTotalRuntime());

        // checking for costliest node
        if (Math.abs(curAnalysedNode.getSelfCost()) > Math.abs(tempMaxCost)) {
            tempMaxCost = curNode.getTotalCost();
            this.tempCostliestNode = curAnalysedNode;
        }

        // checking for heaviest node
        if (curNode.getActualRows() > tempMaxRows) {
            tempMaxRows = curNode.getActualRows();
            this.tempHeaviestNode = curAnalysedNode;
        }

        // checking for slowest node
        if (Math.abs(curAnalysedNode.getSelfTotalTime()) > Math.abs(tempMaxTime)) {
            tempMaxTime = curAnalysedNode.getSelfTotalTime();
            this.tempSlowestNode = curAnalysedNode;
        }

        return curAnalysedNode;
    }

    /**
     * Checks if is analysis done.
     *
     * @return true, if is analysis done
     */
    public boolean isAnalysisDone() {
        return isAnalysisDone;
    }

    /**
     * Sets the analysis done.
     *
     * @param isAnalysDone the new analysis done
     */
    public void setAnalysisDone(boolean isAnalysDone) {
        this.isAnalysisDone = isAnalysDone;
    }

}
