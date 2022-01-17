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

package com.huawei.mppdbide.view.configregistory;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface DSConfigurationColumnsList.
 *
 * @since 3.0.0
 */
public interface DSConfigurationColumnsList {

    /**
     * The Constant CHECKTAB_ENABLED.
     */
    public static final String CHECKTAB_ENABLED = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_CHECKTAB_ENABLED);

    /**
     * The Constant CHECKTAB_DEFERRABLE.
     */
    public static final String CHECKTAB_DEFERRABLE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_CHECKTAB_DEFERRABLE);

    /**
     * The Constant CHECKTAB_DEFERRED.
     */
    public static final String CHECKTAB_DEFERRED = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_CHECKTAB_DEFERRED);

    /**
     * The Constant CHECKTAB_VALIDATED.
     */
    public static final String CHECKTAB_VALIDATED = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_CHECKTAB_VALIDATED);

    /**
     * The Constant CHECKTAB_CONDITION.
     */
    public static final String CHECKTAB_CONDITION = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_CHECKTAB_CONDITION);

    /**
     * The Constant COLUMNTABIS_NULLABLE.
     */
    public static final String COLUMNTABIS_NULLABLE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_COLUMNTAB_ISNULLABLE);

    /**
     * The Constant COLUMNTAB_DATATYPE.
     */
    public static final String COLUMNTAB_DATATYPE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_COLUMNTAB_DATATYPE);

    /**
     * The Constant COLUMNTAB_IS_DEFAULT_EXPRESSION.
     */
    public static final String COLUMNTAB_IS_DEFAULT_EXPRESSION = MessageConfigLoader
            .getProperty(IMessagesConstants.COLUMN_DEFAULT_VALUE_EXPRESSION);

    /**
     * The Constant GEN_VALUE.
     */
    public static final String GEN_VALUE = MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_VALUE);

    /**
     * The Constant KEYTAB_DEFERRABLE.
     */
    public static final String KEYTAB_DEFERRABLE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_DEFERRABLE);

    /**
     * The Constant KEYTAB_DEFERRED.
     */
    public static final String KEYTAB_DEFERRED = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_DEFERRED);

    /**
     * The Constant KEYTAB_VALIDATED.
     */
    public static final String KEYTAB_VALIDATED = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_VALIDATED);

    /**
     * The Constant KEYTAB_TYPE.
     */
    public static final String KEYTAB_TYPE = MessageConfigLoader.getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_TYPE);

    /**
     * The Constant KEYTAB_REFERENCING_TABLE.
     */
    public static final String KEYTAB_REFERENCING_TABLE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_REFERENCINGTABLE);

    /**
     * The Constant KEYTAB_REFERENCING_CLM.
     */
    public static final String KEYTAB_REFERENCING_CLM = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_REFERENCINGCOL);

    /**
     * The Constant KEYTAB_ON_DELETE.
     */
    public static final String KEYTAB_ON_DELETE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_ONDELETE);

    /**
     * The Constant KEYTAB_COLUMNS.
     */
    public static final String KEYTAB_COLUMNS = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_KEYTAB_COLUMNS);

    /**
     * The Constant PARTITION_VALUES.
     */
    public static final String PARTITION_VALUES = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_PARTITIONTAB_PARTITIONVALUE);

    /**
     * The Constant PARTITION_TABLESPACE.
     */
    public static final String PARTITION_TABLESPACE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_PARTITIONTAB_TABLESPACENAME);

    /**
     * The Constant PARTITION_TYPE.
     */
    public static final String PARTITION_TYPE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_PARTITIONTAB_TYPE);

    /**
     * The Constant PARTITION_KEYS.
     */
    public static final String PARTITION_KEYS = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_PARTITIONTAB_KEYS);

    /**
     * The Constant INDEXIS_UNIQUE.
     */
    public static final String INDEXIS_UNIQUE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_INDEXTAB_ISUNIQUE);

    /**
     * The Constant INDEX_COLUMNS.
     */
    public static final String INDEX_COLUMNS = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_INDEXTAB_COLUMNS);

    /**
     * The Constant INDEX_TABLESPACE.
     */
    public static final String INDEX_TABLESPACE = MessageConfigLoader
            .getProperty(IMessagesConstants.OLAP_PROP_INDEXTAB_TABLESPACE);

}
