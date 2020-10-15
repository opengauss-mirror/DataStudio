/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.Locale;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class EnglishLanguageHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class EnglishLanguageHandler extends AbstractLanguageHandler {

    /**
     * Instantiates a new english language handler.
     */
    public EnglishLanguageHandler() {
        super(0);
    }

    /**
     * English language execute.
     */
    @Execute
    public void englishLanguageExecute() {
        execute();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        if (!Locale.getDefault().toString().equals(MPPDBIDEConstants.CHINESE_LOCALE)) {
            return false;
        }
        return true;
    }
}
