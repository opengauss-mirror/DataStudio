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

package com.huawei.mppdbide.debuger.event;

/**
 * Title: IHandlerManger for use
 *
 * @since 3.0.0
 */
public interface IHandlerManger {
    /**
     * description: add handler
     *
     * @param handler handler to add
     * @return void
     */
    void addHandler(EventHander handler);

    /**
     * description: remove handler
     *
     * @param handler handler to remove
     * @return void
     */
    void removeHandler(EventHander handler);

    /**
     * description: remove all handler
     *
     * @return remote all handler
     */
    void removeAllHandler();

    /**
     * description: notify all handler
     *
     * @param event the event to notify
     * @return void
     */
    void notifyAllHandler(Event event);
}
