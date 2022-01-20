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

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class RefreshCounter.
 * 
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
