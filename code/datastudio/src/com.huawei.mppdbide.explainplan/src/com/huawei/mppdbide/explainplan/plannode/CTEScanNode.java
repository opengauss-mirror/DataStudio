/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class CTEScanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CTEScanNode extends OperationalNode {

    /**
     * The cte name.
     */
    @SerializedName("CTE Name")
    protected String cteName = "";

    /**
     * The alias.
     */
    @SerializedName("Alias")
    protected String alias = "";

    /**
     * Instantiates a new CTE scan node.
     */
    public CTEScanNode() {
        super(NodeCategoryEnum.SCAN);
    }

    @Override
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (!"".equals(cteName)) {
            sb.append("CTE Name: ").append(cteName);
            list.add(sb.toString());
        }

        if (!"".equals(alias)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Alias: ").append(alias);
            list.add(sb.toString());
        }

        return list;
    }

    @Override
    public List<String[]> getNodeSpecificProperties() {
        return super.getNodeSpecificProperties();
    }

    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.BUFFERS_IN_DETAIL,
                INDETAILSCATEGORY.CPU_IN_DETAIL, INDETAILSCATEGORY.ACTUALS_IN_DETAIL);
        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        return output;
    }

    @Override
    public String getItemName() {
        if (!"".equals(cteName)) {
            return "CTE: " + this.cteName;
        }

        return null;
    }
}
