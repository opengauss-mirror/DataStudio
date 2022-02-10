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

package org.opengauss.mppdbide.bl.contentassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.trie.PatriciaTrie;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ContentAssistUtil.
 * 
 */

public abstract class ContentAssistUtil implements ContentAssistUtilIf {
    private int posForFindPrefix;
    private boolean isInsert = false;
    private static final String OPENING_BRACE = "(";
    private static final char CLOSING_BRACE = ')';
    private static final char SEMI_COLON = ';';
    private static final String COMMA = ",";
    private int minPosition = 0;

    /**
     * Gets the matching.
     *
     * @param trie the trie
     * @param prefix the prefix
     * @return the matching
     */
    public SortedMap<String, ServerObject> getMatching(PatriciaTrie trie, String prefix) {
        String prefixShort = null;
        if (prefix.length() > 3) {
            prefixShort = prefix.substring(0, 2);
        } else {
            prefixShort = prefix;
        }
        SortedMap<String, ServerObject> map = getAllObjectWithPrefixCaseInsensitive(trie, prefixShort);
        SortedMap<Long, ServerObject> serverObjMap = new TreeMap<Long, ServerObject>();
        for (ServerObject obj : map.values()) {
            serverObjMap.put(obj.getOid(), obj);
        }
        SortedMap<String, ServerObject> resultMap = new TreeMap<String, ServerObject>();
        for (ServerObject obj : serverObjMap.values()) {
            if (obj.getSearchName().toLowerCase(Locale.ENGLISH).startsWith(prefix.toLowerCase(Locale.ENGLISH))) {
                resultMap.put(obj.getSearchName(), obj);
            }
        }

        return (SortedMap<String, ServerObject>) resultMap;
    }

    /**
     * Gets the all object with prefix case insensitive.
     *
     * @param trie the trie
     * @param text the text
     * @return the all object with prefix case insensitive
     */
    public SortedMap<String, ServerObject> getAllObjectWithPrefixCaseInsensitive(PatriciaTrie trie, String text) {
        SortedMap<String, ServerObject> map = new TreeMap<String, ServerObject>();
        ArrayList<String> prefixList = BLUtils.getAllCombinationsOfPrefix(text);
        for (String prefix : prefixList) {
            map.putAll(trie.prefixMap(String.valueOf(prefix)));
        }
        return map;
    }

    /**
     * Find string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @return the string
     */
    @Override
    public String findString(String pretextParam, List<Character> workBreakCharList) {
        String pretext = pretextParam;
        if (pretext.trim().isEmpty()) {
            return null;
        }
        posForFindPrefix = pretext.length() - 1;
        minPosition = getMinimumPosition(pretext.length());
        int oldPos = posForFindPrefix;
        ArrayList<String> strList = new ArrayList<String>();
        String lowerPretext = pretext.toLowerCase(Locale.ENGLISH);
        if (lowerPretext.contains("insert") && lowerPretext.contains("into")) {
            if (isValidForInsertStmt(pretext)) {
                return validateForInsertStmt(pretext, workBreakCharList, strList);
            } else {
                ArrayList<String> strList1 = new ArrayList<String>();
                int position = findDefaultString(pretext, workBreakCharList, oldPos, strList1, posForFindPrefix);
                char ch;
                do {
                    ch = pretext.charAt(position);
                    position--;
                } while (Character.isWhitespace(ch));
                if (ch == OPENING_BRACE.charAt(0) || ch == COMMA.charAt(0)) {
                    posForFindPrefix = ++position;
                    pretext = pretext.substring(0, posForFindPrefix + 1);

                    String str = validateForInsertStmt(pretext, workBreakCharList, strList);
                    if (this.isInsert) {
                        return str + '.' + strList1.get(0);
                    }
                } else {
                    findDefaultString(pretext, workBreakCharList, oldPos, strList, posForFindPrefix);
                }
            }
        } else {
            findDefaultString(pretext, workBreakCharList, oldPos, strList, posForFindPrefix);
        }
        return strList.get(0);

    }

    private int getMinimumPosition(int fullTextLength) {

        if (fullTextLength > MPPDBIDEConstants.MAX_PREFIX_SEARCH_LENGTH) {
            minPosition = fullTextLength - MPPDBIDEConstants.MAX_PREFIX_SEARCH_LENGTH;
        }
        return minPosition;
    }

    private String validateForInsertStmt(String pretext, List<Character> workBreakCharList, ArrayList<String> strList) {
        int oldPos;
        char ch;
        String retStr = null;
        if (pretext.endsWith(COMMA)) {
            while (posForFindPrefix >= minPosition) {
                ch = pretext.charAt(posForFindPrefix);
                if (ch == CLOSING_BRACE || ch == SEMI_COLON) {
                    return MPPDBIDEConstants.INVALID_INSERT;
                }
                if (ch == OPENING_BRACE.charAt(0)) {
                    break;
                }
                --posForFindPrefix;
            }

        }
        oldPos = getValidString(pretext, workBreakCharList, strList);
        while (posForFindPrefix >= minPosition && strList.size() < 3) {
            while (posForFindPrefix >= minPosition) {
                ch = pretext.charAt(posForFindPrefix);
                if (Character.isWhitespace(ch)) {
                    break;
                }
                --posForFindPrefix;
            }

            retStr = pretext.substring(posForFindPrefix + 1, oldPos);

            retStr = retStr.trim();
            if (!retStr.isEmpty()) {
                strList.add(retStr);
            }
            oldPos = posForFindPrefix;
            posForFindPrefix--;
        }

        if (strList.size() >= 3 && "INSERT".equalsIgnoreCase(strList.get(2))
                && "INTO".equalsIgnoreCase(strList.get(1))) {
            setInsert(true);
        } else {
            setInsert(false);
            return MPPDBIDEConstants.INVALID_INSERT;
        }
        return strList.get(0);
    }

    private boolean isValidForInsertStmt(String pretext) {
        return pretext.endsWith(OPENING_BRACE) || pretext.endsWith(COMMA);
    }

    private int getValidString(String pretext, List<Character> workBreakCharList, ArrayList<String> strList) {
        int oldPos;
        char ch;
        --posForFindPrefix;
        oldPos = posForFindPrefix;
        while (posForFindPrefix >= 0 && strList.size() <= 0) {
            ch = pretext.charAt(posForFindPrefix);
            if (!Character.isWhitespace(ch)) {

                posForFindPrefix = findDefaultString(pretext, workBreakCharList, oldPos, strList, posForFindPrefix);
                oldPos = posForFindPrefix;
            }
            --posForFindPrefix;
        }
        return oldPos;
    }

    /**
     * Checks if is insert.
     *
     * @return true, if is insert
     */
    public boolean isInsert() {
        return isInsert;
    }

    /**
     * Sets the insert.
     *
     * @param isInsert the new insert
     */
    public void setInsert(boolean isInsert) {
        this.isInsert = isInsert;
    }

    /**
     * Find default string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @param oldPos the old pos
     * @param strList the str list
     * @param posForFindPrefix the pos for find prefix
     * @return the int
     */
    public abstract int findDefaultString(String pretext, List<Character> workBreakCharList, int oldPos,
            ArrayList<String> strList, int posForFindPrefix);
}
