/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
