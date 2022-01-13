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
 * @since 3.0.0
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
