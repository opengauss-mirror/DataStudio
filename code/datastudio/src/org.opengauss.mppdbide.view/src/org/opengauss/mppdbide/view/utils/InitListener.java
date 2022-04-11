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

package org.opengauss.mppdbide.view.utils;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Title: InitListener
 * 
 * Description: The listener interface for receiving init events. The class that
 * is interested in processing a init event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addInitListener<code> method. When the init event occurs,
 * that object's appropriate method is invoked.InitListener Listener
 * 
 * @since 3.0.0
 */
public class InitListener implements Listener {

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        event.doit = false;

    }

}
