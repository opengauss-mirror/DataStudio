/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
