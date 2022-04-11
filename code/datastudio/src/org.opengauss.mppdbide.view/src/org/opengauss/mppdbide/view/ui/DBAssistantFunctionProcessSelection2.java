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

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionProcessSelection2.
 *
 * @since 3.0.0
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
