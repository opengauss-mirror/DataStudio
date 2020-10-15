/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.explainplan.plannode;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.huawei.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import com.huawei.mppdbide.explainplan.nodetypes.OperationalNode;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractStreamNode.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractStreamNode extends OperationalNode {

    /**
     * The spawn on.
     */
    @SerializedName("Spawn on")
    protected String spawnOn = "";

    /**
     * Instantiates a new abstract stream node.
     */
    public AbstractStreamNode() {
        super(NodeCategoryEnum.STREAM);
    }

    @Override
    public String toText(boolean isAnalyze) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append(getNodeType());
        sb.append(" ");
        sb.append(getCostInfoForTextDisplay(isAnalyze));
        return sb.toString();
    }

    @Override
    public List<String> getAdditionalInfo(boolean isAnalyze) {
        List<String> list = new ArrayList<String>(1);
        List<String> otherInfo = super.getAdditionalInfo(isAnalyze);
        list.addAll(otherInfo);

        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (!"".equals(spawnOn)) {
            sb.append("Spawn on: ");
            sb.append(spawnOn);
            list.add(sb.toString());
        }
        return list;
    }
}
