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

package org.opengauss.mppdbide.explainplan.nodetypes;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class OperationalNode.
 *
 * @since 3.0.0
 */
public abstract class OperationalNode extends BasicNode {

    private PropertyChangeSupport listeners;

    /**
     * The Constant PROPERTY_LAYOUT.
     */
    public static final String PROPERTY_LAYOUT = "Nodelayout";

    /**
     * The max file num.
     */
    @SerializedName("Max File Num")
    protected long maxFileNum;

    /**
     * The min file num.
     */
    @SerializedName("Min File Num")
    protected long minFileNum;

    /**
     * The max memory used.
     */
    @SerializedName("Max Memory Used")
    protected double maxMemoryUsed;

    /**
     * The min memory used.
     */
    @SerializedName("Min Memory Used")
    protected long minMemoryUsed;

    /**
     * The parent.
     */
    protected OperationalNode parent;

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(OperationalNode parent) {
        this.parent = parent;
    }

    /**
     * The children.
     */
    protected ArrayList<OperationalNode> children;
    
    /**
     * The indentation level.
     */    
    private int indentationLevel;

    /**
     * Instantiates a new operational node.
     *
     * @param nodetype the nodetype
     */
    public OperationalNode(NodeCategoryEnum nodetype) {
        super(nodetype);
        children = new ArrayList<OperationalNode>(0);
        this.listeners = new PropertyChangeSupport(this);
    }

    /**
     * Adds the child node.
     *
     * @param childnode the childnode
     */
    public void addChildNode(OperationalNode childnode) {
        children.add(childnode);
    }
    
    /**
     * Adds the indentation level.
     *
     * @param indentationlevel the indentationlevel
     */   
    public void setIndentationLevel(int level) {
        this.indentationLevel = level;
    }

    /**
     * Adds the property change listener.
     *
     * @param listener the listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Gets the listeners.
     *
     * @return the listeners
     */
    public PropertyChangeSupport getListeners() {
        return listeners;
    }

    /**
     * Removes the property change listener.
     *
     * @param listener the listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    /**
     * To string.
     *
     * @return the string
     */
    public String toString() {
        String chilNodeInfo = null;
        if (this.children.size() == 0) {
            return super.toString();
        }

        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        int index = 1;

        for (BasicNode child : this.children) {
            sb.append(index + ". ");
            sb.append(child.toString());
            sb.append(System.lineSeparator());
            index++;
        }

        chilNodeInfo = sb.toString();

        return super.toString() + System.lineSeparator() + chilNodeInfo;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public ArrayList<OperationalNode> getChildren() {
        return children;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public OperationalNode getParent() {
        return this.parent;
    }
    
    /**
     * Gets the indentation level.
     *
     * @return the indentation level
     */   
    public int getIndentationLevel() {
        return this.indentationLevel;
    }

    /**
     * Gets the output.
     *
     * @return the output
     */
    public String[] getOutput() {
        if (this.output != null) {
            return this.output.clone();
        }
        return new String[0];

    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    public List<String> getNodeSpecific() {

        return null;
    }

    /**
     * Gets the DN involved.
     *
     * @param dnsInvolved the dns involved
     * @return the DN involved
     */
    public ArrayList<String> getDNInvolved(ArrayList<String> dnsInvolved) {
        return super.getDNInvolved(dnsInvolved);
    }

    /**
     * Gets the node specific properties.
     *
     * @return the node specific properties
     */
    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> output = super.getNodeSpecificProperties();

        if (1 == this.getChildren().size()) {
            output.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_OPERATIONALNODE_CHILD1NAME),
                    this.getChildren().get(0).getNodeType()).getProp());
        } else if (2 == this.getChildren().size()) {
            output.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_OPERATIONALNODE_CHILD1NAME),
                    this.getChildren().get(0).getNodeType()).getProp());
            output.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_OPERATIONALNODE_CHILD2NAME),
                    this.getChildren().get(1).getNodeType()).getProp());
        } else if (2 < this.getChildren().size()) {
            int index = 1;

            for (OperationalNode c : this.getChildren()) {
                output.add(new ServerProperty(
                        MessageConfigLoader.getProperty(
                                IMessagesConstants.VIS_EXPLAIN_PROP_OPERATIONALNODE_CHILDRENNAME) + '-' + index,
                        c.getNodeType()).getProp());
                index++;
            }

        }

        return output;

    }

    /**
     * Gets the item name.
     *
     * @return the item name
     */
    public String getItemName() {

        return null;
    }

    /**
     * Gets the item details.
     *
     * @return the item details
     */
    public String getItemDetails() {

        return null;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    public String getEntityName() {

        return null;
    }

    /**
     * To text.
     *
     * @param isAnalyze the is analyze
     * @return the string
     */
    public String toText(boolean isAnalyze) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getNodeType());
        sb.append(" ");
        sb.append(getCostInfoForTextDisplay(isAnalyze));
        return sb.toString();
    }

    /**
     * Inits the detail array.
     *
     * @param indetailscategories the indetailscategories
     * @return the array list
     */
    protected ArrayList<INDETAILSCATEGORY> initDetailArray(INDETAILSCATEGORY... indetailscategories) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = new ArrayList<INDETAILSCATEGORY>(5);
        for (INDETAILSCATEGORY field : indetailscategories) {
            detailsNeeded.add(field);
        }
        return detailsNeeded;
    }
}
