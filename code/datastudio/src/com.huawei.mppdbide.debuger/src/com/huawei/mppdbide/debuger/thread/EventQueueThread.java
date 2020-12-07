/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.thread;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Title: the EventQueueThread class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class EventQueueThread extends Thread {
    private static final int DEFAULT_EVENT_SLEEP = 10;
    private LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<>();
    private EventHander eventHander;

    public void setEventHandler(EventHander eventHandler) {
        this.eventHander = eventHandler;
    }
    @Override
    public void run() {
        while (true) {
            try {
                Event event = queue.take();
                if (event.getMsg() == null) {
                    break;
                }
                Thread.sleep(DEFAULT_EVENT_SLEEP);
                if (eventHander != null) {
                    eventHander.handleEvent(event);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MPPDBIDELoggerUtility.debug("event queue exit!");
    }

    public void add(Event event) {
        queue.offer(event);
    }

    public void stopThread() {
        add(new Event(null, null));
    }
}
