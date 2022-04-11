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

package org.opengauss.mppdbide.presentation.edittabledata;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum EditTableRecordExecutionStatus.
 * 
 * @since 3.0.0
 */
public enum EditTableRecordExecutionStatus {
    // NOT_EXECUTED is set for a row when commit operation is not performed
    // SUCCESS is set when a row is successfully commited
    // FAILED is set when a commit for a row is failed
    // FAILED_AND_MODIFIED is used is set for a row when the row was failed and
    // again modified

    NOT_EXECUTED, SUCCESS, FAILED, FAILED_AND_MODIFIED, CONSUMED
}
