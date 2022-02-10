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

package org.opengauss.mppdbide.debuger.thread;

import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.EventHander;
import org.opengauss.mppdbide.debuger.event.IHandlerManger;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Title: the EventQueueThread class
 *
 * @since 3.0.0
 */
public class EventQueueThread extends Thread implements IHandlerManger {
    private static final int DEFAULT_EVENT_HANDLES = 3;
    private static final int DEFAULT_EVENT_SLEEP = 10;
    private static final int TIMEOUT_COUNT = 200;
    private LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    private List<EventHander> eventHandlers = new ArrayList<EventHander>(DEFAULT_EVENT_HANDLES);

    @Override
    public void run() {
        while (true) {
            try {
                Event event = queue.take();
                if (event.getMsg() == null) {
                    break;
                }
                Thread.sleep(DEFAULT_EVENT_SLEEP);
                notifyAllHandler(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        MPPDBIDELoggerUtility.debug("event queue exit!");
    }

    /**
     * add event to queue
     *
     * @param event event to queue
     * @return void
     */
    public void add(Event event) {
        boolean flag = queue.offer(event);
        if (!flag) {
            MPPDBIDELoggerUtility.debug("event add failed!");
        }
    }

    /**
     * exit this thread
     *
     * @return void
     */
    public void stopThread() {
        add(new Event(null, null));
        int count = TIMEOUT_COUNT;
        while (count > 0) {
            if (!isAlive()) {
                break;
            }
            try {
                sleep(DEFAULT_EVENT_SLEEP);
            } catch (InterruptedException e) {
                MPPDBIDELoggerUtility.warn("sleep have error!");
                Thread.currentThread().interrupt();
            }
            count -= 1;
        }
    }

    @Override
    public void addHandler(EventHander handler) {
        if (!eventHandlers.contains(handler)) {
            eventHandlers.add(handler);
        }
    }

    @Override
    public void removeHandler(EventHander handler) {
        eventHandlers.remove(handler);
    }

    @Override
    public void removeAllHandler() {
        eventHandlers.clear();
    }

    @Override
    public void notifyAllHandler(Event event) {
        for (EventHander hander: eventHandlers) {
            hander.handleEvent(event);
        }
    }
}
