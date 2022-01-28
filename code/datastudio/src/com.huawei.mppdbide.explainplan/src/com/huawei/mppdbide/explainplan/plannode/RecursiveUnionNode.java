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
 * Description: The Class RecursiveUnionNode.
 *
 * @since 3.0.0
 */
public class RecursiveUnionNode extends OperationalNode {

    /**
     * Instantiates a new recursive union node.
     */
    public RecursiveUnionNode() {
        super(NodeCategoryEnum.UNION);
    }

    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        return getNodeSpecific();
    }

    @Override
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = null;

        if (!"".equals(subPlan)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Subplan Name: ").append(subPlan);
            list.add(sb.toString());
        }

        return list;
    }

    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        if (!"".equals(subPlan)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NODE_SUBPLANNAME), subPlan)
                            .getProp());
        }

        return arList;
    }

    @Override
    public String getItemName() {
        return null;
    }

    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.BUFFERS_IN_DETAIL,
                INDETAILSCATEGORY.CPU_IN_DETAIL, INDETAILSCATEGORY.ACTUALS_IN_DETAIL);
        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        return output;
    }
}
