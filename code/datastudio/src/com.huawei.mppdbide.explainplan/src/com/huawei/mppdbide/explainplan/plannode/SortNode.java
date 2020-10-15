/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SortNode extends OperationalNode {

    /**
     * The sort key.
     */
    @SerializedName("Sort Key")
    protected List<String> sortKey;

    /**
     * The sort method.
     */
    @SerializedName("Sort Method")
    protected String sortMethod = "";

    /**
     * The sort space used.
     */
    @SerializedName("Sort Space Used")
    protected int sortSpaceUsed;

    /**
     * The sort space type.
     */
    @SerializedName("Sort Space Type")
    protected String sortSpaceType = "";

    /**
     * The dn details.
     */
    @SerializedName("Sort Detail")
    protected List<SortNodeDNDetails> dnDetails;

    /**
     * Instantiates a new sort node.
     */
    public SortNode() {
        super(NodeCategoryEnum.SORT);
    }

    /**
     * Gets the additional info.
     *
     * @param isAnalyze the is analyze
     * @return the additional info
     */
    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        ArrayList<String> list = new ArrayList<String>(1);
        List<String> otherInfo = super.getAdditionalInfo(isAnalyze);
        list.addAll(otherInfo);

        StringBuilder sb = new StringBuilder("Sort Key: ");
        int index = 0;
        if (!this.sortKey.isEmpty()) {
            for (String str : this.sortKey) {
                if (index != 0) {
                    sb.append(", ");
                }
                sb.append(str);
                index++;
            }
        }
        list.add(sb.toString());

        if (isAnalyze) {
            if (!"".equals(sortMethod)) {
                sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                sb.append("Sort Method: ");
                sb.append(sortMethod);
                sb.append(" Memory: ");
                sb.append(sortSpaceUsed);
                sb.append("kB");
                list.add(sb.toString());
            }
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
        ArrayList<String> nodes = new ArrayList<String>(3);
        nodes.add("Sort Method : " + this.sortMethod);
        nodes.add("Sort Space Used : " + this.sortSpaceUsed);
        nodes.add("Sort Space Type : " + this.sortSpaceType);

        return nodes;
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

        arList.add(
                new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_SORT_SORTKEYS),
                        CustomStringUtility.getFormatedOutput(sortKey, ",")).getProp());
        return arList;
    }

}
