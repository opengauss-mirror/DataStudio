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

package org.opengauss.mppdbide.view.ui.table;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * Title: CreateTableFactory
 * 
 * Description:The class CreateTableFactory
 * 
 * @since 3.0.0
 */
public class CreateTableFactory {

    /**
     * Creates a new CreateTable object.
     *
     * @param type the type
     * @param ns the ns
     * @return the table meta data
     */
    public static TableMetaData createTable(OBJECTTYPE type, Namespace ns) {
        switch (type) {
            case PARTITION_TABLE: {
                return new PartitionTable(ns);
            }
            case TABLEMETADATA:
            default: {
                return new TableMetaData(ns);
            }
        }
    }

    /**
     * Creates a new CreateTable object.
     *
     * @param type the type
     * @param table the table
     * @param server the server
     * @return the index UI
     */
    public static IndexUI createIndexUI(OBJECTTYPE type, TableMetaData table, Server server) {

        switch (type) {
            case PARTITION_TABLE: {
                return new IndexUIPartitionTable(table, server);
            }
            case TABLEMETADATA:
            default: {
                return new IndexUI(table, server);
            }
        }

    }

}
