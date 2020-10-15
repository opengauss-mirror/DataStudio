/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.custom.CTabFolder;

import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ActivateObjectBrowserWindow.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ActivateObjectBrowserWindow {

    /**
     * Execute.
     *
     * @param modelService the model service
     * @param application the application
     */
    @Execute
    /**
     * Will activate the Object Browser window
     * 
     * @param application
     * @param service
     */
    public void execute(EModelService modelService, MApplication application) {
        MPartStack partStack = (MPartStack) modelService.find(UIConstants.UI_PARTSTACK_OBJECTBROWSER, application);
        if (partStack != null && partStack.getWidget() != null) {
            boolean isMinimized = ((CTabFolder) partStack.getWidget()).getMinimized();
            UIElement uiElementInstance = UIElement.getInstance();
            if (!isMinimized) {
                uiElementInstance.togglePart(UIConstants.UI_PART_OBJECT_BROWSER_ID, false,
                        UIConstants.UI_TOGGLE_OBJECT_BROWSER_MENU);
            } else {
                uiElementInstance.togglePart(UIConstants.UI_PART_OBJECT_BROWSER_ID, true,
                        UIConstants.UI_TOGGLE_OBJECT_BROWSER_MENU);
            }
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return UIDisplayFactoryProvider.getUIDisplayStateIf().isVersionCompatibile();
    }

}
