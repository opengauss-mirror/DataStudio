/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface RowIndexIdentifier.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface RowIndexIdentifier {

    /**
     * Gets the row index.
     *
     * @param row the row
     * @return the row index
     */
    int getRowIndex(IDSGridDataRow row);
}
