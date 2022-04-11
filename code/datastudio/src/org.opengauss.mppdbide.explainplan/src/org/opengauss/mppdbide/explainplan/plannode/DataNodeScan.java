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
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataNodeScan.
 *
 * @since 3.0.0
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
