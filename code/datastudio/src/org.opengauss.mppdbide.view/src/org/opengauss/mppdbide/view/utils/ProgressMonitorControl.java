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

package org.opengauss.mppdbide.view.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.utils.progressmonitorif.ProgressMonitorControlIf;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ProgressMonitorControl.
 *
 * @since 3.0.0
 */
public class ProgressMonitorControl implements ProgressMonitorControlIf {
    private static final int TABLE_COLUMN_1_WIDTH = 2 * IScreenResolutionUtil.getScreenWidth() / 3;
    private static final int TABLE_COLUMN_2_WIDTH = 50;
    private static boolean isExportImportInProgress;
    private DSJobChangeListener dsJobChangeListenerObj = new DSJobChangeListener();

    private Table table;

    private List<TableEditor> editorLst = new ArrayList<TableEditor>();
    private List<TableEditor> editorBtnLst = new ArrayList<TableEditor>();

    private Composite parent;

    /**
     * Instantiates a new progress monitor control.
     */
    public ProgressMonitorControl() {
        parent = null;
        table = null;
    }

    /**
     * Creates the controls.
     *
     * @param compParent the comp parent
     */
    @PostConstruct
    public void createControls(Composite compParent) {
        this.parent = compParent;

        table = new Table(compParent, SWT.NONE);

        table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        // AvoidLooprForArrayOrObjectAllocationCheck
        for (int j = 0; j < 2; j++) {
            new TableColumn(table, SWT.NONE);
        }

        // AvoidLooprForArrayOrObjectAllocationCheck
        table.getColumn(0).setText(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_PROGRESS_TABLE_HEARDER));
        table.getColumn(1).setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));
        table.getColumn(0).setWidth(TABLE_COLUMN_1_WIDTH);
        table.getColumn(1).setWidth(TABLE_COLUMN_2_WIDTH);
        table.redraw();
        compParent.redraw();
    }

    /**
     * Refresh jobs.
     */
    public void refreshJobs() {
        if (!isExportImportInProgress) {
            setExportImportInProgress(true);
        }
        final IJobManager jm = Job.getJobManager();
        updateProgressTable(jm);
        jm.addJobChangeListener(dsJobChangeListenerObj);

    }

    /**
     * Removes the job listener.
     */
    public void removeJobListener() {
        final IJobManager jm = Job.getJobManager();
        jm.removeJobChangeListener(dsJobChangeListenerObj);
    }

    private class DSJobChangeListener implements IJobChangeListener {
        @Override
        public void sleeping(IJobChangeEvent event) {

        }

        @Override
        public void scheduled(IJobChangeEvent event) {

        }

        @Override
        public void running(IJobChangeEvent event) {

        }

        @Override
        public void done(IJobChangeEvent event) {
            final IJobManager jm = Job.getJobManager();
            Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
            if (allJobs.length == 0) {
                UIElement.getInstance().hideStatusBarWindow();
                setExportImportInProgress(true);

            }
            if (null != jm.currentJob() && jm.currentJob().belongsTo(MPPDBIDEConstants.CANCELABLEJOB)) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        updateProgressTable(jm);
                    }
                });
            }
        }

        @Override
        public void awake(IJobChangeEvent event) {

        }

        @Override
        public void aboutToRun(IJobChangeEvent event) {

        }
    }

    /**
     * Update progress table.
     *
     * @param jm the jm
     */
    private void updateProgressTable(IJobManager jm) {
        Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
        if (table.isDisposed()) {
            return;
        }
        table.removeAll();
        table.clearAll();
        for (TableEditor editor : editorLst) {
            functionDisposecomponent(editor);
            editor = null;
        }

        editorLst.clear();

        for (TableEditor editor : editorBtnLst) {
            functionDisposecomponent(editor);
            editor = null;
        }

        editorBtnLst.clear();

        for (final Job job : allJobs) {
            addProgressBarForAllJobs(job);
        }
        table.getColumn(0).setWidth(TABLE_COLUMN_1_WIDTH);
        table.getColumn(1).setWidth(TABLE_COLUMN_2_WIDTH);
        table.redraw();
        parent.redraw();
    }

    private void addProgressBarForAllJobs(final Job job) {
        TableItem item = new TableItem(table, SWT.NONE);
        ProgressBar bar = new ProgressBar(table, SWT.SMOOTH | SWT.HORIZONTAL | SWT.INDETERMINATE | SWT.LEFT);

        item.setData(new GridData(SWT.FILL, SWT.FILL, true, true));
        TableEditor editor = new TableEditor(table);
        editor.grabHorizontal = true;
        editor.setEditor(bar, item, 0);
        editorLst.add(editor);

        Map<Object, Button> buttons = new HashMap<Object, Button>();
        Button button;
        if (buttons.containsKey(job)) {
            button = buttons.get(job);
        } else {
            button = new Button(table, SWT.TRANSPARENT);
            button.addPaintListener(new ButtonPaintHelper());
            button.setData("row.id", job);
            buttons.put(job, button);
            if (job instanceof UIWorkerJob) {
                UIWorkerJob uiwkrJob = (UIWorkerJob) job;

                if (uiwkrJob.isCancel()) {
                    bar.addPaintListener(new StatusBarPaintListener(
                            MessageConfigLoader.getProperty(IMessagesConstants.CANCELLING_JOB, uiwkrJob.getName()),
                            bar));

                    button.setEnabled(false);
                    button.setGrayed(true);
                } else {
                    bar.addPaintListener(new StatusBarPaintListener(uiwkrJob.getName(), bar));
                }

            }

        }
        addEditorButton(item, button);
    }

    private void addEditorButton(TableItem item, Button button) {
        TableEditor editorBtn = new TableEditor(item.getParent());
        editorBtn.grabHorizontal = true;
        editorBtn.grabVertical = true;
        editorBtn.setEditor(button, item, 1);
        button.addSelectionListener(new SelectionHelper());
        editorBtn.layout();
        editorBtnLst.add(editorBtn);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SelectionHelper.
     */
    private final class SelectionHelper implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            Job jb = null;
            if (!((Button) event.getSource()).isDisposed()) {
                jb = (Job) ((Button) event.getSource()).getData("row.id");
            }

            int choice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORTEXPORT_CONSOLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORTEXPORT_CONSOLE_MSG),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));

            if (choice == UIConstants.OK_ID) {
                if (jb != null) {
                    jb.cancel();
                    ProgressMonitorControl.this.updateProgressTable(Job.getJobManager());
                }
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ButtonPaintHelper.
     */
    private static final class ButtonPaintHelper implements PaintListener {

        @Override
        public void paintControl(PaintEvent event) {

            event.gc.setBackground(event.display.getSystemColor(SWT.COLOR_WHITE));
            event.gc.fillRectangle(event.x, event.y, event.width, event.height);
            event.gc.drawImage(IconUtility.getIconImage(IiconPath.ICO_BAR_CLOSETWO, this.getClass()), event.width / 3,
                    1);
        }

    }

    /**
     * Function disposecomponent.
     *
     * @param editor the e
     */
    private void functionDisposecomponent(TableEditor editor) {
        if (editor != null) {
            if (editor.getEditor() != null) {
                editor.getEditor().dispose();
            }

            editor.dispose();
        }
    }

    /**
     * Sets the export import in progress.
     *
     * @param isExportImportInProgres the new export import in progress
     */
    public static void setExportImportInProgress(boolean isExportImportInProgres) {
        ProgressMonitorControl.isExportImportInProgress = isExportImportInProgres;
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
    private final class StatusBarPaintListener implements PaintListener {
        private String message;
        private ProgressBar bar;

        /**
         * Instantiates a new status bar paint listener.
         *
         * @param message the message
         * @param bar the bar
         */
        private StatusBarPaintListener(String message, ProgressBar bar) {
            this.message = message;
            this.bar = bar;
        }

        @Override
        public void paintControl(PaintEvent event) {
            String string = message;
            Point point = bar.getSize();

            org.eclipse.swt.graphics.FontMetrics fontMetrics = event.gc.getFontMetrics();
            int width = fontMetrics.getAverageCharWidth() * string.length();
            int height = fontMetrics.getHeight();
            if (parent.getDisplay() != null) {
                event.gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_BLACK));
            }
            event.gc.drawString(string, (point.x - width) / 2, (point.y - height) / 2, true);
        }

    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        UIElement.getInstance().removePartFromStack(UIConstants.UI_PART_PROGRESSBAR_ID);
    }
}
