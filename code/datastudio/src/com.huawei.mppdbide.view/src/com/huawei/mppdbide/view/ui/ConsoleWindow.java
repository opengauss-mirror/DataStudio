/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConsoleWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConsoleWindow {
    private ConsoleCoreWindow consoleCoreWindow;

    @Inject
    private EMenuService menuService;

    /**
     * Instantiates a new console window.
     *
     * @param parent the parent
     */
    @Inject
    public ConsoleWindow(Composite parent) {
        consoleCoreWindow = ConsoleCoreWindow.getInstance();
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param modelService the model service
     * @param application the application
     * @param partService the part service
     */
    @PostConstruct
    public void createPartControl(Composite parent, EModelService modelService, MApplication application,
            EPartService partService) {

        // Which ever control is being created first, it has to set the
        // partService and modelService to UIElement. those will be used on
        // further calls.
        IDEStartup.getInstance().init(partService, modelService, application);

        consoleCoreWindow.createConsoleWindow(parent);
        menuService.registerContextMenu(consoleCoreWindow.getControl(), UIConstants.UI_CONSOLE_MENU);
    }

    /**
     * On focus.
     */
    @Focus
    public void onFocus() {
        consoleCoreWindow.onFocus();
    }

    /**
     * Gets the console core.
     *
     * @return the console core
     */
    public ConsoleCoreWindow getConsoleCore() {
        return consoleCoreWindow;
    }

}
