/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class RowAdapter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class RowAdapter extends OperationalNode {

    /**
     * Instantiates a new row adapter.
     */
    public RowAdapter() {
        super(NodeCategoryEnum.ROWADAPTER);
    }

    @Override
    public ArrayList<String> getDNInvolved(ArrayList<String> dnsInvolved) {
        return new ArrayList<String>(5);
    }

    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        return arList;
    }

}
