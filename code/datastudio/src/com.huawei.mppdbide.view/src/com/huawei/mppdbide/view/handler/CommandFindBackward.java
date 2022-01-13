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

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.FindAndReplaceOptions;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandFindBackward.
 *
 * @since 3.0.0
 */
public class CommandFindBackward {

    /* Elements */

    private FindAndReplaceOptions findandreplace;

    private PLSourceEditorCore core;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        Object partObject = UIElement.getInstance().getActivePartObject();

        if (partObject instanceof PLSourceEditor) {
            core = ((PLSourceEditor) partObject).getSourceEditorCore();

        } else if (partObject instanceof SQLTerminal) {
            core = ((SQLTerminal) partObject).getTerminalCore();

        }

        else {
            return;
        }
        findandreplace = core.getFindAndReplaceoptions();

        if (null != findandreplace) {
            findandreplace.setBackwardSearch(true);
            core.findText(false);
        }
    }
}
