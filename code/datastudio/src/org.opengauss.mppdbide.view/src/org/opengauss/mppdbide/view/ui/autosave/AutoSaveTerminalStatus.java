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

package org.opengauss.mppdbide.view.ui.autosave;

/**
 * 
 * Title: enum
 * 
 * Description: The Enum AutoSaveTerminalStatus.
 *
 * @since 3.0.0
 */
public enum AutoSaveTerminalStatus {

    /**
     * The init.
     */
    INIT,
    /**
     * The loading.
     */
    LOADING,
    /**
     * The load finished.
     */
    LOAD_FINISHED,
    /**
     * The write finished.
     */
    WRITE_FINISHED,
    /**
     * The load failed.
     */
    LOAD_FAILED,
    /**
     * The write failed.
     */
    WRITE_FAILED
}
