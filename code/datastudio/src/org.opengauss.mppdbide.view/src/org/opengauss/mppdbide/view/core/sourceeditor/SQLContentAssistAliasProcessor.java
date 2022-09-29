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

package org.opengauss.mppdbide.view.core.sourceeditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.opengauss.mppdbide.bl.contentassist.ContentAssistProcesserData.AutoSuggestComparator;
import org.opengauss.mppdbide.bl.queryparser.ParseContext;
import org.opengauss.mppdbide.bl.serverdatacache.Alias;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.util.OpUtils;
import org.opengauss.mppdbide.presentation.contentassistprocesser.ContentAssistProcesserCore;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.aliasparser.AliasParserManager;
import org.opengauss.mppdbide.view.aliasparser.AliasRequestResponsePacket;
import org.opengauss.mppdbide.view.aliasparser.AliasRequestResponsePacketState;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLContentAssistAliasProcessor.
 *
 * @since 3.0.0
 */
public class SQLContentAssistAliasProcessor {

    private String prefix;
    private String fullContent;
    private int offset;
    private SortedMap<String, ServerObject> aliasMap;
    private List<Character> wordBreakCharList;
    private static final int WAIT_INTERVAL_FOR_ALIAS_PARSER = 10; /* ms */
    private static final int WAIT_ITERATION_FOR_ALIAS_PARSER = 300;
    private AtomicInteger uniqueIds;

    /**
     * Instantiates a new SQL content assist alias processor.
     *
     * @param inPrefix the in prefix
     * @param inFullContent the in full content
     * @param inOffset the in offset
     * @param inWordBreakCharList the in word break char list
     */
    public SQLContentAssistAliasProcessor(String inPrefix, String inFullContent, int inOffset,
            List<Character> inWordBreakCharList) {
        prefix = inPrefix;
        fullContent = inFullContent;
        offset = inOffset;
        uniqueIds = new AtomicInteger(0);
        aliasMap = null;
        wordBreakCharList = inWordBreakCharList;
    }

    /**
     * Compute alias proposal.
     *
     * @param core the core
     * @return true, if successful
     */
    public boolean computeAliasProposal(ContentAssistProcesserCore core) {
        /*
         * Null check for prefix is needed as it can be set to null in the
         * constructor
         */
        if (null == prefix) {
            return false;
        }

        boolean isPrefixEndsWithDot = CustomStringUtility.isEndsWithDot(prefix);
        HashMap<String, List<String>> aliasToTableNameMap = null;

        aliasToTableNameMap = parseAndGetAliasMap(fullContent, isPrefixEndsWithDot);

        OpUtils.setMap(aliasToTableNameMap);
        if (null != aliasToTableNameMap && aliasToTableNameMap.size() > 0) {
            /*
             * Assumption: There will only be 2 parts in the prefix Replace
             * alias in prefix by corresponding table name
             */
            String firstPrefixPart = null;
            String lastPrefixPart = null;
            if (!isPrefixEndsWithDot && !prefix.isEmpty()) {
                String[] splitPrefix = JSQLParserUtils.getSplitQualifiedName(prefix, false);
                if (null != splitPrefix && splitPrefix.length > 1) {
                    firstPrefixPart = splitPrefix[0];
                    lastPrefixPart = splitPrefix[splitPrefix.length - 1];
                }

                if (null != firstPrefixPart) {
                    prefix = firstPrefixPart.concat(".");
                }
            }
            prefix = findMatchingTableNameForAlias(aliasToTableNameMap, core);

            if (null != lastPrefixPart && null != prefix) {
                prefix = prefix.concat(lastPrefixPart);
            }
        }

        return true;
    }

    /**
     * Find matching table name for alias.
     *
     * @param aliasToTableNameMap the alias to table name map
     * @param core the core
     * @return the string
     */
    private String findMatchingTableNameForAlias(HashMap<String, List<String>> aliasToTableNameMap,
            ContentAssistProcesserCore core) {
        /* Replace Alias by its mapping table/view/column name */
        if (null != aliasToTableNameMap && !aliasToTableNameMap.isEmpty() && !prefix.isEmpty()) {
            String element = null;
            for (Entry<String, List<String>> entry : aliasToTableNameMap.entrySet()) {
                element = entry.getKey();
                if (CustomStringUtility.getFormattedStringForAliasCompare(element).equals(CustomStringUtility
                        .getFormattedStringForAliasCompare(prefix.substring(0, prefix.length() - 1)))) {
                    List<String> tableNamelist = aliasToTableNameMap.get(element);
                    /* Only add table name, not schema name */
                    if (tableNamelist.size() > 0) {
                        prefix = core.findString(tableNamelist.get(tableNamelist.size() - 1).concat("."),
                                wordBreakCharList);
                        break;
                    }
                }
            }
        }
        return prefix;
    }

    /**
     * Parses the and get alias map.Parse the query and get parsed data
     *
     * @param currentQuery the current query
     * @param isPrefixEndsWithDot the is prefix ends with dot
     * @return the hash map
     */
    private HashMap<String, List<String>> parseAndGetAliasMap(String currentQuery, boolean isPrefixEndsWithDot) {
        if (null == currentQuery) {
            return null;
        }

        /* Get unique packet id */
        int uniquePacketId = uniqueIds.incrementAndGet();

        /* Build the packet for alias parser */
        AliasRequestResponsePacket packet = new AliasRequestResponsePacket(currentQuery, uniquePacketId,
                isPrefixEndsWithDot, offset);

        /* Add packet to alias parser queue */
        AliasParserManager.getInstance().submitAliasParserJob(packet);

        HashMap<String, List<String>> aliasToTableNameMap = null;

        int aliasParserWaitCounter = WAIT_ITERATION_FOR_ALIAS_PARSER;

        /*
         * Wait for WAIT_ITERATION_FOR_ALIAS_PARSER X
         * WAIT_INTERVAL_FOR_ALIAS_PARSER ms at most
         */
        while (AliasRequestResponsePacketState.RESPONSE != packet.getPacketState() && aliasParserWaitCounter >= 0) {
            try {
                Thread.sleep(WAIT_INTERVAL_FOR_ALIAS_PARSER);
            } catch (InterruptedException e) {
                MPPDBIDELoggerUtility.warn("Warning: InterruptedException occurs");
                return aliasToTableNameMap;
            }
            aliasParserWaitCounter--;
        }

        if (null != packet.getPContext()) {
            /* Received response from alias parser in time */
            ParseContext aliasPc = packet.getPContext();
            aliasToTableNameMap = aliasPc.getAliasToTableNameMap();

            if (!isPrefixEndsWithDot) {
                List<String> matchedAliases = getMatchingAliasNames(prefix, aliasToTableNameMap);
                if (!matchedAliases.isEmpty()) {
                    aliasMap = getAliasProposals(matchedAliases);
                }
            }
        } else {
            /* Did not receive response from alias parser in time */
            AliasParserManager.getInstance().cancelAliasParserJob(uniquePacketId);
        }

        return aliasToTableNameMap;
    }

    /**
     * Gets the matching alias names.
     *
     * @param inPrefix the in prefix
     * @param aliasToTableNameMap the alias to table name map
     * @return the matching alias names
     */
    private ArrayList<String> getMatchingAliasNames(String inPrefix,
            HashMap<String, List<String>> aliasToTableNameMap) {
        ArrayList<String> matchedAliasNames = new ArrayList<String>(1);
        if (!aliasToTableNameMap.isEmpty()) {
            Set<String> aliasList = aliasToTableNameMap.keySet();
            for (String element : aliasList) {
                if (element.startsWith(inPrefix)) {
                    matchedAliasNames.add(element);
                }
            }
        }
        return matchedAliasNames;
    }

    /**
     * Gets the alias proposals.
     *
     * @param matchedAliases the matched aliases
     * @return the alias proposals
     */
    private SortedMap<String, ServerObject> getAliasProposals(List<String> matchedAliases) {
        SortedMap<String, ServerObject> aliasList = new TreeMap<String, ServerObject>(new AutoSuggestComparator());

        for (String element : matchedAliases) {
            Alias aliasObject = new Alias(element, OBJECTTYPE.ALIAS);
            aliasList.put(ServerObject.getQualifiedObjectNameHandleQuotes(aliasObject.getName()) + " - Alias",
                    aliasObject);
        }
        return aliasList;
    }

    /**
     * Gets the processed prefix.
     *
     * @return the processed prefix
     */
    public String getProcessedPrefix() {
        return this.prefix;
    }

    /**
     * Gets the computed alias map.
     *
     * @return the computed alias map
     */
    public SortedMap<String, ServerObject> getComputedAliasMap() {
        return this.aliasMap;
    }
}
