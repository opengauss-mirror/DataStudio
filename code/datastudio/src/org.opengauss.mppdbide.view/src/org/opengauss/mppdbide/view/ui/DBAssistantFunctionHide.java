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

import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.eclipse.dependent.EclipseInjections;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionHide.
 *
 * @since 3.0.0
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
