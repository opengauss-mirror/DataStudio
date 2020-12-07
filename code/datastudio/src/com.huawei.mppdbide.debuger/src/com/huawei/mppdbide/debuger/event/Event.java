/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.event;

import java.util.Optional;

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
        ON_EXIT;
    }
    private EventMessage msg;
    private Object addition;
    private Exception exp;

    public Event(EventMessage msg, Object addtion) {
        this(msg, addtion, null);
    }

    public Event(EventMessage msg, Object addtion, Exception exp) {
        this.msg = msg;
        this.addition = addtion;
        this.exp = exp;
    }

    public EventMessage getMsg() {
        return this.msg;
    }

    public Optional<Object> getAddition() {
        return Optional.ofNullable(this.addition);
    }

    public boolean hasException() {
        return exp != null;
    }

    public Exception getException() {
        return this.exp;
    }
}
