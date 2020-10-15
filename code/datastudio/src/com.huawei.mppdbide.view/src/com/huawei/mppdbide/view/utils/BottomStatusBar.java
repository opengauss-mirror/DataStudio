/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.IStatusMessageList;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;

/**
 * 
 * Title: class
 * 
 * Description: The Class BottomStatusBar.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class BottomStatusBar implements IProgressMonitor {
    private ProgressBar progressBar;
    private Shell shell;
    private StatusMessage statMessage;
    private Label lbl;
    private BottomStatusBarWorker worker;

    /**
     * Creates the controls.
     *
     * @param parent the parent
     * @param parentShell the parent shell
     */
    @PostConstruct
    public void createControls(Composite parent, final Shell parentShell) {
        this.shell = parentShell;

        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        gl.marginHeight = 1;
        gl.marginBottom = 0;
        gl.marginRight = 1;
        gl.marginTop = 1;
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        parent.setLayout(gl);

        GridData gdForm = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdForm.horizontalIndent = 16;
        gdForm.heightHint = 20;
        SashForm form = new SashForm(parent, SWT.NONE);
        form.setLayoutData(gdForm);

        GridLayout glPanes = new GridLayout();
        glPanes.numColumns = 1;
        glPanes.marginHeight = 0;
        glPanes.marginBottom = 0;
        glPanes.marginRight = 0;
        glPanes.marginTop = 0;
        glPanes.horizontalSpacing = 0;
        glPanes.verticalSpacing = 0;

        Composite left = new Composite(form, SWT.NONE);
        left.setLayout(glPanes);
        left.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));

        final Composite right = new Composite(form, SWT.NONE);
        right.setLayout(glPanes);
        right.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));

        /* Set the width to 90% and 10% */
        form.setWeights(new int[] {9, 1});

        lbl = new Label(left, SWT.LEFT);
        lbl.setText("");

        GridData gridDataLbl = new GridData(SWT.FILL);
        gridDataLbl.grabExcessHorizontalSpace = true;
        lbl.setLayoutData(gridDataLbl);

        addProgressBar(right);

        // For Status bar
        ObjectBrowserStatusBarProvider.getStatusBar().init(lbl);

        // Job for Progress bar
        worker = new BottomStatusBarWorker("Bottom Status Bar Worker", "", this);
        worker.schedule();
    }

    /**
     * Adds the progress bar.
     *
     * @param right the right
     */
    private void addProgressBar(final Composite right) {
        progressBar = new ProgressBar(right, SWT.SMOOTH | SWT.HORIZONTAL | SWT.INDETERMINATE | SWT.RIGHT);

        GridData gridDataProgress = new GridData();
        gridDataProgress.horizontalAlignment = SWT.END;
        gridDataProgress.grabExcessHorizontalSpace = true;
        progressBar.setLayoutData(gridDataProgress);

        progressBar.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent mouseEvent) {
                if (mouseEvent.count == 2) {
                    if (getListOfJobs() != 0) {
                        UIElement.getInstance().createProgressBarWindow();
                    }
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });

        progressBar.setVisible(false);
        progressBar.addPaintListener(new StatusBarPaintListener());
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BottomStatusBarWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class BottomStatusBarWorker extends Job {
        private int updateFrequency = 200;
        private BottomStatusBar bottomStatusBar;

        /**
         * Instantiates a new bottom status bar worker.
         *
         * @param name the name
         * @param family the family
         * @param bottomStatusBar1 the bottom status bar 1
         */
        public BottomStatusBarWorker(String name, Object family, BottomStatusBar bottomStatusBar1) {
            super(name);
            this.bottomStatusBar = bottomStatusBar1;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (bottomStatusBar.getListOfJobs() > 0) {
                        if (!bottomStatusBar.isProgressBarVisible()) {
                            bottomStatusBar.setProgressBarVisible(true);
                        }
                    } else {
                        bottomStatusBar.setProgressBarVisible(false);
                    }
                }
            });

            schedule(updateFrequency);
            return Status.OK_STATUS;
        }
    }

    /**
     * The listener interface for receiving statusBarPaint events. The class
     * that is interested in processing a statusBarPaint event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addStatusBarPaintListener<code>
     * method. When the statusBarPaint event occurs, that object's appropriate
     * method is invoked.
     *
     * StatusBarPaintEvent
     */
    private class StatusBarPaintListener implements PaintListener {

        @Override
        public void paintControl(PaintEvent paintEvent) {
            String string = "";
            int length = getListOfJobs();
            if (length != 0) {
                if (length == 1) {
                    string = Integer.toString(length) + "  "
                            + MessageConfigLoader.getProperty(IMessagesConstants.STATUS_JOB_SI);
                } else {
                    string = Integer.toString(length) + "  "
                            + MessageConfigLoader.getProperty(IMessagesConstants.STATUS_JOB);
                }
            }

            Point point = progressBar.getSize();

            FontMetrics fontMetrics = paintEvent.gc.getFontMetrics();
            int width = fontMetrics.getAverageCharWidth() * string.length();
            int height = fontMetrics.getHeight();
            if (shell.getDisplay() != null) {
                paintEvent.gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
            }
            paintEvent.gc.drawString(string, (point.x - width) / 2, (point.y - height) / 2, true);
        }
    }

    /**
     * Begin task.
     *
     * @param name the name
     * @param totalWork the total work
     */
    @Override
    public void beginTask(String name, int totalWork) {
    }

    /**
     * Done.
     */
    @Override
    public void done() {
    }

    /**
     * Internal worked.
     *
     * @param work the work
     */
    @Override
    public void internalWorked(double work) {
    }

    /**
     * Checks if is canceled.
     *
     * @return true, if is canceled
     */
    @Override
    public boolean isCanceled() {
        return false;
    }

    /**
     * Sets the canceled.
     *
     * @param value the new canceled
     */
    @Override
    public void setCanceled(boolean value) {
    }

    /**
     * Sets the task name.
     *
     * @param name the new task name
     */
    @Override
    public void setTaskName(String name) {
    }

    /**
     * Sub task.
     *
     * @param name the name
     */
    @Override
    public void subTask(String name) {
    }

    /**
     * Worked.
     *
     * @param work the work
     */
    @Override
    public void worked(final int work) {
    }

    /**
     * Sets the status message.
     *
     * @param messge the new status message
     */
    public void setStatusMessage(String messge) {
    }

    /**
     * Activate statusbar.
     */
    public void activateStatusbar() {
        IStatusMessageList statMessageList = StatusMessageList.getInstance();
        if (statMessageList.isEmpty()) {
            return;
        }

        statMessage = statMessageList.pop();
    }

    /**
     * Hide statusbar.
     *
     * @param msg the msg
     */
    public void hideStatusbar(StatusMessage msg) {
        if (StatusMessageList.getInstance().isEmpty()) {
            return;
        }

        if (statMessage != null && msg != null) {
            if (!msg.getMessage().equalsIgnoreCase(statMessage.getMessage())) {
                StatusMessageList.getInstance().pop(msg);
                return;
            }
        }

        activateStatusbar();
    }

    /**
     * Gets the list of jobs.
     *
     * @return the list of jobs
     */
    public int getListOfJobs() {
        final IJobManager jm = Job.getJobManager();
        Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
        return allJobs.length;
    }

    /**
     * Checks if is progress bar visible.
     *
     * @return true, if is progress bar visible
     */
    public boolean isProgressBarVisible() {
        if (!progressBar.isDisposed()) {
            return progressBar.isVisible();
        }

        return false;
    }

    /**
     * Sets the progress bar visible.
     *
     * @param isVisible the new progress bar visible
     */
    public void setProgressBarVisible(boolean isVisible) {
        if (!progressBar.isDisposed()) {
            progressBar.setVisible(isVisible);
        }
    }
}
