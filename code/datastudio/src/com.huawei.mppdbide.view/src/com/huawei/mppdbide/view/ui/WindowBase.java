/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.view.core.TableWindowCore;

/**
 * Title: class
 * Description: The Class WindowBase.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class WindowBase<T> {
    /**
     * the instance of table window core base
     */
    protected TableWindowCore<T> tableWindowCore;
    @Inject
    private EPartService partService;

    /**
     * Instantiates a new window base.
     */
    public WindowBase() {
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param modelService the model service
     * @param application the application
     */
    @Inject
    public void createPartControl(Composite parent, EModelService modelService, MApplication application) {
        // Which ever control is being created first, it has to set the
        // partService and modelService to UIElement. those will be used on
        // further calls.
        IDEStartup.getInstance().init(partService, modelService, application);
        tableWindowCore.createPartControl(parent, partService);
    }

    /**
     * Clear.
     */
    public void clear() {
        tableWindowCore.clear();
    }

    /**
     * Refresh.
     */
    public void refresh() {
        tableWindowCore.refresh();
    }
}