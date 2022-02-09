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

package org.opengauss.mppdbide.view.core.edittabledata;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum EditTableDataStates.
 *
 * @since 3.0.0
 */
public enum EditTableDataStates {

    /**
     * The init.
     */
    INIT,
    /**
     * The executing.
     */
    EXECUTING,
    /**
     * The executed.
     */
    EXECUTED,
    /**
     * The editing.
     */
    EDITING,
    /**
     * The edited.
     */
    EDITED,
    /**
     * The posting.
     */
    POSTING,
    /**
     * The posted.
     */
    POSTED,
    /**
     * The commiting.
     */
    COMMITING,
    /**
     * The commited.
     */
    COMMITED,
    /**
     * The rollingback.
     */
    ROLLINGBACK,
    /**
     * The rolledback.
     */
    ROLLEDBACK,
}
