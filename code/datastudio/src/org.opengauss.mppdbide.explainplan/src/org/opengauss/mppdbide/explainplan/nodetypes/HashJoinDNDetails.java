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
 * Description: The Class HashJoinDNDetails.
 *
 * @since 3.0.0
 */
public class HashJoinDNDetails {
    @SerializedName("DN Name")
    private String dnName;

    @SerializedName("Memory Used")
    private double memoryUsed;

    /**
     * Property details.
     *
     * @param childNodes the child nodes
     * @return the list
     */
    public List<Object> propertyDetails(ArrayList<OperationalNode> childNodes) {
        ArrayList<Object> colInfo = new ArrayList<Object>(5);
        colInfo.add(getMemoryUsed());
        ActualInDetail inner = (!childNodes.isEmpty()) ? childNodes.get(0).getActualsByDNName(getDnName()) : null;
        ActualInDetail outer = (childNodes.size() > 1) ? childNodes.get(1).getActualsByDNName(getDnName()) : null;

        colInfo.add((inner == null) ? 0 : inner.getActualRows());
        colInfo.add(outer == null ? 0 : outer.getActualRows());
        colInfo.add((inner == null) ? 0 : inner.getActualStartupTime());
        colInfo.add(outer == null ? 0 : outer.getActualStartupTime());

        colInfo.add((inner == null) ? 0 : inner.getActualTotalTime());
        colInfo.add(outer == null ? 0 : outer.getActualTotalTime());

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
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_JOININDETAIL));

        ArrayList<String> colnames = new ArrayList<String>(5);
        colnames.add("Memory Used");
        colnames.add("Inner node- Rows");
        colnames.add("Outer Node- Rows");
        colnames.add("Inner node- Startup Time");
        colnames.add("Outer Node- Startup Time");
        colnames.add("Inner node- Total Time");
        colnames.add("Outer Node- Total Time");

        dnActualsInDetail.setColCount(7);
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

    /**
     * Sets the dn name.
     *
     * @param dnName the new dn name
     */
    public void setDnName(String dnName) {
        this.dnName = dnName;
    }

    /**
     * Gets the memory used.
     *
     * @return the memory used
     */
    public double getMemoryUsed() {
        return memoryUsed;
    }

    /**
     * Sets the memory used.
     *
     * @param memoryUsed the new memory used
     */
    public void setMemoryUsed(double memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

}
