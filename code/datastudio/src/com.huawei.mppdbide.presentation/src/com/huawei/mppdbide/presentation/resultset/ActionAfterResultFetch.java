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

package com.huawei.mppdbide.presentation.resultset;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ActionAfterResultFetch.
 * 
 * @since 3.0.0
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
