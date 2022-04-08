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

package org.opengauss.mppdbide.presentation.visualexplainplan;

import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;

/**
 * Title: DSSpaceTreeLayoutAlgorithm
 * 
 * @since 3.0.0
 */
public class DSSpaceTreeLayoutAlgorithm extends SpaceTreeLayoutAlgorithm {

    /**
     * Instantiates a new DS space tree layout algorithm.
     *
     * @param orientation the orientation
     */
    public DSSpaceTreeLayoutAlgorithm(int orientation) {
        super(orientation);
        customize();
    }

    private void customize() {
        setLeafGap(50);
        setBranchGap(65);
        setLayerGap(50);
    }
}
