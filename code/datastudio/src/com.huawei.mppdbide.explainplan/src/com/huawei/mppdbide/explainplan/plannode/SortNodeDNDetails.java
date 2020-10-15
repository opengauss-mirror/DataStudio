/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortNodeDNDetails.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortNodeDNDetails {

    /**
     * The dn name.
     */
    @SerializedName("DN Name")
    protected String dnName;

    /**
     * The sort method.
     */
    @SerializedName("Sort Method")
    protected String sortMethod;

    /**
     * The sort space used.
     */
    @SerializedName("Sort Space Used")
    protected int sortSpaceUsed;

    /**
     * The sort space type.
     */
    @SerializedName("Sort Space Type")
    protected String sortSpaceType;
}
