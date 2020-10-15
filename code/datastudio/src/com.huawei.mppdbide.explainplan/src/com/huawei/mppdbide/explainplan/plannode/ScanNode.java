/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ScanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ScanNode extends OperationalNode {

    /**
     * The filter.
     */
    @SerializedName("Filter")
    protected String filter = "";

    /**
     * The rows removed by filter.
     */
    @SerializedName("Rows Removed by Filter")
    protected String rowsRemovedByFilter = "";

    /**
     * The relation name.
     */
    @SerializedName("Relation Name")
    protected String relationName = "";

    /**
     * The alias.
     */
    @SerializedName("Alias")
    protected String alias = "";

    /**
     * The selected partitions.
     */
    @SerializedName("Selected Partitions")
    protected String selectedPartitions = "";

    /**
     * The schema.
     */
    @SerializedName("Schema")
    protected String schema = "";

    /**
     * Instantiates a new scan node.
     */
    public ScanNode() {
        super(NodeCategoryEnum.SCAN);
    }

    /**
     * To text.
     *
     * @param isAnalyze the is analyze
     * @return the string
     */
    @Override
    public String toText(boolean isAnalyze) {
        StringBuilder sb = new StringBuilder(getNodeName());
        sb.append(" ");
        sb.append(getCostInfoForTextDisplay(isAnalyze));
        return sb.toString();
    }

    /**
     * Gets the node name.
     *
     * @return the node name
     */
    @Override
    public String getNodeName() {
        boolean onUsed = false;
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getNodeType());
        if (!"".equals(relationName)) {
            sb.append(" on ");
            onUsed = true;
            sb.append(relationName);
        }

        if (!"".equals(alias) && !alias.equals(relationName)) {
            if (onUsed) {
                sb.append(" ");
            } else {
                sb.append(" on ");
            }
            sb.append(alias);
        }
        return sb.toString();
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (!"".equals(relationName)) {
            sb.append("Relation Name: ").append(relationName);
            list.add(sb.toString());
        }

        if (!"".equals(alias)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Alias: ").append(alias);
            list.add(sb.toString());
        }

        if (!"".equals(selectedPartitions)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Selected Partitions: ").append(selectedPartitions);
            list.add(sb.toString());
        }

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Schema: ").append(schema);
        list.add(sb.toString());

        if (!"".equals(filter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_CSTORESCANNODE_FILTER))
                    .append(": ").append(filter);
            list.add(sb.toString());
        }

        if (!"".equals(rowsRemovedByFilter) && !"0".equals(rowsRemovedByFilter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(MessageConfigLoader
                    .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_CSTORESCANNODE_ROWREMOVEDFILTER)).append(": ")
                    .append(rowsRemovedByFilter);
            list.add(sb.toString());
        }

        return list;
    }

    /**
     * Gets the node specific properties.
     *
     * @return the node specific properties
     */
    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        if (!"".equals(relationName)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SCANTABLENAME),
                    relationName).getProp());
        }

        if (!"".equals(alias)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SCANTABLEALIASNAME),
                    alias).getProp());
        }

        if (!"".equals(schema)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SCHEMANAME), schema)
                            .getProp());
        }

        if (!"".equals(selectedPartitions)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SELECTEDPARTITIONS),
                    selectedPartitions).getProp());
        }

        addPropFilter(arList);
        addPropRowsRemovedByFilter(arList);

        return arList;
    }

    /**
     * Adds the prop filter.
     *
     * @param arList the ar list
     */
    protected void addPropFilter(ArrayList<String[]> arList) {
        // if condition moved to function to remove duplicacy in derived classes
        if (!"".equals(filter)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_CSTORESCANNODE_FILTER),
                    StringEscapeUtils.escapeHtml(filter)).getProp());
        }
    }

    /**
     * Adds the prop rows removed by filter.
     *
     * @param arList the ar list
     */
    protected void addPropRowsRemovedByFilter(ArrayList<String[]> arList) {
        // if condition moved to function to remove duplicacy in derived classes
        if (!"".equals(rowsRemovedByFilter)) {
            arList.add(
                    new ServerProperty(
                            MessageConfigLoader
                                    .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_CSTORESCANNODE_ROWREMOVEDFILTER),
                            rowsRemovedByFilter).getProp());
        }
    }

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @return the per DN specific details
     */
    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.ACTUALS_IN_DETAIL,
                INDETAILSCATEGORY.CPU_IN_DETAIL, INDETAILSCATEGORY.BUFFERS_IN_DETAIL);
        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        return output;
    }

    /**
     * Gets the item name.
     *
     * @return the item name
     */
    @Override
    public String getItemName() {
        if (!"".equals(relationName)) {
            return "Table: " + this.relationName;
        }

        return null;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    @Override
    public String getEntityName() {
        if (!"".equals(relationName) && !"".equals(alias)) {
            return this.relationName + " as " + this.alias;
        }
        return null;
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (!"".equals(filter)) {
            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_CSTORESCANNODE_FILTER))
                    .append(": ").append(filter);
            list.add(sb.toString());
        }

        if (!"".equals(rowsRemovedByFilter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(MessageConfigLoader
                    .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_CSTORESCANNODE_ROWREMOVEDFILTER)).append(": ")
                    .append(rowsRemovedByFilter);
            list.add(sb.toString());
        }

        return list;
    }
}
