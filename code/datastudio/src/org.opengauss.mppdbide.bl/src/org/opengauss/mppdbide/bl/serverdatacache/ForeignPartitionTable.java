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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ForeignPartitionTable.
 * 
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
