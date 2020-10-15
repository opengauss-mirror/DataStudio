/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.explainplan.service.AnalysedPlanNode;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIModelAnalysedPlanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UIModelAnalysedPlanNode extends BasicUIModelPlanNode implements IAdaptable {

    private AnalysedPlanNode model;
    private UIModelOperationalPlanNode modelchild;
    private ArrayList<UIModelAnalysedPlanNode> children;
    private ArrayList<Relationship> sourceRelationship = new ArrayList<Relationship>(1);
    private ArrayList<Relationship> targetRelationship = new ArrayList<Relationship>(1);
    private Object explainPlanNodeDetails;
    private ExecutionPlanTextDisplayGrid textView = null;

    private boolean isAnalyze;
    private boolean visited;

    /**
     * Instantiates a new UI model analysed plan node.
     *
     * @param node the node
     */
    public UIModelAnalysedPlanNode(AnalysedPlanNode node) {
        this.model = node;
    }
    
    /**
     * To text.
     *
     * @param analyze the analyze
     * @return the string
     */
    public String toText(boolean analyze) {
        return model.toText(analyze);
    }

    /**
     * Gets the analysed plan node.
     *
     * @return the analysed plan node
     */
    public AnalysedPlanNode getAnalysedPlanNode() {
        return model;
    }

    /**
     * Sets the visited plan node
     *
     * @param visitedValue the visited value
     */
    public void setVisited(boolean visitedValue) {
        this.visited = visitedValue;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public ArrayList<UIModelAnalysedPlanNode> getChildren() {
        if (this.children == null) {
            children = new ArrayList<UIModelAnalysedPlanNode>(0);
            Relationship rs = null;
            UIModelAnalysedPlanNode newChild = null;

            for (AnalysedPlanNode c : this.getAnalysedPlanNode().getChildNodeStats()) {
                newChild = new UIModelAnalysedPlanNode(c);
                rs = new Relationship(this, newChild);
                children.add(newChild);
                this.addSourceRelationship(rs);
                newChild.addTargetRelationship(rs);
            }
        }

        return this.children;
    }

    /**
     * Checks if is slowest.
     *
     * @return true, if is slowest
     */
    public boolean isSlowest() {
        return model.isSlowest();
    }

    /**
     * Checks if is costliest.
     *
     * @return true, if is costliest
     */
    public boolean isCostliest() {
        return model.isCostliest();
    }

    /**
     * Gets the self cost.
     *
     * @return the self cost
     */
    public double getSelfCost() {
        return model.getSelfCost();
    }

    /**
     * Gets the self time.
     *
     * @return the self time
     */
    public double getSelfTime() {
        return model.getSelfTotalTime();
    }

    /**
     * Checks if is heaviest.
     *
     * @return true, if is heaviest
     */
    public boolean isHeaviest() {
        return model.isHeaviest();
    }

    /**
     * Gets the actual record count.
     *
     * @return the actual record count
     */
    public long getActualRecordCount() {
        return model.getRecordCount();
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    public List<String> getNodeSpecific() {
        return this.getAnalysedPlanNode().getNodeSpecific();
    }

    /**
     * Gets the output.
     *
     * @return the output
     */
    public String[] getOutput() {
        return this.getAnalysedPlanNode().getOutput();
    }

    /**
     * Gets the node title.
     *
     * @return the node title
     */
    public String getNodeTitle() {
        return this.getAnalysedPlanNode().getNodeUniqueNameWithType();
    }

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    public String getNodeType() {
        return this.getAnalysedPlanNode().getNodeType();
    }

    /**
     * Gets the actual max time taken.
     *
     * @return the actual max time taken
     */
    public double getActualMaxTimeTaken() {
        return this.getAnalysedPlanNode().getActualMaxTimeTaken();
    }

    /**
     * Gets the total time contribution percentage.
     *
     * @return the total time contribution percentage
     */
    public double getTotalTimeContributionPercentage() {
        return this.getAnalysedPlanNode().getSelfTimeContributionInOverAllPlan();
    }

    /**
     * Gets the model children.
     *
     * @return the model children
     */
    public List<UIModelOperationalPlanNode> getModelChildren() {
        if (null == this.modelchild) {
            this.modelchild = new UIModelOperationalPlanNode(model.getChild());
        }
        ArrayList<UIModelOperationalPlanNode> modelChildren = new ArrayList<UIModelOperationalPlanNode>(1);
        modelChildren.add(this.modelchild);

        return modelChildren;
    }

    /**
     * Gets the visited
     *
     * @return the visited
     */
    public boolean getVisited() {
        return this.visited;
    }

    /**
     * Gets the operational node.
     *
     * @return the model operational node
     */
    public OperationalNode getOperationalNode() {
        return this.model.getChild();
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
     * Gets the parent relationship.
     *
     * @return the parent relationship
     */
    public String getParentRelationship() {
        return this.model.getParentRelationship();
    }

    /**
     * Gets the plan record count.
     *
     * @return the plan record count
     */
    public long getPlanRecordCount() {
        return model.getPlanRecordCount();
    }

    /**
     * Gets the adapter.
     *
     * @param <T> the generic type
     * @param adapter the adapter
     * @return the adapter
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter == IExplainPlanNodeDetails.class) {
            if (null == this.explainPlanNodeDetails) {
                this.explainPlanNodeDetails = new ExplainPlanNodeDetails(this.getAnalysedPlanNode());
            }

            return (T) this.explainPlanNodeDetails;
        }
        if (PropertyHandlerCore.class == adapter) {
            return (T) new ExplainPlanNodePropertiesCore(this.getAnalysedPlanNode());
        }
        return null;
    }

    /**
     * Flatten.
     *
     * @param flattenedExplainPlan the flattened explain plan
     * @param flattenedExplainPlanEdges the flattened explain plan edges
     */
    public void flatten(List<UIModelAnalysedPlanNode> flattenedExplainPlan,
            List<Relationship> flattenedExplainPlanEdges) {
        flattenedExplainPlan.add(this);
        flattenedExplainPlanEdges.addAll(this.getTargetRelationship());

        for (UIModelAnalysedPlanNode c : this.getChildren()) {
            c.flatten(flattenedExplainPlan, flattenedExplainPlanEdges);
        }
    }

    /**
     * Gets the toop tip text.
     *
     * @return the toop tip text
     */
    public String getToopTipText() {
        StringBuffer sb = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        sb.append("Node: ");
        sb.append(this.getAnalysedPlanNode().getNodeName());

        String itemText = this.getAnalysedPlanNode().getItemName();
        if (null != itemText) {
            sb.append(EnvirnmentVariableValidator.validateAndGetLineSeperator());
            sb.append(itemText);
        }

        itemText = this.getAnalysedPlanNode().getItemDetails();
        if (null != itemText) {
            sb.append(EnvirnmentVariableValidator.validateAndGetLineSeperator());
            sb.append(itemText);
        }

        return sb.toString();
    }

    /**
     * Gets the model in text format.
     *
     * @param totalRuntime the total runtime
     * @return the model in text format
     */
    public ExecutionPlanTextDisplayGrid getModelInTextFormat(double totalRuntime) {
        if (this.textView == null) {
            try {
                this.textView = new ExecutionPlanTextDisplayGrid(this, totalRuntime);
                this.textView.init();
            } catch (DatabaseOperationException | DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.error("error in generating plan view", exception);
                return null;
            }
        }
        return this.textView;
    }

    /**
     * Checks if is analyze.
     *
     * @return true, if is analyze
     */
    public boolean isAnalyze() {
        return isAnalyze;
    }

    /**
     * Sets the analyze.
     *
     * @param analyze the new analyze
     */
    public void setAnalyze(boolean analyze) {
        this.isAnalyze = analyze;
    }
    

    /**
     * Gets the node specific properties.
     *
     * @return the node specific properties
     */
    public List<String> getNodeSpecificProperties() {

        return null;
    }

    /**
     * Gets the additional info.
     *
     * @param analyze the analyze
     * @return the additional info
     */
    public List<String> getAdditionalInfo(boolean analyze) {
        return model.getAdditionalInfo(analyze);
    }

    /**
     * Gets the subplan name.
     *
     * @return the subplan name
     */
    public String getSubplanName() {
        return model.getChild().getSubplanName();
    }
}
