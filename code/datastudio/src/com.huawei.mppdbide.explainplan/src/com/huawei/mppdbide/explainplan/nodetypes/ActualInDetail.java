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

package com.huawei.mppdbide.explainplan.nodetypes;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class ActualInDetail.
 *
 * @since 3.0.0
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
