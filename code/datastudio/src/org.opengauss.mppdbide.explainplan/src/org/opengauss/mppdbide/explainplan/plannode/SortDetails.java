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

package org.opengauss.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortDetails.
 *
 * @since 3.0.0
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
