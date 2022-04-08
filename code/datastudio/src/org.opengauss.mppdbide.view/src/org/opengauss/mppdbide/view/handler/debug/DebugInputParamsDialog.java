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

package org.opengauss.mppdbide.view.handler.debug;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.view.core.ParameterInputDialog;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class DebugInputParamsDialog extends ParameterInputDialog {
    /**
     * descripton: create input param dialog
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
