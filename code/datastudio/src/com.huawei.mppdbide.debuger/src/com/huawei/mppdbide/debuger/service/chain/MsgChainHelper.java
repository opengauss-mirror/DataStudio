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

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 * Title: MsgChainHelper for use
 * Description: MsgChainHelper is a better way to use default chain
 *
 * @since 3.0.0
 */
public class MsgChainHelper {
    private IMsgChain sqlMsgChain = null;
    private IMsgChain eventChain = null;

    public MsgChainHelper(DebugService debugService) {
        sqlMsgChain = new PrepareMsgChian(debugService);
        sqlMsgChain.setNext(new ServerPortMsgChain(debugService));

        eventChain = new ServerExitEventChain(debugService);
    }

    /**
     * description: handle sql msg
     *
     * @param event event of reveive from sql
     * @return void
     */
    public void handleSqlMsg(Event event) {
        sqlMsgChain.handleMsg(event);
    }

    /**
     * description: handle event msg
     *
     * @param event event of reveive from notify handler manager
     * @return void
     */
    public void handleEventMsg(Event event) {
        eventChain.handleMsg(event);
    }
}
