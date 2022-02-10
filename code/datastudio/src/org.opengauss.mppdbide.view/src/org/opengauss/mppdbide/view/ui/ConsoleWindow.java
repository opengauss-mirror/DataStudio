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

package org.opengauss.mppdbide.view.ui;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.view.core.ConsoleCoreWindow;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConsoleWindow.
 *
 * @since 3.0.0
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
