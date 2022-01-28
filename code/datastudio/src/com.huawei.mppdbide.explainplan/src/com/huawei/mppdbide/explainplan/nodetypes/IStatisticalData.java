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
import java.util.Map;

import com.huawei.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IStatisticalData.
 *
 * @since 3.0.0
 */
public interface IStatisticalData {

    /**
     * Gets the total cost.
     *
     * @return the total cost
     */
    double getTotalCost();

    /**
     * Gets the actual max time taken.
     *
     * @return the actual max time taken
     */
    double getActualMaxTimeTaken();

    /**
     * Gets the actual rows.
     *
     * @return the actual rows
     */
    long getActualRows();

    /**
     * Gets the actual total time.
     *
     * @return the actual total time
     */
    double getActualTotalTime();

    /**
     * Gets the node specific properties.
     *
     * @return the node specific properties
     */
    List<String[]> getNodeSpecificProperties();

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @param detailsNeeded the details needed
     * @return the per DN specific details
     */
    Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap,
            ArrayList<INDETAILSCATEGORY> detailsNeeded);

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @return the per DN specific details
     */
    Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap);

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroup the col group
     * @param detailsNeeded the details needed
     * @return the per DN specific column grouping info
     */
    List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup,
            ArrayList<INDETAILSCATEGORY> detailsNeeded);

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroup the col group
     * @return the per DN specific column grouping info
     */
    List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup);

}
