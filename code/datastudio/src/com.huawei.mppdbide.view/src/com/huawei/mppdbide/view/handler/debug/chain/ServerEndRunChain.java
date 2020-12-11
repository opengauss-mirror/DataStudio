/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
 */
public class ServerEndRunChain extends IMsgChain {

    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_END;
    }

    @Override
    protected void disposeMsg(Event event) {
        
    }

}
