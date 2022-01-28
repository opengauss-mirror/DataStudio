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

package com.huawei.mppdbide.view.handler.debug;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class DebugHandlerUtils {
    private static DebugHandlerUtils debugUtils = new DebugHandlerUtils();
    private boolean isDebugStart = false;

    private DebugHandlerUtils() {
    }

    /**
     * description: get singleton DebugHandlerUtils instance
     *
     * @return DebugHandlerUtils the instance
     */
    public static DebugHandlerUtils getInstance() {
        return debugUtils;
    }

    /**
     * description: can start debug
     *
     * @return boolean true if can start debug
     */
    public boolean canStartDebug() {
        if (isDebugStart) {
            return false;
        }

        IDebugObject debugObject = IHandlerUtilities.getSelectedDebugObject();
        if (debugObject != null) {
            return debugObject.getDatabase().isConnected();
        }

        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (plSourceEditor != null) {
            debugObject = plSourceEditor.getDebugObject();
            if (debugObject != null) {
                return debugObject.getDatabase().isConnected();
            }
        }
        return false;
    }

    /**
     * description: can terminate debug
     *
     * @return boolean true if can terminate
     */
    public boolean canTerminateDebug() {
        return isDebugStart;
    }

    /**
     * description: set debug start status
     *
     * @param isDebugStart debug status
     * @return void
     */
    public void setDebugStart(boolean isDebugStart) {
        this.isDebugStart = isDebugStart;
    }

    /**
     * description: this use to show debug view partstack
     *
     * @param isShow show or not
     * @return void
     */
    public void showAllDebugView(boolean isShow) {
        String partId = "com.huawei.mppdbide.partsashcontainer.id.sub.alldebug";
        MUIElement allDebugPart = UIElement.getInstance().getModelService().find(partId,
                UIElement.getInstance().getApplication());
        allDebugPart.setVisible(isShow);
    }

    /**
     * description: show debug source code view
     *
     * @return void
     */
    @SuppressWarnings("restriction")
    public void initDebugSourceView() {
        PreferenceWrapper.getInstance().getPreferenceStore().setValue("sqlterminal.folding", false);
        String viewCommand = "com.huawei.mppdbide.command.id.viewsourceobjectbrowseritem";
        ECommandService commandService = EclipseInjections.getInstance().getCommandService();
        EHandlerService handlerService = EclipseInjections.getInstance().getHandlerService();
        Command command = commandService.getCommand(viewCommand);
        ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
        handlerService.executeHandler(parameterizedCommand);
    }
}
