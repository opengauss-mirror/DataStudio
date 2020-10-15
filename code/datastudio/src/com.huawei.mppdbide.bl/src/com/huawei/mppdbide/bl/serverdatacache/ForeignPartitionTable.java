/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ForeignPartitionTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ForeignPartitionTable extends PartitionTable {

    /**
     * Instantiates a new foreign partition table.
     *
     * @param ns the ns
     */
    public ForeignPartitionTable(Namespace ns) {
        super(ns, OBJECTTYPE.FOREIGN_PARTITION_TABLE);
    }

    /**
     * Convert to foreign partition table.
     *
     * @param rs the rs
     * @param ns the ns
     * @return the partition table
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException the SQL exception
     */
    public static PartitionTable convertToForeignPartitionTable(ResultSet rs, Namespace ns)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        PartitionTable ptab = new ForeignPartitionTable(ns);
        ptab.fillTablePropertiesFromRS(rs);
        ptab.setPartKey(rs.getString("partkey"));

        ns.addTableToGivenSearchPool(ptab);

        return ptab;
    }

    @Override
    public boolean isDropAllowed() {
        return false;
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        return false;
    }

}
