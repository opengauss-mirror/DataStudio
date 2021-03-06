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

package org.opengauss.mppdbide.utils.messaging;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IStatusMessageList.
 *
 * @since 3.0.0
 */
public interface IStatusMessageList {

    /**
     * Push.
     *
     * @param msg the msg
     */
    void push(StatusMessage msg);

    /**
     * Pop.
     *
     * @return the status message
     */
    StatusMessage pop();

    /**
     * Pop.
     *
     * @param message the message
     * @return true, if successful
     */
    boolean pop(StatusMessage message);

    /**
     * Checks if is empty.
     *
     * @return true, if is empty
     */
    boolean isEmpty();

}
