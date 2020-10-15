/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.observer;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSEvent.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
