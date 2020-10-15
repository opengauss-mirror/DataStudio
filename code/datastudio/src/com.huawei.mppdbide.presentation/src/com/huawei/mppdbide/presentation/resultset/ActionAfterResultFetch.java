/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.resultset;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ActionAfterResultFetch.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public enum ActionAfterResultFetch {
    /* Action indicates the connection be closed after result is fetched */
    CLOSE_CONNECTION_AFTER_FETCH,
    /* Indices to keep the connection open rather just commit */
    ISSUE_COMMIT_CONNECTION_AFTER_FETCH,
    /* Indices to keep the connection open rather just rollback */
    ISSUE_ROLLBACK_CONNECTION_AFTER_FETCH,
    /* Do nothing and continue with the next statement */
    ISSUE_NO_OP
}
