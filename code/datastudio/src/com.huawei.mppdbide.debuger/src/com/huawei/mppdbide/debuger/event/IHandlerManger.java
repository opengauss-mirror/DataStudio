/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.event;

/**
 *
 * Title: IHandlerManger for use
 *
 * Description: 
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
 */
public interface IHandlerManger {
    void addHandler(EventHander handler);
    void removeHandler(EventHander handler);
    void removeAllHandler();
    void notifyAllHandler(Event event);
}
