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

package org.opengauss.mppdbide.view.handler;

import java.util.Locale;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class EnglishLanguageHandler.
 *
 * @since 3.0.0
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
