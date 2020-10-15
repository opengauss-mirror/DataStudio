/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum EditTableRecordExecutionStatus.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public enum EditTableRecordExecutionStatus {
    // NOT_EXECUTED is set for a row when commit operation is not performed
    // SUCCESS is set when a row is successfully commited
    // FAILED is set when a commit for a row is failed
    // FAILED_AND_MODIFIED is used is set for a row when the row was failed and
    // again modified

    NOT_EXECUTED, SUCCESS, FAILED, FAILED_AND_MODIFIED, CONSUMED
}
