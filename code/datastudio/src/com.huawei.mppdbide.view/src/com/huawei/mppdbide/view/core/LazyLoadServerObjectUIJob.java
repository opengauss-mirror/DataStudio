/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.messaging.StatusMessage;

/**
 * 
 * Title: class
 * 
 * Description: The Class LazyLoadServerObjectUIJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
