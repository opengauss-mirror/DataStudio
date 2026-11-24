/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.mock.bl;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.EventHandler;

public class EventBroker implements IEventBroker
{
    @Override
    public boolean unsubscribe(EventHandler eventHandler)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean subscribe(String topic, String filter,
            EventHandler eventHandler, boolean headless)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean subscribe(String topic, EventHandler eventHandler)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean send(String topic, Object data)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean post(String topic, Object data)
    {
        return false;
    }
}
