/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class RefreshCounter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public final class RefreshCounter {

    private static volatile RefreshCounter refreshCounter = null;
    private int countValue = 1;
    private static final Object LOCK = new Object();

    /**
     * Instantiates a new refresh counter.
     */
    private RefreshCounter() {

    }

    /**
     * Gets the single instance of RefreshCounter.
     *
     * @return single instance of RefreshCounter
     */
    public static RefreshCounter getInstance() {

        if (refreshCounter == null) {
            synchronized (LOCK) {
                if (refreshCounter == null) {
                    refreshCounter = new RefreshCounter();
                }

            }

        }

        return refreshCounter;
    }

    /**
     * Gets the count value.
     *
     * @return the count value
     */
    public int getCountValue() {
        return countValue;
    }

    /**
     * Sets the count value.
     *
     * @param countValue the new count value
     */
    public void setCountValue(int countValue) {
        this.countValue = countValue;
    }

}
