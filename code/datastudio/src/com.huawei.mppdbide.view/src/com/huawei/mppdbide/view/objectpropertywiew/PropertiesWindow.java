/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.objectpropertywiew;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.view.ui.AbstractUIWindow;
import com.huawei.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PropertiesWindow extends AbstractUIWindow {
    private ViewObjectPropertiesResultDisplayUIManager uiManager;

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param part the part
     */
    @PostConstruct
    public void createPartControl(Composite parent, MPart part) {
        super.setTab(part);
        this.uiManager = (ViewObjectPropertiesResultDisplayUIManager) part.getObject();
        if (uiManager != null) {
            this.uiManager.setPart(part);
        }
        createResultWindow(parent);
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    public ViewObjectPropertiesResultDisplayUIManager getResultDisplayUIManager() {
        return (ViewObjectPropertiesResultDisplayUIManager) super.getResultDisplayUIManager();
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

}
