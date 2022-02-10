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
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class StreamRedistribute.
 *
 * @since 3.0.0
 */
public class StreamRedistribute extends AbstractStreamNode {

    /**
     * The distribute key.
     */
    @SerializedName("Distribute Key")
    protected List<String> distributeKey;

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

        elements.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_STREAM_REDISTRIBUTE_DISTRKEY),
                CustomStringUtility.getFormatedOutput(distributeKey, ",")).getProp());

        elements.add(new ServerProperty(
                MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_PROP_STREAM_REDISTRIBUTE_SPAWNON),
                spawnOn).getProp());
        return elements;
    }

}
