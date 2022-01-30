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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import com.huawei.mppdbide.bl.contentassist.ContentAssistProcesserData;
import com.huawei.mppdbide.bl.contentassist.ContentAssistUtilIf;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLObjectLinkDetector.
 *
 * @since 3.0.0
 */
public class SQLObjectLinkDetector extends AbstractHyperlinkDetector {
    private Database database = null;
    private static final int OBJECTS_WITHOUT_SCHEMA = 1;
    private static final int OBJECTS_WITH_SCHEMA = 2;
    private static final int OBJECTS_ONLY_SCHEMAS = 3;
    private ContentAssistUtilIf contentAssistUtil;
    private ContentAssistProcesserData contentAssistProcesserData;

    /**
     * Detect hyperlinks.
     *
     * @param textViewer the text viewer
     * @param region the region
     * @param canShowMultipleHyperlinks the can show multiple hyperlinks
     * @return the i hyperlink[]
     */
    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        if (null == getDabase() || !getDabase().isConnected()) {
            return null;
        }
        contentAssistProcesserData = new ContentAssistProcesserData(getDabase());
        contentAssistUtil = contentAssistProcesserData.getContentAssistUtil();

        IDocument doc = textViewer.getDocument();
        int offset = region.getOffset();
        String fullContent = doc.get();
        int searchEnd = getSearchEnd(fullContent, offset);

        String neededstring = fullContent.substring(0, searchEnd);
        int searchStart = getSearchStartPos(neededstring);
        neededstring = getSearchString(neededstring, searchStart);

        String searchPrefix = neededstring;

        if (null == searchPrefix) {
            return null;
        }

        String[] prefixes = contentAssistUtil.getPrefixHyperLink(searchPrefix);

        if (prefixes.length == 2) {
            if (prefixes[0].trim().startsWith("\"")) {
                searchStart = searchStart + 1;
            }
        }
        SortedMap<String, ServerObject> map = resolveCurrentItemProposal(getDabase(), prefixes);

        if (null == map || 0 == map.size()) {
            return null;
        }
        IHyperlink[] links = new IHyperlink[map.size()];
        int index = 0;
        for (ServerObject obj : map.values()) {

            links[index] = createHyperLink(searchStart, searchEnd - searchStart, obj);
            index++;
        }
        return links;
    }

    /**
     * Gets the search string.
     *
     * @param fullPretext the full pretext
     * @param pos the pos
     * @return the search string
     */
    private String getSearchString(String fullPretext, int pos) {
        String retStr;

        if (-1 == pos) {
            return null;
        }

        retStr = fullPretext.substring(pos);

        if ("".equals(retStr)) {
            return null;
        }
        return retStr;
    }

    /**
     * Resolve current item proposal.
     *
     * @param db the db
     * @param prefixes the prefixes
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> resolveCurrentItemProposal(Database db, String[] prefixes) {
        int prefixLength = prefixes.length;
        SortedMap<String, ServerObject> retMap = new TreeMap<String, ServerObject>();

        if (null == db || !db.isConnected()) {
            return null;
        }
        switch (prefixLength) {
            case 1: {
                retMap = handleOnPrefLengthisOne(db, prefixes, prefixLength);
                break;
            }
            case 2: {
                retMap = handleOnprefLengthisTwo(db, prefixes, prefixLength);
                break;
            }
            default: {
                break;
            }
        }

        return retMap;
    }

    /**
     * Handle onpref lengthis two.
     *
     * @param db the db
     * @param prefixes the prefixes
     * @param prefixLength the prefix length
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> handleOnprefLengthisTwo(Database db, String[] prefixes, int prefixLength) {
        SortedMap<String, ServerObject> retMap;
        String strPrefix = "";
        strPrefix = prefixes[0];
        // Can be Schema.Table
        retMap = new TreeMap<String, ServerObject>();
        retMap = findMatchingCaseInsensitiveObjects(db, strPrefix, retMap, 3);
        if (!strPrefix.contains("\"")) {
            retMap = getMatchingObjectsIfContaindQuotes(db, retMap, strPrefix);
        }
        strPrefix = prefixes[1];
        SortedMap<String, ServerObject> retMap1 = findMatchingCaseInsensitiveObjects(db, strPrefix, retMap,
                prefixLength);
        if (!strPrefix.contains("\"")) {
            handleIfPrefContainQuotes(db, prefixLength, retMap, strPrefix, retMap1);
        }
        retMap = new TreeMap<String, ServerObject>();
        retMap.putAll(retMap1);
        // Get the object and find the child
        return retMap;
    }

    /**
     * Gets the matching objects if containd quotes.
     *
     * @param db the db
     * @param retMapParam the ret map param
     * @param strPrefix the str prefix
     * @return the matching objects if containd quotes
     */
    private SortedMap<String, ServerObject> getMatchingObjectsIfContaindQuotes(Database db,
            SortedMap<String, ServerObject> retMapParam, String strPrefix) {
        SortedMap<String, ServerObject> retMap = retMapParam;
        if (!strPrefix.equals(strPrefix.toUpperCase(Locale.ENGLISH))) {
            retMap = findMatchingCaseInsensitiveObjects(db, strPrefix.toUpperCase(Locale.ENGLISH), retMap, 3);
        }
        if (!strPrefix.equals(strPrefix.toLowerCase(Locale.ENGLISH))) {
            retMap = findMatchingCaseInsensitiveObjects(db, strPrefix.toLowerCase(Locale.ENGLISH), retMap, 3);
        }
        return retMap;
    }

    /**
     * Handle if pref contain quotes.
     *
     * @param db the db
     * @param prefixLength the prefix length
     * @param retMap the ret map
     * @param strPrefix the str prefix
     * @param retMap1 the ret map 1
     */
    private void handleIfPrefContainQuotes(Database db, int prefixLength, SortedMap<String, ServerObject> retMap,
            String strPrefix, SortedMap<String, ServerObject> retMap1) {
        if (!strPrefix.equals(strPrefix.toUpperCase(Locale.ENGLISH))) {
            retMap1.putAll(findMatchingCaseInsensitiveObjects(db, strPrefix.toUpperCase(Locale.ENGLISH), retMap,
                    prefixLength));
        }
        if (!strPrefix.equals(strPrefix.toLowerCase(Locale.ENGLISH))) {
            retMap1.putAll(findMatchingCaseInsensitiveObjects(db, strPrefix.toLowerCase(Locale.ENGLISH), retMap,
                    prefixLength));
        }
    }

    /**
     * Handle on pref lengthis one.
     *
     * @param db the db
     * @param prefixes the prefixes
     * @param prefixLength the prefix length
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> handleOnPrefLengthisOne(Database db, String[] prefixes, int prefixLength) {
        SortedMap<String, ServerObject> retMap;
        String strPrefix = "";
        strPrefix = prefixes[0];

        // 1. Table name
        retMap = new TreeMap<String, ServerObject>();
        retMap = findMatchingCaseInsensitiveObjects(db, strPrefix, retMap, prefixLength);
        if (!strPrefix.contains("\"")) {
            if (!strPrefix.equals(strPrefix.toUpperCase(Locale.ENGLISH))) {
                retMap = findMatchingCaseInsensitiveObjects(db, strPrefix.toUpperCase(Locale.ENGLISH), retMap,
                        prefixLength);
            }
            if (!strPrefix.equals(strPrefix.toLowerCase(Locale.ENGLISH))) {
                retMap = findMatchingCaseInsensitiveObjects(db, strPrefix.toLowerCase(Locale.ENGLISH), retMap,
                        prefixLength);
            }
        }
        return retMap;
    }

    /**
     * Find matching case insensitive objects.
     *
     * @param db the db
     * @param prefix the prefix
     * @param retMapParam the ret map param
     * @param objLevel the obj level
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> findMatchingCaseInsensitiveObjects(Database db, String prefix,
            SortedMap<String, ServerObject> retMapParam, int objLevel) {
        SortedMap<String, ServerObject> retMap = retMapParam;
        if (OBJECTS_WITHOUT_SCHEMA == objLevel) {
            retMap.putAll(contentAssistUtil.findExactMatchingTables(prefix));
            retMap.putAll(contentAssistUtil.findExactMatchingDebugObjects(prefix));
            retMap.putAll(contentAssistUtil.findExactMatchingViews(prefix));
        }
        if (OBJECTS_ONLY_SCHEMAS == objLevel) {
            retMap.putAll(findMatchingChildObjects(prefix));
        }
        if (OBJECTS_WITH_SCHEMA == objLevel) {
            retMap = findMatchingInChild(retMap, prefix);
        }

        return retMap;
    }

    /**
     * Find matching child objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findMatchingChildObjects(String prefix) {
        SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
        retObj.putAll(contentAssistUtil.findExactMatchingNamespaces(prefix));
        retObj.putAll(contentAssistUtil.findExactMatchingTables(prefix));
        retObj.putAll(contentAssistUtil.findExactMatchingDebugObjects(prefix));
        retObj.putAll(contentAssistUtil.findExactMatchingViews(prefix));
        retObj.putAll(contentAssistUtil.findExactMatchingSequences(prefix));
        retObj.putAll(contentAssistUtil.findExactMatchingSynonyms(prefix));

        return retObj;

    }

    /**
     * Find matching in child.
     *
     * @param servObjs the serv objs
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, ServerObject> findMatchingInChild(SortedMap<String, ServerObject> servObjs,
            String prefix) {
        SortedMap<String, ServerObject> found = new TreeMap<String, ServerObject>();
        for (ServerObject servObj : servObjs.values()) {
            switch (servObj.getType()) {
                case NAMESPACE: {
                    // Redundant check, but its required for static tools
                    if (servObj instanceof Namespace) {
                        Namespace ns = (Namespace) servObj;
                        found.putAll(ns.findMatchingHyperlink(prefix));
                    }
                    break;
                }
                default: {
                    // Ignore other types
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Gets the search end.
     *
     * @param fullContent the full content
     * @param offset the offset
     * @return the search end
     */
    private int getSearchEnd(String fullContent, int offset) {
        if (fullContent.trim().isEmpty()) {
            return 0;
        }

        int pos = offset;
        int boundary = fullContent.length();
        char ch = '\0';
        List<Character> charList = new ArrayList<Character>(25);
        addCharsToCharList(charList);
        while (pos < boundary) {
            ch = fullContent.charAt(pos);

            if (!charList.isEmpty() && !charList.contains(ch) && !Character.isJavaIdentifierPart(ch)) {
                break;
            }
            ++pos;
        }

        return pos;
    }

    /**
     * Creates the hyper link.
     *
     * @param offset the offset
     * @param length the length
     * @param obj the obj
     * @return the SQL editor hyper link
     */
    private SQLEditorHyperLink createHyperLink(int offset, int length, ServerObject obj) {
        return createHyperLink(new Region(offset, length), obj);
    }

    /**
     * Creates the hyper link.
     *
     * @param region the region
     * @param obj the obj
     * @return the SQL editor hyper link
     */
    private SQLEditorHyperLink createHyperLink(Region region, ServerObject obj) {
        return new SQLEditorHyperLink(region, obj);
    }

    /**
     * Gets the search start pos.
     *
     * @param fullPretext the full pretext
     * @return the search start pos
     */
    private int getSearchStartPos(String fullPretext) {
        if (fullPretext.trim().isEmpty()) {
            return -1;
        }

        int pos = fullPretext.length() - 1;
        char ch = '\0';
        List<Character> charList = new ArrayList<Character>(25);
        addCharsToCharList(charList);
        while (pos > 0) {
            ch = fullPretext.charAt(pos);

            charList.add('.');

            if (!Character.isJavaIdentifierPart(ch) && !charList.isEmpty() && !charList.contains(ch)) {
                break;
            }
            --pos;
        }

        return 0 == pos ? 0 : pos + 1;
    }

    /**
     * Adds the chars to char list.
     *
     * @param charList the char list
     */
    private void addCharsToCharList(List<Character> charList) {
        charList.add('"');
        charList.add('*');
        charList.add('&');
        charList.add('$');
        charList.add('_');
        charList.add('#');
        charList.add('@');
        charList.add('+');
        charList.add('-');
        charList.add('/');
        charList.add('<');
        charList.add('>');
        charList.add('=');
        charList.add('~');
        charList.add('!');
        charList.add('%');
        charList.add('^');
        charList.add('|');
        charList.add('`');
        charList.add('?');
        charList.add('\'');
        charList.add('\\');
        charList.add(',');
    }

    /**
     * Sets the database.
     *
     * @param db the new database
     */
    public void setDatabase(Database db) {
        this.database = db;
    }

    /**
     * Gets the dabase.
     *
     * @return the dabase
     */
    public Database getDabase() {
        return this.database;
    }

}
