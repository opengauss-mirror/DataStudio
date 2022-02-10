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

package org.opengauss.mppdbide.utils.observer;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSEventWithCount.
 *
 * @since 3.0.0
 */
public class DSEventWithCount extends DSEvent {
    private int count;

    /**
     * Instantiates a new DS event with count.
     *
     * @param eventType the event type
     * @param obj the obj
     */
    public DSEventWithCount(int eventType, Object obj) {
        super(eventType, obj);
        count = 1;
    }

    /**
     * Instantiates a new DS event with count.
     *
     * @param eventType the event type
     * @param obj the obj
     * @param count the count
     */
    public DSEventWithCount(int eventType, Object obj, int count) {
        super(eventType, obj);
        this.count = count;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public int getCount() {
        return count;
    }

}
