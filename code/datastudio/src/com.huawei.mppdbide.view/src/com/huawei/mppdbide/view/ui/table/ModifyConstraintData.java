/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 17,05,2021]
 * @since 17,05,2021
 */
public class ModifyConstraintData {
    private ConstraintMetaData constraintMetaData;

    public ModifyConstraintData(ConstraintMetaData data) {
        this.constraintMetaData = data;
    }

    /**
     * Description: get constraint
     *
     * @return ConstraintMetaData constraint meta data
     */
    public ConstraintMetaData getConstraint() {
        return this.constraintMetaData;
    }
}
