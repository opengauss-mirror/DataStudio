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

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.contentassist.ContentAssistant;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLContentAssist.
 *
 * @since 3.0.0
 */
public class SQLContentAssist extends ContentAssistant {

    private String[] currentPrefix = new String[0];

    /**
     * Checks if is proposal popup active.
     *
     * @return true, if is proposal popup active
     */
    @Override
    public boolean isProposalPopupActive() {
        return super.isProposalPopupActive();
    }

    /**
     * Hide.
     */
    @Override
    public void hide() {
        super.hide();
    }

    /**
     * Sets the current prefix.
     *
     * @param prefix the new current prefix
     */
    public void setCurrentPrefix(String[] prefix) {
        if (prefix != null) {
            currentPrefix = prefix.clone();
        }
    }

    /**
     * Gets the current prefix.
     *
     * @return the current prefix
     */
    public String[] getCurrentPrefix() {
        return currentPrefix.clone();
    }

    /**
     * Show possible completions.
     *
     * @return the string
     */
    @Override
    public String showPossibleCompletions() {
        SQLContentAssistProcessor.setLookupTemplates(false);
        return super.showPossibleCompletions();
    }

    /**
     * Show context information.
     *
     * @return the string
     */
    @Override
    public String showContextInformation() {
        SQLContentAssistProcessor.setLookupTemplates(true);
        return super.showPossibleCompletions();
    }
}
