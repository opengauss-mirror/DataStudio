/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.thread;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.event.IHandlerManger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Title: the EventQueueThread class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
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
        queue.offer(event);
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
