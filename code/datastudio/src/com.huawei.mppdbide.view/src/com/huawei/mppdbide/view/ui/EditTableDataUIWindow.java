/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.view.core.edittabledata.AbstractEditTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableDataUIWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class EditTableDataUIWindow extends AbstractUIWindow {

    private AbstractEditTableDataResultDisplayUIManager uiManager;

    /**
     * Instantiates a new edits the table data UI window.
     */
    public EditTableDataUIWindow() {
        this.parentComposite = null;
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    public AbstractEditTableDataResultDisplayUIManager getResultDisplayUIManager() {
        return (AbstractEditTableDataResultDisplayUIManager) super.getResultDisplayUIManager();
    }

    /**
     * Gets the display UI manager.
     *
     * @return the display UI manager
     */
    @Override
    protected AbstractResultDisplayUIManager getDisplayUIManager() {
        return this.uiManager;
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     * @param part the part
     */
    @Inject
    public void createPartControl(Composite parent, EPartService partService, EModelService modelService,
            MApplication application, MPart part) {
        super.setTab(part);
        // Which ever control is being created first, it has to set the
        // partService and modelService to UIElement. those will be used on
        // further calls.
        IDEStartup.getInstance().init(partService, modelService, application);
        this.uiManager = (AbstractEditTableDataResultDisplayUIManager) part.getObject();
        this.uiManager.setPart(part);
        createResultWindow(parent);
        uiManager.setDirtyHandler(dirtyHandler);
    }

    /**
     * Creates the result window.
     *
     * @param parent the parent
     */
    public void createResultWindow(Composite parent) {
        this.parentComposite = new Composite(parent, SWT.TOP | SWT.NONE);
        this.uiManager.showResult(this.parentComposite);
    }

    /**
     * Sets the result display UI manager.
     *
     * @param newMgr the new result display UI manager
     */
    public void setResultDisplayUIManager(AbstractEditTableDataResultDisplayUIManager newMgr) {
        this.uiManager = newMgr;
    }

    /**
     * On focus.
     */
    @Focus
    public void onFocus() {
        this.uiManager.onFocus();
    }
}
