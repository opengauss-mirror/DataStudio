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

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.huawei.mppdbide.view.objectpropertywiew.PropertiesWindow;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.ViewEditTableDataUIWindow;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CloseActiveSourceViewerHandler.
 *
 * @since 3.0.0
 */
public class CloseActiveSourceViewerHandler {

    /**
     * Execute.
     *
     * @param activePart the active part
     */
    @Execute
    public void execute(@Active MPart activePart) {
        Object activeObj = activePart.getObject();

        if (activeObj != null) {
            if (activeObj instanceof ViewEditTableDataUIWindow || activeObj instanceof SQLTerminal
                    || activeObj instanceof PLSourceEditor || activeObj instanceof PropertiesWindow) {
                UIElement.getInstance().removePartFromStack(activePart.getElementId());
            }
        }
    }

}
