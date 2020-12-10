/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service.chain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 *
 * Title: ServerPortMsgChain for use
 *
 * Description: 
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
 */
public class ServerPortMsgChain extends IMsgChain {
    // msg to matched
    public static final String SERVER_PORT_MATCH = "YOUR PROXY PORT ID IS:";
    private DebugService debugService;
    public ServerPortMsgChain(DebugService debugService) {
        super();
        this.debugService = debugService;
    }

    @Override
    public boolean matchMsg(Event event) {
        if (event.getMsg() == EventMessage.ON_SQL_MSG
                && event.getStringAddition().contains(SERVER_PORT_MATCH)) {
            return true;
        }
        return false;
    }

    @Override
    protected void disposeMsg(Event event) {
        String msg = event.getStringAddition();
        Matcher matcher = Pattern.compile(SERVER_PORT_MATCH + "(\\d+)").matcher(msg);
        if (matcher.find()) {
            debugService.updateServerPort(Integer.parseInt(matcher.group(1).trim()));
        }
    }

}
