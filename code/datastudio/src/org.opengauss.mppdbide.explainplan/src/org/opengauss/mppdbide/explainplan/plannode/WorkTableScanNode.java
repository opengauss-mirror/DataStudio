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

package org.opengauss.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class WorkTableScanNode.
 *
 * @since 3.0.0
 */
public class WorkTableScanNode extends CTEScanNode {

    /**
     * The filter.
     */
    @SerializedName("Filter")
    protected String filter = "";

    /**
     * The rows removed by filter.
     */
    @SerializedName("Rows Removed by Filter")
    protected String rowsRemovedByFilter = "";

    /**
     * Instantiates a new work table scan node.
     */
    public WorkTableScanNode() {
        super();
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        return getNodeSpecific();
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    @Override
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (!"".equals(filter)) {
            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_WORKTABLESCANNODE_FILTER))
                    .append(": ").append(StringEscapeUtils.escapeHtml(filter));
            list.add(sb.toString());
        }

        if (!"".equals(rowsRemovedByFilter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(MessageConfigLoader
                    .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_WORKTABLESCANNODE_ROWREMOVEDFILTER)).append(": ")
                    .append(rowsRemovedByFilter);
            list.add(sb.toString());
        }

        return list;
    }

    /**
     * Gets the node specific properties.
     *
     * @return the node specific properties
     */
    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> elements = new ArrayList<String[]>(5);

        elements.addAll(moreInfo);

        if (!"".equals(filter)) {
            elements.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_WORKTABLESCANNODE_FILTER),
                    StringEscapeUtils.escapeHtml(filter)).getProp());
        }
        if (!"".equals(rowsRemovedByFilter)) {
            elements.add(new ServerProperty(
                    MessageConfigLoader
                            .getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_WORKTABLESCANNODE_ROWREMOVEDFILTER),
                    rowsRemovedByFilter).getProp());
        }

        return elements;
    }

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @return the per DN specific details
     */
    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.ACTUALS_IN_DETAIL,
                INDETAILSCATEGORY.CPU_IN_DETAIL, INDETAILSCATEGORY.BUFFERS_IN_DETAIL);

        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        return output;
    }
}
