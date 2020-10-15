/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;

/**
 * Title: DSSpaceTreeLayoutAlgorithm
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
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
