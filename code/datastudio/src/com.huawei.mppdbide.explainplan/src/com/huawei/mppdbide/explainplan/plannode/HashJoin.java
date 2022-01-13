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
import com.huawei.mppdbide.explainplan.nodetypes.HashJoinDNDetails;
import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class HashJoin.
 *
 * @since 3.0.0
 */
public class HashJoin extends OperationalNode {

    /**
     * The hash condition.
     */
    @SerializedName("Hash Cond")
    protected String hashCondition = "";

    /**
     * The join type.
     */
    @SerializedName("Join Type")
    protected String joinType = "";

    /**
     * The join D ndetails.
     */
    @SerializedName("VecJoin Detail")
    protected List<HashJoinDNDetails> joinDNdetails;

    /**
     * The join filter.
     */
    @SerializedName("Join Filter")
    protected String joinFilter = "";

    /**
     * The rows removed by join filter.
     */
    @SerializedName("Rows Removed by Join Filter")
    protected String rowsRemovedByJoinFilter = "";

    /**
     * Instantiates a new hash join.
     */
    public HashJoin() {
        super(NodeCategoryEnum.HASHJOIN);
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        List<String> addi = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Hash Cond: ");
        sb.append(hashCondition);
        addi.add(sb.toString());

        if (!"".equals(joinFilter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Join Filter: ");
            sb.append(joinFilter);
            addi.add(sb.toString());
        }

        if (!"".equals(rowsRemovedByJoinFilter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Rows Removed by Join Filter: ");
            sb.append(rowsRemovedByJoinFilter);
            addi.add(sb.toString());
        }
        return addi;
    }

    /**
     * To text.
     *
     * @param isAnalyze the is analyze
     * @return the string
     */
    @Override
    public String toText(boolean isAnalyze) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getNodeName());
        sb.append(" ");
        sb.append(getCostInfoForTextDisplay(isAnalyze));
        return sb.toString();
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    @Override
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        list.add(this.hashCondition);
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

        arList.add(
                new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_JOIN_JOINTYPE),
                        joinType).getProp());

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_HASH_JOIN_JOINCOND), hashCondition)
                        .getProp());
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
                INDETAILSCATEGORY.CPU_IN_DETAIL, INDETAILSCATEGORY.BUFFERS_IN_DETAIL, INDETAILSCATEGORY.LLVM_DETAIL);

        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        if (this.joinDNdetails != null && this.joinDNdetails.size() > 0) {
            List<Object> propsValues = null;
            for (HashJoinDNDetails entry : joinDNdetails) {
                propsValues = buildhashNodePropertyDetails(entry);
                if (!output.containsKey(entry.getDnName())) {
                    output.put(entry.getDnName(), propsValues);
                } else {
                    output.get(entry.getDnName()).addAll(propsValues);
                }
            }

        }

        return output;
    }

    private List<Object> buildhashNodePropertyDetails(HashJoinDNDetails hash) {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);
        String dnName = hash.getDnName();
        ActualInDetail innerActInDetail = this.getChildren().size() >= 2
                ? this.getChildren().get(1).getActualsByDNName(dnName)
                : null;
        ActualInDetail outerActInDetail = this.getChildren().size() >= 1
                ? this.getChildren().get(0).getActualsByDNName(dnName)
                : null;
        ActualInDetail hashNodeInDetail = this.getActualsByDNName(dnName);

        colInfo.add((innerActInDetail == null) ? 0 : innerActInDetail.getActualRows());
        colInfo.add((outerActInDetail == null) ? 0 : outerActInDetail.getActualRows());
        colInfo.add((hashNodeInDetail == null) ? 0 : hashNodeInDetail.getActualRows());
        colInfo.add((innerActInDetail == null) ? 0 : innerActInDetail.getActualStartupTime());
        colInfo.add((outerActInDetail == null) ? 0 : outerActInDetail.getActualStartupTime());
        colInfo.add((hashNodeInDetail == null) ? 0 : hashNodeInDetail.getActualStartupTime());

        colInfo.add((innerActInDetail == null) ? 0 : innerActInDetail.getActualTotalTime());
        colInfo.add((outerActInDetail == null) ? 0 : outerActInDetail.getActualTotalTime());
        colInfo.add((hashNodeInDetail == null) ? 0 : hashNodeInDetail.getActualTotalTime());

        colInfo.add(hash.getMemoryUsed());
        return colInfo;
    }

    private static List<DNIntraNodeDetailsColumn>
            buildHashNodePropertyColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        DNIntraNodeDetailsColumn dnActualsInDetail = new DNIntraNodeDetailsColumn();

        dnActualsInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_JOININDETAIL));

        ArrayList<String> colnameList = new ArrayList<String>(5);
        colnameList.add("Inner node- Rows");
        colnameList.add("Outer Node- Rows");
        colnameList.add("Join Output Rows");

        colnameList.add("Inner node- Startup Time");
        colnameList.add("Outer Node- Startup Time");
        colnameList.add("Join Node Startup Time");

        colnameList.add("Inner node- Total Time");
        colnameList.add("Outer Node- Total Time");
        colnameList.add("Join Node Total Time");

        colnameList.add("Join Node Memory Used");
        dnActualsInDetail.setColCount(10);
        dnActualsInDetail.setColnames(colnameList);

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
        detailsNeeded.add(INDETAILSCATEGORY.ACTUALS_IN_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.CPU_IN_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.LLVM_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.BUFFERS_IN_DETAIL);

        colGroup = super.getPerDNSpecificColumnGroupingInfo(colGroup, detailsNeeded);

        if (this.joinDNdetails != null && this.joinDNdetails.size() != 0) {
            return buildHashNodePropertyColumnGroupingInfo(colGroup);
        } else {
            return colGroup;
        }

    }

    /**
     * Gets the node name.
     *
     * @return the node name
     */
    @Override
    public String getNodeName() {
        StringBuilder sb = new StringBuilder("Hash ");
        // do not include if "Inner"
        if ("Right".equals(joinType) || "Semi".equals(joinType) || "Anti".equals(joinType)) {
            sb.append(joinType);
        }
        sb.append(" Join");
        return sb.toString();
    }

}
