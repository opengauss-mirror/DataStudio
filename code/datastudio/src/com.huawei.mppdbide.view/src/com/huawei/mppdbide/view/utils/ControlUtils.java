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

package com.huawei.mppdbide.view.utils;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * Title: class
 * 
 * Description: The Class ControlUtils.
 *
 * @since 3.0.0
 */
public abstract class ControlUtils {

    /**
     * Gets the control.
     *
     * @param textSqlPreview the text sql preview
     * @return the control
     */
    public static Control getControl(SourceViewer textSqlPreview) {
        Control control = textSqlPreview.getControl();
        if (control instanceof Composite) {
            Composite composite = (Composite) control;
            Control[] childControls = composite.getChildren();
            Control childControl = null;
            for (int i = 0; i < childControls.length; i++) {
                childControl = childControls[i];
                if (childControl instanceof StyledText) {
                    control = childControl;
                    break;
                }
            }
        }

        return control;
    }
}
