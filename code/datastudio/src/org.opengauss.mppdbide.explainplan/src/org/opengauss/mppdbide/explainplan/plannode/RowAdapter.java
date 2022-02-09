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

import org.opengauss.mppdbide.explainplan.nodetypes.NodeCategoryEnum;
import org.opengauss.mppdbide.explainplan.nodetypes.OperationalNode;

/**
 * 
 * Title: class
 * 
 * Description: The Class RowAdapter.
 *
 * @since 3.0.0
 */
public class RowAdapter extends OperationalNode {

    /**
     * Instantiates a new row adapter.
     */
    public RowAdapter() {
        super(NodeCategoryEnum.ROWADAPTER);
    }

    @Override
    public ArrayList<String> getDNInvolved(ArrayList<String> dnsInvolved) {
        return new ArrayList<String>(5);
    }

    @Override
    public List<String[]> getNodeSpecificProperties() {
        List<String[]> moreInfo = super.getNodeSpecificProperties();

        ArrayList<String[]> arList = new ArrayList<String[]>(5);

        arList.addAll(moreInfo);

        return arList;
    }

}
