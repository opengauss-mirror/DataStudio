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

package org.opengauss.mppdbide.view.createfunction;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Title: DebugSourceDataLableProvider for use
 *
 * @since 3.0.0
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
