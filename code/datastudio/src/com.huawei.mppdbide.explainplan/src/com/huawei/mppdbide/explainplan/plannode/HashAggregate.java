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
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class HashAggregate.
 *
 * @since 3.0.0
 */
public class HashAggregate extends OperationalNode {

    /**
     * The group by key.
     */
    @SerializedName("Group By Key")
    protected List<String> groupByKey;

    /**
     * The aggregate details.
     */
    @SerializedName("Aggregate Detail")
    protected List<AggregateDetail> aggregateDetails;

    /**
     * The strategy.
     */
    @SerializedName("Strategy")
    protected String strategy = "";

    /**
     * The filter.
     */
    @SerializedName("Filter")
    protected String filter = "";

    @SerializedName("Rows Removed by Filter")
    private long rowsRemovedbyFilter;

    /**
     * Instantiates a new hash aggregate.
     */
    public HashAggregate() {
        super(NodeCategoryEnum.AGGREGATE);
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

        if (groupByKey != null && groupByKey.size() != 0) {
            StringBuilder sb = new StringBuilder("Group By Key: ");
            int index = 0;
            for (String s : this.groupByKey) {
                if (index != 0) {
                    sb.append(", ");
                }
                sb.append(s);
                index++;
            }
            list.add(sb.toString());
        }
        if (!"".equals(filter)) {
            StringBuilder sb = new StringBuilder("Filter: ");
            sb.append(filter);
            list.add(sb.toString());
        }
        if (rowsRemovedbyFilter != 0) {
            StringBuilder sb = new StringBuilder("Rows Removed by Filter: ");
            sb.append(rowsRemovedbyFilter);
            list.add(sb.toString());
        }
        if (maxFileNum != 0 || minFileNum != 0) {
            StringBuilder sb = new StringBuilder("Max File Num: ");
            sb.append(maxFileNum);
            sb.append("  Min File Num: ");
            sb.append(minFileNum);
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
        if (null != this.groupByKey) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_GROUP_BY_GRPBYKEYS),
                    CustomStringUtility.getFormatedOutput(groupByKey, ",")).getProp());
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
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.ACTUALS_IN_DETAIL,
                INDETAILSCATEGORY.CPU_IN_DETAIL);

        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        if (this.aggregateDetails != null) {
            List<Object> propsValues = null;
            for (AggregateDetail entry : aggregateDetails) {
                propsValues = buildHashAggreagateNodePropertyDetails(entry);
                if (!output.containsKey(entry.getDnName())) {
                    output.put(entry.getDnName(), propsValues);
                } else {
                    output.get(entry.getDnName()).addAll(propsValues);
                }
            }

        }

        return output;
    }

    private List<Object> buildHashAggreagateNodePropertyDetails(AggregateDetail element) {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);
        ActualInDetail outer = !this.getChildren().isEmpty()
                ? this.getChildren().get(0).getActualsByDNName(element.getDnName())
                : null;
        if (outer != null) {
            colInfo.add(outer.getActualRows());
        }
        colInfo.add(element.getTempFileNum());

        return colInfo;
    }

    private static List<DNIntraNodeDetailsColumn>
            buildHashAggregateNodePropertyColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        DNIntraNodeDetailsColumn dnActualsInDetail = new DNIntraNodeDetailsColumn();

        dnActualsInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_AGGREGATEINDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(5);
        colnames.add("Outer node- Rows");
        colnames.add("Temp File Num");
        dnActualsInDetail.setColCount(2);
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
        detailsNeeded.add(INDETAILSCATEGORY.ACTUALS_IN_DETAIL);

        colGroup = super.getPerDNSpecificColumnGroupingInfo(colGroup, detailsNeeded);
        if (this.aggregateDetails != null) {
            colGroup = buildHashAggregateNodePropertyColumnGroupingInfo(colGroup);
        }
        return colGroup;
    }

    /**
     * Gets the node type.
     *
     * @return the node type
     */
    @Override
    public String getNodeType() {
        if ("Hashed".equals(strategy)) {
            return "HashAggregate";
        } else if ("Sorted".equals(strategy)) {
            return "GroupAggregate";
        } else if ("Mixed".equals(strategy)) {
            return "MixedAggregate";
        }
        return super.getNodeType();
    }

}
