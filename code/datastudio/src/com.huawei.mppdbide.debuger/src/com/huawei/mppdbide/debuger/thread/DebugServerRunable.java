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

package com.huawei.mppdbide.debuger.thread;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.DebugService;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Title: the DebugServerRunable class
 *
 * @since 3.0.0
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
        try {
            Optional<Object> optionalObj = debugService.serverDebugCallBack(debugParams);
            retValue = optionalObj.orElse(null);
        } catch (SQLException sqlExp) {
            exp = sqlExp;
        }
        MPPDBIDELoggerUtility.debug("DebugServerRunable server exit!!!");
        event = new Event(Event.EventMessage.ON_EXIT, retValue, exp);
        eventQueueThread.add(event);
    }
}
