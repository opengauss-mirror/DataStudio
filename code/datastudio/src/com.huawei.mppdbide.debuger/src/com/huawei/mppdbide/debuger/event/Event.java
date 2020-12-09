/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.event;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: the Event class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class Event {
    public static enum EventMessage {
        ON_EXIT,
        ON_SQL_MSG,
        DEBUG_BEGIN,
        DEBUG_RUN,
        DEBUG_END,
        BREAKPOINT;
    }
    private static AtomicInteger autoId = new AtomicInteger();
    private EventMessage msg;
    private Object addition;
    private Exception exp;
    private int id = -1;

    public Event(EventMessage msg, Object addtion) {
        this(msg, addtion, null);
    }

    public Event(EventMessage msg, Object addtion, Exception exp) {
        this(msg, addtion, exp, autoId.getAndIncrement());
    }
    
    public Event(EventMessage msg, Object addition, Exception exp, int id) {
        this.msg = msg;
        this.addition = addition;
        this.exp = exp;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public EventMessage getMsg() {
        return this.msg;
    }

    public Optional<Object> getAddition() {
        return Optional.ofNullable(this.addition);
    }
    
    public String getStringAddition() {
        return (String) this.addition;
    }

    public boolean hasException() {
        return exp != null;
    }

    public Exception getException() {
        return this.exp;
    }
    
    @Override
    public String toString() {
        return String.format(
                Locale.ENGLISH,
                "Event{id: %s,type: %s, addition:%s, exp: %s}",
                id,
                msg.toString(),
                addition == null ? "" : addition.toString(),
                exp == null ? "" : exp.getMessage());
    }
}
