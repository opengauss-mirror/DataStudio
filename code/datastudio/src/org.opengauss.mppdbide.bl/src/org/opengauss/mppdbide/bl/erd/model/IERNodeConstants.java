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

package org.opengauss.mppdbide.bl.erd.model;

/**
 * Title: ERNodeConstants
 * 
 */

public interface IERNodeConstants {

    /**
     * The primary key.
     */
    String PRIMARY_KEY = "Primary key";

    /**
     * The unique key.
     */
    String UNIQUE_KEY = "Unique key";

    /**
     * The foreign key.
     */
    String FOREIGN_KEY = "Foreign key";

    /**
     * the Graph node property LABEL.
     */
    String LABEL = "element-label";

    /**
     * the Graph node property NODE_PROPERTY.
     */
    String NODE_PROPERTY = "node-property";

    /**
     * The owner name.
     */
    String OWNER = "OWNER";

    /**
     * The table name.
     */
    String TABLE_NAME = "TABLE_NAME";

    /**
     * The constraint name.
     */
    String CONSTRAINT_NAME = "CONSTRAINT_NAME";

    /**
     * The constraint type.
     */
    String CONSTRAINT_TYPE = "CONSTRAINT_TYPE";

    /**
     * the Foreign key constraint's referenced owner name.
     */
    String R_OWNER = "R_OWNER";

    /**
     * The r table name.
     */
    String R_TABLE_NAME = "R_TABLE_NAME";

    /**
     * the Foreign key constraint's referenced constraint name.
     */
    String R_CONSTRAINT_NAME = "R_CONSTRAINT_NAME";

    /**
     * The col list.
     */
    String COL_LIST = "COL_LIST";

    /**
     * the column name.
     */
    String COLUMN_NAME = "COLUMN_NAME";

    /**
     * The comments.
     */
    String COMMENTS = "COMMENTS";

    /**
     * The CHAR_USED
     */
    String CHAR_USED = "CHAR_USED";

    /**
     * The constraint type.
     */
    String OLAP_CONSTRAINT_TYPE = "constrainttype";

    /**
     * The column list.
     */
    String OLAP_COLUMN_LIST = "columnlist";

    /**
     * The attribute name.
     */
    String OLAP_ATTRIBUTE_NAME = "attname";

    /**
     * The description.
     */
    String OLAP_DESCRIPTION = "description";

}
