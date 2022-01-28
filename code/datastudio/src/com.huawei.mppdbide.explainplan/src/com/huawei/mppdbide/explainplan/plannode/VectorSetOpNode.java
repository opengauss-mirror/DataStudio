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

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.explainplan.nodetypes.SetopDetail;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class VectorSetOpNode.
 *
 * @since 3.0.0
 */
public class VectorSetOpNode extends OperationalNode {

    /**
     * The strategy.
     */
    @SerializedName("Strategy")
    protected String strategy = "";

    /**
     * The command.
     */
    @SerializedName("Command")
    protected String command = "";

    /**
     * The setop details.
     */
    @SerializedName("Setop Detail")
    protected List<SetopDetail> setopDetails;

    /**
     * Instantiates a new vector set op node.
     */
    public VectorSetOpNode() {
        super(NodeCategoryEnum.SETOP);
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

        sb.append("Strategy: ").append(strategy);
        list.add(sb.toString());

        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Command: ").append(command);
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

        ArrayList<String[]> nodes = new ArrayList<String[]>(5);

        nodes.addAll(moreInfo);

        nodes.add(
                new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SETOP_STRATEGY),
                        strategy).getProp());
        nodes.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SETOP_COMMAND),
                command).getProp());

        return nodes;
    }

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroup the col group
     * @return the per DN specific column grouping info
     */
    @Override
    public List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        List<DNIntraNodeDetailsColumn> outputColGroup = super.getPerDNSpecificColumnGroupingInfo(colGroup);

        outputColGroup.add(SetopDetail.fillColumnPropertyHeader());

        return outputColGroup;
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

        getSetOpDetail(output);
        return output;
    }

    private void getSetOpDetail(Map<String, List<Object>> inputMap) {
        if (null != setopDetails) {
            for (SetopDetail element : setopDetails) {
                if (!inputMap.containsKey(element.getDnName())) {
                    List<Object> propsValues = element.propertyDetails();
                    inputMap.put(element.getDnName(), propsValues);
                } else {
                    inputMap.get(element.getDnName()).addAll(element.propertyDetails());
                }
            }
        }

    }
}
