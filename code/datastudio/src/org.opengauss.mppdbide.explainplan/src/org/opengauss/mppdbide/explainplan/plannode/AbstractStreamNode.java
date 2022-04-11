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

import com.google.gson.annotations.SerializedName;
import org.opengauss.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractStreamNode.
 *
 * @since 3.0.0
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
