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

import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class UnknownOperator.
 *
 * @since 3.0.0
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
