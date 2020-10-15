/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.view.ui.saveif.ISaveablePart;
import com.huawei.mppdbide.view.ui.terminal.BatchDropDisplayUIManager;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropUIWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class BatchDropUIWindow implements ISaveablePart {
    private BatchDropDisplayUIManager uiManager;
    private Composite parentComposite;

    @Inject
    private MDirtyable dirtyHandler;

    /**
     * Instantiates a new batch drop UI window.
     */
    public BatchDropUIWindow() {
        this.parentComposite = null;
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param part the part
     */
    @Inject
    public void createPartControl(Composite parent, MPart part) {
        this.uiManager = (BatchDropDisplayUIManager) part.getObject();
        createResultWindow(parent);
        uiManager.setDirtyHandler(dirtyHandler);
    }

    private void createResultWindow(Composite parent) {
        this.parentComposite = new Composite(parent, SWT.TOP | SWT.NONE);
        this.uiManager.showResult(this.parentComposite);
    }

    /**
     * Sets the result display UI manager.
     *
     * @param newMgr the new result display UI manager
     */
    public void setResultDisplayUIManager(BatchDropDisplayUIManager newMgr) {
        this.uiManager = newMgr;
    }

    /**
     * Gets the UI manager.
     *
     * @return the UI manager
     */
    public BatchDropDisplayUIManager getUIManager() {
        return this.uiManager;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        if (this.uiManager != null) {
            this.uiManager.setDisposed();
            BatchDropDisplayUIManager.reduceWindowCount();
            if (this.uiManager.getWindowCount() == 0) {
                BatchDropDisplayUIManager.resetWindowCounter();
            }
        }
    }

    /**
     * Destroy source viewer.
     *
     * @param part the part
     */
    @PreDestroy
    public void destroySourceViewer(@Active MPart part) {
        UIElement.getInstance().removePartFromStack(part.getElementId());
        MemoryCleaner.cleanUpMemory();
    }

    /**
     * Reset data.
     */
    public void resetData() {
        this.uiManager.resetDataResult();
    }

    /**
     * Prompt user to save.
     *
     * @return the save
     */
    @Override
    public Save promptUserToSave() {
        if (0 == this.uiManager.handleStopOperation()) {
            return Save.NO;
        }

        return Save.CANCEL;
    }

    /**
     * Disable buttons.
     */
    public void disableButtons() {
        this.uiManager.disableButtons();
    }
}
