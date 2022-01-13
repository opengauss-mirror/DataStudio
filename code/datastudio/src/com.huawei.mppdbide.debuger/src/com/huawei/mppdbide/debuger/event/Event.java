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

package com.huawei.mppdbide.debuger.event;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Title: the Event class
 *
 * @since 3.0.0
 */
public class Event {
    /**
     * Title: the EventMessage enum
     */
    public static enum EventMessage {
        ON_EXIT,
        ON_SQL_MSG,
        DEBUG_BEGIN,
        DEBUG_RUN,
        DEBUG_END,
        BREAKPOINT_ADD,
        BREAKPOINT_DELETE,
        BREAKPOINT_CHANGE,
        CANCEL_HIGHLIGHT;
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

    /**
     * get event id
     *
     * @return int unique id
     */
    public int getId() {
        return id;
    }

    /**
     * get event type
     *
     * @return EventMessage the eunm of message
     */
    public EventMessage getMsg() {
        return this.msg;
    }

    /**
     * get addition msg
     *
     * @return Optional<Object> return addition msg
     */
    public Optional<Object> getAddition() {
        return Optional.ofNullable(this.addition);
    }

    /**
     * get addition string msg
     *
     * @return String auto convert addtion object to String object
     */
    public String getStringAddition() {
        if (this.addition instanceof String) {
            return (String) this.addition;
        }
        return addition.toString();
    }

    /**
     * get addition integer msg
     *
     * @return int auto convert addtion object to Integer object
     */
    public int getIntegerAddition() {
        if (this.addition instanceof Integer) {
            return (Integer) this.addition;
        }
        return -1;
    }

    /**
     * get if have exception
     *
     * @return boolean true if have exception
     */
    public boolean hasException() {
        return exp != null;
    }

    /**
     * get exception
     *
     * @return Exception if exp occur, this will not null
     */
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
