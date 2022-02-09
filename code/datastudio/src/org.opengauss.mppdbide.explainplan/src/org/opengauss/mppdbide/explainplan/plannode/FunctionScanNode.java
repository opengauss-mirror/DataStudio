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

package org.opengauss.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import org.opengauss.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class FunctionScanNode.
 *
 * @since 3.0.0
 */
public class FunctionScanNode extends OperationalNode {

    /**
     * The function name.
     */
    @SerializedName("Function Name")
    protected String functionName = "";

    /**
     * The alias.
     */
    @SerializedName("Alias")
    protected String alias = "";

    /**
     * The function call.
     */
    @SerializedName("Function Call")
    protected String functionCall = "";

    /**
     * The schema.
     */
    @SerializedName("Schema")
    protected String schema = "";

    /**
     * Instantiates a new function scan node.
     */
    public FunctionScanNode() {
        super(NodeCategoryEnum.FUNCTION);
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

        sb.append("Function Name: ").append(functionName);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Alias: ").append(alias);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Schema: ").append(schema);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Function Call: ").append(functionCall);
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

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_FUNCTIONSCANNODE_FUNCTIONNAME),
                functionName).getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_FUNCTIONSCANNODE_FUNCTIONALIASNAME),
                alias).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_FUNCTIONSCANNODE_SCHEMANAME),
                schema).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_FUNCTIONSCANNODE_FUNCTIONCALL),
                functionCall).getProp());

        return arList;
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
                INDETAILSCATEGORY.CPU_IN_DETAIL);
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
        return "Function: " + this.functionName;
    }
}
