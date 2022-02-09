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

package org.opengauss.mppdbide.view.component.grid;

/**
 * Title: ITableGridStyleLabelFactory
 * 
 * Description:A factory for creating ITableGridStyleLabel objects.
 * 
 * @since 3.0.0
 */
public interface ITableGridStyleLabelFactory {

    /**
     * The col label date datatype.
     */
    String COL_LABEL_DATE_DATATYPE = "DATE_DATATYPE";

    /**
     * The col label time datatype.
     */
    String COL_LABEL_TIME_DATATYPE = "TIME_DATATYPE";

    /**
     * The col label time withtimezone.
     */
    String COL_LABEL_TIME_WITHTIMEZONE = "TIME_WITH_TIMEZONE";

    /**
     * The common grid date format.
     */
    String COMMON_GRID_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * The common grid time format.
     */
    String COMMON_GRID_TIME_FORMAT = "HH:mm:ss";

}
