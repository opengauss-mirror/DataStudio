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

package com.huawei.mppdbide.view.utils;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class TerminalStatusBar.
 *
 * @since 3.0.0
 */
public class TerminalStatusBar {
    private ProgressBar progressBar;
    private Composite statusBarComposite;
    private Composite cancelComposite;
    private Composite progBarComposite;

    private Label lblElapsedTime;
    private Label totalElpTimeInp;
    private boolean execStatus;
    private boolean isSQLTerminal;
    private IExecTimer execTimer;
    private Button cancelButton;

    private ECommandService commandService;
    private EHandlerService handlerService;

    private TerminalStatusBarWorker worker;

    /**
     * Instantiates a new terminal status bar.
     */
    public TerminalStatusBar() {
        this.execStatus = false;
        this.execTimer = new ExecTimer("TerminalStatusBar ");
    }

    /**
     * Instantiates a new terminal status bar.
     *
     * @param statusBar the status bar
     */
    public TerminalStatusBar(TerminalStatusBar statusBar) {
        this.execStatus = statusBar.isExecuting();
        this.execTimer = statusBar.getTimerObjct();
    }

    /**
     * Sets the total elp time inp.
     *
     * @param timeWithUnits the new total elp time inp
     */
    public void setTotalElpTimeInp(String timeWithUnits) {

        totalElpTimeInp.setVisible(true);
        totalElpTimeInp.setText(timeWithUnits);
    }

    /**
     * Creates the status grid.
     *
     * @param parent the parent
     * @param isCancellable the is cancellable
     */
    public void createStatusGrid(Composite parent, boolean isCancellable) {
        statusBarComposite = createComposite(parent);
        this.isSQLTerminal = isCancellable;
        if (!isCancellable) {
            createTotalDebugTimeComp(statusBarComposite);
        }
        addProgressBar(statusBarComposite);
    }

    /**
     * Creates the composite.
     *
     * @param parent the parent
     * @return the composite
     */
    private Composite createComposite(Composite parent) {
        Composite newComposite = new Composite(parent, SWT.BORDER);
        GridData layout = new GridData(SWT.FILL, SWT.NONE, true, false);
        layout.heightHint = 20;
        GridLayout glayout = new GridLayout(3, false);
        glayout.marginWidth = 0;
        glayout.marginHeight = 0;
        glayout.horizontalSpacing = 0;
        glayout.verticalSpacing = 0;
        newComposite.setLayout(glayout);
        newComposite.setLayoutData(layout);
        return newComposite;
    }

    /**
     * Creates the total debug time comp.
     *
     * @param statusComposite the status composite
     */
    private void createTotalDebugTimeComp(Composite statusComposite) {
        Composite totalDbgTimeComp = new Composite(statusComposite, SWT.NONE);
        GridLayout totalDbgTimeCompGL = new GridLayout(3, false);
        totalDbgTimeCompGL.marginWidth = 0;
        totalDbgTimeCompGL.marginHeight = 0;
        totalDbgTimeCompGL.horizontalSpacing = 0;
        totalDbgTimeCompGL.verticalSpacing = 0;
        totalDbgTimeComp.setLayout(totalDbgTimeCompGL);
        GridData totalDbgTimeCompGD = new GridData(SWT.NONE, SWT.NONE, true, false);
        totalDbgTimeCompGD.heightHint = 20;
        totalDbgTimeCompGD.verticalIndent = 2;
        totalDbgTimeComp.setLayoutData(totalDbgTimeCompGD);

        new Label(totalDbgTimeComp, SWT.SEPARATOR | SWT.VERTICAL);

        totalElpTimeInp = new Label(totalDbgTimeComp, SWT.NONE);
        GridData elpTimeInpGD = new GridData(SWT.NONE, SWT.NONE, true, false);
        elpTimeInpGD.widthHint = 130;
        totalElpTimeInp.setLayoutData(elpTimeInpGD);
        totalElpTimeInp.setVisible(false);
    }

    /**
     * Adds the elapsed time.
     *
     * @param statusComposite the status composite
     */
    private void addElapsedTime(Composite statusComposite) {
        Composite elaspedTimeComp = new Composite(statusComposite, SWT.BORDER);
        GridLayout elaspedTimeCompGL = new GridLayout(1, false);
        elaspedTimeComp.setLayout(elaspedTimeCompGL);
        GridData elaspedTimeCompGD = new GridData(SWT.NONE, SWT.NONE, true, false);
        elaspedTimeCompGD.widthHint = 200;
        elaspedTimeComp.setLayoutData(elaspedTimeCompGD);

        lblElapsedTime = new Label(elaspedTimeComp, SWT.NONE);
        lblElapsedTime.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        GridData elpTimeGD = new GridData(SWT.NONE, SWT.NONE, true, false);
        elpTimeGD.widthHint = 200;
        lblElapsedTime.setLayoutData(elpTimeGD);
    }

    /**
     * Adds the progress bar.
     *
     * @param statusComposite the status composite
     */
    private void addProgressBar(Composite statusComposite) {
        Composite progComp = new Composite(statusComposite, SWT.NONE);
        GridLayout gLayout = new GridLayout(3, false);
        gLayout.marginWidth = 0;
        gLayout.marginHeight = 0;
        gLayout.horizontalSpacing = 0;
        gLayout.verticalSpacing = 0;
        gLayout.marginTop = -5;
        progComp.setLayout(gLayout);
        GridData progGD = new GridData(SWT.END, SWT.NONE, true, false);
        progGD.heightHint = 20;
        progComp.setLayoutData(progGD);

        addElapsedTime(progComp);

        Composite progressSection = new Composite(progComp, SWT.NONE);
        GridLayout progSecLayout = new GridLayout(1, false);
        progressSection.setLayout(progSecLayout);

        progBarComposite = new Composite(progressSection, SWT.NONE);
        GridLayout progBarLayout = new GridLayout(1, false);
        progBarLayout.marginWidth = 0;
        progBarLayout.marginHeight = 0;
        progBarLayout.horizontalSpacing = 0;
        progBarLayout.verticalSpacing = 0;
        progBarComposite.setLayout(progBarLayout);
        progBarComposite.setVisible(true);

        progressBar = new ProgressBar(progBarComposite, SWT.SMOOTH | SWT.HORIZONTAL | SWT.INDETERMINATE | SWT.RIGHT);
        GridData gridDataProgress = new GridData();
        gridDataProgress.grabExcessHorizontalSpace = false;
        gridDataProgress.widthHint = 230;
        progressBar.setLayoutData(gridDataProgress);
        progressBar.setVisible(false);
        progressBar.addPaintListener(new StatusBarPaintListener());

        if (isSQLTerminal) {
            addCancelItem(progComp);
        }
    }

    /**
     * Adds the cancel item.
     *
     * @param progBarComp the prog bar comp
     */
    private void addCancelItem(Composite progBarComp) {
        cancelComposite = new Composite(progBarComp, SWT.NONE);
        GridData gdCancelComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);

        cancelComposite.setLayoutData(gdCancelComposite);
        GridLayout cancelLayout = new GridLayout(1, false);
        cancelLayout.marginHeight = 0;
        cancelLayout.marginWidth = 0;
        cancelLayout.marginTop = 0;
        cancelComposite.setLayout(cancelLayout);
        cancelComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        cancelButton = new Button(cancelComposite, SWT.NONE);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_002");
        GridData gdCancelButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdCancelButton.widthHint = 27;
        gdCancelButton.heightHint = 20;
        cancelButton.setLayoutData(gdCancelButton);

        cancelButton.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_CANCEL_TERMINAL, getClass()));

        cancelButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_BUTTON_TOOL_TIP));
        cancelButton.setEnabled(false);
        cancelButton.getParent().setToolTipText(cancelButton.getToolTipText());
        cancelButton.addSelectionListener(new SelectionListenerHelper(commandService, handlerService));
        cancelComposite.setVisible(false);
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
            String lblString = "";
            if (isExecuting()) {
                lblString = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_DYANMIC_TIME_LABEL)
                        + execTimer.getDynamicElapsedTime(isExecuting());
            } else {
                lblString = "";
            }

            Point point = progressBar.getSize();

            FontMetrics fontMetrics = paintEvent.gc.getFontMetrics();
            int width = fontMetrics.getAverageCharWidth() * lblString.length();
            int height = fontMetrics.getHeight();
            paintEvent.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
            paintEvent.gc.drawString(lblString, (point.x - width) / 2, (point.y - height) / 2, true);
        }
    }

    /**
     * Sets the progress bar visible.
     *
     * @param isVisible the new progress bar visible
     */
    public void setProgressBarVisible(Boolean isVisible) {
        if (!progressBar.isDisposed()) {
            this.progressBar.setVisible(isVisible);
        }
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
     * Sets the execution stat.
     *
     * @param execStat the new execution stat
     */
    public void setExecutionStat(Boolean execStat) {
        this.execStatus = execStat;
    }

    /**
     * Checks if is executing.
     *
     * @return true, if is executing
     */
    public boolean isExecuting() {
        if (!progressBar.isDisposed()) {
            return this.execStatus;
        }
        return false;
    }

    /**
     * Gets the timer objct.
     *
     * @return the timer objct
     */
    public IExecTimer getTimerObjct() {
        return this.execTimer;
    }

    /**
     * Show progres bar.
     */
    public void showProgresBar() {
        this.setExecutionStat(true);
        if (isSQLTerminal) {
            this.cancelComposite.setVisible(true);
            this.cancelButton.setEnabled(true);
        }
        this.progBarComposite.setVisible(true);
        lblElapsedTime.setText("");
        this.execTimer.start();
        this.worker = new TerminalStatusBarWorker("Bottom Terminal/Editor Status Bar Worker", this);
        this.worker.schedule();
    }

    /**
     * Just show bar.
     */
    public void justShowBar() {
        this.setExecutionStat(true);
        this.progBarComposite.setVisible(true);
        lblElapsedTime.setText("");
        if (this.worker == null) {
            this.worker = new TerminalStatusBarWorker("Bottom Terminal/Editor Status Bar Worker", this);
        }
        this.worker.schedule();
    }

    /**
     * Hide progres bar.
     */
    public void hideProgresBar() {
        this.setExecutionStat(false);
        if (!execTimer.isTimerStop()) {
            this.execTimer.stopAndLogNoException();
        }
        if (isSQLTerminal && !cancelComposite.isDisposed()) {
            this.cancelComposite.setVisible(false);
            this.cancelButton.setEnabled(false);
        }
        this.progressBar.setVisible(false);
        this.progBarComposite.setVisible(false);
        try {
            lblElapsedTime.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_ELAPSED_TIME_LABEL)
                    + execTimer.getElapsedTime());
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Failed to get elapsed time.", exception);
        }
    }

    /**
     * Sets the handler services.
     *
     * @param commandServce the command servce
     * @param handlerServce the handler servce
     */
    public void setHandlerServices(ECommandService commandServce, EHandlerService handlerServce) {
        this.commandService = commandServce;
        this.handlerService = handlerServce;
    }

    /**
     * Gets the cancel button.
     *
     * @return the cancel button
     */
    public Button getCancelButton() {
        return cancelButton;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TerminalStatusBarWorker.
     */
    private static class TerminalStatusBarWorker extends Job {
        private TerminalStatusBar terminalStatusBar;

        /**
         * Instantiates a new terminal status bar worker.
         *
         * @param name the name
         * @param terminalStatusBar1 the terminal status bar 1
         */
        public TerminalStatusBarWorker(String name, TerminalStatusBar terminalStatusBar1) {
            super(name);
            this.terminalStatusBar = terminalStatusBar1;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    if (terminalStatusBar.isExecuting()) {
                        if (!terminalStatusBar.isProgressBarVisible()) {
                            terminalStatusBar.setProgressBarVisible(true);
                        }
                    } else {
                        terminalStatusBar.setProgressBarVisible(false);
                    }
                }
            });
            return Status.OK_STATUS;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SelectionListenerHelper.
     */
    private static final class SelectionListenerHelper implements SelectionListener {
        private ECommandService comdService;
        private EHandlerService ehandlrService;

        /**
         * Instantiates a new selection listener helper.
         *
         * @param commandService the command service
         * @param handlerService the handler service
         */
        private SelectionListenerHelper(ECommandService commandService, EHandlerService handlerService) {
            this.comdService = commandService;
            this.ehandlrService = handlerService;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Command cmd = comdService.getCommand("com.huawei.mppdbide.command.id.cancelSql");
            ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
            ehandlrService.executeHandler(parameterizedCmd);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }
}
