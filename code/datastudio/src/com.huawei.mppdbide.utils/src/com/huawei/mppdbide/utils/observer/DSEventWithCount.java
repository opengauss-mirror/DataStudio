/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.observer;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSEventWithCount.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
