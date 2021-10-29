/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Title: CreateTriggerDataLableProvider for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2021-4-30]
 * @since 2021-4-30
 */
public class CreateTriggerDataLableProvider extends ColumnLabelProvider {
    private int titleIndex;

    public CreateTriggerDataLableProvider(int titleIndex) {
        this.titleIndex = titleIndex;
    }

    public CreateTriggerDataLableProvider() {
        this(-1);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof CreateTriggerParam && titleIndex > -1) {
            CreateTriggerParam data = (CreateTriggerParam) element;
            return data.getValue(titleIndex);
        }
        return "";
    }
}
