/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

/**
 * Title: ITableGridStyleLabelFactory
 * 
 * Description:A factory for creating ITableGridStyleLabel objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
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
