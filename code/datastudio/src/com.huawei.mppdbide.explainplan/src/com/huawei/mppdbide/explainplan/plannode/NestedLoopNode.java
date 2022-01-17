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
import com.huawei.mppdbide.explainplan.nodetypes.ActualInDetail;
import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class NestedLoopNode.
 *
 * @since 3.0.0
 */
public class NestedLoopNode extends OperationalNode {

    /**
     * The join type.
     */
    @SerializedName("Join Type")
    protected String joinType = "";

    /**
     * Instantiates a new nested loop node.
     */
    public NestedLoopNode() {
        super(NodeCategoryEnum.NESTEDLOOP);
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

        arList.add(
                new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_JOIN_JOINTYPE),
                        joinType).getProp());

        if (!"".equals(subPlan)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NODE_SUBPLANNAME), subPlan)
                            .getProp());
        }

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
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.CPU_IN_DETAIL,
                INDETAILSCATEGORY.LLVM_DETAIL);

        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);
        List<Object> propsValues = null;

        for (ActualInDetail index : actualsInDetail) {
            propsValues = buildJoinNodePropertyDetails(index.getDnName());
            if (!output.containsKey(index.getDnName())) {
                output.put(index.getDnName(), propsValues);
            } else {
                output.get(index.getDnName()).addAll(propsValues);
            }
        }

        return output;
    }

    private List<Object> buildJoinNodePropertyDetails(String dnName) {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);
        ActualInDetail inner = (!this.getChildren().isEmpty()) ? this.getChildren().get(0).getActualsByDNName(dnName)
                : null;
        ActualInDetail outer = (this.getChildren().size() > 1) ? this.getChildren().get(1).getActualsByDNName(dnName)
                : null;
        ActualInDetail hashNode = this.getActualsByDNName(dnName);

        colInfo.add((inner == null) ? 0 : inner.getActualRows());
        colInfo.add((outer == null) ? 0 : outer.getActualRows());
        colInfo.add((hashNode == null) ? 0 : hashNode.getActualRows());

        colInfo.add((inner == null) ? 0 : inner.getActualStartupTime());
        colInfo.add((outer == null) ? 0 : outer.getActualStartupTime());
        colInfo.add((hashNode == null) ? 0 : hashNode.getActualStartupTime());

        colInfo.add((inner == null) ? 0 : inner.getActualTotalTime());
        colInfo.add((outer == null) ? 0 : outer.getActualTotalTime());
        colInfo.add((hashNode == null) ? 0 : hashNode.getActualTotalTime());
        return colInfo;
    }

    private static List<DNIntraNodeDetailsColumn>
            buildHashNodePropertyColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        DNIntraNodeDetailsColumn dnActualsInDetail = new DNIntraNodeDetailsColumn();

        dnActualsInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_JOININDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(5);
        colnames.add("Inner node- Rows");
        colnames.add("Outer Node- Rows");
        colnames.add("Join Output Rows");

        colnames.add("Inner node- Startup Time");
        colnames.add("Outer Node- Startup Time");
        colnames.add("Join Node Startup Time");

        colnames.add("Inner node- Total Time");
        colnames.add("Outer Node- Total Time");
        colnames.add("Join Node Total Time");

        dnActualsInDetail.setColCount(9);
        dnActualsInDetail.setColnames(colnames);

        colGroup.add(dnActualsInDetail);

        return colGroup;

    }

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroupParam the col group param
     * @return the per DN specific column grouping info
     */
    @Override
    public List<DNIntraNodeDetailsColumn>
            getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroupParam) {

        List<DNIntraNodeDetailsColumn> colGroup = colGroupParam;
        ArrayList<INDETAILSCATEGORY> detailsNeeded = new ArrayList<INDETAILSCATEGORY>(5);
        detailsNeeded.add(INDETAILSCATEGORY.CPU_IN_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.LLVM_DETAIL);
        colGroup = super.getPerDNSpecificColumnGroupingInfo(colGroup, detailsNeeded);

        return buildHashNodePropertyColumnGroupingInfo(colGroup);
    }
}
