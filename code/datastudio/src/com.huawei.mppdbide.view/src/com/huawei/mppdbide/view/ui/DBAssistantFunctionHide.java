/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionHide.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBAssistantFunctionHide extends BrowserFunction {

    /**
     * Instantiates a new DB assistant function hide.
     *
     * @param browser the browser
     * @param name the name
     */
    public DBAssistantFunctionHide(Browser browser, String name) {
        super(browser, name);
    }

    /**
     * Function.
     *
     * @param arguments the arguments
     * @return the object
     */
    @Override
    public Object function(Object[] arguments) {
        if ("hide".equals(arguments[0])) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    MPartStack part = (MPartStack) EclipseInjections.getInstance().getMS()
                            .find(UIConstants.UI_PARTSTACK_ID_SQL_ASSISTANT, EclipseInjections.getInstance().getApp());

                    if (part != null && part.getTags() != null
                            && !part.getTags().contains(IPresentationEngine.MINIMIZED)) {
                        part.getTags().add(IPresentationEngine.MINIMIZED);
                    }
                }
            });
        }
        return super.function(arguments);
    }
}
