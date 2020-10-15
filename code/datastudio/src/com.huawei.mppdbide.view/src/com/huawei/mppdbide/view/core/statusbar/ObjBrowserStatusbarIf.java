/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.statusbar;

import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.utils.messaging.Message;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ObjBrowserStatusbarIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface ObjBrowserStatusbarIf {

    /**
     * Display message.
     *
     * @param msg the msg
     */
    void displayMessage(Message msg);

    /**
     * Destroy.
     */
    void destroy();

    /**
     * Inits the.
     *
     * @param toolItem the tool item
     */
    void init(Label toolItem);
}
