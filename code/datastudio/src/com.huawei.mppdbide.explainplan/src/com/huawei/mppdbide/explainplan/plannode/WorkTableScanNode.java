/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class WorkTableScanNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
