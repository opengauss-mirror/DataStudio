/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 30-August-2019
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
