/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.jface.text.contentassist.ContentAssistant;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLContentAssist.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
