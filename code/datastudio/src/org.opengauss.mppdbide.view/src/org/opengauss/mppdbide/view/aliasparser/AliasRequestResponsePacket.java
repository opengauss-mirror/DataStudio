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

package org.opengauss.mppdbide.view.aliasparser;

import java.util.ArrayList;

import org.opengauss.mppdbide.bl.queryparser.ParseContext;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AliasRequestResponsePacket.
 *
 * @since 3.0.0
 */
public class AliasRequestResponsePacket {
    private int packetId;
    /*
     * Request
     */
    private String queries;
    /*
     * Response
     */
    private ParseContext pContext;
    /*
     * State
     */
    private AliasRequestResponsePacketState state;
    /*
     * prefix with dot flag
     */
    private boolean isPrefixEndsWithDot;
    /*
     * prefix offset
     */
    private int offset;

    private ArrayList<int[]> queryBounds;
    private static final int INVALID_ARRAY_INDEX = -1;

    /**
     * Instantiates a new alias request response packet.
     *
     * @param queries the queries
     * @param packetId the packet id
     * @param isPrefixEndsWithDot the is prefix ends with dot
     * @param offset the offset
     */
    public AliasRequestResponsePacket(String queries, int packetId, boolean isPrefixEndsWithDot, int offset) {
        this.packetId = packetId;
        this.queries = queries;
        this.pContext = null;
        this.state = AliasRequestResponsePacketState.INIT;
        this.isPrefixEndsWithDot = isPrefixEndsWithDot;
        this.offset = offset;
        this.queryBounds = new ArrayList<int[]>(1);
    }

    /**
     * Gets the p context.
     *
     * @return the p context
     */
    public ParseContext getPContext() {
        return pContext;
    }

    /**
     * Sets the p context.
     *
     * @param inPContext the new p context
     */
    public void setPContext(ParseContext inPContext) {
        this.pContext = inPContext;
    }

    /**
     * Gets the queries.
     *
     * @return the queries
     */
    public String getQueries() {
        return this.queries;
    }

    /**
     * Gets the packet id.
     *
     * @return the packet id
     */
    public int getPacketId() {
        return this.packetId;
    }

    /**
     * Gets the packet state.
     *
     * @return the packet state
     */
    public AliasRequestResponsePacketState getPacketState() {
        return this.state;
    }

    /**
     * Change state.
     *
     * @param newState the new state
     * @return true, if successful
     */
    public boolean changeState(AliasRequestResponsePacketState newState) {
        if (stateChangeAllowed(newState)) {
            this.state = newState;
            return true;
        }
        return false;
    }

    /* Check if state transition of packet is valid */
    private boolean stateChangeAllowed(AliasRequestResponsePacketState newState) {
        boolean returnValue;
        if (MPPDBIDELoggerUtility.isDebugEnabled()) {
            MPPDBIDELoggerUtility.debug(
                    "AliasRequestResponsePacket state change request received: FROM " + this.state + " TO " + newState);
        }
        switch (this.state) {
            case INIT: {
                if (newState == AliasRequestResponsePacketState.REQUEST) {
                    returnValue = true;
                } else {
                    returnValue = false;
                }
                break;
            }
            case REQUEST: {
                if (newState == AliasRequestResponsePacketState.RESPONSE) {
                    returnValue = true;
                } else {
                    returnValue = false;
                }
                break;
            }
            default: {
                returnValue = false;
                break;
            }
        }
        return returnValue;
    }

    private boolean getPrefixEndsWithDot() {
        return isPrefixEndsWithDot;
    }

    private int getOffset() {
        return offset;
    }

    /**
     * Calculate query extents.
     *
     * @param queryArray the query array
     */
    public void calculateQueryExtents(ArrayList<String> queryArray) {
        int prevOffset = 0;
        int queryLength = 0;
        /* Calculate start and end offsets for each query */
        for (int item = 0; item < queryArray.size(); item++) {
            queryLength = queryArray.get(item).length();
            queryBounds.add(new int[] {prevOffset, prevOffset + queryLength});
            prevOffset += queryLength + 1;
        }
    }

    private int getCurrentQuery() {
        for (int element = 0; element < queryBounds.size(); element++) {
            if (offset >= queryBounds.get(element)[0] && offset <= queryBounds.get(element)[1]) {
                return element;
            }
        }
        return INVALID_ARRAY_INDEX;
    }

    private String addDummyToken(String currentQuery, int number) {
        return currentQuery.substring(0, number).concat("##unknown").concat(currentQuery.substring(number));
    }

    /**
     * Extract formatted current query.
     *
     * @param queryArray the query array
     * @return the string
     */
    public String extractFormattedCurrentQuery(ArrayList<String> queryArray) {
        int currentQueryIndex = getCurrentQuery();
        String currentQuery = null;
        if (currentQueryIndex != INVALID_ARRAY_INDEX) {
            currentQuery = queryArray.get(currentQueryIndex);
        }

        /* Add a dummy string at current cursor location : Parser requirement */
        if (null != currentQuery && getPrefixEndsWithDot() && currentQueryIndex < queryBounds.size()
                && queryBounds.get(currentQueryIndex).length > 0) {
            currentQuery = addDummyToken(currentQuery, getOffset() - queryBounds.get(currentQueryIndex)[0]);
        }
        if (null != currentQuery) {
            currentQuery = currentQuery.concat(";");
        }
        this.queryBounds.clear();
        /* End query by semicolon */
        return currentQuery;
    }
}
