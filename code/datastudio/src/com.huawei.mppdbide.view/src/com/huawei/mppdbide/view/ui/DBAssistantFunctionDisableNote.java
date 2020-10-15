/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
