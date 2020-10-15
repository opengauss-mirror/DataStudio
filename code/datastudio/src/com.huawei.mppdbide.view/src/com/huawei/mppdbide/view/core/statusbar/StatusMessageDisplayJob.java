/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
