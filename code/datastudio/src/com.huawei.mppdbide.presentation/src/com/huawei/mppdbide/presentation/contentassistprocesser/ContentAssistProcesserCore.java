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

package com.huawei.mppdbide.presentation.contentassistprocesser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.contentassist.ContentAssistProcesserData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ContentAssistProcesserCore.
 * 
 * @since 3.0.0
 */
public class ContentAssistProcesserCore {
    private LinkedHashMap<String, ServerObject> autoMap = null;
    private String[] currentPrefix = new String[0];
    private int replaceLength = 0;
    private ContentAssistProcesserData contentData;

    /**
     * Instantiates a new content assist processer core.
     *
     * @param database the database
     */
    public ContentAssistProcesserCore(Database database) {
        this.contentData = new ContentAssistProcesserData(database);
    }

    /**
     * Gets the context proposals.
     *
     * @param prefix the prefix
     * @param fullPretext the full pretext
     * @return the context proposals
     */
    public LinkedHashMap<String, ServerObject> getContextProposals(String prefix, String fullPretext) {
        // Handle empty String
        if (null == prefix) {
            autoSuggectForNullPrefix();
        } else {
            String[] prefixes = contentData.getSplitPrefixByDots(prefix);
            String[] unQuotedPrefix = unquotePrefixes(prefixes);
            boolean isEndsWithDot = CustomStringUtility.isEndsWithDot(prefix);
            int prefixLen = prefix.length();

            // Mark the object items for lazy loading of schema.
            setCurrentPrefix(unQuotedPrefix);
            if ((prefixLen != 0 && isEndsWithDot) || (prefixLen != 0 && contentData.isInsert()
                    && (fullPretext.endsWith("(") || fullPretext.endsWith(",")))) {
                autoMap = contentData.findExactMatchingObjects(unQuotedPrefix);
            } else {
                autoMap = contentData.findPrefixMatchingObjects(unQuotedPrefix);
                String lastWord = prefixes.length > 0 ? prefixes[prefixes.length - 1] : "";
                setReplaceLength(lastWord.length());
            }
        }

        return autoMap;
    }

    /**
     * Find string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @return the string
     */
    public String findString(String pretext, List<Character> workBreakCharList) {
        return contentData.getFindString(pretext, workBreakCharList);
    }

    private String[] unquotePrefixes(String[] prefixes) {
        String[] unquotedPrefixes = new String[prefixes.length];

        for (int i = 0; i < prefixes.length; i++) {
            unquotedPrefixes[i] = contentData.getRemovdLiteralEscapesAndQuotes(prefixes[i]);
        }

        return unquotedPrefixes;
    }

    /**
     * Auto suggect for null prefix.
     */
    public void autoSuggectForNullPrefix() {
        autoMap = contentData.findPrefixMatchingObjects(new String[] {""});
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
     * Sets the current prefix.
     *
     * @param currentPrefix the new current prefix
     */
    public void setCurrentPrefix(String[] currentPrefix) {
        this.currentPrefix = currentPrefix.clone();
    }

    /**
     * Gets the replace length.
     *
     * @return the replace length
     */
    public int getReplaceLength() {
        return replaceLength;
    }

    /**
     * Sets the replace length.
     *
     * @param replaceLength the new replace length
     */
    public void setReplaceLength(int replaceLength) {
        this.replaceLength = replaceLength;
    }

    /**
     * Checks if is any non loaded object.
     *
     * @return true, if is any non loaded object
     */
    public boolean isAnyNonLoadedObject() {
        return contentData.getNonLoaded().size() > 0;

    }

    /**
     * Find non loaded database objects on demand.
     *
     * @param connection the connection
     * @return the sorted map
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public LinkedHashMap<String, ServerObject> findNonLoadedDatabaseObjectsOnDemand(DBConnection connection)
            throws MPPDBIDEException {
        return contentData.findNonLoadedObjects(connection, currentPrefix);
    }
}
