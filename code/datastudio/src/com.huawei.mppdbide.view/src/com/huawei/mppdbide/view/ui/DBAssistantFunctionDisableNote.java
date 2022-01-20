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

package com.huawei.mppdbide.view.ui;

import java.io.IOException;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;

import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBAssistantFunctionDisableNote.
 *
 * @since 3.0.0
 */
public class DBAssistantFunctionDisableNote extends BrowserFunction {

    /**
     * The Constant DB_ASSISTANT_NOTE_DISABLE.
     */
    public static final String DB_ASSISTANT_NOTE_DISABLE = "environment.sessionsetting.datastudiodbassistantnotedisable";

    /**
     * Instantiates a new DB assistant function disable note.
     *
     * @param browser the browser
     * @param name the name
     */
    public DBAssistantFunctionDisableNote(Browser browser, String name) {
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
        PreferenceWrapper.getInstance().getPreferenceStore().setValue(DB_ASSISTANT_NOTE_DISABLE, true);
        try {
            PreferenceWrapper.getInstance().getPreferenceStore().save();
        } catch (IOException e) {
            DBAssistantWindow.setNoNeedToPrint(true);
        }
        return super.function(arguments);
    }
}
