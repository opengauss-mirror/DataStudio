/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
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
