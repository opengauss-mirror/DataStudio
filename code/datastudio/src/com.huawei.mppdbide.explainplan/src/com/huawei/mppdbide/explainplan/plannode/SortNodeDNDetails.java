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

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortNodeDNDetails.
 *
 * @since 3.0.0
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
