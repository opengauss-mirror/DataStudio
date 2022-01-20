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

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class CpuInDetail.
 *
 * @since 3.0.0
 */
public class CpuInDetail {
    @SerializedName("DN Name")
    private String dnName;

    /**
     * The exclusive cycles per row.
     */
    @SerializedName("Exclusive Cycles/Row")
    protected long exclusiveCyclesPerRow;

    /**
     * The exclusive cycles.
     */
    @SerializedName("Exclusive Cycles")
    protected long exclusiveCycles;

    /**
     * The inclusive cycles.
     */
    @SerializedName("Inclusive Cycles")
    protected long inclusiveCycles;

    /**
     * Property details.
     *
     * @return the list
     */
    public List<Object> propertyDetails() {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);

        colInfo.add(exclusiveCyclesPerRow);
        colInfo.add(exclusiveCycles);
        colInfo.add(inclusiveCycles);

        return colInfo;
    }

    /**
     * Fill column property header.
     *
     * @return the DN intra node details column
     */
    public static DNIntraNodeDetailsColumn fillColumnPropertyHeader() {
        DNIntraNodeDetailsColumn dnActualsInDetail = new DNIntraNodeDetailsColumn();

        dnActualsInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_CPUINDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(5);
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_CPU_EXCLUSIVECYCLESROW));
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_CPU_EXCLUSIVECYCLES));
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_BASICNODE_CPU_INCLUSIVECYCLES));

        dnActualsInDetail.setColCount(3);
        dnActualsInDetail.setColnames(colnames);

        return dnActualsInDetail;
    }

    /**
     * Gets the dn name.
     *
     * @return the dn name
     */
    public String getDnName() {
        return dnName;
    }
}
