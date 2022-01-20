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

package com.huawei.mppdbide.view.component.grid;

/**
 * Title: IEditTableGridStyleLabelFactory
 * 
 * Description:A factory for creating IEditTableGridStyleLabel objects.
 * 
 * @since 3.0.0
 */
public interface IEditTableGridStyleLabelFactory {

    /**
     * The drop down.
     */
    int DROP_DOWN = 2;

    /**
     * The date short.
     */
    int DATE_SHORT = 1048576;

    /**
     * The canonical limit.
     */
    int CANONICAL_LIMIT = 1000;

    /**
     * The col label insert.
     */
    String COL_LABEL_INSERT = "INSERT";

    /**
     * The col label update.
     */
    String COL_LABEL_UPDATE = "UPDATE";

    /**
     * The col label delete.
     */
    String COL_LABEL_DELETE = "DELTEE";

    /**
     * The col label change success.
     */
    String COL_LABEL_CHANGE_SUCCESS = "EDIT_SUCCESS";

    /**
     * The col label change failed.
     */
    String COL_LABEL_CHANGE_FAILED = "EDIT_FAILED";

    /**
     * The col label boolean datatype.labels for the datatype configuration
     */
    String COL_LABEL_BOOLEAN_DATATYPE = "BOOLEAN_DATATYPE";

    /**
     * The col label date time datatype.
     */
    String COL_LABEL_DATE_TIME_DATATYPE = "DATE_TIME_DATATYPE";

    /**
     * The check box config label.
     */
    String CHECK_BOX_CONFIG_LABEL = "CHECK_BOX_CONFIG_LABEL";

    /**
     * The date data type.
     */
    String DATE_DATA_TYPE = "DATE_DATA_TYPE";

    /**
     * The drop down list data type.
     */
    String DROP_DOWN_LIST_DATA_TYPE = "DROP_DOWN_LIST_DATA_TYPE";

    /**
     * The drop down list on delete.
     */
    String DROP_DOWN_LIST_ON_DELETE = "DROP_DOWN_LIST_ON_DELETE";

    /**
     * The combo box data type.
     */
    String COMBO_BOX_DATA_TYPE = "COMBO_BOX_DATA_TYPE";

    /**
     * The col label readonly cell.
     */
    String COL_LABEL_READONLY_CELL = "DISTRIBUTED_COLUMN";

    /**
     * The col label copy readonly cell.
     */
    String COL_LABEL_COPY_READONLY_CELL = "PARTIAL_COPY_CELL";

    /**
     * The col header label readonly cell.
     */
    String COL_HEADER_LABEL_READONLY_CELL = "READ_ONLY_HEADER";

    /**
     * The col label edit inserted distributable column.
     */
    String COL_LABEL_EDIT_INSERTED_DISTRIBUTABLE_COLUMN = "EDIT_DISTRIBUTABLE_COLUMN";

    /**
     * The col label null values.
     */
    String COL_LABEL_NULL_VALUES = "NULL_VALUES";

    /**
     * The col label failed and modified.
     */
    String COL_LABEL_FAILED_AND_MODIFIED = "FAILED_AND_MODIFIED";

    /**
     * The col label custom dialog.
     */
    String COL_LABEL_CUSTOM_DIALOG = "CUSTOM_DIALOG";

    /**
     * The col label custom dialog referencing table.
     */
    String COL_LABEL_CUSTOM_DIALOG_REFERENCING_TABLE = "CUSTOM_DIALOG_REFERENCING_TABLE";

    /**
     * The col label custom clms dialog.
     */
    String COL_LABEL_CUSTOM_CLMS_DIALOG = "COL_LABEL_CUSTOM_CLMS_DIALOG";

    /**
     * The col label not supported multidialog.
     */
    String COL_LABEL_NOT_SUPPORTED_MULTIDIALOG = "MULTIDIALOG_NOT_SUPPORTED";

    /**
     * The drop down list part type.
     */
    String DROP_DOWN_LIST_PART_TYPE = "DROP_DOWN_LIST_PART_TYPE";

    /**
     * The col label blob type cell.
     */
    String COL_LABEL_BLOB_TYPE_CELL = "COL_LABEL_BLOB_TYPE_CELL";

    /**
     * The col label cursor type cell.
     */
    String COL_LABEL_CURSOR_TYPE_CELL = "COL_LABEL_CURSOR_TYPE_CELL";
    
    /**
     * The col label out parameter type cell.
     */
    String COL_LABEL_OUT_PARA_TYPE_CELL = "COL_LABEL_OUT_PARA_TYPE_CELL";

    /**
     * The col label bytea type cell.
     */
    String COL_LABEL_BYTEA_TYPE_CELL = "COL_LABEL_BYTEA_TYPE_CELL";
}
