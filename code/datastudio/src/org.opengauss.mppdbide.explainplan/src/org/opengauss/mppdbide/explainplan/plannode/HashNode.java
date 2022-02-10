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

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.explainplan.nodetypes.HashDetail;
import org.opengauss.mppdbide.explainplan.nodetypes.INDETAILSCATEGORY;
import org.opengauss.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.presentation.objectproperties.DNIntraNodeDetailsColumn;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class HashNode.
 *
 * @since 3.0.0
 */
public class HashNode extends OperationalNode {

    /**
     * The hash buckets.
     */
    @SerializedName("Hash Buckets")
    protected long hashBuckets;

    /**
     * The hash batches.
     */
    @SerializedName("Hash Batches")
    protected long hashBatches;

    /**
     * The original hash batches.
     */
    @SerializedName("Original Hash Batches")
    protected long originalHashBatches;

    /**
     * The peak memory usage.
     */
    @SerializedName("Peak Memory Usage")
    protected long peakMemoryUsage;

    /**
     * The hash detail.
     */
    @SerializedName("Hash Detail")
    protected List<HashDetail> hashDetail;

    @SerializedName("Max Hash Buckets")
    private long maxHashBuckets;
    @SerializedName("Min Hash Buckets")
    private long minHashBuckets;
    @SerializedName("Max Hash Batches")
    private long maxHashBatches;
    @SerializedName("Min Hash Batches")
    private long minHashBatches;
    @SerializedName("Max Peak Memory Usage")
    private long maxMemoryUsage;
    @SerializedName("min Peak Memory Usage")
    private long minMemoryUsage;
    @SerializedName("Max Original Hash Batches")
    private long maxOriginalBatches;
    @SerializedName("Min Original Hash Batches")
    private long minOriginalBatches;

    /**
     * Instantiates a new hash node.
     */
    public HashNode() {
        super(NodeCategoryEnum.HASH);
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        List<String> list = new ArrayList<String>(1);
        List<String> otherInfo = super.getAdditionalInfo(isAnalyze);
        list.addAll(otherInfo);

        if (hashBatches > 0) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Buckets: ");
            sb.append(hashBuckets);
            sb.append(" Batches: ");
            sb.append(hashBatches);
            sb.append(" Memory Usage: ");
            sb.append(peakMemoryUsage);
            list.add(sb.toString());
        }
        if (isAnalyze) {
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Max Buckets: ");
            sb.append(maxHashBuckets);
            sb.append(" Max Batches: ");
            sb.append(maxHashBatches);
            if (maxHashBatches != maxOriginalBatches || minHashBatches != minOriginalBatches) {
                sb.append(" (max originally ");
                sb.append(maxOriginalBatches);
                sb.append(") ");
            }
            sb.append(" Max Memory Usage: ");
            sb.append(maxMemoryUsage);
            sb.append("kB");

            list.add(sb.toString());
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Min Buckets: ");
            sb.append(minHashBuckets);
            sb.append(" Min Batches: ");
            sb.append(minHashBatches);
            if (maxHashBatches != maxOriginalBatches || minHashBatches != minOriginalBatches) {
                sb.append(" (min originally ");
                sb.append(minOriginalBatches);
                sb.append(") ");
            }
            sb.append(" Min Memory Usage: ");
            sb.append(minMemoryUsage);
            sb.append("kB");
            list.add(sb.toString());
        }
        return list;
    }

    /**
     * To text.
     *
     * @param isAnalyze the is analyze
     * @return the string
     */
    @Override
    public String toText(boolean isAnalyze) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getNodeType());
        sb.append(" ");
        sb.append(getCostInfoForTextDisplay(isAnalyze));
        return sb.toString();
    }

    /**
     * Gets the node specific.
     *
     * @return the node specific
     */
    @Override
    public List<String> getNodeSpecific() {
        if (null == hashDetail) {
            ArrayList<String> arList = new ArrayList<String>(1);
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Hash Buckets: ").append(hashBuckets);
            arList.add(sb.toString());
            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Hash Batches: ").append(hashBatches);
            arList.add(sb.toString());

            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Original Hash Batches: ").append(originalHashBatches);
            arList.add(sb.toString());

            sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            sb.append("Peak Memory Usage: ").append(peakMemoryUsage);
            arList.add(sb.toString());

            return arList;
        }

        return null;
    }

    private void fillnoDNHashDetail(ArrayList<String[]> arList) {
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_HASHNODE_HASHBUCKETS), hashBuckets)
                        .getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_HASHNODE_HASHBATCHES), hashBatches)
                        .getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_HASHNODE_ORIGINALHASHBATCHES),
                originalHashBatches).getProp());
        arList.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_HASHNODE_PEAKMEMORYUSAGE),
                peakMemoryUsage).getProp());
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

        /* If DN does not have hash node specific information */
        if (null == hashDetail) {
            fillnoDNHashDetail(arList);
        }

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
        ArrayList<INDETAILSCATEGORY> detailsNeeded = initDetailArray(INDETAILSCATEGORY.ACTUALS_IN_DETAIL,
                INDETAILSCATEGORY.CPU_IN_DETAIL, INDETAILSCATEGORY.BUFFERS_IN_DETAIL);
        Map<String, List<Object>> output = super.getPerDNSpecificDetails(inputMap, detailsNeeded);

        /* Add HASH DN Information */
        if (null != hashDetail) {
            getHashDetail(output);
        }
        return output;
    }

    /**
     * Gets the per DN specific column grouping info.
     *
     * @param colGroup the col group
     * @return the per DN specific column grouping info
     */
    @Override
    public List<DNIntraNodeDetailsColumn> getPerDNSpecificColumnGroupingInfo(List<DNIntraNodeDetailsColumn> colGroup) {
        List<DNIntraNodeDetailsColumn> outputColGroup = super.getPerDNSpecificColumnGroupingInfo(colGroup);

        if (null != hashDetail) {
            outputColGroup.add(HashDetail.fillColumnPropertyHeader());
        }

        return outputColGroup;
    }

    private void getHashDetail(Map<String, List<Object>> inputMap) {
        for (HashDetail entry : hashDetail) {
            if (!inputMap.containsKey(entry.getDnName())) {
                List<Object> propsValues = entry.propertyDetails();
                inputMap.put(entry.getDnName(), propsValues);
            } else {
                inputMap.get(entry.getDnName()).addAll(entry.propertyDetails());
            }
        }

    }
}
