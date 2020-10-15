/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortDetails.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortDetails {

    /**
     * The sort method.
     */
    @SerializedName("Sort Method")
    protected String sortMethod;

    /**
     * The dn name.
     */
    @SerializedName("DN Name")
    protected String dnName;

    /**
     * The sort key.
     */
    @SerializedName("Sort Key")
    protected List<String> sortKey;

    /**
     * The sort space used.
     */
    @SerializedName("Sort Space Used")
    protected String sortSpaceUsed;

    /**
     * Gets the sort details.
     *
     * @return the sort details
     */
    public List<String> getSortDetails() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(" Sort Method: ").append(sortMethod);
        list.add(sb.toString());
        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(" Sort Key: ");

        int size = sortKey.size();
        if (size > 0) {
            for (int index = 0; index < size - 1; index++) {
                sb.append(sortKey.get(index));
                sb.append(",");
            }

            sb.append(sortKey.get(sortKey.size() - 1));
        }
        list.add(sb.toString());
        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(" Sort Space Used: ").append(sortSpaceUsed);
        list.add(sb.toString());

        return list;
    }

}
