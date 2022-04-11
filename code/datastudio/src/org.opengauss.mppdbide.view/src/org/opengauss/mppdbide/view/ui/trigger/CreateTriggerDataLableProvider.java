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

package org.opengauss.mppdbide.view.ui.trigger;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Title: CreateTriggerDataLableProvider for use
 *
 * @since 3.0.0
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
