/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 * Title: MsgChainHelper for use
 * Description: MsgChainHelper is a better way to use default chain
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
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
