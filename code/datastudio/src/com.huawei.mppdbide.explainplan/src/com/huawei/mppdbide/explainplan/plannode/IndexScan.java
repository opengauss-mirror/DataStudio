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

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexScan.
 *
 * @since 3.0.0
 */
public class IndexScan extends ScanNode {

    /**
     * The scan direction.
     */
    @SerializedName("Scan Direction")
    protected String scanDirection = "";

    /**
     * The index name.
     */
    @SerializedName("Index Name")
    protected String indexName = "";

    /**
     * The index cond.
     */
    @SerializedName("Index Cond")
    protected String indexCond = "";

    /**
     * The rows removed index recheck.
     */
    @SerializedName("Rows Removed by Index Recheck")
    protected String rowsRemovedIndexRecheck = "";

    /**
     * Instantiates a new index scan.
     */
    public IndexScan() {
        super();
    }

    /**
     * To text.
     *
     * @param isAnalyze the is analyze
     * @return the string
     */
    @Override
    public String toText(boolean isAnalyze) {
        StringBuilder sb = new StringBuilder(getNodeName());
        sb.append(" ");
        sb.append(getCostInfoForTextDisplay(isAnalyze));
        return sb.toString();
    }

    /**
     * Gets the node name.
     *
     * @return the node name
     */
    @Override
    public String getNodeName() {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getNodeType());
        sb.append(" using ");
        sb.append(indexName);
        if (!"".equals(relationName)) {
            sb.append(" on ");
            sb.append(relationName);
            sb.append(" ");
        }

        if (!"".equals(alias) && !alias.equals(relationName)) {
            sb.append(alias);
        }
        return sb.toString();
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        List<String> list = new ArrayList<String>(1);
        if (!"".equals(indexCond)) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Index Cond: ");
            sb.append(indexCond);
            list.add(sb.toString());
        }
        if (!"".equals(filter)) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Filter: ");
            sb.append(filter);
            list.add(sb.toString());
        }
        return list;
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    @Override
    public List<String> getNodeSpecific() {
        List<String> moreInfo = super.getNodeSpecific();
        ArrayList<String> arList = new ArrayList<String>(1);

        arList.add(moreInfo.toString());

        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Scan Direction: ").append(scanDirection);
        arList.add(sb.toString());
        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Index Name: ").append(indexName);
        arList.add(sb.toString());
        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Index Cond: ").append(indexCond);
        arList.add(sb.toString());
        sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("Rows Removed by Index Recheck: ").append(rowsRemovedIndexRecheck);
        arList.add(sb.toString());

        if (!"".equals(filter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Filter: ").append(filter);
            arList.add(sb.toString());
        }

        if (!"".equals(rowsRemovedByFilter)) {
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Rows Removed by Filter: ").append(rowsRemovedByFilter);
            arList.add(sb.toString());
        }

        return arList;
    }

    /**
     * Gets the node specific properties.
     *
     * @return the node specific properties
     */
    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_INDEXSCANNODE_SCANDIRECTION),
                scanDirection).getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_INDEXSCANNODE_INDEXNAME), indexName)
                        .getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_INDEXSCANNODE_INDEXCOND), indexCond)
                        .getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_INDEXSCANNODE_ROWSREMOVEDRECHECK),
                rowsRemovedIndexRecheck).getProp());

        addPropFilter(arList);
        addPropRowsRemovedByFilter(arList);

        return arList;
    }

    /**
     * Gets the per DN specific details.
     *
     * @param inputMap the input map
     * @return the per DN specific details
     */
    @Override
    public Map<String, List<Object>> getPerDNSpecificDetails(Map<String, List<Object>> inputMap) {
        return inputMap;
    }

    /**
     * Gets the item name.
     *
     * @return the item name
     */
    @Override
    public String getItemName() {
        return "Index: " + this.indexName;
    }
}
