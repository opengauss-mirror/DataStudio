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

package org.opengauss.mppdbide.presentation;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum ContextExecutionOperationType.
 * 
 * @since 3.0.0
 */
public enum ContextExecutionOperationType {
    CONTEXT_OPERATION_TYPE_NEW_PL_SQL_CREATION, CONTEXT_OPERATION_TYPE_PL_SQL_COMPILATION,
    CONTEXT_OPERATION_TYPE_SQL_TERMINAL_EXECUTION, CONTEXT_OPERATION_TYPE_VIEW_OBJECT_DATA,
    CONTEXT_OPERATION_TYPE_VIEW_OBJECT_PROPERTY,
}
