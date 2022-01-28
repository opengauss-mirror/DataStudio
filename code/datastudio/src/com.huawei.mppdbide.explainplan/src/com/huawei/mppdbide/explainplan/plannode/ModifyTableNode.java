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
 * Description: The Class ModifyTableNode.
 *
 * @since 3.0.0
 */
public class ModifyTableNode extends OperationalNode {

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
     * The operation.
     */
    @SerializedName("Operation")
    protected String operation = "";

    /**
     * The schema.
     */
    @SerializedName("Schema")
    protected String schema = "";

    /**
     * The remote query.
     */
    @SerializedName("Remote query")
    protected String remoteQuery = "";

    /**
     * Instantiates a new modify table node.
     */
    public ModifyTableNode() {
        super(NodeCategoryEnum.MODIFYTABLE);
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    @Override
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        sb.append("Operation: ").append(operation);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Relation Name: ").append(relationName);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Alias: ").append(alias);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Schema: ").append(schema);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Remote Query: ").append(StringEscapeUtils.escapeHtml(remoteQuery));
        list.add(sb.toString());

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

        ArrayList<String[]> index = new ArrayList<String[]>(5);

        index.addAll(moreInfo);

        index.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_MODIFYTABLE_OPERATION), operation)
                        .getProp());
        index.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SCANTABLENAME),
                relationName).getProp());

        index.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SCANTABLEALIASNAME), alias)
                        .getProp());

        index.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SCANNODE_SCHEMANAME), schema)
                        .getProp());

        index.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NODE_REMOTEQUERY),
                StringEscapeUtils.escapeHtml(remoteQuery)).getProp());

        return index;
    }

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @return the per DN specific details
     */
    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.CPU_IN_DETAIL,
                INDETAILSCATEGORY.ACTUALS_IN_DETAIL, INDETAILSCATEGORY.BUFFERS_IN_DETAIL);
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
        return "Table: " + this.relationName;
    }
}
