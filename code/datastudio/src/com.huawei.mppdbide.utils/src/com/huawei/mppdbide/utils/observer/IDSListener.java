/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.observer;

/**
 * Title: IDSListener
 * 
 * Description:The listener interface for receiving IDS events. The class that
 * is interested in processing a IDS event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addIDSListener<code> method. When the IDS event occurs,
 * that object's appropriate method is invoked.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
 */

public interface IDSListener {

    /**
     * Handle event.
     *
     * @param event the event
     */
    void handleEvent(DSEvent event);
}
