/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionProcessSelection.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBAssistantFunctionProcessSelection extends BrowserFunction {

    /**
     * Instantiates a new DB assistant function process selection.
     *
     * @param browser the browser
     * @param name the name
     */
    public DBAssistantFunctionProcessSelection(Browser browser, String name) {
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
        if (DBAssistantWindow.getViewer().getControl().isVisible()) {
            Clipboard cb = new Clipboard(Display.getDefault());
            cb.clearContents();

            cb.setContents(new Object[] {query}, new Transfer[] {TextTransfer.getInstance()});
        }
        return super.function(arguments);
    }
}
