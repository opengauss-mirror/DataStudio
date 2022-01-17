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

package com.huawei.mppdbide.view.handler.importexporttabledata;

import java.util.ArrayList;
import java.util.Iterator;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportTableData.
 *
 * @since 3.0.0
 */
public class ImportExportTableData {

    /**
     * Gets the coloumns.
     *
     * @param columns the columns
     * @return the coloumns
     */
    protected static ArrayList<String> getColoumns(OLAPObjectList<ColumnMetaData> columns) {
        ArrayList<String> colList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        Iterator<ColumnMetaData> itr = null;
        ColumnMetaData column = null;
        boolean hasMore = false;
        itr = columns.getList().iterator();
        hasMore = itr.hasNext();

        while (hasMore) {
            column = itr.next();
            colList.add(column.getName());
            hasMore = itr.hasNext();
        }
        return colList;
    }
}
