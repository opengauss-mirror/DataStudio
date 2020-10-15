/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class UnknownOperator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UnknownOperator extends OperationalNode {

    /**
     * Instantiates a new unknown operator.
     */
    public UnknownOperator() {
        super(NodeCategoryEnum.UNKNOWN);
    }

    @Override
    public String getNodeCategoryName() {
        String retName = this.getNodeType();
        if (null != retName) {
            String nodeNameFirstPart = retName.split(" ")[0];
            if ("Vector".equalsIgnoreCase(nodeNameFirstPart)) {
                retName = nodeType.substring(nodeNameFirstPart.length() + 1);
            }
        }

        return retName;
    }

}
