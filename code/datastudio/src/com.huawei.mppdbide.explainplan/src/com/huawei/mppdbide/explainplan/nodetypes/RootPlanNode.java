/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.nodetypes;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class RootPlanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
