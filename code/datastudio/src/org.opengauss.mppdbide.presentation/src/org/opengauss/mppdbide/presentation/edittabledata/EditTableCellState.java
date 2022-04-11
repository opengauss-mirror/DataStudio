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
 * Description: The Enum EditTableCellState.
 * 
 * @since 3.0.0
 */
public enum EditTableCellState {
    // modified state is set for a cell when it has been modified commit it is
    // failed MODIFIED_FAILED is set for a cell after the commit is performed.
    // label accumulator MODIFIED State to make the cell green and
    // MODIFIED_FAILED is used for the make it red
    MODIFIED, MODIFIED_FAILED
}
