/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.ui.model;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface CompartmentUnitNames.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface CompartmentUnitNames {

    /**
     * The node header.
     */
    int NODE_HEADER = 0;

    /**
     * The output details.
     */
    int OUTPUT_DETAILS = 1;

    /**
     * The node specific details.
     */
    int NODE_SPECIFIC_DETAILS = 2;

    /**
     * The heavy node analysis.
     */
    int HEAVY_NODE_ANALYSIS = 3;

    /**
     * The costly node analysis.
     */
    int COSTLY_NODE_ANALYSIS = 4;

    /**
     * The slow node analysis.
     */
    int SLOW_NODE_ANALYSIS = 5;

    /**
     * The max compartment unit.keep changing this number evevry time a new is
     * added.
     */
    int MAX_COMPARTMENT_UNIT = 6;

}
