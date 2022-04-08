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

package org.opengauss.mppdbide.presentation;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IUIWorkerJobNotifier.
 *
 * @since 3.0.0
 */
public interface IUIWorkerJobNotifier extends Comparable {

    /**
     * This method will be called to notify a worker, which is waiting to obtain
     * SQL terminal connection
     *
     * @param notify the new notified
     */
    public void setNotified(boolean notify);

    /**
     * This method will be called to cancel a worker, which is waiting to obtain
     * SQL terminal connection
     *
     * @param cancel the new cancelled
     */
    public void setCancelled(boolean cancel);
}
