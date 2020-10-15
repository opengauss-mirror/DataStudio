/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionProcessSelection2.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBAssistantFunctionProcessSelection2 extends BrowserFunction {

    /**
     * Instantiates a new DB assistant function process selection 2.
     *
     * @param browser the browser
     * @param name the name
     */
    public DBAssistantFunctionProcessSelection2(Browser browser, String name) {
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
        String query = arguments[0].toString();
        if (DBAssistantWindow.getViewer() != null && DBAssistantWindow.getViewer().getControl().isVisible()) {
            int offset = DBAssistantWindow.getViewer().getTextWidget().getCaretOffset();

            StringBuilder txt = new StringBuilder(DBAssistantWindow.getViewer().getTextWidget().getText());

            txt.insert(offset, query);

            DBAssistantWindow.getViewer().getDocument().set(txt.toString());
            DBAssistantWindow.getViewer().getTextWidget().setCaretOffset(offset + query.length());
        }
        return super.function(arguments);
    }
}
