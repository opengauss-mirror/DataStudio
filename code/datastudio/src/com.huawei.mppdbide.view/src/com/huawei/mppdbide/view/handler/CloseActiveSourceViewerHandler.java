/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
