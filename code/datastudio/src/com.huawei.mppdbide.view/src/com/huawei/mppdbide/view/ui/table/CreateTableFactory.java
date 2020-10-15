/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * Title: CreateTableFactory
 * 
 * Description:The class CreateTableFactory
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author pWX759367
 * @version [DataStudio 6.5.1, 12-Jul-2019]
 * @since 12-Jul-2019
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
