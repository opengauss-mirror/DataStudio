/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Title: DebugSourceDataLableProvider for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public class DebugSourceDataLableProvider extends ColumnLabelProvider {
    private int titleIndex;

    public DebugSourceDataLableProvider(int titleIndex) {
        this.titleIndex = titleIndex;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IDebugSourceData) {
            IDebugSourceData data = (IDebugSourceData) element;
            return data.getValue(titleIndex).toString();
        }
        return super.getText(element);
    }
}
