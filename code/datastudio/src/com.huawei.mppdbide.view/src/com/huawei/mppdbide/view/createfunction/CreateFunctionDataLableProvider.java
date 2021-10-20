/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Title: DebugSourceDataLableProvider for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public class CreateFunctionDataLableProvider extends ColumnLabelProvider {
    private int titleIndex;

    public CreateFunctionDataLableProvider(int titleIndex) {
        this.titleIndex = titleIndex;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof CreateFunctionParam) {
            CreateFunctionParam data = (CreateFunctionParam) element;
            return data.getValue(titleIndex).toString();
        }
        return super.getText(element);
    }

    @Override
    public Color getForeground(Object element) {
        if (element instanceof CreateFunctionParam) {
            return new Color(Display.getCurrent(), new RGB(255, 0, 0));
        }
        return super.getForeground(element);
    }
}
