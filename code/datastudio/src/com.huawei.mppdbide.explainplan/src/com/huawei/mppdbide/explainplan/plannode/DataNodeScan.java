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
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataNodeScan.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DataNodeScan extends ScanNode {

    /**
     * The primary node count.
     */
    @SerializedName("Primary node count")
    protected long primaryNodeCount;

    /**
     * The node count.
     */
    @SerializedName("Node count")
    protected long nodeCount;

    /**
     * The remote query.
     */
    @SerializedName("Remote Query")
    protected String remoteQuery = "";

    /**
     * Instantiates a new data node scan.
     */
    public DataNodeScan() {
        super();
    }

    @Override
    public List<String> getNodeSpecific() {
        ArrayList<String> list = new ArrayList<String>(1);
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_DATANODESCAN_PRIMARYNODECOUNT))
                .append(": ").append(primaryNodeCount);
        list.add(sb.toString());
        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_DATANODESCAN_NODECOUNT))
                .append(": ").append(nodeCount);
        list.add(sb.toString());

        if (!"".equals(remoteQuery)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NODE_REMOTEQUERY))
                    .append(": ").append(StringEscapeUtils.escapeHtml(remoteQuery));
            list.add(sb.toString());
        }

        return list;
    }

    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_DATANODESCAN_PRIMARYNODECOUNT),
                primaryNodeCount).getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_DATANODESCAN_NODECOUNT), nodeCount)
                        .getProp());

        if (!"".equals(remoteQuery)) {
            arList.add(new ServerProperty(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_NODE_REMOTEQUERY),
                    StringEscapeUtils.escapeHtml(remoteQuery)).getProp());
        }
        return arList;
    }

    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap);

        return output;
    }
}
