/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.thread;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.DebugService;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Title: the DebugServerRunable class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class DebugServerRunable implements Runnable {
    private DebugService debugService;
    private List<?> debugParams;
    private EventQueueThread eventQueueThread;
    public DebugServerRunable(DebugService debugService, List<?> params, EventQueueThread eventQueueThread) {
        this.debugService = debugService;
        this.debugParams = params;
        this.eventQueueThread = eventQueueThread;
    }

    @Override
    public void run() {
        Event event;
        Object retValue = null;
        Exception exp = null;
        try (ResultSet rs = debugService.serverDebugCallBack(debugParams)) {
            if (rs.next()) {
                retValue = rs.getObject(1);
            }
        } catch (SQLException sqlExp) {
            exp = sqlExp;
        }
        MPPDBIDELoggerUtility.debug("DebugServerRunable server exit!!!");
        event = new Event(Event.EventMessage.ON_EXIT, retValue, exp);
        eventQueueThread.add(event);
    }
}
