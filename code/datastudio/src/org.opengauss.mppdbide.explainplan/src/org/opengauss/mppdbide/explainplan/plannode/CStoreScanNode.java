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

import org.opengauss.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;

/**
 * 
 * Title: class
 * 
 * Description: The Class CStoreScanNode.
 *
 * @since 3.0.0
 */
public class CStoreScanNode extends ScanNode {

    /**
     * Instantiates a new c store scan node.
     */
    public CStoreScanNode() {
        super();
    }

    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        return getNodeSpecific();
    }

    @Override
    public List<String> getNodeSpecific() {
        return super.getNodeSpecific();
    }

    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        addPropFilter(arList);
        addPropRowsRemovedByFilter(arList);

        return arList;
    }

    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = new ArrayList<INDETAILSCATEGORY>(5);
        detailsNeeded.add(INDETAILSCATEGORY.ACTUALS_IN_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.CPU_IN_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.LLVM_DETAIL);
        detailsNeeded.add(INDETAILSCATEGORY.BUFFERS_IN_DETAIL);

        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        return output;
    }

}
