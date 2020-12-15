/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.event;

/**
 * Title: IHandlerManger for use
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
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
