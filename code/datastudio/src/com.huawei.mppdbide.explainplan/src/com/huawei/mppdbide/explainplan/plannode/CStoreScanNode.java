/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;

/**
 * 
 * Title: class
 * 
 * Description: The Class CStoreScanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
