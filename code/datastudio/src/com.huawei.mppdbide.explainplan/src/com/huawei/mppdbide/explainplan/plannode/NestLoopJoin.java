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
 * Description: The Class NestLoopJoin.
 *
 * @since 3.0.0
 */
public class NestLoopJoin extends OperationalNode {

    /**
     * The join type.
     */
    @SerializedName("Join Type")
    protected String joinType = "";

    /**
     * The join filter.
     */
    @SerializedName("Join Filter")
    protected String joinFilter = "";

    /**
     * The rows removed by join filter.
     */
    @SerializedName("Rows Removed by Join Filter")
    protected int rowsRemovedByJoinFilter;

    /**
     * Instantiates a new nest loop join.
     */
    public NestLoopJoin() {
        super(NodeCategoryEnum.NESTLOOPJOIN);
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

        if (!"".equals(joinFilter)) {
            StringBuilder sb = new StringBuilder("Join Filter : ");
            sb.append(joinFilter);
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

        ArrayList<String[]> elements = new ArrayList<String[]>(5);

        elements.addAll(moreInfo);

        elements.add(
                new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_JOIN_JOINTYPE),
                        joinType).getProp());
        elements.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NESTLOOP_JOIN_JOINFILTER),
                joinFilter).getProp());

        elements.add(new ServerProperty(
                MessageConfigLoader
                        .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NESTLOOP_JOIN_ROWSREMOVEDBYJOINFILTER),
                rowsRemovedByJoinFilter).getProp());
        return elements;
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

        Map<String, List<Object>> op = super.getPerDNSpecificDetails(inputMap, detailsNeeded);
        List<Object> propsValues = null;

        for (ActualInDetail entry : actualsInDetail) {
            propsValues = buildJoinNodePropertyDetails(entry.getDnName());
            if (!op.containsKey(entry.getDnName())) {
                op.put(entry.getDnName(), propsValues);
            } else {
                op.get(entry.getDnName()).addAll(propsValues);
            }
        }

        return op;
    }

    private List<Object> buildJoinNodePropertyDetails(String dnName) {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);
        ActualInDetail innerDetail = (!this.getChildren().isEmpty())
                ? this.getChildren().get(0).getActualsByDNName(dnName)
                : null;
        ActualInDetail outerDetail = (this.getChildren().size() > 1)
                ? this.getChildren().get(1).getActualsByDNName(dnName)
                : null;
        ActualInDetail hashNode = this.getActualsByDNName(dnName);

        colInfo.add((innerDetail == null) ? 0 : innerDetail.getActualRows());
        colInfo.add((outerDetail == null) ? 0 : outerDetail.getActualRows());
        colInfo.add((hashNode == null) ? 0 : hashNode.getActualRows());

        colInfo.add((innerDetail == null) ? 0 : innerDetail.getActualStartupTime());
        colInfo.add((outerDetail == null) ? 0 : outerDetail.getActualStartupTime());
        colInfo.add((hashNode == null) ? 0 : hashNode.getActualStartupTime());

        colInfo.add((innerDetail == null) ? 0 : innerDetail.getActualTotalTime());
        colInfo.add((outerDetail == null) ? 0 : outerDetail.getActualTotalTime());
        colInfo.add((hashNode == null) ? 0 : hashNode.getActualTotalTime());

        return colInfo;
    }

    private static List<DNIntraNodeDetailsColumn>
            buildHashNodePropertyColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        DNIntraNodeDetailsColumn dnActualsInDetail = new DNIntraNodeDetailsColumn();

        dnActualsInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_JOININDETAIL));

        ArrayList<String> clmnames = new ArrayList<String>(5);
        clmnames.add("Inner node- Rows");
        clmnames.add("Outer Node- Rows");
        clmnames.add("Join Output Rows");

        clmnames.add("Inner node- Startup Time");
        clmnames.add("Outer Node- Startup Time");
        clmnames.add("Join Node Startup Time");

        clmnames.add("Inner node- Total Time");
        clmnames.add("Outer Node- Total Time");
        clmnames.add("Join Node Total Time");

        dnActualsInDetail.setColCount(9);
        dnActualsInDetail.setColnames(clmnames);

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
        List<DNIntraNodeDetailsColumn> colGrp = colGroupParam;
        ArrayList<INDETAILSCATEGORY> detailsNeeded = new ArrayList<INDETAILSCATEGORY>(5);
        detailsNeeded.add(INDETAILSCATEGORY.CPU_IN_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.LLVM_DETAIL);
        colGrp = super.getPerDNSpecificColumnGroupingInfo(colGrp, detailsNeeded);

        return buildHashNodePropertyColumnGroupingInfo(colGrp);
    }
}
