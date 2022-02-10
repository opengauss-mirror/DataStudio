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

package org.opengauss.mppdbide.explainplan.ui.model;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface CompartmentUnitNames.
 *
 * @since 3.0.0
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
