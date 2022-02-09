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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetopDetail.
 *
 * @since 3.0.0
 */
public class SetopDetail {
    @SerializedName("DN Name")
    private String dnName;

    @SerializedName("Temp File Num")
    private long tempFileNum;

    /**
     * Property details.
     *
     * @return the list
     */
    public List<Object> propertyDetails() {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);

        colInfo.add(getTempFileNum());

        return colInfo;
    }

    /**
     * Fill column property header.
     *
     * @return the DN intra node details column
     */
    public static DNIntraNodeDetailsColumn fillColumnPropertyHeader() {
        DNIntraNodeDetailsColumn dnBufferInDetail = new DNIntraNodeDetailsColumn();

        dnBufferInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_SETOPDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(1);
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SETOPNODE_TEMPFILENUM));

        dnBufferInDetail.setColCount(1);
        dnBufferInDetail.setColnames(colnames);

        return dnBufferInDetail;
    }

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }

    private long getTempFileNum() {
        return tempFileNum;
    }
}
