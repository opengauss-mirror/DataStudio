/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.nodetypes;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class ActualInDetail.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ActualInDetail {
    @SerializedName("DN Name")
    private String dnName;

    @SerializedName("Actual Startup Time")
    private double actualStartupTime;

    @SerializedName("Actual Total Time")
    private double actualTotalTime;

    @SerializedName("Actual Rows")
    private long actualRows;

    @SerializedName("Actual Loops")
    private long actualLoops;

    /**
     * Gets the actual rows.
     *
     * @return the actual rows
     */
    public long getActualRows() {
        return actualRows;
    }

    /**
     * Gets the actual startup time.
     *
     * @return the actual startup time
     */
    public double getActualStartupTime() {
        return actualStartupTime;
    }

    /**
     * Gets the actual total time.
     *
     * @return the actual total time
     */
    public double getActualTotalTime() {
        return actualTotalTime;
    }

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }

    /**
     * Gets the actual loops.
     *
     * @return the actual loops
     */
    public long getActualLoops() {
        return actualLoops;
    }
}
