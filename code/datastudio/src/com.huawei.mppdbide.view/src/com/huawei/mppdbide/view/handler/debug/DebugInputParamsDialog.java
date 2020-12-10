/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.core.ParameterInputDialog;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 05,12,2020]
 * @since 05,12,2020
 */
public class DebugInputParamsDialog extends ParameterInputDialog {

    /**
     * descript: create input param dialog
     * 
     * @param parentShell shell param
     */
    public DebugInputParamsDialog(Shell parentShell) {
        super(parentShell);
        setDefaultImage(IconUtility.getIconImage(IiconPath.ICO_TOOL_128X128, this.getClass()));
    }
    
    @Override
    public void executePressed() {
    }

}
