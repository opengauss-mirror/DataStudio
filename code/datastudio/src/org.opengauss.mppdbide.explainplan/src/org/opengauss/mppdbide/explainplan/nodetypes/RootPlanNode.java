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

package org.opengauss.mppdbide.explainplan.nodetypes;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class RootPlanNode.
 *
 * @since 3.0.0
 */
public class RootPlanNode extends OperationalNode {

    /**
     * The total run time.
     */
    @SerializedName("Total Runtime")
    protected double totalRunTime;

    /**
     * The triggers.
     */
    @SerializedName("Triggers")
    protected List<String> triggers;

    /**
     * Instantiates a new root plan node.
     */
    public RootPlanNode() {
        super(NodeCategoryEnum.UNKNOWN);
    }

    /**
     * Gets the total runtime.
     *
     * @return the total runtime
     */
    public double getTotalRuntime() {
        return totalRunTime;
    }

}
