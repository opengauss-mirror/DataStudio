/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class AggregateDetail.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AggregateDetail {
    
    /**
     * The dn Name
     */
    @SerializedName("DN Name")
    protected String dnName;
    
    /**
     * The tempFile Num
     */
    @SerializedName("Temp File Num")
    protected int tempFileNum;

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }

    /**
     * Gets the temp file num.
     *
     * @return the temp file num
     */
    public int getTempFileNum() {
        return tempFileNum;
    }
}
