/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum EditTableCellState.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public enum EditTableCellState {
    // modified state is set for a cell when it has been modified commit it is
    // failed MODIFIED_FAILED is set for a cell after the commit is performed.
    // label accumulator MODIFIED State to make the cell green and
    // MODIFIED_FAILED is used for the make it red
    MODIFIED, MODIFIED_FAILED
}
