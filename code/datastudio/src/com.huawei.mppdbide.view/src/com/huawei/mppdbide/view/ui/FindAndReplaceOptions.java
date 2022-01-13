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

/**
 * 
 * Title: class
 * 
 * Description: The Class FindAndReplaceOptions.
 *
 * @since 3.0.0
 */
public class FindAndReplaceOptions {

    private String searchText;
    private boolean wrapAround;
    private boolean backwardSearch;
    private boolean caseSensitive;
    private boolean wholeWord;
    private String replaceText;

    /**
     * Gets the search text.
     *
     * @return the search text
     */
    public String getSearchText() {
        return searchText;
    }

    /**
     * Sets the search text.
     *
     * @param text the new search text
     */
    public void setSearchText(String text) {
        this.searchText = text;
    }

    /**
     * Checks if is wrap around.
     *
     * @return true, if is wrap around
     */
    public boolean isWrapAround() {
        return wrapAround;
    }

    /**
     * Sets the wrap around.
     *
     * @param wrapAround the new wrap around
     */
    public void setWrapAround(boolean wrapAround) {
        this.wrapAround = wrapAround;
    }

    /**
     * Checks if is forward search.
     *
     * @return true, if is forward search
     */
    public boolean isForwardSearch() {
        return !isBackwardSearch();
    }

    /**
     * Checks if is backward search.
     *
     * @return true, if is backward search
     */
    public boolean isBackwardSearch() {
        return backwardSearch;
    }

    /**
     * Sets the backward search.
     *
     * @param backwardSearch the new backward search
     */
    public void setBackwardSearch(boolean backwardSearch) {
        this.backwardSearch = backwardSearch;
    }

    /**
     * Checks if is case sensitive.
     *
     * @return true, if is case sensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Sets the case sensitive.
     *
     * @param caseSensitive the new case sensitive
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Checks if is whole word.
     *
     * @return true, if is whole word
     */
    public boolean isWholeWord() {
        return wholeWord;
    }

    /**
     * Sets the whole word.
     *
     * @param wholeWord the new whole word
     */
    public void setWholeWord(boolean wholeWord) {
        this.wholeWord = wholeWord;
    }

    /**
     * Gets the replace text.
     *
     * @return the replace text
     */
    public String getReplaceText() {
        return replaceText;
    }

    /**
     * Sets the replace text.
     *
     * @param replaceText the new replace text
     */
    public void setReplaceText(String replaceText) {
        this.replaceText = replaceText;
    }

}
