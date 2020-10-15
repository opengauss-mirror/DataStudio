/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.statusbar;

import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.utils.messaging.GlobaMessageQueueUtil;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjBrowserStatusbar.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class ObjBrowserStatusbar implements ObjBrowserStatusbarIf {
    private static volatile ObjBrowserStatusbar instance;
    private Label statusItem;
    private MessageQueue messages;
    private StatusMessageDisplayJob displayJob = null;
    private static final Object INSTANCELOCK = new Object();

    // This Class represents DS Status bar, not OB. Name is kept to reduce
    // impact.
    private ObjBrowserStatusbar() {
    }

    /**
     * Inits the.
     *
     * @param toolItem the tool item
     */
    public void init(Label toolItem) {
        initObjects(toolItem);
    }

    private void initObjects(Label toolItem) {
        statusItem = toolItem;
        synchronized (INSTANCELOCK) {
            messages = GlobaMessageQueueUtil.getInstance().getMessageQueue();
        }
    }

    /**
     * Gets the single instance of ObjBrowserStatusbar.
     *
     * @return single instance of ObjBrowserStatusbar
     */
    public static ObjBrowserStatusbar getInstance() {
        if (null == instance) {
            synchronized (INSTANCELOCK) {
                if (null == instance) {
                    instance = new ObjBrowserStatusbar();
                }
            }
        }
        return instance;
    }

    /**
     * Display message.
     *
     * @param msg the msg
     */
    public void displayMessage(Message msg) {
        synchronized (INSTANCELOCK) {
            if (null == displayJob) {
                displayJob = new StatusMessageDisplayJob(statusItem, messages);
                displayJob.schedule();
            }

            messages.push(msg);
        }
    }

    /**
     * Destroy.
     */
    public void destroy() {
        synchronized (INSTANCELOCK) {
            if (null != displayJob) {
                displayJob.setExit();
            }
        }
    }

}
