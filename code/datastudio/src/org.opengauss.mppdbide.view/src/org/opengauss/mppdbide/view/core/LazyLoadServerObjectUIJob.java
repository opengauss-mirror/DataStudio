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

package org.opengauss.mppdbide.view.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;

/**
 * 
 * Title: class
 * 
 * Description: The Class LazyLoadServerObjectUIJob.
 *
 * @since 3.0.0
 */
public class LazyLoadServerObjectUIJob extends UIJob {
    private TreeViewer viewer;
    private ServerObject obj;
    private boolean isFirst = true;
    private LazyBackendLoader loader;
    private IExecTimer timer;
    private StatusMessage statusMsg;
    private static final int LOAD_RECHECK_SCHEDULE_TIME = 300;

    /**
     * Instantiates a new lazy load server object UI job.
     *
     * @param jobDisplay the job display
     * @param jobName the job name
     * @param serverObject the server object
     * @param viewer the viewer
     * @param statusMsg the status msg
     * @param timer the timer
     */
    public LazyLoadServerObjectUIJob(Display jobDisplay, String jobName, Object serverObject, TreeViewer viewer,
            StatusMessage statusMsg, IExecTimer timer) {
        super(jobDisplay, jobName);
        obj = (ServerObject) serverObject;
        this.viewer = viewer;
        this.timer = timer;
        this.statusMsg = statusMsg;
    }

    /**
     * Run in UI thread.
     *
     * @param monitor the monitor
     * @return the i status
     */
    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        boolean isAllLoaded = false;
        if (!obj.isLoaded()) {
            if (isFirst) {
                loader = new LazyServerObjectBackendLoader(obj, statusMsg, timer);
                loader.schedule();
                isFirst = false;
            }
        } else {
            isAllLoaded = true;
        }

        viewer.setExpandedState(obj, true);
        viewer.refresh(obj, true);

        if (null != loader && loader.isFailed()) {
            return Status.OK_STATUS;
        }

        if (!isAllLoaded) {
            schedule(LOAD_RECHECK_SCHEDULE_TIME);
        }

        return Status.OK_STATUS;
    }
}
