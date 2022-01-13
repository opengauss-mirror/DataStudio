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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;

/**
 * 
 * Title: class
 * 
 * Description: The Class StatusMessageDisplayJob.
 *
 * @since 3.0.0
 */
public class StatusMessageDisplayJob extends Job {
    private volatile Label toolItem;
    private MessageQueue msgs;
    private static final int SUCCESS_MESSAGE_SCHEDULE_TIME = 1000;
    private static final int MESSAGE_SCHEDULE_TIME = 3000;
    private boolean isExit = false;
    private final Object INSTANCE_LOCK = new Object();

    /**
     * Instantiates a new status message display job.
     *
     * @param toolItem the tool item
     * @param messages the messages
     */
    public StatusMessageDisplayJob(Label toolItem, MessageQueue messages) {
        super("Object Browser status display");
        this.toolItem = toolItem;
        this.msgs = messages;
    }

    /**
     * Run.
     *
     * @param monitor the monitor
     * @return the i status
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {

        try {
            while (!isExit) {
                Message msg = msgs.pop();
                while (msg != null) {
                    final String tmpMsg = msg.getMessage();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (INSTANCE_LOCK) {
                                if (!toolItem.isDisposed()) {
                                    toolItem.setText(tmpMsg);
                                    toolItem.pack();
                                }

                            }
                        }
                    });

                    msg = msgs.pop();
                    Thread.sleep(MESSAGE_SCHEDULE_TIME);
                }

                Thread.sleep(SUCCESS_MESSAGE_SCHEDULE_TIME);
            }
        } catch (InterruptedException exception) {
            MPPDBIDELoggerUtility.error("InterruptedException while displaying stutus message ..", exception);
        }
        return Status.OK_STATUS;
    }

    /**
     * Sets the exit.
     */
    public void setExit() {
        isExit = true;
    }

}
