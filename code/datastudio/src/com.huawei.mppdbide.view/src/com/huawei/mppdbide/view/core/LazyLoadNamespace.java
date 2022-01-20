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

package com.huawei.mppdbide.view.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ILazyLoadObject;
import com.huawei.mppdbide.bl.serverdatacache.SystemNamespace;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class LazyLoadNamespace.
 *
 * @since 3.0.0
 */
public class LazyLoadNamespace extends UIJob {
    private List<ILazyLoadObject> namespaces;
    private List<ILazyLoadObject> toBeLoaded;
    private TreeViewer viewer;
    private Map<ILazyLoadObject, Boolean> loadStatus;

    private static final int LOAD_RECHECK_SCHEDULE_TIME = 500;
    private LazyBackendLoader loader;
    private StatusMessage statusMsg;
    private String objectName;
    private IExecTimer timer;
    private boolean needExpandViewer;
    private boolean isUserSchema;
    private Database db;

    /**
     * Instantiates a new lazy load namespace.
     *
     * @param jobDisplay the job display
     * @param name the name
     * @param namespaceList the namespace list
     * @param viewer the viewer
     * @param statusMsg the status msg
     * @param objectName the object name
     * @param timer the timer
     * @param expandObjectInViewer the expand object in viewer
     * @param isUserSchema the is user schema
     */
    public LazyLoadNamespace(Display jobDisplay, String name, List<ILazyLoadObject> namespaceList, TreeViewer viewer,
            StatusMessage statusMsg, String objectName, IExecTimer timer, boolean expandObjectInViewer,
            boolean isUserSchema) {
        super(jobDisplay, name);
        this.namespaces = namespaceList;
        this.viewer = viewer;
        this.statusMsg = statusMsg;
        this.loadStatus = new HashMap<ILazyLoadObject, Boolean>(5);
        this.objectName = objectName;
        this.timer = timer;
        this.needExpandViewer = expandObjectInViewer;
        this.isUserSchema = isUserSchema;
    }

    /**
     * Run in UI thread.
     *
     * @param monitor the monitor
     * @return the i status
     */
    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        toBeLoaded = new ArrayList<ILazyLoadObject>(namespaces.size());
        boolean isAllLoaded = true;

        for (ILazyLoadObject ns : namespaces) {
            if (ns == null || viewer == null) {
                return Status.OK_STATUS;
            }
            db = ns.getDatabase();
            if (ns.isNotLoaded()) {
                loadSettingByUserScheme(ns);
                ns.setLoadingInProgress();
                handleExpandState(ns, true);

                viewer.refresh(ns, true);
                toBeLoaded.add(ns);
                isAllLoaded = false;
                this.loadStatus.put(ns, false);
            } else if (ns.isLoadingInProgress()) {
                handleExpandState(ns, true);
                isAllLoaded = false;

            } else if (validateNamespace(ns)) {
                handleExpandState(ns, true);

                viewer.refresh(ns, true);
                updateObjectBrowserNameSpace(ns);
                this.loadStatus.put(ns, true);
            }
        }

        if (null != loader && loader.isFailed()) {
            return Status.OK_STATUS;
        }

        if (toBeLoaded.size() > 0) {
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForSchema(objectName, db.getDbName(),
                    db.getServerName(), IMessagesConstants.LAZYLOAD_NAMESPACE_PROGRESS_NAME);
            loader = new LazyNamespaceBackendLoader(progressLabel, toBeLoaded, statusMsg, objectName, timer);
            loader.setTaskDB(db);
            loader.schedule();
        } else {
            updateBottomStatusBar();
        }

        scheduleLoadWhenAllNotLoaded(isAllLoaded);

        return Status.OK_STATUS;
    }

    private boolean validateNamespace(ILazyLoadObject ns) {
        return this.loadStatus != null && ns != null && (ns.isLoaded() || ns.isLoadFailed())
                && !this.loadStatus.isEmpty() && this.loadStatus.get(ns) != null && !this.loadStatus.get(ns);
    }

    /**
     * Update bottom status bar.
     */
    private void updateBottomStatusBar() {
        // Remove the progress bar on the bottom status bar as there are no
        // namespaces to be loaded
        if (null != statusMsg) {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bottomStatusBar != null) {
                bottomStatusBar.hideStatusbar(statusMsg);
            }
        }
    }

    /**
     * Update object browser name space.
     *
     * @param ns the ns
     */
    private void updateObjectBrowserNameSpace(ILazyLoadObject ns) {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (null != objectBrowserModel) {
            if (ns instanceof UserNamespace) {
                objectBrowserModel.updatObject(ns.getDatabase().getUserNamespaceGroup());
            } else if (ns instanceof SystemNamespace) {
                objectBrowserModel.updatObject(ns.getDatabase().getSystemNamespaceGroup());
            }
        }
    }

    /**
     * Load setting by user scheme.
     *
     * @param ns the ns
     */
    private void loadSettingByUserScheme(ILazyLoadObject ns) {
        if (isUserSchema) {
            ns.getDatabase().setLoadingUserNamespaceInProgress(true);
        } else {
            ns.getDatabase().setLoadingSystemNamespaceInProgress(true);
        }
    }

    /**
     * Handle expand state.
     *
     * @param ns the ns
     * @param expanded the expanded
     */
    private void handleExpandState(ILazyLoadObject ns, boolean expanded) {
        if (this.needExpandViewer) {
            viewer.setExpandedState(ns, expanded);
        }
    }

    /**
     * Schedule load when all not loaded.
     *
     * @param isAllLoaded the is all loaded
     */
    private void scheduleLoadWhenAllNotLoaded(boolean isAllLoaded) {
        if (!isAllLoaded) {
            schedule(LOAD_RECHECK_SCHEDULE_TIME);
        }
    }
}
