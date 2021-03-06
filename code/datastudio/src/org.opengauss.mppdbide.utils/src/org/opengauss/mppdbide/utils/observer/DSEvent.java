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
 * Description: The Class DSEvent.
 *
 * @since 3.0.0
 */
public class DSEvent {
    private int type;
    private Object object;

    /**
     * Instantiates a new DS event.
     *
     * @param eventType the event type
     * @param obj the obj
     */
    public DSEvent(int eventType, Object obj) {
        this.type = eventType;
        this.object = obj;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    public Object getObject() {
        return object;
    }
}
