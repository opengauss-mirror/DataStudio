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

package com.huawei.mppdbide.debuger.service.chain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 * Title: ServerPortMsgChain for use
 *
 * @since 3.0.0
 */
public class ServerPortMsgChain extends IMsgChain {
    /**
     *  msg to matched
     */
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
