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

import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ActualInDetailUtility.
 *
 * @since 3.0.0
 */
public class ActualInDetailUtility {

    /**
     * Fill column property header.
     *
     * @return the DN intra node details column
     */
    public static DNIntraNodeDetailsColumn fillColumnPropertyHeader() {
        DNIntraNodeDetailsColumn dnActualsInDetail = new DNIntraNodeDetailsColumn();

        dnActualsInDetail.setGroupColumnName(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_ACTUALSINDETAIL_COLUMNGRP));

        ArrayList<String> colnames = new ArrayList<String>(5);
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_ACTUALS_COLUMN_STARTUPTIME));
        colnames.add(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_ACTUALS_COLUMN_TOTALTIME));
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_ACTUALS_COLUMN_ROWS));
        colnames.add(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_PERDN_ACTUALS_COLUMN_LOOPS));

        dnActualsInDetail.setColCount(4);
        dnActualsInDetail.setColnames(colnames);

        return dnActualsInDetail;
    }

}
